;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns cljs.core.async.interop)

(defmacro <p!
  "EXPERIMENTAL: Takes the value of a promise resolution. The value of a rejected promise
  will be thrown wrapped in a instance of ExceptionInfo, acessible via ex-cause."
  [exp]
  `(let [v# (cljs.core.async/<! (cljs.core.async.interop/p->c ~exp))]
     (if (and
          (instance? cljs.core/ExceptionInfo v#)
          (= (:error (ex-data v#)) :promise-error))
       (throw v#)
       v#)))
