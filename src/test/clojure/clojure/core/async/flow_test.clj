;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.
(ns clojure.core.async.flow-test
  (:require [clojure.test :refer :all]
            [clojure.core.async.flow :as flow])
  (:import [java.util.concurrent TimeUnit Future ExecutionException]))

(set! *warn-on-reflection* true)

(deftest test-futurize
  (testing "Happy path use of futurize"
    (let [in-es? (atom false)
          es (reify java.util.concurrent.Executor
               (^void execute [_ ^Runnable r]
                (reset! in-es? true)
                (future (.run r))))]
      (is (= 16 @((flow/futurize #(* % %) {:exec :mixed}) 4)))
      (is (= 16 @((flow/futurize #(* % %)) 4)))
      (is (= 16 @((flow/futurize #(* % %) {:exec es}) 4)))
      (is @in-es?)))
  (testing "ASYNC-275 regression: futurize Future instance propagates exceptions"
    (let [cause (ex-info "boom" {})
          fut   ((flow/futurize (fn [] (throw cause)) {:exec :mixed}))
          ex    (try
                  (.get ^Future fut 1000 TimeUnit/MILLISECONDS)
                  (catch ExecutionException e e))]
      (is (= cause (.getCause ^ExecutionException ex))))))

(defn minimal-step
  ([init-state] (minimal-step init-state {}))
  ([init-state desc]
   (fn
     ([] desc)
     ([_args] init-state)
     ([state _trans] state)
     ([state _in _msg] [state nil]))))

(defn ping-state-with [step-fn]
  (let [g (flow/create-flow {:procs {:p {:proc (flow/process step-fn)}}})]
    (try
      (flow/start g)
      (::flow/state (flow/ping-proc g :p))
      (finally
        (flow/stop g)))))

(deftest test-ping-state-not-datafied
  (testing "ping-proc returns ::flow/state with nondata values unchanged"
    (let [state-val {:a-var #'identity :a-fn inc}
          ping-state (ping-state-with (minimal-step state-val))]
      (is (= state-val ping-state) "state should be returned unchanged"))))

(deftest test-ping-map-fn
  (testing ":ping-map-fn can redact sensitive keys from state"
    (let [ping-state (ping-state-with (minimal-step {:visible 42 :hidden "secret"}
                                                    {:ping-map-fn #(select-keys % [:visible])}))]
      (is (= {:visible 42} ping-state) "secret was not removed"))))

