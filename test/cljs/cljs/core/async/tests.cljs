(ns cljs.core.async.tests
  (:require [cljs.core.async :refer [buffer dropping-buffer sliding-buffer put! take! chan]]
            [cljs.core.async.impl.dispatch :as dispatch]
            [cljs.core.async.impl.buffers :as buff]
            [cljs.core.async.impl.protocols :refer [full? add! remove!]])
  (:require-macros [cljs.core.async.test-helpers :as h]))


(def asserts (atom 0))

(defn is [x]
  (if x
    (swap! asserts inc)
    (assert x "FAIL")))

(.log js/console "starting tests...")

(let [fb (buffer 2)]
  (is (= 0 (count fb)))

  (add! fb :1)
  (is (= 1 (count fb)))

  (add! fb :2)
  (is (= 2 (count fb)))

  (is (full? fb))
  #_(assert (throws? (add! fb :3)))

  (is (= :1 (remove! fb)))
  (is (not (full? fb)))

  (is (= 1 (count fb)))
  (is (= :2 (remove! fb)))

  (is (= 0 (count fb)))
  #_(is (helpers/throws? (remove! fb))))



(let [fb (dropping-buffer 2)]
  (is (= 0 (count fb)))

  (add! fb :1)
  (is (= 1 (count fb)))

  (add! fb :2)
  (is (= 2 (count fb)))

  (is (not (full? fb)))
  (add! fb :3)

  (is (= 2 (count fb)))

  (is (= :1 (remove! fb)))
  (is (not (full? fb)))

  (is (= 1 (count fb)))
  (is (= :2 (remove! fb)))

  (is (= 0 (count fb)))
  #_(is (throws? (remove! fb))))



(let [fb (sliding-buffer 2)]
  (is (= 0 (count fb)))

  (add! fb :1)
  (is (= 1 (count fb)))

  (add! fb :2)
  (is (= 2 (count fb)))

  (is (not (full? fb)))
  (add! fb :3)

  (is (= 2 (count fb)))
  
  (is (= :2 (remove! fb)))
  (is (not (full? fb)))

  (is (= 1 (count fb)))
  (is (= :3 (remove! fb)))

  (is (= 0 (count fb)))
  #_(is (throws? (remove! fb))))


(let [c (chan 1)]
  (put! c 42 #(is true) true)
  (take! c #(is (= 42 %)) true))

(let [c (chan)]
  (put! c 42 #(is true) true)
  (take! c #(is (= 42 %)) true))

(js/setTimeout
 (fn [] (.log js/console (str  "..done " @asserts " asserts")) 1000))


(h/deftest runner-tests
  (h/testing "do blocks"
    (is (= 42
           (h/runner (do (pause 42)))))
    (is (= 42
           (h/runner (do (pause 44)
                         (pause 42))))))
  (h/testing "if expressions"
    (is (= true
           (h/runner (if (pause true)
                       (pause true)
                       (pause false)))))
    (is (= false
           (h/runner (if (pause false)
                       (pause true)
                       (pause false)))))
    (is (= true 
           (h/runner (when (pause true)
                       (pause true)))))
    (is (= nil
           (h/runner (when (pause false)
                       (pause true))))))
  
  (h/testing "loop expressions"
    (is (= 100
           (h/runner (loop [x 0]
                       (if (< x 100)
                         (recur (inc (pause x)))
                         (pause x)))))))
  
  (h/testing "let expressions"
    (is (= 3
           (h/runner (let [x 1 y 2]
                       (+ x y))))))
  
  #_(h/testing "vector destructuring"
    (h/is= 3
         (h/runner (let [[x y] [1 2]]
                     (+ x y)))))

  #_(h/testing "hash-map destructuring"
    (is (= 3
           (h/runner (let [{:keys [x y] x2 :x y2 :y :as foo} {:x 1 :y 2}]
                       (assert (and foo (pause x) y x2 y2 foo))
                       (+ x y))))))
  
  (h/testing "hash-map literals"
    (is (= {:1 1 :2 2 :3 3}
           (h/runner {:1 (pause 1)
                      :2 (pause 2)
                      :3 (pause 3)}))))
  (h/testing "hash-set literals"
    (is (= #{1 2 3}
           (h/runner #{(pause 1)
                       (pause 2)
                       (pause 3)}))))
  (h/testing "vector literals"
    (is (= [1 2 3]
           (h/runner [(pause 1)
                      (pause 2)
                      (pause 3)]))))
  #_(h/testing "dotimes"
      (is (= 42 (h/runner
                 (dotimes [x 10]
                   (pause x))
                 42))))
  
  (h/testing "fn closures"
    (is (= 42
           (h/runner
            (let [x 42
                  _ (pause x)
                  f (fn [] x)]
              (f))))))

  (h/testing "case"
    (is (= 43
           (h/runner
            (let [value :bar]
              (case value
                :foo (pause 42)
                :bar (pause 43)
                :baz (pause 44))))))
    (is (= :default
           (h/runner
            (case :baz
              :foo 44
              :default)))))
  
  )
