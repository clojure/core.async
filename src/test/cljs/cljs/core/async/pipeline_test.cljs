(ns cljs.core.async.pipeline-test
  (:require [cljs.core.async :as a :refer [<! >! chan close! to-chan pipeline-async]])
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

(defn test-size [n size]
  (let [r (range n)]
    (go (is= r (<! (pipeline-tester pipeline-async n r identity-async))))))

(deftest async-pipeline-test-sizes
  (test-size 1 0)
  (test-size 1 10)
  (test-size 10 10)
  (test-size 20 10)
  (test-size 5 1000))

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
