(ns clojure.core.async-test
  (:require [clojure.core.async :refer :all :as a]
            [clojure.test :refer :all]))


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

(deftest limit-async-take!-put!
  (testing "async put! limit"
    (let [c (chan)]
      (dotimes [x 1024]
        (put! c x))
      (is (thrown? AssertionError
                   (put! c 42)))
      (is (= (<!! c) 0)))) ;; make sure the channel unlocks
  (testing "async take! limit"
    (let [c (chan)]
      (dotimes [x 1024]
        (take! c (fn [x])))
      (is (thrown? AssertionError
                   (take! c (fn [x]))))
      (is (nil? (>!! c 42)))))) ;; make sure the channel unlocks

(deftest puts-fulfill-when-buffer-available
  (is (= :proceeded
         (let [c (chan 1)
               p (promise)]
           (>!! c :full)  ;; fill up the channel
           (put! c :enqueues #(deliver p :proceeded))  ;; enqueue a put
           (<!! c)        ;; make room in the buffer
           (deref p 250 :timeout)))))



(deftest ops-tests
  (testing map<
    (is (= [2 3 4 5]
           (<!! (a/into [] (a/map< inc (a/to-chan [1 2 3 4])))))))
  (testing map>
    (is (= [2 3 4 5]
           (let [out (chan)
                 in (a/map> inc out)]
             (onto-chan in [1 2 3 4])
             (<!! (a/into [] out))))))
  (testing filter<
    (is (= [2 4 6]
           (<!! (a/into [] (a/filter< even? (a/to-chan [1 2 3 4 5 6])))))))
  (testing remove<
    (is (= [1 3 5]
           (<!! (a/into [] (a/remove< even? (a/to-chan [1 2 3 4 5 6])))))))
  (testing filter>
    (is (= [2 4 6]
           (let [out (chan)
                 in (filter> even? out)]
             (onto-chan in [1 2 3 4 5 6])
             (<!! (a/into [] out))))))
  (testing remove>
    (is (= [1 3 5]
           (let [out (chan)
                 in (remove> even? out)]
             (onto-chan in [1 2 3 4 5 6])
             (<!! (a/into [] out))))))
  (testing mapcat<
    (is (= [0 0 1 0 1 2]
           (<!! (a/into [] (mapcat< range
                                    (a/to-chan [1 2 3])))))))
  (testing mapcat>
    (is (= [0 0 1 0 1 2]
           (let [out (chan)
                 in (mapcat> range out)]
             (onto-chan in [1 2 3])
             (<!! (a/into [] out))))))
  (testing pipe
    (is (= [1 2 3 4 5]
           (let [out (chan)]
             (pipe (a/to-chan [1 2 3 4 5])
                   out)
             (<!! (a/into [] out))))))
  (testing split
    ;; Must provide buffers for channels else the tests won't complete
    (let [[even odd] (a/split even? (a/to-chan [1 2 3 4 5 6]) 5 5)]
      (is (= [2 4 6]
             (<!! (a/into [] even))))
      (is (= [1 3 5]
             (<!! (a/into [] odd))))))
  (testing map
    (is (= [0 4 8 12]
           (<!! (a/into [] (a/map + [(a/to-chan (range 4))
                                     (a/to-chan (range 4))
                                     (a/to-chan (range 4))
                                     (a/to-chan (range 4))]))))))
  (testing merge
    ;; merge uses alt, so results can be in any order, we're using
    ;; frequencies as a way to make sure we get the right result.
    (is (= {0 4
            1 4
            2 4
            3 4}
           (frequencies (<!! (a/into [] (a/merge [(a/to-chan (range 4))
                                                  (a/to-chan (range 4))
                                                  (a/to-chan (range 4))
                                                  (a/to-chan (range 4))]))))))))
