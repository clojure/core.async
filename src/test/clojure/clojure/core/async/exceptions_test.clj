;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns clojure.core.async.exceptions-test
  "Verify that exceptions thrown on a thread pool managed by
  core.async will propagate out to the JVM's default uncaught
  exception handler."
  (:require [clojure.test :refer [deftest is]]
            [clojure.stacktrace :refer [root-cause]]
            [clojure.core.async :refer [chan go thread put! take! <!! >!!]]))

(defn with-default-uncaught-exception-handler [handler f]
  (let [old-handler (Thread/getDefaultUncaughtExceptionHandler)]
    (Thread/setDefaultUncaughtExceptionHandler
     (reify Thread$UncaughtExceptionHandler
       (uncaughtException [_ thread throwable]
         (handler thread throwable))))
    (f)
    (Thread/setDefaultUncaughtExceptionHandler old-handler)))

(deftest exception-in-go
  (let [log (promise)]
    (with-default-uncaught-exception-handler
      (fn [_ throwable] (deliver log throwable))
      #(let [ex (Exception. "This exception is expected")
             ret (go (throw ex))]
         (<!! ret)
         (is (identical? ex (root-cause @log)))))))

(deftest exception-in-thread
  (let [log (promise)]
    (with-default-uncaught-exception-handler
      (fn [_ throwable] (deliver log throwable))
      #(let [ex (Exception. "This exception is expected")
             ret (thread (throw ex))]
         (<!! ret)
         (is (identical? ex (root-cause @log)))))))

(deftest exception-in-put-callback
  (let [log (promise)]
    (with-default-uncaught-exception-handler
      (fn [_ throwable] (deliver log throwable))
      #(let [ex (Exception. "This exception is expected")
             c (chan)]
         (put! c :foo (fn [_] (throw ex)))
         (<!! c)
         (is (identical? ex (root-cause @log)))))))

(deftest exception-in-take-callback
  (let [log (promise)]
    (with-default-uncaught-exception-handler
      (fn [_ throwable] (deliver log throwable))
      #(let [ex (Exception. "This exception is expected")
             c (chan)]
         (take! c (fn [_] (throw ex)))
         (>!! c :foo)
         (is (identical? ex (root-cause @log)))))))
