;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns clojure.core.async
  (:require [clojure.core.async.impl.protocols :as impl]
            [clojure.core.async.impl.channels :as channels]
            [clojure.core.async.impl.buffers :as buffers]
            [clojure.core.async.impl.timers :as timers]
            [clojure.core.async.impl.dispatch :as dispatch]
            [clojure.core.async.impl.ioc-macros :as ioc]
            [clojure.core.async.impl.ioc-alt]
            [clojure.core.async.impl.mutex :as mutex]
            )
  (:import [clojure.core.async ThreadLocalRandom]
           [java.util.concurrent.locks Lock]
           [java.util.concurrent Executors Executor ThreadFactory]))

(set! *warn-on-reflection* true)

(defn- fn-handler
  [f]
  (reify
   Lock
   (lock [_])
   (unlock [_])
   
   impl/Handler
   (active? [_] true)
   (lock-id [_] 0)
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

(defn timeout
  "Returns a channel that will close after msecs"
  [msecs]
  (timers/timeout msecs))

(defn <!!
  "takes a val from port. Will return nil if closed. Will block
  if nothing is available."
  [port]
  (let [p (promise)
        ret (impl/take! port (fn-handler (fn [v] (deliver p v))))]
    (if ret
      @ret
      (deref p))))

(defn <!
  "takes a val from port. Must be called inside a (go ...) block. Will
  return nil if closed. Will park if nothing is available."
  [port]
  (assert nil "<! used not in (go ...) block"))

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

(defn >!!
  "puts a val into port. nil values are not allowed. Will block if no
  buffer space is available. Returns nil."
  [port val]
  (let [p (promise)
        ret (impl/put! port val (fn-handler (fn [] (deliver p nil))))]
    (if ret
      @ret
      (deref p))))

(defn >!
  "puts a val into port. nil values are not allowed. Must be called
  inside a (go ...) block. Will park if no buffer space is available."
  [port val]
  (assert nil ">! used not in (go ...) block"))

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

(defn close!
  "Closes a channel. The channel will no longer accept any puts (they
  will be ignored). Data in the channel remains available for taking, until
  exhausted, after which takes will return nil. If there are any
  pending takes, they will be dispatched with nil. Closing a closed
  channel is a no-op. Returns nil."
  [chan]
  (impl/close! chan))

(defonce ^:private ^java.util.concurrent.atomic.AtomicLong id-gen (java.util.concurrent.atomic.AtomicLong.))

(defn- random-array
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

(defn- alt-flag []
  (let [^Lock m (mutex/mutex)
        flag (atom true)
        id (.incrementAndGet id-gen)]
    (reify
     Lock
     (lock [_] (.lock m))
     (unlock [_] (.unlock m))

     impl/Handler
     (active? [_] @flag)
     (lock-id [_] id)
     (commit [_]
             (reset! flag nil)
             true))))

(defn- alt-handler [^Lock flag cb]
  (reify
     Lock
     (lock [_] (.lock flag))
     (unlock [_] (.unlock flag))

     impl/Handler
     (active? [_] (impl/active? flag))
     (lock-id [_] (impl/lock-id flag))
     (commit [_]
             (impl/commit flag)
             cb)))

(defn do-alts
  "returns derefable [val port] if immediate, nil if enqueued"
  [fret ports opts]
  (let [flag (alt-flag)
        n (count ports)
        ^ints idxs (random-array n)
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
       (.lock ^Lock flag)
       (let [got (and (impl/active? flag) (impl/commit flag))]
         (.unlock ^Lock flag)
         (when got
           (channels/box [(:default opts) :default])))))))

(defn alts!!
  "Like alts!, except takes will be made as if by <!!, and puts will
  be made as if by >!!, will block until completed, and not intended
  for use in (go ...) blocks."
  [ports & {:as opts}]
  (let [p (promise)
        ret (do-alts (partial deliver p) ports opts)]
    (if ret
      @ret
      (deref p))))

(defn alts!
  "Completes at most one of several channel operations. Must be called
  inside a (go ...) block. ports is a set of channel endpoints, which
  can be either a channel to take from or a vector of
  [channel-to-put-to val-to-put], in any combination. Takes will be
  made as if by <!, and puts will be made as if by >!. Unless
  the :priority option is true, if more than one port operation is
  ready a non-deterministic choice will be made. If no operation is
  ready and a :default value is supplied, [default-val :default] will
  be returned, otherwise alts! will park until the first operation to
  become ready completes. Returns [val port] of the completed
  operation, where val is the value taken for takes, and nil for puts.

  opts are passed as :key val ... Supported options:

  :default val - the value to use if none of the operations are immediately ready
  :priority true - (default nil) when true, the operations will be tried in order.

  Note: there is no guarantee that the port exps or val exprs will be
  used, nor in what order should they be, so they should not be
  depended upon for side effects."

  [ports & {:as opts}]
  (assert nil "alts! used not in (go ...) block"))

(defn do-alt [alts clauses]
  (assert (even? (count clauses)) "unbalanced clauses")
  (let [clauses (partition 2 clauses)
        opt? #(keyword? (first %)) 
        opts (filter opt? clauses)
        clauses (remove opt? clauses)
        [clauses bindings]
        (reduce
         (fn [[clauses bindings] [ports expr]]
           (let [ports (if (vector? ports) ports [ports])
                 [ports bindings]
                 (reduce
                  (fn [[ports bindings] port]
                    (if (vector? port)
                      (let [[port val] port
                            gp (gensym)
                            gv (gensym)]
                        [(conj ports [gp gv]) (conj bindings [gp port] [gv val])])
                      (let [gp (gensym)]
                        [(conj ports gp) (conj bindings [gp port])])))
                  [[] bindings] ports)]
             [(conj clauses [ports expr]) bindings]))
         [[] []] clauses)
        gch (gensym "ch")
        gret (gensym "ret")]
    `(let [~@(mapcat identity bindings)
           [val# ~gch :as ~gret] (~alts [~@(apply concat (map first clauses))] ~@(apply concat opts))]
       (cond
        ~@(mapcat (fn [[ports expr]]
                    [`(or ~@(map (fn [port]
                                   `(= ~gch ~(if (vector? port) (first port) port)))
                                 ports))
                     (if (and (seq? expr) (vector? (first expr)))
                       `(let [~(first expr) ~gret] ~@(rest expr)) 
                       expr)])
                  clauses)
        (= ~gch :default) val#))))

(defmacro alt!!
  "Like alt!, except as if by alts!!, will block until completed, and
  not intended for use in (go ...) blocks."

  [& clauses]
  (do-alt `alts!! clauses))

(defmacro alt!
  "Makes a single choice between one of several channel operations,
  as if by alts!, returning the value of the result expr corresponding
  to the operation completed. Must be called inside a (go ...) block.

  Each clause takes the form of:

  channel-op[s] result-expr

  where channel-ops is one of:

  take-port - a single port to take
  [take-port | [put-port put-val] ...] - a vector of ports as per alts!
  :default | :priority - an option for alts!

  and result-expr is either a list beginning with a vector, whereupon that
  vector will be treated as a binding for the [val port] return of the
  operation, else any other expression.

  (alt!
    [c t] ([val ch] (foo ch val))
    x ([v] v)
    [[out val]] :wrote
    :default 42)

  Each option may appear at most once. The choice and parking
  characteristics are those of alts!."

  [& clauses]
  (do-alt `alts! clauses))

(defmacro go
  "Asynchronously executes the body, returning immediately to the
  calling thread. Additionally, any visible calls to <!, >! and alt!/alts!
  channel operations within the body will block (if necessary) by
  'parking' the calling thread rather than tying up an OS thread (or
  the only JS thread when in ClojureScript). Upon completion of the
  operation, the body will be resumed.

  Returns a channel which will receive the result of the body when
  completed"
  [& body]
  (binding [ioc/*symbol-translations* '{alts! clojure.core.async.impl.ioc-alt/alts!
                                        clojure.core.async/alts! clojure.core.async.impl.ioc-alt/alts!
                                        case case}
            ioc/*local-env* &env]
    `(let [c# (chan 1)]
       (dispatch/run
        (fn []
          (let [f# ~(ioc/state-machine body 1)
                state# (-> (f#)
                           (ioc/aset-all! ioc/USER-START-IDX c#))]
            ((ioc/async-chan-wrapper state#)))))
       c#)))

(defonce ^:private ^Executor thread-macro-executor
  (let [counter (atom 0)
        name-format "async-thread-macro-%d"]
    (Executors/newCachedThreadPool
     (reify
       ThreadFactory
       (newThread [this runnable]
         (doto (Thread. runnable)
           (.setName (format name-format (swap! counter inc)))))))))

(defn thread-call
  "Executes f in another thread, returning immediately to the calling
  thread. Returns a channel which will receive the result of calling
  f when completed."
  [f]
  (let [c (chan 1)]
    (.execute thread-macro-executor
              (fn []
                (let [ret (try (f)
                               (catch Throwable t
                                 nil))]
                  (if ret
                    (put! c ret #(close! c))
                    (close! c)))))
    c))

(defmacro thread
  "Synchronously executes the body in another thread, returning
  immediately to the calling thread. Returns a channel which will
  receive the result of the body when completed."
  [& body]
  `(thread-call (fn [] ~@body)))
