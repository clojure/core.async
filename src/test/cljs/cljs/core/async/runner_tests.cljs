;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns cljs.core.async.runner-tests
  (:require [cljs.core.async :refer [buffer dropping-buffer sliding-buffer put! take! chan close!]]
            [cljs.core.async.impl.dispatch :as dispatch]
            [cljs.core.async.impl.buffers :as buff]
            [cljs.core.async.impl.protocols :refer [full? add! remove!]]
            [cljs.core.async.impl.ioc-helpers :as ioch])
  (:require-macros [cljs.core.async.test-helpers :as h :refer [is= is deftest testing runner locals-test]]
                   [cljs.core.async.macros :as m :refer [go]]
                   [cljs.core.async.impl.ioc-macros :as ioc]))

(defn pause [state blk val]
  (ioc/aset-all! state ioch/STATE-IDX blk ioch/VALUE-IDX val)
  :recur)

(deftest runner-tests
  (testing "macros add locals to the env"
    (is= :pass
         (runner (let [x 42]
                   (pause (locals-test))))))
  (testing "do blocks"
    (is= 42
         (runner (do (pause 42))))
    (is= 42
         (runner (do (pause 44)
                     (pause 42)))))
  (testing "if expressions"
    (is= true
         (runner (if (pause true)
                   (pause true)
                   (pause false))))
    (is= false
         (runner (if (pause false)
                   (pause true)
                   (pause false))))
    (is= true
         (runner (when (pause true)
                   (pause true))))
    (is= nil
         (runner (when (pause false)
                   (pause true)))))

  (testing "loop expressions"
    (is= 100
         (runner (loop [x 0]
                   (if (< x 100)
                     (recur (inc (pause x)))
                     (pause x)))))
    (is= [:b :a]
         (runner (loop [a :a b :b n 1]
                   (if (pos? n)
                     (recur b a (dec n)) ;; swap bindings
                     [a b]))))
    (is= 1
         (runner (loop [x 0
                        y (inc x)]
                   y))))

  (testing "let expressions"
    (is= 3
         (runner (let [x 1 y 2]
                   (+ x y)))))

  (testing "vector destructuring"
    (is= 3
         (runner (let [[x y] [1 2]]
                   (+ x y)))))

  (testing "hash-map destructuring"
    (is= 3
         (runner (let [{:keys [x y] x2 :x y2 :y :as foo} {:x 1 :y 2}]
                   (assert (and foo (pause x) y x2 y2 foo))
                   (+ x y)))))

  (testing "hash-map literals"
    (is= {:1 1 :2 2 :3 3}
         (runner {:1 (pause 1)
                  :2 (pause 2)
                  :3 (pause 3)})))
  (testing "hash-set literals"
    (is= #{1 2 3}
         (runner #{(pause 1)
                   (pause 2)
                   (pause 3)})))
  (testing "vector literals"
    (is= [1 2 3]
         (runner [(pause 1)
                  (pause 2)
                  (pause 3)])))
  (testing "dotimes"
    (is= 42 (runner
             (dotimes [x 10]
               (pause x))
             42)))

  (testing "set! with field"
    (let [x (js-obj)]
      (runner (set! (.-foo x) "bar")
              (is= (.-foo x) "bar"))
      (is= (.-foo x) "bar")))

  (testing "set! with var"
    (def test-target 0)
    (runner (set! test-target 42))
    (is= test-target 42))

  (testing "keywords as functions"
    (is (= :bar
           (runner (:foo (pause {:foo :bar}))))))

  (testing "vectors as functions"
    (is (= 2
           (runner ([1 2] 1)))))

  (testing "dot forms"
    (is (= 8 (runner (. js/Math (pow 2 3)))))
    (is (= 8 (runner (. js/Math pow 2 3)))))

  (testing "quote"
    (is= '(1 2 3)
         (runner (pause '(1 2 3)))))

  (testing "fn closures"
    (is= 42
         (runner
          (let [x 42
                _ (pause x)
                f (fn [] x)]
            (f)))))

  (testing "case"
    (is= 43
         (runner
          (let [value :bar]
            (case value
              :foo (pause 42)
              :bar (pause 43)
              :baz (pause 44)))))
    (is= :default
         (runner
          (case :baz
            :foo 44
            :default)))
    (is= nil
         (runner
          (case true
            false false
            nil)))
    (is= 42
         (runner
          (loop [x 0]
            (case (int x)
              0 (recur (inc x))
              1 42)))))

  (testing "try"
    (is= 42
         (runner
          (try 42
               (catch js/Error ex ex))))
    (is= 42
         (runner
          (try
            (assert false)
            (catch js/Error ex 42))))

    (is= 42
         (runner
          (try
            (assert false)
            (catch :default ex 42))))

    (let [a (atom false)
          v (runner
             (try
               true
               (catch js/Error ex false)
               (finally (pause (reset! a true)))))]
      (is (and @a v)))

    (let [a (atom false)
          v (runner
             (try
               (assert false)
               (catch js/Error ex true)
               (finally (reset! a true))))]
      (is (and @a v)))

    (testing "https://clojure.atlassian.net/browse/ASYNC-73"
      (let [a (atom false)]
        (runner
         (try
           (throw (js/Error. "asdf"))
           (catch ExceptionInfo e)
           (catch js/Error e)
           (finally
             (reset! a true))))
        (is @a))
      (is (runner
           (try
             (throw (new js/TypeError "unexpected"))
             (catch ExceptionInfo e
               false)
             (catch :default e
               true)))))

    (testing "https://clojure.atlassian.net/browse/ASYNC-172"
      (is (= 123 (runner
                  (try (throw 123)
                       (catch :default e 123))))))

    (let [a (atom false)
          v (try (runner
                  (try
                    (assert false)
                    (finally (reset! a true))))
                 (catch js/Error ex ex))]
      (is (and @a v)))

    (let [a (atom 0)
          v (runner
             (try
               (try
                 42
                 (finally (swap! a inc)))
               (finally (swap! a inc))))]
      (is (= @a 2)))

    (let [a (atom 0)
          v (try (runner
                  (try
                    (try
                      (assert false)
                      (finally (swap! a inc)))
                    (finally (swap! a inc))))
                 (catch js/Error ex ex))]
      (is (= @a 2)))

    (let [a (atom 0)
          v (try (runner
                  (try
                    (try
                      (assert false)
                      (catch js/Error ex (throw ex))
                      (finally (swap! a inc)))
                    (catch js/Error ex (throw ex))
                    (finally (swap! a inc))))
                 (catch js/Error ex ex))]
      (is (= @a 2)))

    (let [a (atom 0)
          v (try (runner
                  (try
                    (try
                      (assert false)
                      (catch js/Error ex (pause (throw ex)))
                      (finally (pause (swap! a inc))))
                    (catch js/Error ex (pause (throw ex)))
                    (finally (pause (swap! a inc)))))
                 (catch js/Error ex ex))]
      (is (= @a 2)))))
