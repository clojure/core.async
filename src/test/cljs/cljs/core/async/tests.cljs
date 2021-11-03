;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns cljs.core.async.tests
  (:require-macros
   [cljs.core.async.macros :as m :refer [go alt!]])
  (:require
   [cljs.core.async :refer
    [buffer dropping-buffer sliding-buffer put! take! chan promise-chan
     close! take partition-by offer! poll! <! >! alts!] :as async]
   [cljs.core.async.impl.dispatch :as dispatch]
   [cljs.core.async.impl.buffers :as buff]
   [cljs.core.async.impl.timers :as timers :refer [timeout]]
   [cljs.core.async.impl.protocols :refer [full? add! remove!]]
   [cljs.core.async.test-helpers :refer [latch inc!]]
   [cljs.test :as test :refer-macros [deftest is run-tests async testing]]
   [goog.object :as gobj]))

(enable-console-print!)

(deftest test-put-take-chan-1
  (async done
    (let [c (chan 1)
          l (latch 2 done)]
      (put! c 42 #(do (is true) (inc! l)))
      (take! c #(do (is (= 42 %))) (inc! l)))))

(deftest test-put-take-chan
  (async done
    (let [c (chan)
          l (latch 2 done)]
      (put! c 42 #(do (is true) (inc! l)))
      (take! c #(do (is (= 42 %))) (inc! l)))))

(defn identity-chan
  [x]
  (let [c (chan 1)]
    (go (>! c x)
        (close! c))
    c))

(defn debug [x]
  (.log js/console x)
  x)

(deftest test-identity-chan
  (async done
    (go
      (is (= (<! (identity-chan 42)) 42))
      (done))))

(deftest test-identity-chan-alts!
  (async done
    (let [c (identity-chan 42)]
      (go
        (is (= [42 c] (alts! [c])))
        (done)))))

(deftest alt-tests
  (async done
    (testing "alts! works at all"
      (let [c (identity-chan 42)]
        (go
          (is (= [42 c] (alts! [c])))
          (done))))))

(deftest test-alt!-and-alts!
  (async done
    (let [l (latch 2 done)]
      (testing "alt! works"
        (go
          (is (= [42 :foo] (alt! (identity-chan 42) ([v] [v :foo]))))
          (inc! l)))
      (testing "alts! can use default"
        (go
          (is
            (= [42 :default]
               (alts! [(chan 1)] :default 42)))
          (inc! l))))))

#_(deftest timeout-tests
  (async done
    (let [l (latch 2 done)]
      (testing "timeout will return same channel if within delay"
        (is (= (timeout 10) (timeout 10)))
        (is (= 1 (count (seq timers/timeouts-map)))))
      (testing "timeout map is empty after timeout expires"
        (go
          (<! (timeout 300))
          (is (= 0 (count (seq timers/timeouts-map))))
          (inc! l)))
      (testing "timeout map is empty after timeout expires with namespaced take"
        (go
          (async/<! (timeout 300))
          (is (= 0 (count (seq timers/timeouts-map))))
          (inc! l))))))

(deftest queue-limits
  (testing "async put!s are limited"
    (let [c (chan)]
      (dotimes [x 1024]
        (put! c x))
      (is (thrown? js/Error (put! c 42)))
      (take! c (fn [x] (is (= x 0))))))
  (testing "async take!s are limited"
    (let [c (chan)]
      (dotimes [x 1024]
        (take! c (fn [x])))
      (is (thrown? js/Error (take! c (fn [x]))))
      (put! c 42))))

(deftest close-on-exception-tests
  (async done
    (let [l (latch 2 done)]
      (testing "go blocks"
        (go
          (alt! (go (assert false "This exception is expected"))
            ([v] (is (nil? v)) (inc! l))
            ;; if this fails, channel did not close
            (timeout 500) ([v] (is false) (inc! l)))
          (alt! (go (alts! [(identity-chan 42)])
                  (assert false "This exception is expected"))
            ([v] (is (nil? v)) (inc! l))
            ;; if this fails, channel did not close
            (timeout 500) ([v] (is false) (inc! l))))))))

(deftest cleanup
  (async done
    (let [l (latch 2 done)]
      (testing "alt handlers are removed from put!"
        (go
          (let [c (chan)]
            (dotimes [x 1024]
              (alts! [[c x]] :default 42))
            (put! c 42))
          (inc! l)))
      (testing "alt handlers are removed from take!"
        (go
          (let [c (chan)]
            (dotimes [x 1024]
              (alts! [c] :default 42))
            (take! c (fn [x] nil)))
          (inc! l))))))

(deftest test-map<
  (async done
    (go
      (is (= [2 3 4 5]
            (<! (async/into [] (async/map< inc (async/to-chan! [1 2 3 4]))))))
      (done))))

(deftest test-map>
  (async done
    (go
      (is (= [2 3 4 5]
             (let [out (chan)
                   in (async/map> inc out)]
               (async/onto-chan! in [1 2 3 4])
               (<! (async/into [] out)))))
      (done))))

(deftest test-filter<
  (async done
    (go
      (is (= [2 4 6]
             (<! (async/into [] (async/filter< even? (async/to-chan! [1 2 3 4 5 6]))))))
      (done))))

(deftest test-remoev<
  (async done
    (go
      (is (= [1 3 5]
             (<! (async/into [] (async/remove< even? (async/to-chan! [1 2 3 4 5 6]))))))
      (done))))

(deftest test-filter>
  (async done
    (go
      (is (= [2 4 6]
             (let [out (chan)
                   in (async/filter> even? out)]
               (async/onto-chan! in [1 2 3 4 5 6])
               (<! (async/into [] out)))))
      (done))))

(deftest test-remove>
  (async done
    (go
      (is (= [1 3 5]
             (let [out (chan)
                   in (async/remove> even? out)]
               (async/onto-chan! in [1 2 3 4 5 6])
               (<! (async/into [] out)))))
      (done))))

(deftest test-mapcat<
  (async done
    (go
      (is (= [0 0 1 0 1 2]
             (<! (async/into [] (async/mapcat< range (async/to-chan! [1 2 3]))))))
      (done))))

(deftest test-mapcat>
  (async done
    (go
      (is (= [0 0 1 0 1 2]
             (let [out (chan)
                   in (async/mapcat> range out)]
               (async/onto-chan! in [1 2 3])
               (<! (async/into [] out)))))
      (done))))

(deftest test-pipe
  (async done
    (go
      (is (= [1 2 3 4 5]
             (let [out (chan)]
               (async/pipe (async/to-chan! [1 2 3 4 5])
                 out)
               (<! (async/into [] out)))))
      (done))))

(deftest test-split
  (async done
    ;; Must provide buffers for channels else the tests won't complete
    (go
      (let [[even odd] (async/split even? (async/to-chan! [1 2 3 4 5 6]) 5 5)]
        (is (= [2 4 6] (<! (async/into [] even))))
        (is (= [1 3 5] (<! (async/into [] odd)))))
      (done))))

(deftest test-map
  (async done
    (go
      (is (= [0 4 8 12]
             (<! (async/into []
                   (async/map +
                     [(async/to-chan! (range 4))
                      (async/to-chan! (range 4))
                      (async/to-chan! (range 4))
                      (async/to-chan! (range 4))])))))
      (done))))

(deftest test-merge
  (async done
    ;; merge uses alt, so results can be in any order, we're using
    ;; frequencies as a way to make sure we get the right result.
    (go
      (is (= {0 4, 1 4, 2 4, 3 4}
            (frequencies
              (<! (async/into []
                    (async/merge
                      [(async/to-chan! (range 4))
                       (async/to-chan! (range 4))
                       (async/to-chan! (range 4))
                       (async/to-chan! (range 4))]))))))
      (done))))

(deftest test-mult
  (async done
    (go
      (let [a (chan 4)
            b (chan 4)
            src (chan)
            m (async/mult src)]
        (async/tap m a)
        (async/tap m b)
        (async/pipe (async/to-chan! (range 4)) src)
        (is (= [0 1 2 3] (<! (async/into [] a))))
        (is (= [0 1 2 3] (<! (async/into [] b))))
        (done)))))

(deftest test-mix
  (async done
    (go
      (let [out (chan)
            mx (async/mix out)
            take-out (chan)
            take6 (go (dotimes [x 6]
                        (>! take-out (<! out)))
                    (close! take-out))]
        (async/admix mx (async/to-chan! [1 2 3]))
        (async/admix mx (async/to-chan! [4 5 6]))
        (is (= #{1 2 3 4 5 6} (<! (async/into #{} take-out))))
        (done)))))

(deftest test-pub-sub
  (async done
    (go
      (let [a-ints (chan 5)
            a-strs (chan 5)
            b-ints (chan 5)
            b-strs (chan 5)
            src (chan)
            p (async/pub src (fn [x]
                               (if (string? x)
                                 :string
                                 :int)))]
        (async/sub p :string a-strs)
        (async/sub p :string b-strs)
        (async/sub p :int a-ints)
        (async/sub p :int b-ints)
        (async/pipe (async/to-chan! [1 "a" 2 "b" 3 "c"]) src)
        (is (= [1 2 3]
              (<! (async/into [] a-ints))))
        (is (= [1 2 3]
              (<! (async/into [] b-ints))))
        (is (= ["a" "b" "c"]
              (<! (async/into [] a-strs))))
        (is (= ["a" "b" "c"]
              (<! (async/into [] b-strs)))))
      (done))))

(deftest test-unique
  (async done
    (go
      (is (= [1 2 3 4]
             (<! (async/into [] (async/unique (async/to-chan! [1 1 2 2 3 3 3 3 4]))))))
      (done))))

(deftest test-partition
  (async done
    (go
      (is (= [[1 2] [2 3]]
             (<! (async/into [] (async/partition 2 (async/to-chan! [1 2 2 3]))))))
      (done))))


(deftest test-partition-by
  (async done
    (go
      (is (= [["a" "b"] [1 :2 3] ["c"]]
             (<! (async/into [] (async/partition-by string? (async/to-chan! ["a" "b" 1 :2 3 "c"]))))))
      (done))))

(deftest test-reduce
  (async done
    (let [l (latch 3 done)]
      (go (is (= 0 (<! (async/reduce + 0 (async/to-chan! [])))))
        (inc! l))
      (go (is (= 45 (<! (async/reduce + 0 (async/to-chan! (range 10))))))
        (inc! l))
      (go (is (= :foo (<! (async/reduce #(if (= %2 2) (reduced :foo) %1) 0
                            (async/to-chan! (range 10))))))
        (inc! l)))))

(deftest dispatch-bugs
  (async done
    (testing "puts are moved to buffers"
      (let [c (chan 1)
            a (atom 0)]
        (put! c 42 (fn [_] (swap! a inc))) ;; Goes into buffer
        (put! c 42 (fn [_] (swap! a inc))) ;; Goes into puts
        (take! c
          (fn [_]
            ;; Should release the iten in the puts and
            ;; put its value into the buffer, dispatching the callback
            (go
              (<! (timeout 500))
              ;; Thus this should be 2
              (is (= @a 2))
              (done))))))))

 (defn integer-chan
   "Returns a channel upon which will be placed integers from 0 to n (exclusive) at 10 ms intervals, using the provided xform"
   [n xform]
   (let [c (chan 1 xform)]
     (go
       (loop [i 0]
         (if (< i n)
           (do
             (<! (timeout 10))
             (>! c i)
             (recur (inc i)))
           (close! c))))
     c))

(deftest test-transducers
  (async done
    (let [l (latch 6 done)]
      (testing "base case without transducer"
        (go (is (= (range 10)
                   (<! (async/into [] (integer-chan 10 nil)))))
          (inc! l)))
      (testing "mapping transducer"
        (go (is (= (map str (range 10))
                   (<! (async/into [] (integer-chan 10 (map str))))))
          (inc! l)))
      (testing "filtering transducer"
        (go (is (= (filter even? (range 10))
                   (<! (async/into [] (integer-chan 10 (filter even?))))))
          (inc! l)))
      (testing "flatpmapping transducer"
        (let [pair-of (fn [x] [x x])]
          (go (is (= (mapcat pair-of (range 10))
                     (<! (async/into [] (integer-chan 10 (mapcat pair-of))))))
            (inc! l))))
      (testing "partitioning transducer"
        (go (is (= [[0 1 2 3 4] [5 6 7]]
                   (<! (async/into [] (integer-chan 8 (partition-all 5))))))
          (inc! l))
        (go (is (= [[0 1 2 3 4] [5 6 7 8 9]]
                   (<! (async/into [] (integer-chan 10 (partition-all 5))))))
          (inc! l))))))

(deftest test-bufferless
  (async done
    (let [c (chan)
          l (latch 2 done)]
      (go
        (is (= [:value c] (async/alts! [c (async/timeout 6000)] :priority true)))
        (inc! l))
      (go
        (is (= [true c] (async/alts! [[c :value] (async/timeout 6000)] :priority true)))
        (inc! l)))))

(deftest test-promise-chan
  (async done
    (let [l (latch 3 done)]
      (testing "put on promise-chan fulfills all pending takers"
          (let [c  (promise-chan)
                t1 (go (<! c))
                t2 (go (<! c))]
            (go
              (>! c :val)
              (is (= :val (<! t1) (<! t2)))
              (testing "then puts succeed but are dropped"
                (go (>! c :LOST))
                (is (= :val (<! c))))
              (testing "then takes succeed with the original value"
                (is (= :val (<! c) (<! c) (<! c))))
              (testing "then after close takes continue returning val"
                (close! c)
                (is (= :val (<! c) (<! c))))
              (inc! l))))
      (testing "close on promise-chan fulfills all pending takers"
        (go
          (let [c  (promise-chan)
                t1 (go (<! c))
                t2 (go (<! c))]
            (close! c)
            (is (= nil (<! t1) (<! t2)))
            (testing "then takes return nil"
              (is (= nil (<! t1) (<! t1) (<! t2) (<! t2)))))
          (inc! l)))
      (testing "close after put on promise-chan continues delivering promised value"
        (go
          (let [c (promise-chan)]
            (>! c :val) ;; deliver
            (is (= :val (<! c) (<! c)))
            (close! c)
            (is (= :val (<! c) (<! c))))
          (inc! l))))))

(deftest test-offer-poll-go
  (let [c (chan 2)]
    (is (= [true true 5 6 nil]
           [(offer! c 5) (offer! c 6) (poll! c) (poll! c) (poll! c)])))
  (let [c (chan 2)]
    (is (true? (offer! c 1)))
    (is (true? (offer! c 2)))
    (is (nil? (offer! c 3)))
    (is (= 1 (poll! c)))
    (is (= 2 (poll! c)))
    (is (nil? (poll! c))))
  (let [c (chan)]
    (is (nil? (offer! c 1)))
    (is (nil? (poll! c)))))

(deftest test-transduce
  (go
    (= [1 2 3 4 5]
      (<! (async/transduce (map inc) conj [] (async/to-chan! (range 5)))))))

(def ^:dynamic foo 42)

(deftest test-locals-alias-globals
  (async done
    (go
      (let [old foo]
        (set! foo 45)
        (is (= foo 45))
        (is (= old 42))
        (set! foo old)
        (is (= old 42))
        (is (= foo 42)))
      (done))))

(deftest test-js-literals
  (async done
    (go
      (let [arr #js [1 2 3]]
        (is (= 2 (aget arr 1))))
      (let [obj #js {:foo 1}]
        (is (= 1 (gobj/get obj "foo"))))
      (testing "ASYNC-132 / 117- can't close over local in #js in go"
        (let [bar 42]
          (is (= 42 (aget #js [1 bar 3] 1)))
          (is (= 42 (gobj/get #js {:foo bar} "foo")))))
      (done))))

(deftest test-js-literals-chans
  (let [c0 (chan)
        c1 (chan)]
   (async done
     (go
       (let [arr #js [1 (<! c0) 3]]
         (is (= 2 (aget arr 1))))
       (let [obj #js {:foo (<! c1)}]
         (is (= 1 (gobj/get obj "foo"))))
       (done))
     (go
       (>! c0 2)
       (>! c1 1)))))

(comment

  (test/run-tests)

  )
