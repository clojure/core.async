(ns cljs.core.async.tests
  (:require [cljs.core.async :refer [buffer dropping-buffer sliding-buffer put! take! chan close!]]
            [cljs.core.async.impl.dispatch :as dispatch]
            [cljs.core.async.impl.buffers :as buff]
            [cljs.core.async.impl.timers :as timers :refer [timeout]]
            [cljs.core.async.impl.protocols :refer [full? add! remove!]])
  (:require-macros [cljs.core.async.test-helpers :as h :refer [is= is deftest testing runner]]
                   [cljs.core.async.macros :as m :refer [go alt!]]))

(let [c (chan 1)]
  (put! c 42 #(is true) true)
  (take! c #(is (= 42 %)) true))

(let [c (chan)]
  (put! c 42 #(is true) true)
  (take! c #(is (= 42 %)) true))

(defn identity-chan
  [x]
  (let [c (chan 1)]
    (go (>! c x)
        (close! c))
    c))

(defn debug [x]
  (.log js/console x)
  x)

(go (is= (debug (<! (identity-chan 42))) 42))

(let [c (identity-chan 42)]
  (go (is= [42 c]
           (alts! [c]))))

(deftest alt-tests
  (testing "alts! works at all"
    (let [c (identity-chan 42)]
      (go (is= [42 c]
               (alts! [c])))))

  (testing "alt! works"
    (go
     (is= [42 :foo]
          (alt! (identity-chan 42) ([v] [v :foo])))))
  (testing "alts! can use default"
    (go
     (is= [42 :default]
          (alts!
           [(chan 1)] :default 42)))))

(deftest timeout-tests
  (testing "timeout will return same channel if within delay"
    (is= (timeout 10) (timeout 5))
    (is= 1 (count (seq timers/timeouts-map))))

  (testing "timeout map is empty after timeout expires"
    (go
      (<! (timeout 300))
      (is= 0 (count (seq timers/timeouts-map))))))
