(ns core.async.mutex-test
  (:use clojure.test)
  (:import (core.async.mutex Mutex)))

(deftest mutex-test
  (let [lock (Mutex.)]
    (.lock lock)
    (try
      ;; do stuff
      (finally
       (.unlock lock)))))
