(ns cljs.core.async.impl.ioc-helpers
  (:require [cljs.core.async.impl.protocols :as impl])
  (:require-macros [cljs.core.async.impl.ioc-macros :as ioc]))

(def ^:const FN-IDX 0)
(def ^:const STATE-IDX 1)
(def ^:const VALUE-IDX 2)
(def ^:const BINDINGS-IDX 3)
(def ^:const USER-START-IDX 4)

(defn aset-object [arr idx o]
  (aget arr idx o))

(defn aget-object [arr idx]
  (aget arr idx))


(defn finished?
  "Returns true if the machine is in a finished state"
  [state-array]
  (identical? (aget state-array STATE-IDX) :finished))

(defn- fn-handler
  [f]
  (reify
   impl/Handler
   (active? [_] true)
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
  (if-let [cb (impl/put! c val (fn-handler (fn []
                                             (ioc/aset-all! state VALUE-IDX nil STATE-IDX blk)
                                             (run-state-machine-wrapped state))))]
    (do (ioc/aset-all! state VALUE-IDX @cb STATE-IDX blk)
        :recur)
    nil))

(defn ioc-alts! [state cont-block ports & {:as opts}]
  (ioc/aset-all! state STATE-IDX cont-block)
  (when-let [cb (cljs.core.async/do-alts
                  (fn [val]
                    (ioc/aset-all! state VALUE-IDX val)
                    (run-state-machine-wrapped state))
                  ports
                  opts)]
    (ioc/aset-all! state VALUE-IDX @cb)
    :recur))

(defn return-chan [state value]
  (let [^not-native c (aget state USER-START-IDX)]
           (when-not (nil? value)
             (impl/put! c value (fn-handler (fn [] nil))))
           (impl/close! c)
           c))


