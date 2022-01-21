(ns clojure.core.async.lab-test
  (:require
    [clojure.test :refer [deftest is]]
    [clojure.core.async.lab :refer [broadcast multiplex]]
    [clojure.core.async :as async]))

(deftest multiplex-test
  (is (apply = (let [even-chan (async/chan)
                     odd-chan (async/chan)
                     muxer (multiplex even-chan odd-chan)
                     odds (filter odd? (range 10))
                     evens (filter even? (range 10))
                     odd-fn #(doseq [odd odds]
                               (async/>!! odd-chan odd))
                     _odd-pusher (doto (Thread. ^Runnable odd-fn) (.start))
                     even-fn #(doseq [even evens]
                                (async/>!! even-chan even))
                     _even-pusher (doto (Thread. ^Runnable even-fn) (.start))
                     expected (set (range 10))
                     observed (set (for [_ (range 10)] (async/<!! muxer)))]
                 [expected observed]))
      "Multiplexing multiple channels returns a channel which returns
      the values written to each.")
  (is (let [short-chan (async/chan)
            long-chan (async/chan)
            muxer (multiplex short-chan long-chan)
            long-fn #(do (dotimes [i 10000]
                           (async/>!! long-chan i))
                         (async/close! short-chan))
            _long-pusher (doto (Thread. ^Runnable long-fn) (.start))
            short-fn #(do (dotimes [i 10]
                            (async/>!! short-chan i))
                          (async/close! short-chan))
            _short-pusher (doto (Thread. ^Runnable short-fn) (.start))
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
                     _ (async/>!! broadcaster :foo)
                     expected (repeat 5 :foo)
                     observed (doall (map async/<!! broadcast-receivers))]
                 [expected observed]))
      "Broadcasting to multiple channels returns a channel which will
      write to all the target channels.")
  (is (apply = (let [broadcast-receivers (repeatedly 5 async/chan)
                     broadcaster (apply broadcast broadcast-receivers)
                     read-channels (take 4 broadcast-receivers)
                     _ (future (async/>!! broadcaster :foo)
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
                     _ (future (dotimes [i 100]
                         (async/>!! broadcaster i)))
                     observed (for [_ (range 100)]
                                (async/<!! (first broadcast-receivers)))
                     expected (range 100)]
                 [expected observed])) "When all channels are sufficiently buffered, reads on one channel are not throttled by reads from other channels."))
