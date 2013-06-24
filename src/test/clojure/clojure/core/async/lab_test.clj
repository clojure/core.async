(ns clojure.core.async.lab-test
  (:use clojure.test
        clojure.core.async.lab)
  (:require [clojure.core.async :as async]))

(deftest multiplex-test
  (is (apply = (let [even-chan (async/chan)
                     odd-chan (async/chan)
                     muxer (multiplex even-chan odd-chan)
                     odds (filter odd? (range 10))
                     evens (filter even? (range 10))
                     odd-pusher (doto (Thread. #(doseq [odd odds]
                                                  (async/>!! odd-chan odd)))
                                  (.start))
                     even-pusher (doto (Thread. #(doseq [even evens]
                                                   (async/>!! even-chan even)))
                                   (.start))
                     expected (set (range 10))
                     observed (set (for [_ (range 10)] (async/<!! muxer)))]
                 [expected observed]))
      "Multiplexing multiple channels returns a channel which returns
      the values written to each.")
  (is (let [short-chan (async/chan)
            long-chan (async/chan)
            muxer (multiplex short-chan long-chan)
            semaphore (promise)
            long-pusher (doto (Thread. #(do (dotimes [i 10000]
                                              (async/>!! long-chan i))
                                            (async/close! short-chan)))
                          (.start))
            short-pusher (doto (Thread. #(do (dotimes [i 10]
                                               (async/>!! short-chan i))
                                             (async/close! short-chan)))
                           (.start))
            observed (for [_ (range 10010)] (async/<!! muxer))]
        (every? identity observed))
      "A closed channel will deliver nil, but the multiplexed channel
      will never deliver nil until all channels are closed.")
  (is (apply = (let [chans (take 5 (repeatedly #(async/chan)))
                     muxer (apply multiplex chans)]
                 (doseq [chan chans]
                   (async/close! chan))
                 [nil (async/<!! muxer)]))
      "When all of a multiplexer's channels are closed, it behaves
      like a closed channel on read."))
