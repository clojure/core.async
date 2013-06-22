(ns cljs.core.async.test-helpers
  (:require [cljs.core.async.impl.ioc-macros :as ioc]))

(defmacro runner
  "Creates a runner block. The code inside the body of this macro will be translated
  into a state machine. At run time the body will be run as normal. This transform is
  only really useful for testing."
  [& body]
  (binding [ioc/*symbol-translations* '{pause clojure.core.async.ioc-macros/pause
                                        case case
                                        try try}
            ioc/*local-env* &env]
    `(cljs.core.async.impl.ioc-helpers/runner-wrapper ~(ioc/state-machine body 0))))

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
