;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns clojure.core.async
  (:refer-clojure :exclude [reduce into merge map])
  (:require [clojure.core.async.impl.protocols :as impl]
            [clojure.core.async.impl.channels :as channels]
            [clojure.core.async.impl.buffers :as buffers]
            [clojure.core.async.impl.timers :as timers]
            [clojure.core.async.impl.dispatch :as dispatch]
            [clojure.core.async.impl.ioc-macros :as ioc]
            [clojure.core.async.impl.mutex :as mutex]
            [clojure.core.async.impl.concurrent :as conc]
            )
  (:import [clojure.core.async ThreadLocalRandom]
           [java.util.concurrent.locks Lock]
           [java.util.concurrent Executors Executor]))

(alias 'core 'clojure.core)

(set! *warn-on-reflection* true)

(defn fn-handler
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
        (core/reduce
         (fn [[clauses bindings] [ports expr]]
           (let [ports (if (vector? ports) ports [ports])
                 [ports bindings]
                 (core/reduce
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
           [val# ~gch :as ~gret] (~alts [~@(apply concat (core/map first clauses))] ~@(apply concat opts))]
       (cond
        ~@(mapcat (fn [[ports expr]]
                    [`(or ~@(core/map (fn [port]
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

(defn ioc-alts! [state cont-block ports & {:as opts}]
  (ioc/aset-all! state ioc/STATE-IDX cont-block)
  (when-let [cb (clojure.core.async/do-alts
                  (fn [val]
                    (ioc/aset-all! state ioc/VALUE-IDX val)
                    (ioc/run-state-machine-wrapped state))
                  ports
                  opts)]
    (ioc/aset-all! state ioc/VALUE-IDX @cb)
    :recur))


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
  `(let [c# (chan 1)
         captured-bindings# (clojure.lang.Var/getThreadBindingFrame)]
     (dispatch/run
      (fn []
        (let [f# ~(ioc/state-machine body 1 &env ioc/async-custom-terminators)
              state# (-> (f#)
                         (ioc/aset-all! ioc/USER-START-IDX c#
                                        ioc/BINDINGS-IDX captured-bindings#))]
          (ioc/run-state-machine-wrapped state#))))
     c#))

(defonce ^:private ^Executor thread-macro-executor
  (Executors/newCachedThreadPool (conc/counted-thread-factory "async-thread-macro-%d" true)))

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
                  (when-not (nil? ret)
                    (>!! c ret))
                  (close! c))))
    c))

(defmacro thread
  "Executes the body in another thread, returning immediately to the
  calling thread. Returns a channel which will receive the result of
  the body when completed."
  [& body]
  `(thread-call (fn [] ~@body)))

;;;;;;;;;;;;;;;;;;;; ops ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn map<
  "Takes a function and a source channel, and returns a channel which
  contains the values produced by applying f to each value taken from
  the source channel"
  [f ch]
  (reify
   impl/Channel
   (close! [_] (impl/close! ch))

   impl/ReadPort
   (take! [_ fn1]
     (let [ret
       (impl/take! ch
         (reify
          Lock
          (lock [_] (.lock ^Lock fn1))
          (unlock [_] (.unlock ^Lock fn1))
          
          impl/Handler
          (active? [_] (impl/active? fn1))
          (lock-id [_] (impl/lock-id fn1))
          (commit [_]
           (let [f1 (impl/commit fn1)]
             #(f1 (if (nil? %) nil (f %)))))))]
       (if (and ret (not (nil? @ret)))
         (channels/box (f @ret))
         ret)))
   
   impl/WritePort
   (put! [_ val fn0] (impl/put! ch val fn0))))

(defn map>
  "Takes a function and a target channel, and returns a channel which
  applies f to each value before supplying it to the target channel."
  [f ch]
  (reify
   impl/Channel
   (close! [_] (impl/close! ch))
   
   impl/ReadPort
   (take! [_ fn1] (impl/take! ch fn1))
   
   impl/WritePort
   (put! [_ val fn0]
    (impl/put! ch (f val) fn0))))

(defmacro go-loop
  "Like (go (loop ...))"
  [bindings & body]
  `(go (loop ~bindings ~@body)))

(defn filter>
  "Takes a predicate and a target channel, and returns a channel which
  supplies only the values for which the predicate returns true to the
  target channel."
  [p ch]
  (reify
   impl/Channel
   (close! [_] (impl/close! ch))

   impl/ReadPort
   (take! [_ fn1] (impl/take! ch fn1))
   
   impl/WritePort
   (put! [_ val fn0]
    (if (p val)
      (impl/put! ch val fn0)
      (channels/box nil)))))

(defn remove>
  "Takes a predicate and a target channel, and returns a channel which
  supplies only the values for which the predicate returns false to the
  target channel."
  [p ch]
  (filter> (complement p) ch))

(defn filter<
  "Takes a predicate and a source channel, and returns a channel which
  contains only the values taken from the source channel for which the
  predicate returns true. The returned channel will be unbuffered by
  default, or a buf-or-n can be supplied. The channel will close
  when the source channel closes."
  ([p ch] (filter< p ch nil))
  ([p ch buf-or-n]
     (let [out (chan buf-or-n)]
       (go-loop []
         (let [val (<! ch)]
           (if (nil? val)
             (close! out)
             (do (when (p val)
                   (>! out val))
                 (recur)))))
       out)))

(defn remove<
  "Takes a predicate and a source channel, and returns a channel which
  contains only the values taken from the source channel for which the
  predicate returns false. The returned channel will be unbuffered by
  default, or a buf-or-n can be supplied. The channel will close
  when the source channel closes."
  ([p ch] (remove< p ch nil))
  ([p ch buf-or-n] (filter< (complement p) ch buf-or-n)))

(defn- mapcat* [f in out]
  (go-loop []
    (let [val (<! in)]
      (if (nil? val)
        (close! out)
        (let [vals (f val)]
          (doseq [v vals]
            (>! out val))
          (recur))))))

(defn mapcat<
  "Takes a function and a source channel, and returns a channel which
  contains the values in each collection produced by applying f to
  each value taken from the source channel. f must return a
  collection.

  The returned channel will be unbuffered by default, or a buf-or-n
  can be supplied. The channel will close when the source channel
  closes."
  ([f in] (mapcat< f in nil))
  ([f in buf-or-n]
    (let [out (chan buf-or-n)]
      (mapcat* f in out)
      out)))

(defn mapcat>
  "Takes a function and a target channel, and returns a channel which
  applies f to each value put, then supplies each element of the result
  to the target channel. f must return a collection.

  The returned channel will be unbuffered by default, or a buf-or-n
  can be supplied. The target channel will be closed when the source
  channel closes."
  
  ([f out] (mapcat> f out nil))
  ([f out buf-or-n]
     (let [in (chan buf-or-n)]
       (mapcat* f in out)
       in)))

(defn pipe
  "Takes elements from the from channel and supplies them to the to
  channel. By default, the to channel will be closed when the
  from channel closes, but can be determined by the close?
  parameter."
  ([from to] (pipe from to true))
  ([from to close?]
     (go-loop []
      (let [v (<! from)]
        (if (nil? v)
          (when close? (close! to))
          (do (>! to v)
              (recur)))))
     to))

(defn split
  "Takes a predicate and a source channel and returns a vector of two
  channels, the first of which will contain the values for which the
  predicate returned true, the second those for which it returned
  false.

  The out channels will be unbuffered by default, or two buf-or-ns can
  be supplied. The channels will close after the source channel has
  closed."
  ([p ch] (split p ch nil nil))
  ([p ch t-buf-or-n f-buf-or-n]
     (let [tc (chan t-buf-or-n)
           fc (chan f-buf-or-n)]
       (go-loop []
         (let [v (<! ch)]
           (if (nil? v)
             (do (close! tc) (close! fc))
             (do (>! (if (p v) tc fc) v)
                 (recur)))))
       [tc fc])))

(defn reduce
  "f should be a function of 2 arguments. Returns a channel containing
  the single result of applying f to init and the first item from the
  channel, then applying f to that result and the 2nd item, etc. If
  the channel closes without yielding items, returns init and f is not
  called. ch must close before reduce produces a result."
  [f init ch]
  (go-loop [ret init]
    (let [v (<! ch)]
      (if (nil? v)
        ret
        (recur (f ret v))))))

(defn- bounded-count
  "Returns the smaller of n or the count of coll, without examining
  more than n items if coll is not counted"
  [n coll]
  (if (counted? coll)
    (min n (count coll))
    (loop [i 0 s (seq coll)]
      (if (and s (< i n))
        (recur (inc i) (next s))
        i))))

(defn onto-chan
  "Puts the contents of coll into the supplied channel.

  By default the channel will be closed after the items are copied,
  but can be determined by the close? parameter.

  Returns a channel which will close after the items are copied."
  ([ch coll] (onto-chan ch coll true))
  ([ch coll close?]
     (go-loop [vs (seq coll)]
       (if vs
         (do (>! ch (first vs))
             (recur (next vs)))
         (when close?
           (close! ch))))))

(defn to-chan
  "Creates and returns a channel which contains the contents of coll,
  closing when exhausted."
  [coll]
  (let [ch (chan (bounded-count 100 coll))]
    (onto-chan ch coll)
    ch))

(defprotocol Mux
  (muxch* [_]))

(defprotocol Mult
  (tap* [m ch close?])
  (untap* [m ch])
  (untap-all* [m]))

(defn mult
  "Creates and returns a mult(iple) of the supplied channel. Channels
  containing copies of the channel can be created with 'tap', and
  detached with 'untap'.

  Each item is distributed to all taps in parallel and synchronously,
  i.e. each tap must accept before the next item is distributed. Use
  buffering/windowing to prevent slow taps from holding up the mult.

  If a tap put throws an exception, it will be removed from the mult."
  [ch]
  (let [cs (atom {}) ;;ch->close?
        m (reify
           Mux
           (muxch* [_] ch)
           
           Mult
           (tap* [_ ch close?] (swap! cs assoc ch close?) nil)
           (untap* [_ ch] (swap! cs dissoc ch) nil)
           (untap-all* [_] (reset! cs {}) nil))
        dchan (chan 1)
        dctr (atom nil)
        done #(when (zero? (swap! dctr dec))
                (put! dchan true))]
    (go-loop []
     (let [val (<! ch)]
       (if (nil? val)
         (doseq [[c close?] @cs]
           (when close? (close! c)))
         (let [chs (keys @cs)]
           (reset! dctr (count chs))
           (doseq [c chs]
               (try
                 (put! c val done)
                 (catch Exception e
                   (swap! dctr dec)
                   (untap* m c))))
           ;;wait for all
           (<! dchan)
           (recur)))))
    m))

(defn tap
  "Copies the mult source onto the supplied channel.

  By default the channel will be closed when the source closes,
  but can be determined by the close? parameter."
  ([mult ch] (tap mult ch true))
  ([mult ch close?] (tap* mult ch close?) ch))

(defn untap
  "Disconnects a target channel from a mult"
  [mult ch]
  (untap* mult ch))

(defn untap-all
  "Disconnects all target channels from a mult"
  [mult] (untap-all* mult))

(defprotocol Mix
  (admix* [m ch])
  (unmix* [m ch])
  (unmix-all* [m])
  (toggle* [m state-map])
  (solo-mode* [m mode]))

(defn mix
  "Creates and returns a mix of one or more input channels which will
  be put on the supplied out channel. Input sources can be added to
  the mix with 'admix', and removed with 'unmix'. A mix supports
  soloing, muting and pausing multiple inputs atomically using
  'toggle', and can solo using either muting or pausing as determined
  by 'solo-mode'.

  Each channel can have zero or more boolean modes set via 'toggle':

  :solo - when true, only this (ond other soloed) channel(s) will appear
          in the mix output channel. :mute and :pause states of soloed
          channels are ignored. If solo-mode is :mute, non-soloed
          channels are muted, if :pause, non-soloed channels are
          paused.

  :mute - muted channels will have their contents consumed but not included in the mix
  :pause - paused channels will not have their contents consumed (and thus also not included in the mix)
"
  [out]
  (let [cs (atom {}) ;;ch->attrs-map
        solo-modes #{:mute :pause}
        attrs (conj solo-modes :solo)
        solo-mode (atom :mute)
        change (chan)
        changed #(put! change true)
        pick (fn [attr chs]
               (reduce-kv
                   (fn [ret c v]
                     (if (attr v)
                       (conj ret c)
                       ret))
                   #{} chs))
        calc-state (fn []
                     (let [chs @cs
                           mode @solo-mode
                           solos (pick :solo chs)
                           pauses (pick :pause chs)]
                       {:solos solos
                        :mutes (pick :mute chs)
                        :reads (conj
                                (if (and (= mode :pause) (not (empty? solos)))
                                  (vec solos)
                                  (vec (remove pauses (keys chs))))
                                change)}))
        m (reify
           Mux
           (muxch* [_] out)
           Mix
           (admix* [_ ch] (swap! cs assoc ch {}) (changed))
           (unmix* [_ ch] (swap! cs dissoc ch) (changed))
           (unmix-all* [_] (reset! cs {}) (changed))
           (toggle* [_ state-map] (swap! cs (partial merge-with core/merge) state-map) (changed))
           (solo-mode* [_ mode]
             (assert (solo-modes mode) (str "mode must be one of: " solo-modes))
             (reset! solo-mode mode)
             (changed)))]
    (go-loop [{:keys [solos mutes reads] :as state} (calc-state)]
      (let [[v c] (alts! reads)]
        (if (or (nil? v) (= c change))
          (do (when (nil? v)
                (swap! cs dissoc c))
              (recur (calc-state)))
          (do (when (or (solos c)
                        (and (empty? solos) (not (mutes c))))
                (>! out v))
            (recur state)))))
    m))

(defn admix
  "Adds ch as an input to the mix"
  [mix ch]
  (admix* mix ch))

(defn unmix
  "Removes ch as an input to the mix"
  [mix ch]
  (unmix* mix ch))

(defn unmix-all
  "removes all inputs from the mix"
  [mix]
  (unmix-all* mix))

(defn toggle
  "Atomically sets the state(s) of one or more channels in a mix. The
  state map is a map of channels -> channel-state-map. A
  channel-state-map is a map of attrs -> boolean, where attr is one or
  more of :mute, :pause or :solo. Any states supplied are merged with
  the current state.

  Note that channels can be added to a mix via toggle, which can be
  used to add channels in a particular (e.g. paused) state."
  [mix state-map]
  (toggle* mix state-map))

(defn solo-mode
  "Sets the solo mode of the mix. mode must be one of :mute or :pause"
  [mix mode]
  (solo-mode* mix mode))

(defprotocol Pub
  (sub* [p v ch close?])
  (unsub* [p v ch])
  (unsub-all* [p] [p v]))

(defn pub
  "Creates and returns a pub(lication) of the supplied channel,
  partitioned into topics by the topic-fn. topic-fn will be applied to
  each value on the channel and the result will determine the 'topic'
  on which that value will be put. Channels can be subscribed to
  receive copies of topics using 'sub', and unsubscribed using
  'unsub'. Each topic will be handled by an internal mult on a
  dedicated channel. By default these internal channels are
  unbuffered, but a buf-fn can be supplied which, given a topic,
  creates a buffer with desired properties.

  Each item is distributed to all subs in parallel and synchronously,
  i.e. each sub must accept before the next item is distributed. Use
  buffering/windowing to prevent slow subs from holding up the pub.

  Note that if buf-fns are used then each topic is handled
  asynchronously, i.e. if a channel is subscribed to more than one
  topic it should not expect them to be interleaved identically with
  the source."
  ([ch topic-fn] (pub ch topic-fn (constantly nil)))
  ([ch topic-fn buf-fn]
     (let [mults (atom {}) ;;topic->mult
           ensure-mult (fn [topic]
                         (or (get @mults topic)
                             (get (swap! mults
                                         #(if (% topic) % (assoc % topic (mult (chan (buf-fn topic))))))
                                  topic)))
           p (reify
              Mux
              (muxch* [_] ch)
              
              Pub
              (sub* [p topic ch close?]
                    (let [m (ensure-mult topic)]
                      (tap m ch close?)))
              (unsub* [p topic ch]
                      (when-let [m (get @mults topic)]
                        (untap m ch)))
              (unsub-all* [_] (reset! mults {}))
              (unsub-all* [_ topic] (swap! mults dissoc topic)))]
       (go-loop []
         (let [val (<! ch)]
           (if (nil? val)
             (doseq [m (vals @mults)]
               (close! (muxch* m)))
             (let [topic (topic-fn val)
                   m (get @mults topic)]
               (when m
                 (try
                   (>! (muxch* m) val)
                   (catch Exception e
                     (swap! mults dissoc topic))))
               (recur)))))
       p)))

(defn sub
  "Subscribes a channel to a topic of a pub.

  By default the channel will be closed when the source closes,
  but can be determined by the close? parameter."
  ([p topic ch] (sub p topic ch true))
  ([p topic ch close?] (sub* p topic ch close?)))

(defn unsub
  "Unsubscribes a channel from a topic of a pub"
  [p topic ch]
  (unsub* p topic ch))

(defn unsub-all
  "Unsubscribes all channels from a pub, or a topic of a pub"
  ([p] (unsub-all* p))
  ([p topic] (unsub-all* p topic)))

;;; these are down here because they alias core fns, don't want accidents above

(defn map
  "Takes a function and a collection of source channels, and returns a
  channel which contains the values produced by applying f to the set
  of first items taken from each source channel, followed by applying
  f to the set of second items from each channel, until any one of the
  channels is closed, at which point the output channel will be
  closed. The returned channel will be unbuffered by default, or a
  buf-or-n can be supplied"
  ([f chs] (map f chs nil))
  ([f chs buf-or-n]
     (let [chs (vec chs) 
           out (chan buf-or-n)
           cnt (count chs)
           rets (object-array cnt)
           dchan (chan 1)
           dctr (atom nil)
           done (mapv (fn [i]
                         (fn [ret]
                           (aset rets i ret)
                           (when (zero? (swap! dctr dec))
                             (put! dchan (java.util.Arrays/copyOf rets cnt)))))
                       (range cnt))]
       (go-loop []
         (reset! dctr cnt)
         (dotimes [i cnt]
           (try
             (take! (chs i) (done i))
             (catch Exception e
               (swap! dctr dec))))
         (let [rets (<! dchan)]
           (if (some nil? rets)
             (close! out)
             (do (>! out (apply f rets))
                 (recur)))))
       out)))

(defn merge
  "Takes a collection of source channels and returns a channel which
  contains all values taken from them. The returned channel will be
  unbuffered by default, or a buf-or-n can be supplied. The channel
  will close after all the source channels have closed."
  ([chs] (merge chs nil))
  ([chs buf-or-n]
     (let [out (chan buf-or-n)]
       (go-loop [cs (vec chs)]
         (if (pos? (count cs))
           (let [[v c] (alts! cs)]
             (if (nil? v)
               (recur (filterv #(not= c %) cs))
               (do (>! out v)
                   (recur cs))))
           (close! out)))
       out)))

(defn into
  "Returns a channel containing the single (collection) result of the
  items taken from the channel conjoined to the supplied
  collection. ch must close before into produces a result."
  [coll ch]
  (reduce conj coll ch))
