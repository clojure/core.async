;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns cljs.core.async.interop
  (:require [cljs.core.async :as async]))

(defn p->c
  "EXPERIMENTAL: Puts the promise resolution into a promise-chan and returns it.
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
