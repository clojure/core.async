;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns clojure.core.async.lab
  "core.async HIGHLY EXPERIMENTAL feature exploration

  Caveats:

  1. Everything defined in this namespace is experimental, and subject
  to change or deletion without warning.

  2. Many features provided by this namespace are highly coupled to
  implementation details of core.async. Potential features which
  operate at higher levels of abstraction are suitable for inclusion
  in the examples.

  3. Features provided by this namespace MAY be promoted to
  clojure.core.async at a later point in time, but there is no
  guarantee any of them will."
  (:require [clojure.core.async :as async]
            [clojure.core.async.impl.protocols :as impl]
            [clojure.core.async.impl.mutex :as mutex]
            [clojure.core.async.impl.dispatch :as dispatch]
            [clojure.core.async.impl.channels :as channels])
  (:import [java.util HashSet Set Collection]
           [java.util.concurrent.locks Lock]))

(deftype MultiplexingReadPort
    [^Lock mutex ^Set read-ports]
  impl/ReadPort
  (take! [this handler]
    (if (empty? read-ports)
      (channels/box nil)
      (do
        (.lock mutex)
        (let [^Lock handler handler
              commit-handler (fn []
                               (.lock handler)
                               (let [take-cb (and (impl/active? handler) (impl/commit handler))]
                                 (.unlock handler)
                                 take-cb))
              fret (fn [[val alt-port]]
                     (if (nil? val)
                       (do (.lock mutex)
                           (.remove read-ports alt-port)
                           (.unlock mutex)
                           (impl/take! this handler))
                       (when-let [take-cb (commit-handler)]
                         (dispatch/run #(take-cb val)))))
              current-ports (seq read-ports)]
          (if-let [alt-res (async/do-alts fret current-ports {})]
            (let [[val alt-port] @alt-res]
              (if (nil? val)
                (do (.remove read-ports alt-port)
                    (.unlock mutex)
                    (recur handler))
                (do (.unlock mutex)
                    (when-let [take-cb (commit-handler)]
                      (dispatch/run #(take-cb val))))))
            (do
              (.unlock mutex)
                nil)))))))

(defn multiplex
  "Returns a multiplexing read port which, when read from, produces a
  value from one of ports.

  If at read time only one port is available to be read from, the
  multiplexing port will return that value. If multiple ports are
  available to be read from, the multiplexing port will return one
  value from a port chosen non-deterministicly. If no port is
  available to be read from, parks execution until a value is
  available."
  [& ports]
  (->MultiplexingReadPort (mutex/mutex) (HashSet. ^Collection ports)))

;; WIP: Unfinished work beyond this point

(defn- broadcast-write
  [fret val port-set]
  (if (empty? port-set)
    (fret)
    (let [clauses (map (fn [port] [port val]) port-set)
          recur-step (fn [[_ port]] (broadcast-write fret val (dissoc port-set port)))]
      (async/do-alts recur-step clauses {}))))

(deftype BroadcastingWritePort
    [write-ports mutex]
  impl/WritePort
  (put! [port val handler]
    (let [fret (fn [_]
                 ((impl/commit handler)))]
      (broadcast-write fret val (set write-ports)))))

(defn broadcast
  "Returns a broadcasting write port which, when written to, writes
  the value to each of ports.

  Writes to the broadcasting port will park until the value is written
  to each of the ports used to create it. For this reason, it is
  strongly advised that each of the underlying ports support buffered
  writes."
  [& ports]
  (->BroadcastingWritePort ports))

