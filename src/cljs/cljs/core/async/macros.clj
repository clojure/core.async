(ns cljs.core.async.macros
  (:require [cljs.core.async.impl.ioc-macros :as ioc]))

(defmacro runner
  "Creates a runner block. The code inside the body of this macro will be translated
  into a state machine. At run time the body will be run as normal. This transform is
  only really useful for testing."
  [& body]
  (binding [ioc/*symbol-translations* '{pause clojure.core.async.ioc-macros/pause
                                        case case}]
    `(cljs.core.async/runner-wrapper ~(ioc/state-machine body 0))))


(defmacro go
  "Asynchronously executes the body, returning immediately to the
  calling thread. Additionally, any visible calls to <!, >! and alt!/alts!
  channel operations within the body will block (if necessary) by
  'parking' the calling thread rather than tying up an OS thread (or
  the only JS thread when in ClojureScript). Upon completion of the
  operation, the body will be resumed.

  Returns a channel which will receive the result of the body when
  completed"
  [& body]
  (binding [ioc/*symbol-translations* '{alts! alts!
                                        case case}
            ioc/*local-env* &env]
    `(let [c# (cljs.core.async/chan 1)]
       (cljs.core.async.impl.dispatch/run
        (fn []
          (let [f# ~(ioc/state-machine body 1)
                state# (-> (f#)
                           (ioc/aset-all! cljs.core.async.impl.ioc-helpers/USER-START-IDX c#))]
            (cljs.core.async.impl/ioc-helpers/async-chan-wrapper state#))))
       c#)))
