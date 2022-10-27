;   Copyright (c) Rich Hickey. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns ^{:skip-wiki true}
    clojure.core.async.impl.runtime
  (:require [clojure.core.async.impl.protocols :as impl])
  (:import [java.util.concurrent.locks Lock]
           [java.util.concurrent.atomic AtomicReferenceArray]))

(def ^:const FN-IDX 0)
(def ^:const STATE-IDX 1)
(def ^:const VALUE-IDX 2)
(def ^:const BINDINGS-IDX 3)
(def ^:const EXCEPTION-FRAMES 4)
(def ^:const CURRENT-EXCEPTION 5)
(def ^:const USER-START-IDX 6)

(defn aset-object [^AtomicReferenceArray arr idx ^Object o]
  (.set arr idx o))

(defn aget-object [^AtomicReferenceArray arr idx]
  (.get arr idx))

(defmacro aset-all!
  [arr & more]
  (assert (even? (count more)) "Must give an even number of args to aset-all!")
  (let [bindings (partition 2 more)
        arr-sym (gensym "statearr-")]
    `(let [~arr-sym ~arr]
       ~@(map
          (fn [[idx val]]
            `(aset-object ~arr-sym ~idx ~val))
          bindings)
       ~arr-sym)))

;; unused fn
(defn finished?
  "Returns true if the machine is in a finished state"
  [state-array]
  (identical? (aget-object state-array STATE-IDX) ::finished))


(defn- fn-handler
  [f]
  (reify
   Lock
   (lock [_])
   (unlock [_])

   impl/Handler
   (active? [_] true)
   (blockable? [_] true)
   (lock-id [_] 0)
   (commit [_] f)))

(defn run-state-machine [state]
  ((aget-object state FN-IDX) state))

(defn run-state-machine-wrapped [state]
  (try
    (run-state-machine state)
    (catch Throwable ex
      (impl/close! (aget-object state USER-START-IDX))
      (throw ex))))

(defn take! [state blk c]
  (if-let [cb (impl/take! c (fn-handler
                                   (fn [x]
                                     (aset-all! state VALUE-IDX x STATE-IDX blk)
                                     (run-state-machine-wrapped state))))]
    (do (aset-all! state VALUE-IDX @cb STATE-IDX blk)
        :recur)
    nil))

(defn put! [state blk c val]
  (if-let [cb (impl/put! c val (fn-handler (fn [ret-val]
                                             (aset-all! state VALUE-IDX ret-val STATE-IDX blk)
                                             (run-state-machine-wrapped state))))]
    (do (aset-all! state VALUE-IDX @cb STATE-IDX blk)
        :recur)
    nil))

(defn return-chan [state value]
  (let [c (aget-object state USER-START-IDX)]
           (when-not (nil? value)
             (impl/put! c value (fn-handler (fn [] nil))))
           (impl/close! c)
           c))

(def async-custom-terminators
  {'clojure.core.async/<! `take!
   'clojure.core.async/>! `put!
   'clojure.core.async/alts! 'clojure.core.async/ioc-alts!
   :Return `return-chan})
