(ns cljs.core.async.tests
  (:require [cljs.core.async :refer [buffer dropping-buffer sliding-buffer put! take! chan close!] :as async]
            [cljs.core.async.impl.dispatch :as dispatch]
            [cljs.core.async.impl.buffers :as buff]
            [cljs.core.async.impl.timers :as timers :refer [timeout]]
            [cljs.core.async.impl.protocols :refer [full? add! remove!]])
  (:require-macros [cljs.core.async.test-helpers :as h :refer [is= is deftest testing runner throws?]]
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

(go (is= (<! (identity-chan 42)) 42))

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

  #_(testing "timeout map is empty after timeout expires"
    (go
     (<! (timeout 300))
     (is= 0 (count (seq timers/timeouts-map)))))
  #_(testing "timeout map is empty after timeout expires with namespaced take"
    (go
     (async/<! (timeout 300))
     (is= 0 (count (seq timers/timeouts-map))))))

(deftest put-limits
  (testing "async put!s are limited"
    (let [c (chan)]
      (dotimes [x 1024]
        (put! c x))
      (is (throws? (put! c 42)))
      (take! c (fn [x] (is= x 0)))))

  (testing "async take!s are limited"
    (let [c (chan)]
      (dotimes [x 1024]
        (take! c (fn [x])))
      (is (throws? (take! c (fn [x]))))
      (put! c 42))))

(deftest close-on-exception-tests
  (testing "go blocks"
    (go
     (alt! (go (assert false "This exception is expected")) ([v] (is (nil? v)))
           (timeout 500) ([v] (assert false "Channel did not close")))
     (alt! (go (alts! [(identity-chan 42)])
               (assert false "This exception is expected"))  ([v] (is (nil? v)))
               (timeout 500) ([v] (assert false "Channel did not close"))))))
