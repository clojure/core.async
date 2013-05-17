;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns core.async.dispatch-test
  (:require [clojure.test :refer :all]
            [core.async.dispatch :as dispatch])
  (:import [java.util.concurrent ThreadFactory]))

(deftest test-counted-thread-factory
  (testing "Creates numbered threads"
    (let [^ThreadFactory factory (dispatch/counted-thread-factory "foo-%d" true)
          threads (repeatedly 3 #(.newThread factory (constantly nil)))]
      (is (= ["foo-1" "foo-2" "foo-3"] (map #(.getName ^Thread %) threads))))))

