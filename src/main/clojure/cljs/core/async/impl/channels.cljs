;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns cljs.core.async.impl.channels
  (:require [cljs.core.async.impl.protocols :as impl]
            [cljs.core.async.impl.dispatch :as dispatch]
            [cljs.core.async.impl.buffers :as buffers]))



(defn box [val]
  (reify cljs.core/IDeref
    (-deref [_] val)))

(deftype PutBox [handler val])

(deftype ManyToManyChannel [takes puts ^not-native buf ^:mutable closed]
  impl/WritePort
  (put! [this val ^not-native handler]
    (assert (not (nil? val)) "Can't put nil in on a channel")
    (if (or closed
            ^boolean (not (impl/active? handler)))
      (box nil)
      (loop []
        (let [taker (.pop takes)]
          (if-not (nil? taker)
            (if ^boolean (impl/active? taker)
                (let [take-cb (impl/commit taker)
                      _ (impl/commit handler)]
                  (dispatch/run (fn [] (take-cb val)))
                  (box nil))
                (recur))
            
            (if (not (or (nil? buf)
                         ^boolean (impl/full? buf)))
              (let [_ (impl/commit handler)]
                (do (impl/add! buf val)
                    (box nil)))
              (do
                (assert (< (.-length puts) impl/MAX-QUEUE-SIZE)
                        (str "No more than " impl/MAX-QUEUE-SIZE
                             " pending puts are allowed on a single channel."
                             " Consider using a windowed buffer."))
                (.unbounded-unshift puts (PutBox. handler val))
                nil)))))))

  impl/ReadPort
  (take! [this ^not-native handler]
    
    (if (not (impl/active? handler))
      nil
      (if (and (not (nil? buf)) (pos? (count buf)))
        (let [_ (impl/commit handler)]
          (box (impl/remove! buf)))
        (loop []
          (let [putter (.pop puts)]
            (if-not (nil? putter)
              (let [put-handler (.-handler putter)
                    val (.-val putter)]
                (if ^boolean (impl/active? put-handler)
                    (let [put-cb (impl/commit put-handler)
                          _ (impl/commit handler)]
                      (dispatch/run put-cb)
                      (box val))
                    (recur)))
              (if ^boolean closed
                (let [_ (impl/commit handler)]
                  (box nil))
                (do
                  (assert (< (.-length takes) impl/MAX-QUEUE-SIZE)
                          (str "No more than " impl/MAX-QUEUE-SIZE
                               " pending takes are allowed on a single channel."))
                  (.unbounded-unshift takes handler)
                  nil))))))))
  
  impl/Channel
  (close! [this]
    (if ^boolean closed
        nil
        (do (set! closed true)
            (loop []
              (let [taker (.pop takes)]
                (when-not (nil? taker)
                  (when ^boolean (impl/active? taker)
                    (let [take-cb (impl/commit taker)]
                      (dispatch/run (fn [] (take-cb nil)))))
                  (recur))))
            nil))))

(defn chan [buf]
  (ManyToManyChannel. (buffers/ring-buffer 32) (buffers/ring-buffer 32) buf nil))

