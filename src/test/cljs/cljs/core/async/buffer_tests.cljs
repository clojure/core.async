(ns cljs.core.async.buffer-tests
  (:require-macros [cljs.core.async.macros :as m :refer [go]])
  (:require [cljs.core.async
             :refer [unblocking-buffer? buffer dropping-buffer sliding-buffer
                     put! take! chan close!]]
            [cljs.core.async.impl.dispatch :as dispatch]
            [cljs.core.async.impl.buffers :as buff :refer [promise-buffer]]
            [cljs.core.async.impl.protocols
             :refer [full? add! remove! close-buf!]]
            [cljs.core.async.test-helpers :refer-macros [throws?]]
            [cljs.test :refer-macros [deftest testing is]]))

(deftest unblocking-buffer-tests
  (testing "buffers"
    (is (not (unblocking-buffer? (buffer 1))))
    (is (unblocking-buffer? (dropping-buffer 1)))
    (is (unblocking-buffer? (sliding-buffer 1)))))

(deftest buffer-tests
  (testing "fixed-buffer"
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
      #_(is (helpers/throws? (remove! fb)))))

  (testing "dropping-buffer"
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
      #_(is (throws? (remove! fb)))))

  (testing "sliding-buffer"
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
      #_(is (throws? (remove! fb))))))

(deftest promise-buffer-tests
  (let [pb (promise-buffer)]
    (is (= 0 (count pb)))

    (add! pb :1)
    (is (= 1 (count pb)))

    (add! pb :2)
    (is (= 1 (count pb)))

    (is (not (full? pb)))
    (is (not (throws? (add! pb :3))))
    (is (= 1 (count pb)))

    (is (= :1 (remove! pb)))
    (is (not (full? pb)))

    (is (= 1 (count pb)))
    (is (= :1 (remove! pb)))

    (is (= nil (close-buf! pb)))
    (is (= nil (remove! pb)))))
