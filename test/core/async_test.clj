(ns core.async-test
  (:use clojure.test
        core.async)
  (:require [core.async.channels :refer [chan]]
            [core.async.buffers :refer [fixed-buffer]]))


(defn default-chan []
  (chan (fixed-buffer 1)))

(defn drain [c]
  (close! c)
  (dorun (take-while #(not (nil? %)) 
                     (repeatedly #(<! c)))))


(deftest basic-channel-test
  (let [c (default-chan)
        f (future (<! c))]
    (>! c 42)
    (is (= @f 42))))

(def DEREF_WAIT 20)

(deftest writes-block-on-full-buffer
  (let [c (default-chan)
        _ (>! c 42)
        blocking (deref (future (>! c 43)) DEREF_WAIT :blocked)]
    (is (= blocking :blocked))
    #_(drain c)))

(deftest unfulfilled-readers-block
  (let [c (default-chan)
        r1 (future (<! c))
        r2 (future (<! c))
        _ (>! c 42)
        r1v (deref r1 DEREF_WAIT :blocked)
        r2v (deref r2 DEREF_WAIT :blocked)]
    (is (and (or (= r1v :blocked) (= r2v :blocked))
             (or (= 42 r1v) (= 42 r2v))))))

(deftest test-<!-and-put!
  (let [executed (atom false)
        test-channel (chan nil)]
    (put! test-channel :test-val #(swap! executed not))
    (is (not @executed) "The provided callback does not execute until
    a reader can consume the written value.")
    (is (= :test-val (<! test-channel))
        "The written value is provided over the test channel when a reader arrives.")
    (is @executed "The provided callback executes once the reader has arrived.")))

(deftest test->!-and-take!
  (let [read-promise (promise)
        test-channel (chan nil)]
    (take! test-channel #(do (is (= % :test-val "The read value is
    provided to the callback function when a writer arrives."))
                             (deliver read-promise %)))
    (is (not (realized? read-promise)) "The provided callback does not execute until
    a writer can provide a value to read.")
    (>! test-channel :test-val)
    (is (realized? read-promise) "The provided callback executes once a writer provides a value.")
    #_(is (= :test-val @read-promise)
        "The provided callback executes once the writer has arrived.")))
