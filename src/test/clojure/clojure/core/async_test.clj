(ns clojure.core.async-test
  (:refer-clojure :exclude [map into reduce transduce merge take partition partition-by])
  (:require [clojure.core.async.impl.buffers :as b]
            [clojure.core.async :refer :all :as a]
            [clojure.test :refer :all]))


(defn default-chan []
  (chan 1))

(deftest buffers-tests
  (is (not (unblocking-buffer? (buffer 1))))
  (is (unblocking-buffer? (dropping-buffer 1)))
  (is (unblocking-buffer? (sliding-buffer 1)))
  (is (unblocking-buffer? (b/promise-buffer))))

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
    (is (= blocking :blocked))))

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
    (put! test-channel :test-val (fn [_] (deliver executed true)))
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
                 (put! test-channel :foo (fn [_] (deliver write-promise (Thread/currentThread))) true)
                 [starting-thread @write-promise]))
      "When on-caller? requested, and a reader can consume the value,
      put!'s callback executes on the same thread.")
  (is (apply not= (let [starting-thread (Thread/currentThread)
                        test-channel (chan nil)
                        write-promise (promise)]
                    (take! test-channel (fn [_] nil))
                    (put! test-channel :foo (fn [_] (deliver write-promise (Thread/currentThread))) false)
                    [starting-thread @write-promise]))
      "When on-caller? is false, but a reader can consume the value,
      put!'s callback executes on a different thread.")
  (is (apply not= (let [starting-thread (Thread/currentThread)
                        test-channel (chan nil)
                        write-promise (promise)]
                    (put! test-channel :foo (fn [_] (deliver write-promise (Thread/currentThread))) true)
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
      (is (true? (>!! c 42)))))) ;; make sure the channel unlocks

(deftest puts-fulfill-when-buffer-available
  (is (= :proceeded
         (let [c (chan 1)
               p (promise)]
           (>!! c :full)  ;; fill up the channel
           (put! c :enqueues (fn [_] (deliver p :proceeded)))  ;; enqueue a put
           (<!! c)        ;; make room in the buffer
           (deref p 250 :timeout)))))

(deftest offer-poll
  (let [c (chan 2)]
    (is (true? (offer! c 1)))
    (is (true? (offer! c 2)))
    (is (nil? (offer! c 3)))
    (is (= 1 (<!! c)))
    (is (= 2 (poll! c)))
    (is (nil? (poll! c))))
  (let [c (chan)]
    (is (nil? (offer! c 1)))
    (is (nil? (poll! c)))))

(deftest test-promise-chan
  (testing "put on promise-chan fulfills all pending takers"
    (let [c (promise-chan)
          t1 (thread (<!! c))
          t2 (thread (<!! c))]
      (>!! c :val)
      (is (= :val (<!! t1) (<!! t2)))
      (testing "then puts succeed but are dropped"
        (>!! c :LOST)
        (is (= :val (<!! c))))
      (testing "then takes succeed with the original value"
        (is (= :val (<!! c) (<!! c) (<!! c))))
      (testing "then after close takes continue returning value"
        (close! c)
        (is (= :val (<!! c) (<!! c))))))
  (testing "close on promise-chan fulfills all pending takers"
    (let [c (promise-chan)
          t1 (thread (<!! c))
          t2 (thread (<!! c))]
      (close! c)
      (is (= nil (<!! t1) (<!! t2)))
      (testing "then takes return nil"
        (is (= nil (<!! t1) (<!! t1) (<!! t2) (<!! t2))))))
  (testing "close after put on promise-chan continues delivering promised value"
    (let [c (promise-chan)]
      (>!! c :val) ;; deliver
      (is (= :val (<!! c) (<!! c)))
      (close! c)
      (is (= :val (<!! c) (<!! c))))))

(def ^:dynamic test-dyn false)

(deftest thread-tests
  (testing "bindings"
    (binding [test-dyn true]
      (is (<!! (thread test-dyn))))))


(deftest ops-tests
  (testing "map<"
    (is (= [2 3 4 5]
           (<!! (a/into [] (a/map< inc (a/to-chan! [1 2 3 4])))))))
  (testing "map>"
    (is (= [2 3 4 5]
           (let [out (chan)
                 in (a/map> inc out)]
             (a/onto-chan! in [1 2 3 4])
             (<!! (a/into [] out))))))
  (testing "filter<"
    (is (= [2 4 6]
           (<!! (a/into [] (a/filter< even? (a/to-chan! [1 2 3 4 5 6])))))))
  (testing "remove<"
    (is (= [1 3 5]
           (<!! (a/into [] (a/remove< even? (a/to-chan! [1 2 3 4 5 6])))))))

  (testing "to-chan"
    (is (= (range 10)
           (<!! (a/into [] (a/to-chan (range 10))))))
    (is (= (range 10)
          (<!! (a/into [] (a/to-chan! (range 10))))))
    (is (= (range 10)
          (<!! (a/into [] (a/to-chan!! (range 10)))))))

  (testing "onto-chan"
    (let [ch (chan 10)]
      (a/onto-chan ch (range 10))
      (is (= (range 10) (<!! (a/into [] ch)))))
    (let [ch (chan 10)]
      (a/onto-chan! ch (range 10))
      (is (= (range 10) (<!! (a/into [] ch)))))
    (let [ch (chan 10)]
      (a/onto-chan!! ch (range 10))
      (is (= (range 10) (<!! (a/into [] ch))))))

  (testing "filter>"
    (is (= [2 4 6]
           (let [out (chan)
                 in (filter> even? out)]
             (a/onto-chan! in [1 2 3 4 5 6])
             (<!! (a/into [] out))))))
  (testing "remove>"
    (is (= [1 3 5]
           (let [out (chan)
                 in (remove> even? out)]
             (a/onto-chan! in [1 2 3 4 5 6])
             (<!! (a/into [] out))))))
  (testing "mapcat<"
    (is (= [0 0 1 0 1 2]
           (<!! (a/into [] (mapcat< range
                                    (a/to-chan! [1 2 3])))))))
  (testing "mapcat>"
    (is (= [0 0 1 0 1 2]
           (let [out (chan)
                 in (mapcat> range out)]
             (a/onto-chan! in [1 2 3])
             (<!! (a/into [] out))))))


  (testing "pipe"
    (is (= [1 2 3 4 5]
           (let [out (chan)]
             (pipe (a/to-chan! [1 2 3 4 5])
                   out)
             (<!! (a/into [] out))))))
  (testing "split"
    ;; Must provide buffers for channels else the tests won't complete
    (let [[even odd] (a/split even? (a/to-chan! [1 2 3 4 5 6]) 5 5)]
      (is (= [2 4 6]
             (<!! (a/into [] even))))
      (is (= [1 3 5]
             (<!! (a/into [] odd))))))
  (testing "map"
    (is (= [0 4 8 12]
           (<!! (a/into [] (a/map + [(a/to-chan! (range 4))
                                     (a/to-chan! (range 4))
                                     (a/to-chan! (range 4))
                                     (a/to-chan! (range 4))])))))
    (is (nil? (<!! (a/map + [])))))
  (testing "merge"
    ;; merge uses alt, so results can be in any order, we're using
    ;; frequencies as a way to make sure we get the right result.
    (is (= {0 4
            1 4
            2 4
            3 4}
           (frequencies (<!! (a/into [] (a/merge [(a/to-chan! (range 4))
                                                  (a/to-chan! (range 4))
                                                  (a/to-chan! (range 4))
                                                  (a/to-chan! (range 4))])))))))

  (testing "mult"
    (let [a (chan 4)
          b (chan 4)
          src (chan)
          m (mult src)]
      (tap m a)
      (tap m b)
      (pipe (a/to-chan! (range 4)) src)
      (is (= [0 1 2 3]
             (<!! (a/into [] a))))
      (is (= [0 1 2 3]
             (<!! (a/into [] b)))))

    ;; ASYNC-127
    (let [ch (to-chan! [1 2 3])
          m (mult ch)
          t-1 (chan)
          t-2 (chan)
          t-3 (chan)]
      (tap m t-1)
      (tap m t-2)
      (tap m t-3)
      (close! t-3)
      (is (= 1 (<!! t-1)))
      (is (= nil (a/poll! t-1))) ;; t-2 hasn't taken yet
      (is (= 1 (<!! t-2)))
      (is (= 2 (<!! t-1))) ;; now available
      (is (= nil (a/poll! t-1)))))

  (testing "mix"
    (let [out (chan)
          mx (mix out)]
      (admix mx (a/to-chan! [1 2 3]))
      (admix mx (a/to-chan! [4 5 6]))

      (is (= #{1 2 3 4 5 6}
             (<!! (a/into #{} (a/take 6 out))))))

    ;; ASYNC-145
    (let [out (chan 2500)
          mix (mix out)]
      (dotimes [i 2048]
        (let [c (chan)]
          (admix mix c)
          (put! c i)))

      (is (= (set (range 2048))
            (<!! (a/into #{} (a/take 2048 out)))))))

  (testing "pub-sub"
    (let [a-ints (chan 5)
          a-strs (chan 5)
          b-ints (chan 5)
          b-strs (chan 5)
          src (chan)
          p (pub src (fn [x]
                       (if (string? x)
                         :string
                         :int)))]
      (sub p :string a-strs)
      (sub p :string b-strs)
      (sub p :int a-ints)
      (sub p :int b-ints)
      (pipe (a/to-chan! [1 "a" 2 "b" 3 "c"]) src)
      (is (= [1 2 3]
             (<!! (a/into [] a-ints))))
      (is (= [1 2 3]
             (<!! (a/into [] b-ints))))
      (is (= ["a" "b" "c"]
             (<!! (a/into [] a-strs))))
      (is (= ["a" "b" "c"]
             (<!! (a/into [] b-strs))))))

  (testing "unique"
    (is (= [1 2 3 4]
           (<!! (a/into [] (a/unique (a/to-chan! [1 1 2 2 3 3 3 3 4])))))))

  (testing "partition"
    (is (= [[1 2] [2 3]]
           (<!! (a/into [] (a/partition 2 (a/to-chan! [1 2 2 3])))))))
  (testing "partition-by"
    (is (= [["a" "b"] [1 :2 3] ["c"]]
           (<!! (a/into [] (a/partition-by string? (a/to-chan! ["a" "b" 1 :2 3 "c"])))))))

  (testing "reduce"
    (is (= 0 (<!! (a/reduce + 0 (a/to-chan! [])))))
    (is (= 45 (<!! (a/reduce + 0 (a/to-chan! (range 10))))))
    (is (= :foo (<!! (a/reduce #(if (= %2 2) (reduced :foo) %1) 0 (a/to-chan! (range 10)))))))
  )

;; transducer yielding n copies of each input value
;; (into [] (xerox 2) [1 2 3]) => [1 1 2 2 3 3]
(defn xerox [n]
  (fn [f1]
    (fn
      ([] (f1))
      ([result] (f1 result))
      ([result input]
       (loop [res result
              i n]
         (if (pos? i)
           (let [a (f1 result input)]
             (if (reduced? a)
               a
               (recur a (dec i))))
           res))))))

(defn check-expanding-transducer [buffer-size in multiplier takers]
  (let [input (range in)
        xf (xerox multiplier)
        expected (apply interleave (repeat multiplier input))
        counter (atom 0)
        res (atom [])
        c (chan buffer-size xf)]
    (dotimes [x takers]
      (take! c #(do
                 (when (some? %) (swap! res conj %))
                 (swap! counter inc))))
    (onto-chan! c input)

    ;; wait for all takers to report
    (while (< @counter takers)
      (Thread/sleep 50))

    ;; check expected results
    (is (= (sort (clojure.core/take takers expected))
          (sort @res)))))

(deftest expanding-transducer-delivers-to-multiple-pending
  (doseq [b (range 1 10)
          t (range 1 10)]
    (check-expanding-transducer b 3 3 t)))

(deftest expanding-transducer-puts-can-ignore-buffer-fullness
  (testing "non-blocking puts behave as expected"
    ;; put coll, expanding xf,
    (let [c (chan 1 (mapcat identity))]
      (is (true? (>!! c [1 2 3])))
      (is (= 1 (<!! c)))
      (is (nil? (offer! c [4 5 6])))
      (is (= 2 (<!! c)))
      (is (= 3 (<!! c)))
      (is (true? (offer! c [4 5 6])))))

  (testing "blocking puts can execute during takes even when the buffer is full"
    (let [c (chan 1 (mapcat identity))]
      (is (true? (>!! c [1 2 3])))
      (is (= 1 (<!! c)))
      (is (nil? (offer! c [4 5 6])))
      (let [counter (atom 0)
            blocking-put (future (let [r (>!! c [4 5 6])]
                                   (swap! counter inc)
                                   r))]
        (is (= 0 @counter))
        (is (= 2 (<!! c)))

        ;; don't allow puts, buffer still full
        (is (false? (deref blocking-put 10 false)))
        (is (= 0 @counter))

        (is (nil? (offer! c [7 8 9])))))))

;; in 1.7+, use (map f)
(defn mapping [f]
  (fn [f1]
    (fn
      ([] (f1))
      ([result] (f1 result))
      ([result input]
       (f1 result (f input)))
      ([result input & inputs]
       (f1 result (apply f input inputs))))))

(deftest test-transduce
  (is (= [1 2 3 4 5]
         (<!! (a/transduce (mapping inc) conj [] (a/to-chan! (range 5)))))))
