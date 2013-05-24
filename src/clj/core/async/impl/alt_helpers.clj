(ns core.async.impl.alt-helpers
  (:require [core.async.impl.protocols :as impl])
  (:import [core.async Mutex ThreadLocalRandom]))


(defonce ^:private ^java.util.concurrent.atomic.AtomicLong id-gen (java.util.concurrent.atomic.AtomicLong.))


(defn- mutex []
  (let [m (Mutex.)]
    (reify
     impl/Locking
     (lock [_] (.lock m))
     (unlock [_] (.unlock m)))))



(defn random-array
  [n]
  (let [rand (ThreadLocalRandom/current)
        a (int-array n)]
    (loop [i 1]
      (if (= i n)
        a
        (do
          (let [j (.nextInt rand (inc i))]
            (aset a i (aget a j))
            (aset a j i)
            (recur (inc i))))))))


(defn alt-flag []
  (let [m (mutex)
        flag (atom true)
        id (.incrementAndGet id-gen)]
    (reify
     impl/Locking
     (lock [_] (impl/lock m))
     (unlock [_] (impl/unlock m))

     impl/Handler
     (active? [_] @flag)
     (lock-id [_] id)
     (commit [_]
             (reset! flag nil)
             true))))

(defn alt-handler [flag cb]
  (reify
     impl/Locking
     (lock [_] (impl/lock flag))
     (unlock [_] (impl/unlock flag))

     impl/Handler
     (active? [_] (impl/active? flag))
     (lock-id [_] (impl/lock-id flag))
     (commit [_]
             (impl/commit flag)
             cb)))
