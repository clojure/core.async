(ns clojure.core.pipeline-test
  (:require [clojure.test :refer (deftest is are)]
            [clojure.core.async :as a :refer [<! >! <!! >!! go go-loop thread chan close! to-chan!
			                                  pipeline pipeline-blocking pipeline-async]]))

;; in Clojure 1.7, use (map f) instead of this
(defn mapping [f]
  (fn [f1]
    (fn
      ([] (f1))
      ([result] (f1 result))
      ([result input]
         (f1 result (f input)))
      ([result input & inputs]
         (f1 result (apply f input inputs))))))

(defn pipeline-tester [pipeline-fn n inputs xf]
  (let [cin (to-chan! inputs)
        cout (chan 1)]
    (pipeline-fn n cout xf cin)
    (<!! (go-loop [acc []] 
		(let [val (<! cout)]
		  (if (not (nil? val))
            (recur (conj acc val))
            acc))))))
			
(def identity-mapping (mapping identity))
(defn identity-async [v ch] (thread (>!! ch v) (close! ch)))

(deftest test-sizes
  (are [n size]
    (let [r (range size)]
      (and
       (= r (pipeline-tester pipeline n r identity-mapping))
       (= r (pipeline-tester pipeline-blocking n r identity-mapping))
       (= r (pipeline-tester pipeline-async n r identity-async))))
    1 0
    1 10
    10 10
    20 10
    5 1000))

(deftest test-close?
  (doseq [pf [pipeline pipeline-blocking]]
    (let [cout (chan 1)]
      (pf 5 cout identity-mapping (to-chan! [1]) true)
      (is (= 1 (<!! cout)))
      (is (= nil (<!! cout))))
    (let [cout (chan 1)]
      (pf 5 cout identity-mapping (to-chan! [1]) false)
      (is (= 1 (<!! cout)))
      (>!! cout :more)
      (is (= :more (<!! cout))))
    (let [cout (chan 1)]
      (pf 5 cout identity-mapping (to-chan! [1]) nil)
      (is (= 1 (<!! cout)))
      (>!! cout :more)
      (is (= :more (<!! cout))))))

(deftest test-ex-handler
  (doseq [pf [pipeline pipeline-blocking]]
    (let [cout (chan 1)
          chex (chan 1)
          ex-mapping (mapping (fn [x] (if (= x 3) (throw (ex-info "err" {:data x})) x)))
          ex-handler (fn [e] (do (>!! chex e) :err))]
      (pf 5 cout ex-mapping (to-chan! [1 2 3 4]) true ex-handler)
      (is (= 1 (<!! cout)))
      (is (= 2 (<!! cout)))
      (is (= :err (<!! cout)))
      (is (= 4 (<!! cout)))
      (is (= {:data 3} (ex-data (<!! chex)))))))

(defn multiplier-async [v ch]
  (thread
   (dotimes [i v]
     (>!! ch i))
   (close! ch)))

(deftest test-af-multiplier
  (is (= [0 0 1 0 1 2 0 1 2 3]
         (pipeline-tester pipeline-async 2 (range 1 5) multiplier-async))))

(def sleep-mapping (mapping #(do (Thread/sleep %) %)))

(deftest test-blocking
  (let [times [2000 50 1000 100]]
    (is (= times (pipeline-tester pipeline-blocking 2 times sleep-mapping)))))

(defn slow-fib [n]
  (if (< n 2) n (+ (slow-fib (- n 1)) (slow-fib (- n 2)))))

(deftest test-compute
  (let [input (take 50 (cycle (range 15 38)))]
    (is (= (slow-fib (last input))
           (last (pipeline-tester pipeline 8 input (mapping slow-fib)))))))

(deftest test-async
  (is (= (range 1 101)
         (pipeline-tester pipeline-async 1 (range 100)
                          (fn [v ch] (future (>!! ch (inc v)) (close! ch)))))))
