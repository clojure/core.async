;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns ^{:skip-wiki true}
  clojure.core.async.impl.dispatch
  (:require [clojure.core.async.impl.protocols :as impl]
            [clojure.core.async.impl.exec.threadpool :as tp]))

(set! *warn-on-reflection* true)

(def ^:private go-checking
  (delay (Boolean/getBoolean "clojure.core.async.go-checking")))

(defonce ^:private throw-on-block (ThreadLocal.))

(defonce executor
  (delay (tp/thread-pool-executor #(.set ^ThreadLocal throw-on-block @go-checking))))

(defn check-blocking-in-dispatch
  "If go-checking is enabled and the current thread is a dispatch pool thread,
  throw an exception with the blocking op"
  []
  (when (.get ^ThreadLocal throw-on-block)
    (throw (IllegalStateException. "Invalid blocking call in dispatch thread"))))

(defn run
  "Runs Runnable r in a thread pool thread"
  [^Runnable r]
  (impl/exec @executor r))
