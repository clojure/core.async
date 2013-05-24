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
  (binding [ioc/*symbol-translations* '{pause core.async.ioc-macros/pause
                                        case case}]
    `(runner-wrapper ~(ioc/state-machine body))))

(deftest case-test
  )


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
  (testing "dotimes"
    (is (= 42 (runner
               (dotimes [x 10]
                 (pause x))
               42))))
  
  (testing "fn closures"
    (is (= 42
           (runner
            (let [x 42
                  _ (pause x)
                  f (fn [] x)]
              (f))))))

  (testing "case"
    (is (= 43
           (runner
            (let [value :bar]
              (case value
                :foo (pause 42)
                :bar (pause 43)
                :baz (pause 44))))))
    (is (= :default
           (runner
            (case :baz
              :foo 44
              :default))))))



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

(defn rand-timeout [x]
  (timeout (rand-int x)))

(deftest alt-tests
  (testing "alt works at all"
    (is (= [:foo 42]
           (<! (go (alt
                    :foo (<! (identity-chan 42))))))))
  (testing "prefer default"
    (is (= [:default 42]
           (<! (go (alt
                    :foo (<! (chan))
                    :default 42))))))
  (testing "alt randomly selects"
    (is (= #{:one :two :three}
           (<! (go (loop [acc #{}
                          cnt 0]
                     (if (< cnt 10)
                       (let [[label _] (alt
                                        :one (<! (rand-timeout 100))
                                        :two (<! (rand-timeout 100))
                                        :three (<! (rand-timeout 100)))]
                         (recur (conj acc label) (inc cnt))))
                     acc))))))
  
  (testing "case with go"
    (is (= :1
           (<! (go (case (name :1)
                     "0" :0
                     "1" :1
                     :3))))))

  (testing "nil result of go"
    (is (= nil
           (<! (go nil))))))

