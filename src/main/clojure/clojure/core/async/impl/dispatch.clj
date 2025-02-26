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

(defonce ^:private in-dispatch (ThreadLocal.))

(defonce executor nil)

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

(defn- make-ctp-named
  [workflow]
  (Executors/newCachedThreadPool (counted-thread-factory (str "async-" (name workflow) "-%d") true)))

(def virtual-threads-available?
  (try
    (Class/forName "java.lang.Thread$Builder$OfVirtual")
    true
    (catch ClassNotFoundException _
      false)))

(defn- make-io-executor
  []
  (if virtual-threads-available?
    (-> (Thread/ofVirtual)
        (Thread$Builder/.name "async-vthread-io-" 0)
        .factory
        Executors/newThreadPerTaskExecutor)
    (make-ctp-named :io)))

(defn ^:private create-default-executor
  [workload]
  (case workload
    :compute (make-ctp-named :compute)
    :io      (make-io-executor)
    :mixed   (make-ctp-named :mixed)))

(def executor-for
  "Given a workload tag, returns an ExecutorService instance and memoizes the result. By
  default, core.async will defer to a user factory (if provided via sys prop) or construct
  a specialized ExecutorService instance for each tag :io, :compute, and :mixed. When
  given the tag :core-async-dispatch it will default to the executor service for :io."
  (memoize
   (fn ^ExecutorService [workload]
     (let [sysprop-factory (when-let [esf (System/getProperty "clojure.core.async.executor-factory")]
                             (requiring-resolve (symbol esf)))
           sp-exec (and sysprop-factory (sysprop-factory workload))]
       (or sp-exec
           (if (= workload :core-async-dispatch)
             (executor-for :io)
             (create-default-executor workload)))))))

(defn exec
  [^Runnable r workload]
  (let [^ExecutorService e (executor-for workload)]
    (.execute e r)))

(defn run
  "Runs Runnable r on current thread when :on-caller? meta true, else in a thread pool thread."
  [^Runnable r]
  (if (-> r meta :on-caller?)
    (try (.run r) (catch Throwable t (ex-handler t)))
    (exec r :core-async-dispatch)))
