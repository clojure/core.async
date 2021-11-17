;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns cljs.core.async.impl.ioc-helpers
  (:require [cljs.core.async.impl.protocols :as impl])
  (:require-macros [cljs.core.async.impl.ioc-macros :as ioc]))

(def ^:const FN-IDX 0)
(def ^:const STATE-IDX 1)
(def ^:const VALUE-IDX 2)
(def ^:const BINDINGS-IDX 3)
(def ^:const EXCEPTION-FRAMES 4)
(def ^:const CURRENT-EXCEPTION 5)
(def ^:const USER-START-IDX 6)

(defn aset-object [arr idx o]
  (aget arr idx o))

(defn aget-object [arr idx]
  (aget arr idx))


(defn finished?
  "Returns true if the machine is in a finished state"
  [state-array]
  (keyword-identical? (aget state-array STATE-IDX) :finished))

(defn- fn-handler
  [f]
  (reify
   impl/Handler
   (active? [_] true)
   (blockable? [_] true)
   (commit [_] f)))


(defn run-state-machine [state]
  ((aget-object state FN-IDX) state))

(defn run-state-machine-wrapped [state]
  (try
    (run-state-machine state)
    (catch js/Object ex
      (impl/close! ^not-native (aget-object state USER-START-IDX))
      (throw ex))))

(defn take! [state blk ^not-native c]
  (if-let [cb (impl/take! c (fn-handler
                                   (fn [x]
                                     (ioc/aset-all! state VALUE-IDX x STATE-IDX blk)
                                     (run-state-machine-wrapped state))))]
    (do (ioc/aset-all! state VALUE-IDX @cb STATE-IDX blk)
        :recur)
    nil))

(defn put! [state blk ^not-native c val]
  (if-let [cb (impl/put! c val (fn-handler (fn [ret-val]
                                             (ioc/aset-all! state VALUE-IDX ret-val STATE-IDX blk)
                                             (run-state-machine-wrapped state))))]
    (do (ioc/aset-all! state VALUE-IDX @cb STATE-IDX blk)
        :recur)
    nil))

(defn return-chan [state value]
  (let [^not-native c (aget state USER-START-IDX)]
           (when-not (nil? value)
             (impl/put! c value (fn-handler (fn [_] nil))))
           (impl/close! c)
           c))


