(ns cljs.core.async.test-helpers
  (:require [cljs.core.async.impl.ioc-macros :as ioc]))

(defmacro runner
  "Creates a runner block. The code inside the body of this macro will be translated
  into a state machine. At run time the body will be run as normal. This transform is
  only really useful for testing."
  [& body]
  (let [terminators {'pause 'cljs.core.async.runner-tests/pause}]
    `(let [state# (~(ioc/state-machine body 0 &env terminators))]
       (cljs.core.async.impl.ioc-helpers/run-state-machine state#)
       (assert (cljs.core.async.impl.ioc-helpers/finished? state#) "state did not return finished")
       (aget state# ~ioc/VALUE-IDX))))

(defmacro deftest
  [nm & body]
  `(do (.log js/console (str "Testing: " ~(str nm) "..."))
       ~@body))

(defmacro testing
  [nm & body]
    `(do (.log js/console (str "    " ~nm "..."))
       ~@body))

(defmacro is=
  [a b]
  `(let [a# ~a
         b# ~b]
     (assert (= a# b#) (str a# " != " b#))))

(defmacro is
  [a]
  `(assert ~a))
