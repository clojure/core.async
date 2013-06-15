(ns cljs.core.async.tests
  (:require [cljs.core.async :refer [buffer dropping-buffer sliding-buffer put! take! chan]]
            [cljs.core.async.impl.dispatch :as dispatch]
            [cljs.core.async.impl.buffers :as buff]
            [cljs.core.async.impl.protocols :refer [full? add! remove!]]))


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
