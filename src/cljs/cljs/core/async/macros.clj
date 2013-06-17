(ns cljs.core.async.macros
  (:require [cljs.core.async.impl.ioc-macros :as ioc]))


(defn runner-wrapper
  "Simple wrapper that runs the state machine to completion"
  [f]
  (loop [state (f)]
    (if (ioc/finished? state)
      (aget ^objects state ioc/VALUE-IDX)
      (recur (f state)))))

(defmacro runner
  "Creates a runner block. The code inside the body of this macro will be translated
  into a state machine. At run time the body will be run as normal. This transform is
  only really useful for testing."
  [& body]
  (binding [ioc/*symbol-translations* '{pause clojure.core.async.ioc-macros/pause
                                        case case}]
    `(cljs.core.async/runner-wrapper ~(ioc/state-machine body 0))))

