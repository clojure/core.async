(ns clojure.core.async.mutex-test
  (:use clojure.test)
  (:import (clojure.core.async Mutex)))

(deftest mutex-test
  (let [lock (Mutex.)]
    (.lock lock)
    (try
      ;; do stuff
      (finally
       (.unlock lock)))))
