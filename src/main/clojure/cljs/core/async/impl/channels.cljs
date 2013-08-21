;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns cljs.core.async.impl.channels
  (:require [cljs.core.async.impl.protocols :as impl]
            [cljs.core.async.impl.dispatch :as dispatch]))



(defn box [val]
  (reify cljs.core/IDeref
    (-deref [_] val)))

(defprotocol MMC
  (cleanup [_]))

(deftype ManyToManyChannel [takes puts ^not-native buf closed]
  MMC
  (cleanup [_]
    (loop [idx 0]
      (when (< idx (.-length puts))
        (let [[itm val] (aget puts idx)]
          (if ^boolean (impl/active? ^not-native itm)
            (recur (inc idx))
            (do (.splice puts idx 1)
                (recur idx))))))

    (loop [idx 0]
      (when (< idx (.-length takes))
        (let [itm (aget takes idx)]
          (if ^boolean (impl/active? ^not-native itm)
            (recur (inc idx))
            (do (.splice takes idx 1)
                (recur idx)))))))

  impl/WritePort
  (put! [this val ^not-native handler]
    (assert (not (nil? val)) "Can't put nil in on a channel")
    (cleanup this)
    (if ^boolean @closed
      (box nil)
      (let [[put-cb take-cb] (loop [taker-idx 0]
                               (when (< taker-idx (.-length takes))
                                 (let [^not-native taker (aget takes taker-idx)]
                                   (if (and ^boolean (impl/active? taker)
                                            ^boolean (impl/active? handler))
                                     (do (.splice takes taker-idx 1)
                                         [(impl/commit handler) (impl/commit taker)])
                                     (recur (inc taker-idx))))))]
        (if (not (or (nil? put-cb) (nil? take-cb)))
          (do (dispatch/run (fn [] (take-cb val)))
              (box nil))
          (if (not (or (nil? buf) ^boolean (impl/full? buf)))
            (let [put-cb (and ^boolean (impl/active? handler)
                              (not (nil? (impl/commit handler))))]
              (if-not (false? put-cb)
                (do (impl/add! buf val)
                    (box nil))
                nil))
            (do
              (assert (< (.-length puts) impl/MAX-QUEUE-SIZE)
                      (str "No more than " impl/MAX-QUEUE-SIZE
                           " pending puts are allowed on a single channel."
                           " Consider using a windowed buffer."))
              (.push puts [handler val])
              nil))))))

  impl/ReadPort
  (take! [this ^not-native handler]
    (cleanup this)
    (if (and (not (nil? buf)) (pos? (count buf)))
      (let [take-cb (and ^boolean (impl/active? handler) (not (nil? (impl/commit handler))))]
        (if-not (false? take-cb)
          (box (impl/remove! buf))
          nil))
      (let [[take-cb put-cb val] (loop [put-idx 0]
                                   (when (< put-idx (.-length puts))
                                     (let [[putter val] (aget puts put-idx)
                                            ret (when (and ^boolean (impl/active? handler) 
                                                           ^boolean (impl/active? ^not-native putter))
                                                  [(impl/commit handler) 
                                                   (impl/commit ^not-native putter) 
                                                   val])]
                                       (if-not (nil? ret)
                                         (do (.splice puts put-idx 1)
                                           ret)
                                         (recur (inc put-idx))))))]
        (if (not (or (nil? put-cb) (nil? take-cb)))
          (do (dispatch/run put-cb)
              (box val))
          (if ^boolean @closed
            (let [take-cb (and ^boolean (impl/active? handler) (not (nil? (impl/commit handler))))]
              (if-not (false? take-cb)
                (box nil)
                nil))
            (do
              (assert (< (.-length takes) impl/MAX-QUEUE-SIZE)
                      (str "No more than " impl/MAX-QUEUE-SIZE
                           " pending takes are allowed on a single channel."))
              (.push takes handler)
              nil))))))
  impl/Channel
  (close! [this]
    (cleanup this)
    (if ^boolean @closed
      nil
      (do (reset! closed true)
          (dotimes [idx (.-length takes)]
            (let [^not-native taker (aget takes idx)]
              (when ^boolean (impl/active? taker)
                (let [take-cb (impl/commit taker)]
                  (when-not (nil? take-cb)
                    (dispatch/run (fn [] (take-cb nil))))))))
          nil))))

(defn chan [buf]
  (ManyToManyChannel. (make-array 0) (make-array 0) buf (atom nil)))

