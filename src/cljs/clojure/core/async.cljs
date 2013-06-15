(ns cljs.core.async
    (:require [cljs.core.async.impl.protocols :as impl]
              [cljs.core.async.impl.channels :as channels]
              [cljs.core.async.impl.buffers :as buffers]))



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

(defn put!
  "Asynchronously puts a val into port, calling fn0 when complete. nil
   values are not allowed. Will throw if closed. If
   on-caller? (default true) is true, and the put is immediately
   accepted, will call fn0 on calling thread.  Returns nil."
  ([port val fn0] (put! port val fn0 true))
  ([port val fn0 on-caller?]
     (let [ret (impl/put! port val (fn-handler fn0))]
       (when ret
         (if on-caller?
           (fn0)
           (dispatch/run fn0)))
       nil)))
