(ns core.async.ioc-macros-test
  (:require [core.async.impl.ioc-macros :as ioc]
            [core.async :refer :all]
            [clojure.test :refer :all]))

(defn runner-wrapper
  "Simple wrapper that runs the state machine to completion"
  [f]
  (loop [state (f)]
    (if (ioc/finished? state)
      (::ioc/value state)
      (recur (f state)))))

(defmacro runner
  "Creates a runner block. The code inside the body of this macro will be translated
  into a state machine. At run time the body will be run as normal. This transform is
  only really useful for testing."
  [& body]
  (binding [ioc/*symbol-translations* '{pause core.async.ioc-macros/pause}]
    `(runner-wrapper ~(ioc/state-machine body))))

(deftest runner-tests
  (testing "do blocks"
    (is (= 42
           (runner (do (pause 42)))))
    (is (= 42
           (runner (do (pause 44)
                       (pause 42))))))
  (testing "if expressions"
    (is (= true
           (runner (if (pause true)
                     (pause true)
                     (pause false)))))
    (is (= false
           (runner (if (pause false)
                     (pause true)
                     (pause false)))))
    (is (= true
           (runner (when (pause true)
                     (pause true)))))
    (is (= nil
           (runner (when (pause false)
                     (pause true))))))
  
  (testing "loop expressions"
    (is (= 100
           (runner (loop [x 0]
                     (if (< x 100)
                       (recur (inc (pause x)))
                       (pause x)))))))
  
  (testing "let expressions"
    (is (= 3
           (runner (let [x 1 y 2]
                     (+ x y))))))
  
  (testing "vector destructuring"
    (is (= 3
           (runner (let [[x y] [1 2]]
                     (+ x y))))))

  (testing "hash-map destructuring"
    (is (= 3
           (runner (let [{:keys [x y] x2 :x y2 :y :as foo} {:x 1 :y 2}]
                     (assert (and foo (pause x) y x2 y2 foo))
                     (+ x y))))))
  
  (testing "hash-map literals"
    (is (= {:1 1 :2 2 :3 3}
           (runner {:1 (pause 1)
                    :2 (pause 2)
                    :3 (pause 3)}))))
  (testing "hash-set literals"
    (is (= #{1 2 3}
           (runner #{(pause 1)
                     (pause 2)
                     (pause 3)}))))
  (testing "vector literals"
    (is (= [1 2 3]
           (runner [(pause 1)
                    (pause 2)
                    (pause 3)]))))
  
  (testing "fn closures"
    (is (= 42
           (runner
            (let [x 42
                  _ (pause x)
                  f (fn [] x)]
              (f)))))))



(defn identity-chan 
  "Defines a channel that instantly writes the given value"
  [x]
  (let [c (chan 1)]
    (>! c x)
    (close! c)
    c))

(deftest async-test
  (testing "values are returned correctly"
    (is (= 10
           (<! (go (<! (identity-chan 10)))))))
  (testing "writes work"
    (is (= 11
           (<! (go (let [c (chan 1)]
                     (>! c (<! (identity-chan 11)))
                     (<! c))))))))

(deftest enqueued-chan-ops
  (testing "enqueued channel puts re-enter async properly"
    (is (= [:foo 42]
           (let [c (chan)
                 result-chan (go (>! c :foo) 42)]
             [(<! c) (<! result-chan)]))))
  (testing "enqueued channel takes re-enter async properly"
    (is (= :foo
           (let [c (chan)
                 async-chan (go (<! c))]
             (>! c :foo)
             (<! async-chan)))))
  (testing "puts into channels with full buffers re-enter async properly"
    (is (= :bar
           (let [c (chan 1)
                 async-chan (go
                             (>! c :foo)
                             (>! c :bar)
                             (<! c))]
             (<! c)
             (<! async-chan))))))

(deftest alt-tests
  (testing "alt works at all"
    (is (= [:foo 42]
           (<! (go (alt
                    :foo (<! (identity-chan 42))))))))
  (testing "prefer default"
    (is (= [:default 42]
           (<! (go (alt
                    :foo (<! (chan))
                    :default 42)))))))

