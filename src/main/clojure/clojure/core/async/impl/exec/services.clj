;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns clojure.core.async.impl.exec.services
  (:require [clojure.core.async.impl.concurrent :as conc])
  (:import [java.util.concurrent Executors ExecutorService]
           [clojure.lang Var]))

(set! *warn-on-reflection* true)

(defonce ^ExecutorService mixed-executor
  (Executors/newCachedThreadPool (conc/counted-thread-factory "async-mixed-%d" true)))

(defonce ^ExecutorService io-executor
  (Executors/newCachedThreadPool (conc/counted-thread-factory "async-io-%d" true)))

(defonce ^ExecutorService compute-executor
  (Executors/newCachedThreadPool (conc/counted-thread-factory "async-compute-%d" true)))

(defn best-fit-thread-call
  [f exec]
  (let [c (clojure.core.async/chan 1)
        ^ExecutorService e (case exec
                             :compute compute-executor
                             :io io-executor
                             mixed-executor)]
    (let [binds (Var/getThreadBindingFrame)]
      (.execute e
                (fn []
                  (Var/resetThreadBindingFrame binds)
                  (try
                    (let [ret (f)]
                      (when-not (nil? ret)
                        (clojure.core.async/>!! c ret)))
                    (finally
                      (clojure.core.async/close! c))))))
    c))
