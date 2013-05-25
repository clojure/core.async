(ns core.async-test
  (:use clojure.test
        core.async))


(defn default-chan []
  (chan 1))

(defn drain [c]
  (close! c)
  (dorun (take-while #(not (nil? %)) 
                     (repeatedly #(<!! c)))))


(deftest basic-channel-test
  (let [c (default-chan)
        f (future (<!! c))]
    (>!! c 42)
    (is (= @f 42))))

(def DEREF_WAIT 20)

(deftest writes-block-on-full-buffer
  (let [c (default-chan)
        _ (>!! c 42)
        blocking (deref (future (>!! c 43)) DEREF_WAIT :blocked)]
    (is (= blocking :blocked))
    #_(drain c)))

(deftest unfulfilled-readers-block
  (let [c (default-chan)
        r1 (future (<!! c))
        r2 (future (<!! c))
        _ (>!! c 42)
        r1v (deref r1 DEREF_WAIT :blocked)
        r2v (deref r2 DEREF_WAIT :blocked)]
    (is (and (or (= r1v :blocked) (= r2v :blocked))
             (or (= 42 r1v) (= 42 r2v))))))

(deftest test-<!!-and-put!
  (let [executed (promise)
        test-channel (chan nil)]
    (put! test-channel :test-val #(deliver executed true))
    (is (not (realized? executed)) "The provided callback does not execute until
    a reader can consume the written value.")
    (is (= :test-val (<!! test-channel))
        "The written value is provided over the channel when a reader arrives.")
    (is @executed "The provided callback executes once the reader has arrived.")))

(deftest test->!!-and-take!
  (is (= :test-val (let [read-promise (promise)
                         test-channel (chan nil)]
                     (take! test-channel #(deliver read-promise %))
                     (is (not (realized? read-promise))
                         "The read waits until a writer provides a value.")
                     (>!! test-channel :test-val)
                     (is (realized? read-promise)
                         "The read callback executes when a writer provides a value.")
                     (deref read-promise 1000 false)))
      "The written value is the value provided to the read callback."))

(deftest take!-on-caller?
  (is (apply not= (let [starting-thread (Thread/currentThread)
                      test-channel (chan nil)
                      read-promise (promise)]
                  (take! test-channel (fn [_] (deliver read-promise (Thread/currentThread))) true)
                  (>!! test-channel :foo)
                  [starting-thread @read-promise]))
      "When on-caller? requested, but no value is immediately
      available, take!'s callback executes on another thread.")
  (is (apply = (let [starting-thread (Thread/currentThread)
                      test-channel (chan nil)
                      read-promise (promise)]
                  (put! test-channel :foo (constantly nil))
                  (take! test-channel (fn [_] (deliver read-promise (Thread/currentThread))) true)
                  [starting-thread @read-promise]))
      "When on-caller? requested, and a value is ready to read,
      take!'s callback executes on the same thread.")
  (is (apply not= (let [starting-thread (Thread/currentThread)
                      test-channel (chan nil)
                      read-promise (promise)]
                  (put! test-channel :foo (constantly nil))
                  (take! test-channel (fn [_] (deliver read-promise (Thread/currentThread))) false)
                  [starting-thread @read-promise]))
      "When on-caller? is false, and a value is ready to read,
      take!'s callback executes on a different thread."))

(deftest put!-on-caller?
  (is (apply = (let [starting-thread (Thread/currentThread)
                     test-channel (chan nil)
                     write-promise (promise)]
                 (take! test-channel (fn [_] nil))
                 (put! test-channel :foo #(deliver write-promise (Thread/currentThread)) true)
                 [starting-thread @write-promise]))
      "When on-caller? requested, and a reader can consume the value,
      put!'s callback executes on the same thread.")
  (is (apply not= (let [starting-thread (Thread/currentThread)
                        test-channel (chan nil)
                        write-promise (promise)]
                    (take! test-channel (fn [_] nil))
                    (put! test-channel :foo #(deliver write-promise (Thread/currentThread)) false)
                    [starting-thread @write-promise]))
      "When on-caller? is false, but a reader can consume the value,
      put!'s callback executes on a different thread.")
  (is (apply not= (let [starting-thread (Thread/currentThread)
                        test-channel (chan nil)
                        write-promise (promise)]
                    (put! test-channel :foo #(deliver write-promise (Thread/currentThread)) true)
                    (take! test-channel (fn [_] nil))
                    [starting-thread @write-promise]))
      "When on-caller? requested, but no reader can consume the value,
      put!'s callback executes on a different thread."))
