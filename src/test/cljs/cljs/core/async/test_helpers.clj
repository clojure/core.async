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

(defmacro assert-go-block-completes
  [nm & body]
  `(let [body-chan# (do ~@body)
         timeout# (fn [] (let [c# (cljs.core.async/chan)]
                           (cljs.core.async.macros/go
                             (cljs.core.async/<! (cljs.core.async/timeout 10000))
                             (cljs.core.async/>! c# ::timeout)
                             (cljs.core.async/close! c#))
                           c#))]
     (when (satisfies? cljs.core.async.impl.protocols.Channel body-chan#)
       (cljs.core.async.macros/go
         (let [[v# c#] (cljs.core.async/alts! [body-chan# (timeout#)] :priority true)]
           (assert (not= ::timeout v#)
                   (str "test timed out: " ~nm ))))
       true)))

(defmacro deftest
  [nm & body]
  `(do (.log js/console (str "Testing: " ~(str nm) "..."))
       (assert-go-block-completes ~(str nm) ~@body)))

(defmacro throws?
  [& exprs]
  `(try ~@exprs false
        (catch ~'js/Object e# true)))

(defmacro testing
  [nm & body]
    `(do (.log js/console (str "    " ~nm "..."))
         (assert-go-block-completes ~(str nm) ~@body)))

(defmacro is=
  [a b]
  `(let [a# ~a
         b# ~b]
     (assert (= a# b#) (str a# " != " b#))))

(defmacro is
  [a]
  `(assert ~a))

(defmacro locals-test []
  (if (get-in &env [:locals] 'x)
    :pass
    :fail))
