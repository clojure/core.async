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
  (:import [java.util.concurrent DelayQueue Delayed TimeUnit]
           [clojure.lang IDeref]))

(set! *warn-on-reflection* true)

(defonce ^:private timeouts-queue
  (DelayQueue.))

(deftype TimeoutQueueEntry [channel timestamp]
  Delayed
  (getDelay [this time-unit]
    (.convert time-unit
              (- timestamp (System/currentTimeMillis))
              TimeUnit/MILLISECONDS))
  (compareTo [this other]
    (.compareTo ^Long (.getDelay this TimeUnit/MILLISECONDS)
                ^Long (.getDelay ^Delayed other TimeUnit/MILLISECONDS)))
  IDeref
  (deref [this]
    channel))

(defn timeout
  "returns a channel that will close after msecs"
  [msecs]
  (let [timeout (+ (System/currentTimeMillis) msecs)
        timeout-channel (channels/chan nil)
        timeout-delay (TimeoutQueueEntry. timeout-channel timeout)]
    (locking timeouts-queue
      (.put ^DelayQueue timeouts-queue timeout-delay))
    timeout-channel))

(defn timeout-channel
  []
  (let [timeout-delay (.take ^DelayQueue timeouts-queue)]
    (proto/close! @timeout-delay))
  (recur))

(defonce timeout-daemon
  (doto (Thread. ^Runnable timeout-channel "core.async.timers/timeout-daemon")
    (.setDaemon true)
    (.start)))
