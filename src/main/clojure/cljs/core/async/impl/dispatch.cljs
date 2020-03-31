;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns cljs.core.async.impl.dispatch
  (:require [cljs.core.async.impl.buffers :as buffers]
            [goog.async.nextTick]))

(def tasks (buffers/ring-buffer 32))
(def running? false)
(def queued? false)

(def TASK_BATCH_SIZE 1024)

(declare queue-dispatcher)

(defn process-messages []
  (set! running? true)
  (set! queued? false)
  (loop [count 0]
    (let [m (.pop tasks)]
      (when-not (nil? m)
        (m)
        (when (< count TASK_BATCH_SIZE)
          (recur (inc count))))))
  (set! running? false)
  (when (> (.-length tasks) 0)
    (queue-dispatcher)))

(defn queue-dispatcher []
  (when-not (and queued? running?)
    (set! queued? true)
    (goog.async.nextTick process-messages)))

(defn run [f]
  (.unbounded-unshift tasks f)
  (queue-dispatcher))

(defn queue-delay [f delay]
  (js/setTimeout f delay))

