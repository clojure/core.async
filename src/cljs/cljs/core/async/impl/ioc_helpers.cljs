(ns cljs.core.async.impl.ioc-helpers)

(def ^:const FN-IDX 0)
(def ^:const STATE-IDX 1)
(def ^:const VALUE-IDX 2)
(def ^:const ACTION-IDX 3)
(def ^:const BINDINGS-IDX 4)
(def ^:const USER-START-IDX 5)


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


