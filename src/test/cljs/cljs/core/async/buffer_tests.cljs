;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns cljs.core.async.buffer-tests
  (:require [cljs.core.async
             :refer [unblocking-buffer? buffer dropping-buffer sliding-buffer]]
            [cljs.core.async.impl.buffers :refer [promise-buffer]]
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

      ; already overflown
      (add! fb :3)
      (is (= 3 (count fb)))
      (is (full? fb))

      (is (= :1 (remove! fb)))
      (is (= 2 (count fb)))
      (is (full? fb))

      (is (= :2 (remove! fb)))
      (is (= 1 (count fb)))
      (is (not (full? fb)))

      (is (= :3 (remove! fb)))
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
    (is (= :1 (remove! pb)))))
