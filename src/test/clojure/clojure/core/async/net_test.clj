(ns clojure.core.async.net-test
  (:require [clojure.test :refer :all]
            [clojure.core.async :refer :all]
            [clojure.core.async.net :refer [duct-> simulated-connection register-chan channel-transports
                                            localhost-tcp-transports]]))


(defn send-seq [c seq]
  (go (doseq [x seq]
        (>! c x))))

(defn take-seq [c count]
  (loop [acc []
         x 0]
    (if (< x count)
      (recur (conj acc (<!! c)) (inc x))
      acc)))

(deftest basic-local-net-tests
  (testing "can send and receive a single message"
    (let [[local remote] (apply simulated-connection (channel-transports))
          local-chan (chan)
          remote-chan (chan)]
      (register-chan remote "test" remote-chan)
      (duct-> local-chan local "test")
      (send-seq local-chan (range 10))
      (dotimes [x 10]
        (is (= (<!! remote-chan) x)))))

  (testing "can send and receive in reverse"
    (let [[local remote] (apply simulated-connection (channel-transports))
          local-chan (chan)
          remote-chan (chan)]
      (register-chan local "test" local-chan)
      (duct-> remote-chan remote "test")
      (send-seq remote-chan (range 10))
      (dotimes [x 10]
        (is (= (<!! local-chan) x)))))

  (testing "multiple ducts can point to the same remote endpoint"
    (let [[local remote] (apply simulated-connection (channel-transports))
          local-chan1 (chan)
          local-chan2 (chan)
          remote-chan (chan)]
      (register-chan remote "test" remote-chan)

      (duct-> local-chan1 local "test")
      (duct-> local-chan2 local "test")
      
      (send-seq local-chan1 (range 3))
      (send-seq local-chan2 (range 3))
      
      (is (= (frequencies (take-seq remote-chan 6))
             {0 2 1 2 2 2})))))


(deftest basic-localhost-tcp-tests
  (testing "can send and receive a single message"
    (let [[local remote] (apply simulated-connection (localhost-tcp-transports 10422))
          local-chan (chan)
          remote-chan (chan)]
      (register-chan remote "test" remote-chan)
      (duct-> local-chan local "test")
      (send-seq local-chan (range 10))
      (dotimes [x 10]
        (is (= (<!! remote-chan) x))))))
