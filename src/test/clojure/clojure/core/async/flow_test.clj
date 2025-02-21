;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns clojure.core.async.flow-test
  (:require [clojure.test :refer :all]
            [clojure.core.async :as a]
            [clojure.core.async.flow :as flow]
            [clojure.core.async.flow.spi :as fspi]))

(deftest test-step1)

;; Test that a proc's describe returns the proc fn's description
(deftest test-proc-description-from-lift
  (let [step-fn (flow/lift1->step identity)
        proc (flow/process step-fn)]
    (is (some? (step-fn)))
    (is (= (step-fn) (fspi/describe proc)))))

(deftest test-proc-description-from-lift
  (let [step-fn (flow/lift1->step identity)
        proc (flow/process step-fn)]
    (is (some? (step-fn)))
    (is (= (step-fn) (fspi/describe proc)))))



(comment
  (def f (flow/lift1->step identity))
  (f)
  (fspi/describe (flow/process f))

  (test-proc-description)
  )