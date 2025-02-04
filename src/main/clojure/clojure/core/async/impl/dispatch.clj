;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns ^{:skip-wiki true}
  clojure.core.async.impl.dispatch
  (:require [clojure.core.async.impl.protocols :as impl])
  (:import [java.util.concurrent Executors ExecutorService ThreadFactory]))

(set! *warn-on-reflection* true)

(defn counted-thread-factory
  "Create a ThreadFactory that maintains a counter for naming Threads.
     name-format specifies thread names - use %d to include counter
     daemon is a flag for whether threads are daemons or not
     opts is an options map:
       init-fn - function to run when thread is created"
  ([name-format daemon]
    (counted-thread-factory name-format daemon nil))
  ([name-format daemon {:keys [init-fn] :as opts}]
   (let [counter (atom 0)]
     (reify
       ThreadFactory
       (newThread [_this runnable]
         (let [body (if init-fn
                      (fn [] (init-fn) (.run ^Runnable runnable))
                      runnable)
               t (Thread. ^Runnable body)]
           (doto t
             (.setName (format name-format (swap! counter inc)))
             (.setDaemon daemon))))))))

(defonce
  ^{:doc "Number of processors reported by the JVM"}
  processors (.availableProcessors (Runtime/getRuntime)))

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
                        (counted-thread-factory "async-dispatch-%d" true
                          {:init-fn init-fn}))]
     (reify impl/Executor
       (impl/exec [_ r]
         (.execute executor-svc ^Runnable r))))))

(defonce ^:private in-dispatch (ThreadLocal.))

(defonce executor
  (delay (thread-pool-executor #(.set ^ThreadLocal in-dispatch true))))

(defn in-dispatch-thread?
  "Returns true if the current thread is a go block dispatch pool thread"
  []
  (boolean (.get ^ThreadLocal in-dispatch)))

(defn check-blocking-in-dispatch
  "If the current thread is a dispatch pool thread, throw an exception"
  []
  (when (.get ^ThreadLocal in-dispatch)
    (throw (IllegalStateException. "Invalid blocking call in dispatch thread"))))

(defn ex-handler
  "conveys given Exception to current thread's default uncaught handler. returns nil"
  [ex]
  (-> (Thread/currentThread)
      .getUncaughtExceptionHandler
      (.uncaughtException (Thread/currentThread) ex))
  nil)

(defn- sys-prop-call
  [prop otherwise]
  (let [esf (System/getProperty prop)
        esfn (or (and esf (requiring-resolve (symbol esf))) otherwise)]
    (esfn)))

(defn construct-es
  [workload]
  (case workload
    :compute (sys-prop-call "clojure.core.async.compute-es-fn"
                            #(Executors/newCachedThreadPool (counted-thread-factory "async-compute-%d" true)))
    :io      (sys-prop-call "clojure.core.async.io-es-fn"
                            #(Executors/newCachedThreadPool (counted-thread-factory "async-io-%d" true)))
    :mixed   (sys-prop-call "clojure.core.async.mixed-es-fn"
                            #(Executors/newCachedThreadPool (counted-thread-factory "async-mixed-%d" true)))
    (throw (IllegalArgumentException. (str "Illegal workload tag " workload)))))

(defonce ^ExecutorService mixed-executor (construct-es :mixed))

(defonce ^ExecutorService io-executor (construct-es :io))

(defonce ^ExecutorService compute-executor (construct-es :compute))

(defn es-for [workload]
  (case workload
    :compute compute-executor
    :io io-executor
    :mixed mixed-executor
    nil))

(defn exec
  [^Runnable r workload]
  (if-let [^ExecutorService e (es-for workload)]
    (.execute e r)
    (impl/exec @executor r)))

(defn run
  "Runs Runnable r on current thread when :on-caller? meta true, else in a thread pool thread."
  ([^Runnable r]
   (if (-> r meta :on-caller?)
     (try (.run r) (catch Throwable t (ex-handler t)))
     (exec r nil)))
  ([^Runnable r workload]
   (exec r workload)))

