;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns core.async.timers
  (:require [core.async.protocols :as proto]
            [core.async.channels :as channels])
  (:import [java.util.concurrent DelayQueue Delayed TimeUnit]))

(set! *warn-on-reflection* true)

(defonce ^:private ^DelayQueue timeouts-queue
  (DelayQueue.))

(deftype TimeoutQueueEntry [channel ^long timestamp]
  Delayed
  (getDelay [this time-unit]
    (.convert time-unit
              (- timestamp (System/currentTimeMillis))
              TimeUnit/MILLISECONDS))
  (compareTo
   [this other]
   (let [ostamp (.timestamp ^TimeoutQueueEntry other)]
     (if (< timestamp ostamp)
       -1
       (if (= timestamp ostamp)
         0
         1))))
  proto/Channel
  (close! [this]
    (proto/close! channel)))

(defn timeout
  "returns a channel that will close after msecs"
  [msecs]
  (let [timeout (+ (System/currentTimeMillis) msecs)
        timeout-channel (channels/chan nil)
        timeout-delay (TimeoutQueueEntry. timeout-channel timeout)]
    (.put timeouts-queue timeout-delay)
    timeout-channel))

(defn- timeout-worker
  []
  (let [q timeouts-queue]
    (loop []
      (proto/close! (.take q))
      (recur))))

(defonce timeout-daemon
  (doto (Thread. ^Runnable timeout-worker "core.async.timers/timeout-daemon")
    (.setDaemon true)
    (.start)))
