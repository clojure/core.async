(ns clojure.core.async.timers-test
  (:require [clojure.test :refer :all]
            [clojure.core.async.impl.timers :refer :all]
            [clojure.core.async :as async]))

(deftest timeout-interval-test
  (let [start-stamp (System/currentTimeMillis)
        test-timeout (timeout 500)]
    (is (<= (+ start-stamp 500)
            (do (async/<!! test-timeout)
                (System/currentTimeMillis)))
        "Reading from a timeout channel does not complete until the specified milliseconds have elapsed.")))

(deftest timeout-ordering-test
  (let [test-atom (atom [])
        timeout-channels [(timeout 800)
                          (timeout 600)
                          (timeout 700)
                          (timeout 500)]
        threads (doall (for [i (range 4)]
                         (let [f #(do (async/<!! (timeout-channels i))
                                      (swap! test-atom conj i))]
                           (doto (Thread. ^Runnable f) (.start)))))]
    (doseq [thread threads]
      (.join ^Thread thread))
    (is (= @test-atom [3 1 2 0])
        "Timeouts close in order determined by their delays, not in order determined by their creation.")))
