(ns cljs.core.async.pipeline-test
  (:require [cljs.core.async :as a :refer [<! >! chan close! to-chan pipeline-async pipeline put!]])
  (:require-macros [cljs.core.async.test-helpers :as h :refer [is= is deftest testing runner]]
                   [cljs.core.async.macros :as m :refer [go go-loop]]))

(defn pipeline-tester [pipeline-fn n inputs xf]
  (let [cin (to-chan inputs)
        cout (chan 1)]
    (pipeline-fn n cout xf cin)
    (go-loop [acc []]
             (let [val (<! cout)]
               (if (not (nil? val))
                 (recur (conj acc val))
                 acc)))))

(defn identity-async [v ch]
  (go (>! ch v) (close! ch)))

(defn test-size-async [n size]
  (let [r (range size)]
    (go (is= r (<! (pipeline-tester pipeline-async n r identity-async))))))

(defn test-size-compute [n size]
  (let [r (range size)]
    (go (is= r (<! (pipeline-tester pipeline n r (map identity)))))))

(deftest pipeline-test-sizes
  (testing "pipeline async test sizes"
    (test-size-async 1 0)
    (test-size-async 1 10)
    (test-size-async 10 10)
    (test-size-async 20 10)
    (test-size-async 5 1000))
  (testing "pipeline compute test sizes"
    (test-size-compute 1 0)
    (test-size-compute 1 10)
    (test-size-compute 10 10)
    (test-size-compute 20 10)
    (test-size-compute 5 1000)))

(deftest test-close?
  (go
    (let [cout (chan 1)]
      (pipeline 5 cout (map identity) (to-chan [1]) true)
      (is (= 1 (<! cout)))
      (is (= nil (<! cout))))
    (let [cout (chan 1)]
      (pipeline 5 cout (map identity) (to-chan [1]) false)
      (is (= 1 (<! cout)))
      (>! cout :more)
      (is (= :more (<! cout))))
    (let [cout (chan 1)]
      (pipeline 5 cout (map identity) (to-chan [1]) nil)
      (is (= 1 (<! cout)))
      (>! cout :more)
      (is (= :more (<! cout))))))

(deftest test-ex-handler
  (go
    (let [cout (chan 1)
          chex (chan 1)
          ex-mapping (map (fn [x] (if (= x 3) (throw (ex-info "err" {:data x})) x)))
          ex-handler (fn [e] (do (put! chex e) :err))]
      (pipeline 5 cout ex-mapping (to-chan [1 2 3 4]) true ex-handler)
      (is (= 1 (<! cout)))
      (is (= 2 (<! cout)))
      (is (= :err (<! cout)))
      (is (= 4 (<! cout)))
      (is (= {:data 3} (ex-data (<! chex)))))))

(defn multiplier-async [v ch]
  (go
    (dotimes [i v]
      (>! ch i))
    (close! ch)))

(deftest async-pipelines-af-multiplier
  (go
    (is= [0 0 1 0 1 2 0 1 2 3]
         (<! (pipeline-tester pipeline-async 2 (range 1 5) multiplier-async)))))

(defn incrementer-async [v ch]
  (go
    (>! ch (inc v))
    (close! ch)))

(deftest pipelines-async
  (go
    (is= (range 1 101)
         (<! (pipeline-tester pipeline-async 1 (range 100) incrementer-async)))))

(defn slow-fib [n]
  (if (< n 2) n (+ (slow-fib (- n 1)) (slow-fib (- n 2)))))

(deftest pipelines-compute
  (let [input (take 50 (cycle (range 15 38)))]
    (go
      (is (= (slow-fib (last input))
             (last (<! (pipeline-tester pipeline 8 input (map slow-fib)))))))))
