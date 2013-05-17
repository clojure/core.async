(ns core.async.timers-test
  (:require [clojure.test :refer :all]
            [core.async.timers :refer :all]
            [core.async :as async]))

(deftest timeout-interval-test
  (let [start-stamp (System/currentTimeMillis)
        test-timeout (timeout 500)]
    (is (<= (+ start-stamp 500)
            (do (async/<! test-timeout)
                (System/currentTimeMillis)))
        "Reading from a timeout channel does not complete until the specified milliseconds have elapsed.")))

(deftest timeout-ordering-test
  (let [test-atom (atom [])
        timeout-channels [(timeout 800)
                          (timeout 600)
                          (timeout 700)
                          (timeout 500)]
        insert-vals [:d :b :c :a]
        threads (dorun (for [i (range 4)]
                         (doto (Thread. #(do (async/<! (timeout-channels i))
                                             (swap! test-atom conj (insert-vals i))))
                           (.start))))]
    (doseq [thread threads]
      (.join thread))
    (is (= @test-atom [:a :b :c :d])
        "Timeouts close in order determined by their delays, not in order determined by their creation.")))
