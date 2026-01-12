;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns clojure.core.async.flow-test
  (:require [clojure.test :refer :all]
            [clojure.core.async.flow :as flow]))

(deftest test-futurize
  (testing ""
    (let [es (reify java.util.concurrent.ExecutorService
               (^java.util.concurrent.Future submit [_ ^Callable f]
                (future-call (comp vector f))))]
      (is (= 16 @((flow/futurize #(* % %) {:exec :mixed}) 4)))
      (is (= 16 @((flow/futurize #(* % %)) 4)))
      (is (= [16] @((flow/futurize #(* % %) {:exec es}) 4))))))

