;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns clojure.core.async.impl.dispatch
  (:require [clojure.string :as str]
            [clojure.core.async.impl.protocols :as impl]
            [clojure.core.async.impl.exec.forkjoin :as fj]))

(set! *warn-on-reflection* true)

(def java-spec-version (System/getProperty "java.specification.version", ""))

(defn version-data
  "Parse version string (1.7, 1.7.0_2, etc) into a vector of parts."
  [version]
  (str/split version #"\.|_"))

(defn class-exists?
  "Try to load class-name and return true or false if it works."
  [class-name]
  (try
    (Class/forName class-name)
    true
    (catch Throwable t
      false)))

;; Known classes representative of different ForkJoin versions
(def JSR166e "jsr166e.ForkJoinPool")
(def JSR166y "jsr166y.ForkJoinPool")
(def CLJ166y "clojure.jsr166y.ForkJoinPool")
(def JUC "java.util.concurrent.ForkJoinPool")
(def TPE "java.util.concurrent.ThreadPoolExecutor")

;; Mapping of representative classes to the executor to use
(def class-to-executor-ns
  {JSR166e 'clojure.core.async.impl.exec.jsr166e/fork-join-executor
   JSR166y 'clojure.core.async.impl.exec.jsr166y/fork-join-executor
   CLJ166y 'clojure.core.async.impl.exec.cljjsr/fork-join-executor
   JUC 'clojure.core.async.impl.exec.forkjoin/fork-join-executor
   TPE 'clojure.core.async.impl.exec.threadpool/thread-pool-executor})

;; For each JDK spec version, list preferred executor versions
(def jdk-checks
  {"1.8" [JSR166e JUC]
   "1.7" [JSR166e JSR166y CLJ166y JUC]
   "1.6" [JSR166y CLJ166y TPE]
   "else" [JUC CLJ166y TPE]})

(defn best-executor
  "Determine best executor for current environment and load it."
  [jdk class-check-fn]
  (let [ver (str/join "." (take 2 (version-data java-spec-version)))
        checks (get jdk-checks ver (jdk-checks "else"))
        class-checks (map #(when (class-check-fn %) %) checks)
        best-class (first (drop-while nil? class-checks))]
    ((resolve (class-to-executor-ns best-class)))))

(def executor (delay (best-executor (version-data java-spec-version) class-exists?)))

(defn run
  "Runs fn0 in a thread pool thread"
  [^Runnable task]
  (impl/exec @executor task))

