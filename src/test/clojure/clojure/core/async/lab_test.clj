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

(deftest broadcast-test
  (is (apply = (let [broadcast-receivers (repeatedly 5 #(async/chan 1))
                     broadcaster (apply broadcast broadcast-receivers)
                     broadcast-result (async/>!! broadcaster :foo)
                     expected (repeat 5 :foo)
                     observed (doall (map async/<!! broadcast-receivers))]
                 [expected observed]))
      "Broadcasting to multiple channels returns a channel which will
      write to all the target channels.")
  (is (apply = (let [broadcast-receivers (repeatedly 5 async/chan)
                     broadcaster (apply broadcast broadcast-receivers)
                     read-channels (take 4 broadcast-receivers)
                     broadcast-future (future (async/>!! broadcaster :foo)
                                              (async/>!! broadcaster :bar))
                     first-reads (doall (map async/<!! read-channels))
                     timeout-channel (async/timeout 500)
                     alt-read (async/alts!! (conj read-channels timeout-channel))
                     expected [(repeat 4 :foo) [nil timeout-channel]]
                     observed [first-reads alt-read]]
                 (async/<!! (last broadcast-receivers))
                 (doseq [channel broadcast-receivers]
                   (async/<!! channel))
                 [expected observed]))
      "Broadcasts block further writes if one of the channels cannot
      complete its write.")
  (is (apply = (let [broadcast-receivers (repeatedly 5 #(async/chan 100))
                     broadcaster (apply broadcast broadcast-receivers)
                     broadcast-future (future (dotimes [i 100]
                                                (async/>!! broadcaster i)))
                     observed (for [i (range 100)]
                                            (async/<!! (first broadcast-receivers)))
                     expected (range 100)]
                 [expected observed])) "When all channels are sufficiently buffered, reads on one channel are not throttled by reads from other channels."))

(deftest spool-test
  (testing "nil sequence"
    (let [c (spool nil)]
      (is (= nil (async/<!! c)))))
  (testing "spool empty sequence"
    (let [c (spool [])]
      (is (= nil (async/<!! c)))
      (is (= nil (async/<!! c)))))
  (testing "spool finite sequence"
    (let [c (spool [1 2])]
      (is (= 1 (async/<!! c)))
      (is (= 2 (async/<!! c)))
      (is (= nil (async/<!! c)))))
  (testing "spool infinite sequence"
    (let [c (spool (repeat 5))]
      (is (= 5 (async/<!! c)))
      (is (= 5 (async/<!! c)))))
  (testing "use existing channel"
    (let [c (async/chan 10)]
      (spool [1 2] c)
      (is (= 1 (async/<!! c)))
      (is (= 2 (async/<!! c)))
      (is (= nil (async/<!! c))))))
