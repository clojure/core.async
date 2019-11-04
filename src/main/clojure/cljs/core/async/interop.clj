(ns cljs.core.async.interop)

(defmacro <p!
  "Takes the value of a promise resolution. The value of a rejected promise 
  will be thrown wrapped in a instance of ExceptionInfo, acessible via ex-cause."
  [exp]
  `(let [v# (cljs.core.async/<! (cljs.core.async.interop/p->c ~exp))]
     (if (and
          (instance? cljs.core/ExceptionInfo v#)
          (= (:error (ex-data v#)) :promise-error))
       (throw v#)
       v#)))
