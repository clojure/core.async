(ns cljs.core.async.impl.ioc-helpers
  (:require [cljs.core.async.impl.protocols :as impl])
  (:require-macros [cljs.core.async.impl.ioc-macros :as ioc]))

(def ^:const FN-IDX 0)
(def ^:const STATE-IDX 1)
(def ^:const VALUE-IDX 2)
(def ^:const ACTION-IDX 3)
(def ^:const BINDINGS-IDX 4)
(def ^:const USER-START-IDX 5)

(defn- fn-handler
  [f]
  (reify
   impl/Handler
   (active? [_] true)
   (lock-id [_] 0)
   (commit [_] f)))

(defn finished?
  "Returns true if the machine is in a finished state"
  [^objects state-array]
  (identical? (aget state-array STATE-IDX) :finished))


(defn runner-wrapper
  "Simple wrapper that runs the state machine to completion"
  [f]
  (loop [state (f)]
    (if (cljs.core.async.impl.ioc-helpers/finished? state)
      (aget ^objects state VALUE-IDX)
      (recur (f state)))))


(defn async-chan-wrapper
  "State machine wrapper that uses the async library."
  ([state]
     (let [state ((aget state FN-IDX) state)
           _ (assert state)
           value (aget state VALUE-IDX)]
       (case (aget state ACTION-IDX)
         :take!
         (when-let [cb (impl/take! value (fn-handler
                                          (fn [x]
                                            (->> x
                                                 (ioc/aset-all! state VALUE-IDX)
                                                 async-chan-wrapper))))]
           (recur (ioc/aset-all! state VALUE-IDX @cb)))
         :put!
         (let [[chan value] value]
           (when-let [cb (impl/put! chan value (fn-handler (fn []
                                                             (->> nil
                                                                  (ioc/aset-all! state VALUE-IDX)
                                                                  async-chan-wrapper))))]
             (recur (ioc/aset-all! state VALUE-IDX @cb))))
         
         :return
         (let [c (aget state USER-START-IDX)]
           (when-not (nil? value)
             (impl/put! c value (fn-handler (fn [] nil))))
           (impl/close! c)
           c)

                                        ; Default case, return nil
         nil))))


