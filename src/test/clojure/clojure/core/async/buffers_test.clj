;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns clojure.core.async.buffers-test
  (:require [clojure.test :refer :all]
            [clojure.core.async.impl.buffers :refer :all]
            [clojure.core.async.impl.protocols :refer [full? add! remove! close-buf!]]))

(defmacro throws? [expr]
  `(try
     ~expr
     false
     (catch Throwable _# true)))

(deftest fixed-buffer-tests
  (let [fb (fixed-buffer 2)]
    (is (= 0 (count fb)))

    (add! fb :1)
    (is (= 1 (count fb)))

    (add! fb :2)
    (is (= 2 (count fb)))

    (is (= :1 (remove! fb)))
    (is (not (full? fb)))

    (is (= 1 (count fb)))
    (is (= :2 (remove! fb)))

    (is (= 0 (count fb)))
    (is (throws? (remove! fb)))))

(deftest dropping-buffer-tests
  (let [fb (dropping-buffer 2)]
    (is (= 0 (count fb)))

    (add! fb :1)
    (is (= 1 (count fb)))

    (add! fb :2)
    (is (= 2 (count fb)))

    (is (not (full? fb)))
    (is (not (throws? (add! fb :3))))
    (is (= 2 (count fb)))

    (is (= :1 (remove! fb)))
    (is (not (full? fb)))

    (is (= 1 (count fb)))
    (is (= :2 (remove! fb)))

    (is (= 0 (count fb)))
    (is (throws? (remove! fb)))))

(deftest sliding-buffer-tests
  (let [fb (sliding-buffer 2)]
    (is (= 0 (count fb)))

    (add! fb :1)
    (is (= 1 (count fb)))

    (add! fb :2)
    (is (= 2 (count fb)))

    (is (not (full? fb)))
    (is (not (throws? (add! fb :3))))
    (is (= 2 (count fb)))
    
    (is (= :2 (remove! fb)))
    (is (not (full? fb)))

    (is (= 1 (count fb)))
    (is (= :3 (remove! fb)))

    (is (= 0 (count fb)))
    (is (throws? (remove! fb)))))

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
