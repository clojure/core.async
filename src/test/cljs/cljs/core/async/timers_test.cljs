;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns cljs.core.async.timers-test
  (:require [cljs.core.async.impl.timers :as timers]
            [cljs.test :refer-macros [deftest testing is are]]))

(deftest skip-list-tests
  (testing "accessing empty skip list"
    (is (nil? (.ceilingEntry (timers/skip-list) 1000)))
    (is (nil? (.floorEntry (timers/skip-list) 1000))))
  (testing "accessing singleton skip list"
    (let [skip-list (doto (timers/skip-list)
                      (.put 1000 :a))]
      (are [entry k] (= entry (seq (.ceilingEntry skip-list k)))
        [1000 :a] 500
        [1000 :a] 1000
        nil 1500)
      (are [entry k] (= entry (seq (.floorEntry skip-list k)))
        nil 500
        [1000 :a] 1000
        [1000 :a] 1500)))
  (testing "accessing multi-entry skip list"
    (let [skip-list (doto (timers/skip-list)
                      (.put 1000 :a)
                      (.put 2000 :b)
                      (.put 3000 :c)
                      (.put 4000 :d)
                      (.put 5000 :e)
                      (.put 6000 :f))]
      (are [entry k] (= entry (seq (.ceilingEntry skip-list k)))
        [1000 :a] 500
        [1000 :a] 1000
        [2000 :b] 1500
        [2000 :b] 2000
        [3000 :c] 2500
        [3000 :c] 3000
        [4000 :d] 3500
        [4000 :d] 4000
        [5000 :e] 4500
        [5000 :e] 5000
        [6000 :f] 5500
        [6000 :f] 6000
        nil 6500)
      (are [entry k] (= entry (seq (.floorEntry skip-list k)))
        nil 500
        [1000 :a] 1000
        [1000 :a] 1500
        [2000 :b] 2000
        [2000 :b] 2500
        [3000 :c] 3000
        [3000 :c] 3500
        [4000 :d] 4000
        [4000 :d] 4500
        [5000 :e] 5000
        [5000 :e] 5500
        [6000 :f] 6000
        [6000 :f] 6500)))
  (testing "removing from singleton skip list"
    (let [skip-list (doto (timers/skip-list)
                      (.put 1000 :a)
                      (.remove 1000))]
      (are [entry k] (= entry (seq (.ceilingEntry skip-list k)))
        nil 500
        nil 1000
        nil 1500)
      (are [entry k] (= entry (seq (.floorEntry skip-list k)))
        nil 500
        nil 1000
        nil 1500)))
  (testing "removing from multi-entry skip list"
    (let [skip-list (doto (timers/skip-list)
                      (.put 1000 :a)
                      (.put 2000 :b)
                      (.put 3000 :c)
                      (.put 4000 :d)
                      (.remove 2000)
                      (.put 5000 :e)
                      (.put 6000 :f)
                      (.remove 4000))]
      (are [entry k] (= entry (seq (.ceilingEntry skip-list k)))
        [1000 :a] 500
        [1000 :a] 1000
        [3000 :c] 1500
        [3000 :c] 2000
        [3000 :c] 2500
        [3000 :c] 3000
        [5000 :e] 3500
        [5000 :e] 4000
        [5000 :e] 4500
        [5000 :e] 5000
        [6000 :f] 5500
        [6000 :f] 6000
        nil 6500)
      (are [entry k] (= entry (seq (.floorEntry skip-list k)))
        nil 500
        [1000 :a] 1000
        [1000 :a] 1500
        [1000 :a] 2000
        [1000 :a] 2500
        [3000 :c] 3000
        [3000 :c] 3500
        [3000 :c] 4000
        [3000 :c] 4500
        [5000 :e] 5000
        [5000 :e] 5500
        [6000 :f] 6000
        [6000 :f] 6500))))
