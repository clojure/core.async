;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns cljs.core.async.interop-tests
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [<!]]
            [cljs.core.async.interop :refer [p->c] :refer-macros [<p!]]
            [cljs.test :refer-macros [deftest is async]]))

(deftest interop-resolve
  (async done
         (go
           (is (= (<p! (js/Promise.resolve 42)) 42))
           (done))))

(deftest interop-resolve-nil
  (async done
         (go
           (is (= (<p! (js/Promise.resolve)) nil))
           (done))))

(deftest interop-multiple-resolve
  (async done
         (go
           (let [total (atom 0)]
             (swap! total + (<p! (js/Promise.resolve 1)))
             (swap! total + (<p! (js/Promise.resolve 2)))
             (swap! total + (<p! (js/Promise.resolve 3)))
             (is (= @total 6))
             (done)))))

(deftest interop-catch
  (async done
         (let [err (js/Error. "Rejected")]
           (go
             (is (= err
                    (ex-cause
                     (is (thrown?
                          js/Error
                          (<p! (js/Promise.reject err)))))))
             (done)))))

(deftest interop-catch-non-error
  (async done
         (let [err "Rejected"]
           (go
             (is (= err
                    (ex-cause
                     (is (thrown?
                          js/Error
                          (<p! (js/Promise.reject err)))))))
             (done)))))

(deftest interop-nested
  (async done
         (go
           (let [total (atom 0)
                 first-res (<p! (js/Promise.resolve 1))
                 second-res (<p! (js/Promise.resolve 2))]
             (swap! total + (<p! (js/Promise.resolve 3)))
             (swap! total + (<p! (js/Promise.resolve 5)))
             (swap! total + first-res)
             (swap! total + second-res)
             (is (= @total 11))
             (done)))))

(deftest interop-multiple-consumer
  (async done
         (go
          (let [p (js/Promise.resolve 42)]
            (is (= (<p! p) 42))
            (is (= (<p! p) 42))
            (done)))))

(deftest interop-p->c-semantics
  (async done
         (go
           (let [c (p->c (js/Promise.resolve 42))]
             (is (= (<! c) 42))
             (is (= (<! c) 42))
             (done)))))
