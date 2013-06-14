(ns cljs.core.async.tests
  (:require [cljs.core.async.impl.dispatch :as dispatch]
            [cljs.core.async.impl.buffers :as buff]
            [cljs.core.async.impl.protocols :refer [full? add! remove!]]))

#_(dispatch/run (fn [] (assert (= 1 2))))

(def asserts (atom 0))

(defn is [x]
  (if x
    (swap! asserts inc)))

(.log js/console "starting tests...")

(let [fb (buff/fixed-buffer 2)]
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



(let [fb (buff/dropping-buffer 2)]
  (is (= 0 (count fb)))

  (add! fb :1)
  (is (= 1 (count fb)))

  (add! fb :2)
  (is (= 2 (count fb)))

  (is (not (full? fb)))
  #_(is (not (throws? (add! fb :3))))
  (is (= 2 (count fb)))

  (is (= :1 (remove! fb)))
  (is (not (full? fb)))

  (is (= 1 (count fb)))
  (is (= :2 (remove! fb)))

  (is (= 0 (count fb)))
  #_(is (throws? (remove! fb))))



(let [fb (buff/sliding-buffer 2)]
  (is (= 0 (count fb)))

  (add! fb :1)
  (is (= 1 (count fb)))

  (add! fb :2)
  (is (= 2 (count fb)))

  (is (not (full? fb)))
  #_(is (not (throws? (add! fb :3))))
  (is (= 2 (count fb)))
  
  (is (= :2 (remove! fb)))
  (is (not (full? fb)))

  (is (= 1 (count fb)))
  (is (= :3 (remove! fb)))

  (is (= 0 (count fb)))
  #_(is (throws? (remove! fb))))


(.log js/console (str  "..done " @asserts " asserts"))
