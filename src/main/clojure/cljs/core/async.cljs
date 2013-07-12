(ns cljs.core.async
    (:require [cljs.core.async.impl.protocols :as impl]
              [cljs.core.async.impl.channels :as channels]
              [cljs.core.async.impl.buffers :as buffers]
              [cljs.core.async.impl.timers :as timers]
              [cljs.core.async.impl.ioc-helpers :as helpers]))



(defn- fn-handler [f]
  (reify
    impl/Handler
    (active? [_] true)
    (commit [_] f)))

(defn buffer
  "Returns a fixed buffer of size n. When full, puts will block/park."
  [n]
  (buffers/fixed-buffer n))

(defn dropping-buffer
  "Returns a buffer of size n. When full, puts will complete but
  val will be dropped (no transfer)."
  [n]
  (buffers/dropping-buffer n))

(defn sliding-buffer
  "Returns a buffer of size n. When full, puts will complete, and be
  buffered, but oldest elements in buffer will be dropped (not
  transferred)."
  [n]
  (buffers/sliding-buffer n))

(defn chan
  "Creates a channel with an optional buffer. If buf-or-n is a number,
  will create and use a fixed buffer of that size."
  ([] (chan nil))
  ([buf-or-n] (channels/chan (if (number? buf-or-n) (buffer buf-or-n) buf-or-n))))

(defn <port
  "Returns a receive-only port wrapping the given channel."
  [channel]
  (channels/<port channel))

(defn >port
  "Returns a send-only port wrapping the given channel."
  [channel]
  (channels/>port channel))

(defn timeout
  "Returns a channel that will close after msecs"
  [msecs]
  (timers/timeout msecs))

(defn take!
  "Asynchronously takes a val from port, passing to fn1. Will pass nil
   if closed. If on-caller? (default true) is true, and value is
   immediately available, will call fn1 on calling thread.
   Returns nil."
  ([port fn1] (take! port fn1 true))
  ([port fn1 on-caller?]
     (let [ret (impl/take! port (fn-handler fn1))]
       (when ret
         (let [val @ret]
           (if on-caller?
             (fn1 val)
             (dispatch/run #(fn1 val)))))
       nil)))

(defn- nop [])

(defn put!
  "Asynchronously puts a val into port, calling fn0 (if supplied) when
   complete. nil values are not allowed. Will throw if closed. If
   on-caller? (default true) is true, and the put is immediately
   accepted, will call fn0 on calling thread.  Returns nil."
  ([port val] (put! port val nop))
  ([port val fn0] (put! port val fn0 true))
  ([port val fn0 on-caller?]
     (let [ret (impl/put! port val (fn-handler fn0))]
       (when (and ret (not= fn0 nop))
         (if on-caller?
           (fn0)
           (dispatch/run fn0)))
       nil)))

(defn close!
  ([port]
     (impl/close! port)))


(defn- random-array
  [n]
  (let [a (make-array n)]
    (dotimes [x n]
      (aset a x 0))
    (loop [i 1]
      (if (= i n)
        a
        (do
          (let [j (rand-int i)]
            (aset a i (aget a j))
            (aset a j i)
            (recur (inc i))))))))

(defn- alt-flag []
  (let [flag (atom true)]
    (reify
      impl/Handler
      (active? [_] @flag)
      (commit [_]
        (reset! flag nil)
        true))))

(defn- alt-handler [flag cb]
  (reify
    impl/Handler
    (active? [_] (impl/active? flag))
    (commit [_]
      (impl/commit flag)
      cb)))

(defn do-alts
  "returns derefable [val port] if immediate, nil if enqueued"
  [fret ports opts]
  (let [flag (alt-flag)
        n (count ports)
        idxs (random-array n)
        priority (:priority opts)
        ret
        (loop [i 0]
          (when (< i n)
            (let [idx (if priority i (aget idxs i))
                  port (nth ports idx)
                  wport (when (vector? port) (port 0))
                  vbox (if wport
                         (let [val (port 1)]
                           (impl/put! wport val (alt-handler flag #(fret [nil wport]))))
                         (impl/take! port (alt-handler flag #(fret [% port]))))]
              (if vbox
                (channels/box [@vbox (or wport port)])
                (recur (inc i))))))]
    (or
     ret
     (when (contains? opts :default)
       (when-let [got (and (impl/active? flag) (impl/commit flag))]
         (channels/box [(:default opts) :default]))))))



