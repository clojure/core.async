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
  (:import [java.util.concurrent Executors]))

(set! *warn-on-reflection* true)

(def ^:private pool-size
  "Value is set via clojure.core.async.pool-size system property; defaults to 8; uses a
   delay so property can be set from code after core.async namespace is loaded but before
   any use of the async thread pool."
  (delay (or (Long/getLong "clojure.core.async.pool-size") 8)))

(defn thread-pool-executor
  ([]
    (thread-pool-executor nil))
  ([init-fn]
   (let [executor-svc (Executors/newFixedThreadPool
                        @pool-size
                        (conc/counted-thread-factory "async-dispatch-%d" true
                          {:init-fn init-fn}))]
     (reify impl/Executor
       (impl/exec [_ r]
         (.execute executor-svc ^Runnable r))))))
