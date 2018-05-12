;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns ^{:skip-wiki true}
  clojure.core.async.impl.timers
  (:require [clojure.core.async.impl.protocols :as impl]
            [clojure.core.async.impl.channels :as channels])
  (:import [java.util.concurrent DelayQueue Delayed TimeUnit ConcurrentSkipListMap]))

(set! *warn-on-reflection* true)

(defonce ^:private ^DelayQueue timeouts-queue
  (DelayQueue.))

(defonce ^:private ^ConcurrentSkipListMap timeouts-map
  (ConcurrentSkipListMap.))

(def ^:const TIMEOUT_RESOLUTION_MS 10)

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
  impl/Channel
  (close! [this]
    (impl/close! channel)))

(defn- timeout-worker
  []
  (let [q timeouts-queue]
    (loop []
      (let [^TimeoutQueueEntry tqe (.take q)]
        (.remove timeouts-map (.timestamp tqe) tqe)
        (impl/close! tqe))
      (recur))))

(defonce timeout-daemon
  (delay
   (doto (Thread. ^Runnable timeout-worker "clojure.core.async.timers/timeout-daemon")
     (.setDaemon true)
     (.start))))

(defn timeout
  "returns a channel that will close after msecs"
  [^long msecs]
  @timeout-daemon
  (let [timeout (+ (System/currentTimeMillis) msecs)
        me (.ceilingEntry timeouts-map timeout)]
    (or (when (and me (< (.getKey me) (+ timeout TIMEOUT_RESOLUTION_MS)))
          (.channel ^TimeoutQueueEntry (.getValue me)))
        (let [timeout-channel (channels/chan nil)
              timeout-entry (TimeoutQueueEntry. timeout-channel timeout)]
          (.put timeouts-map timeout timeout-entry)
          (.put timeouts-queue timeout-entry)
          timeout-channel))))
