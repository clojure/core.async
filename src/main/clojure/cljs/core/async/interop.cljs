(ns cljs.core.async.interop
  (:require [cljs.core.async :as async]))

(defn p->c
  "Puts the promise resolution into a promise-chan and returns it.
   The value of a rejected promise will be wrapped in a instance of
   ExceptionInfo, acessible via ex-cause."
  [p]
  (let [c (async/promise-chan)]
    (.then p
           (fn [res]
             (if (nil? res)
               (async/close! c)
               (async/put! c res)))
           (fn [err]
             (async/put! c
                         (ex-info "Promise error"
                                  {:error :promise-error}
                                  err))))
    c))
