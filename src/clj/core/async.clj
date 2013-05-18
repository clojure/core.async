;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns core.async
  (:require [core.async.protocols :as proto]
            [core.async.dispatch :as dispatch]
            [core.async.ioc-macros :as ioc])
  (:import [core.async ThreadLocalRandom Mutex]))

(set! *warn-on-reflection* true)

(defn- fn-handler
  [f]
  (reify
   proto/Locking
   (lock [_])
   (unlock [_])
   
   proto/Handler
   (active? [_] true)
   (lock-id [_] 0)
   (commit [_] f)))

(defn- mutex []
  (let [m (Mutex.)]
    (reify
     proto/Locking
     (lock [_] (.lock m))
     (unlock [_] (.unlock m)))))

(defn <!
  "takes a val from port. Will return nil if closed. Will block/park
  if nothing is available. Can participate in alt"
  [port]
  (let [p (promise)
        cb (proto/take! port (fn-handler (fn [v] (deliver p v))))]
    (when cb (cb))
    (deref p)))

(defn take!
  "Asynchronously takes a val from port, passing to fn1. Will pass nil
   if closed. If on-caller? (default true) is true, and value is
   immediately available, will call fn1 on calling thread.
   Returns nil."
  ([port fn1] (take! port fn1 true))
  ([port fn1 on-caller?]
     (let [cb (proto/take! port (fn-handler fn1))]
       (when cb
         (if on-caller?
           (cb)
           (dispatch/run cb)))
       nil)))

(defn >!
  "puts a val into port. Will block/park if no buffer space is
  available. Returns nil. Can participate in alt"
  [port val]
  (let [p (promise)
        cb (proto/put! port val (fn-handler (fn [] (deliver p nil))))]
    (when cb (cb))
    (deref p)
    nil))

(defn put!
  "Asynchronously puts a val into port, calling fn0 when
   complete. Will throw if closed. If on-caller? (default true) is
   true, and the put is immediately accepted, will call fn0 on calling
   thread.  Returns nil."
  ([port val fn0] (put! port val fn0 true))
  ([port val fn0 on-caller?]
     (let [cb (proto/put! port val (fn-handler fn0))]
       (when cb
         (if on-caller?
           (cb)
           (dispatch/run cb)))
       nil)))

(defn close!
  "Closes a channel. The channel will no longer accept any puts (they
  will throw). Data in the channel remains avaiable for taking, until
  exhausted, after which takes will return nil. If there are any
  pending takes, they will be dispatched with nil. Closing a closed
  channel is a no-op. Returns nil."
  [chan]
  (proto/close! chan))

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

(defonce ^:private ^java.util.concurrent.atomic.AtomicLong id-gen (java.util.concurrent.atomic.AtomicLong.))

(defn- alt-flag []
  (let [m (Mutex.)
        flag (atom true)
        id (.incrementAndGet id-gen)]
    (reify
     proto/Locking
     (lock [_] (proto/lock m))
     (unlock [_] (proto/unlock m))

     proto/Handler
     (active? [_] @flag)
     (lock-id [_] id)
     (commit [_]
             (reset! flag nil)
             true))))

(defn- alt-handler [flag cb]
  (reify
     proto/Locking
     (lock [_] (proto/lock flag))
     (unlock [_] (proto/unlock flag))

     proto/Handler
     (active? [_] (proto/active? flag))
     (lock-id [_] (proto/lock-id flag))
     (commit [_]
             (proto/commit flag)
             cb)))

(defn do-alt [clauses]
  (assert (even? (count clauses)) "unbalanced clauses")
  (let [clauses (partition 2 clauses)
        default (first (filter #(= :default (first %)) clauses))
        clauses (remove #(= :default (first %)) clauses)]
    (assert (every? keyword? (map first clauses)) "alt clauses must begin with keywords")
    (assert (every? sequential? (map second clauses)) "alt exprs must be async calls")
    (assert (every? #{"<!" ">!"} (map #(-> % second first name) clauses)) "alt exprs must be <! or >!")
    (let [gp (gensym)
          gflag (gensym)
          ops (map (fn [[label [op port arg]]]
                     (case (name op)
                           "<!" `(fn [] (take! ~port (alt-handler ~gflag (fn [val#] (deliver ~gp [~label val#])))))
                           ">!" `(fn [] (put! ~port  ~arg (alt-handler ~gflag (fn [] (deliver ~gp [~label nil])))))))
                   clauses)
          defops (when default
                  `((proto/lock ~gflag)
                    (let [got# (and (proto/active? ~gflag) (proto/commit ~gflag))]
                      (proto/unlock ~gflag)
                      (when got#
                        (deliver ~gp ~(second default))))))]
      `(let [~gp (promise)
             ~gflag (alt-flag)
             ops# [~@ops]
             n# ~(count clauses)
             idxs# (random-array n#)]
         (loop [i# 0]
           (when (< i# n#)
             (let [idx# (nth idxs# i#)
                   cb# ((nth ops# idx#))]
               (if cb#
                 (cb#)
                 (recur (inc i#))))))
         ~@defops
         (deref ~gp)))))

(defmacro alt
  "Makes a non-deterministic choice between one of several channel operations (<! and/or >!)

  Each clause takes the form of:

  :keyword-label channel-op

  where channel op is (<! port-expr) or (>! port-expr val-expr)

  The label :default is reserved, and its argument can be any
  expression.  If more than one of the operations is ready to
  complete, a pseudo-random choice is made. If none of the operations
  are ready to complete, and a :default clause is provided, [:default
  val-of-expression] will be returned. Else alt will block/park until
  any one of the operations is ready to complete, and its label and
  value (if any) are returned. At most one of the operations will
  complete.

  alt returns a vector of [:chosen-label taken-val], taken-val being
  nil for >! ops and closed channels.

  Note: there is no guarantee that the port-exps or val-exprs will be
  used, nor in what order should they be, so they should not be
  depended upon for side effects."

  [& clauses]
  (do-alt clauses))

(defmacro async
  "Asynchronously executes the body, returning immediately to the
  calling thread. Additionally, any visible calls to <!, >! and alt
  channel operations within the body will block (if necessary) by
  'parking' the calling thread rather than tying up an OS thread (or
  the only JS thread when in ClojureScript). Upon completion of the
  operation, the body will be resumed.

  Returns a channel which will receive the result of the body when
  completed"
  [& body]
  (binding [ioc/*symbol-translations* '{await ioc/pause}]
    `(ioc/async-chan-wrapper ~(ioc/state-machine body))))

