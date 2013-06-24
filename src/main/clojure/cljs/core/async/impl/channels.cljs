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

(deftype ManyToManyChannel [takes puts buf closed]
  MMC
  (cleanup [_]
    (loop [idx 0]
      (when (< idx (.-length puts))
        (let [[itm val] (aget puts idx)]
          (if (impl/active? itm)
            (recur (inc idx))
            (do (.splice puts idx 1)
                (recur idx))))))

    (loop [idx 0]
      (when (< idx (.-length takes))
        (let [itm (aget takes idx)]
          (if (impl/active? itm)
            (recur (inc idx))
            (do (.splice takes idx 1)
                (recur idx)))))))

  impl/WritePort
  (put! [this val handler]
    (assert (not (nil? val)) "Can't put nil in on a channel")
    (cleanup this)
    (if @closed
      (box nil)
      (let [[put-cb take-cb] (loop [taker-idx 0]
                               (when (< taker-idx (.-length takes))
                                 (let [taker (aget takes taker-idx)]
                                   (if (and (impl/active? taker)
                                            (impl/active? handler))
                                     (do (.splice takes taker-idx 1)
                                         [(impl/commit handler) (impl/commit taker)])
                                     (recur (inc taker-idx))))))]
        (if (and put-cb take-cb)
          (do (dispatch/run (fn [] (take-cb val)))
              (box nil))
          (if (and buf (not (impl/full? buf)))
            (let [put-cb (and (impl/active? handler)
                              (impl/commit handler))]
              (if put-cb
                (do (impl/add! buf val)
                    (box nil))
                nil))
            (.unshift puts [handler val]))))))

  impl/ReadPort
  (take! [this handler]
    (cleanup this)
    (if (and buf (pos? (count buf)))
      (if-let [take-cb (and (impl/active? handler) (impl/commit handler))]
        (box (impl/remove! buf))
        nil)
      (let [[take-cb put-cb val] (loop [put-idx 0]
                                   (when (< put-idx (.-length puts))
                                     (let [[putter val] (aget puts put-idx)]
                                       (if-let [ret (when (and (impl/active? handler) 
                                                               (impl/active? putter))
                                                      [(impl/commit handler) 
                                                       (impl/commit putter) 
                                                       val])]
                                         (do (.splice puts put-idx 1)
                                             ret)
                                         (recur (inc put-idx))))))]
        (if (and put-cb take-cb)
          (do (dispatch/run put-cb)
              (box val))
          (if @closed
            (if-let [take-cb (and (impl/active? handler) (impl/commit handler))]
              (box nil)
              nil)
            (do (.unshift takes handler)
                nil))))))
  impl/Channel
  (close! [this]
    (cleanup this)
    (if @closed
      nil
      (do (reset! closed true)
          (dotimes [idx (.-length takes)]
            (let [taker (aget takes idx)
                  take-cb (and (impl/active? taker) (impl/commit taker))]
              (when take-cb
                (dispatch/run (fn [] (take-cb nil))))))
          nil))))

(defn chan [buf]
  (ManyToManyChannel. (make-array 0) (make-array 0) buf (atom nil)))

