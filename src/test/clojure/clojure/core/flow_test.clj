(ns clojure.core.flow-test
  (:require [clojure.core.async.flow :as flow]
            [clojure.test :refer :all]))

(defn tap-n-drop [x] (tap> x) nil)

(deftest chan-opts-tests
  (testing ":chan-opts only specified on in side of connected pair"
    (is (thrown? clojure.lang.ExceptionInfo
                 (flow/create-flow
                  {:procs {:source {:proc (-> identity flow/lift1->step flow/process)
                                    :chan-opts {:out {:buf-or-n 11
                                                      :xform (map (fn [x] (str "Saw " x)))}}}
                           :sink {:proc (-> #'tap-n-drop flow/lift1->step flow/process)}}
                   :conns [[[:source :out] [:sink :in]]]})))
    (is (flow/create-flow
         {:procs {:source {:proc (-> identity flow/lift1->step flow/process)}
                  :sink {:proc (-> #'tap-n-drop flow/lift1->step flow/process)
                         :chan-opts {:in {:buf-or-n 11
                                          :xform (map (fn [x] (str "Saw " x)))}}}}
          :conns [[[:source :out] [:sink :in]]]}))))
