;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns clojure.core.async.impl.exec.threadpool
  (:require [clojure.core.async.impl.protocols :as impl]
            [clojure.core.async.impl.concurrent :as conc])
  (:import [java.util.concurrent Executors Executor]))

(set! *warn-on-reflection* true)

(defonce the-executor
  (Executors/newCachedThreadPool
   (conc/counted-thread-factory "async-dispatch-%d" true)))

(defn thread-pool-executor
  ([] (thread-pool-executor the-executor))
  ([^Executor executor-svc]
     (reify impl/Executor
       (impl/exec [this r]
         (.execute executor-svc ^Runnable r)))))


