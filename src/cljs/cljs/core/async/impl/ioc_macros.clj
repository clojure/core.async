;   Copyright (c) Rich Hickey. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

;; by Timothy Baldridge
;; April 13, 2013

(ns cljs.core.async.impl.ioc-macros
  (:refer-clojure :exclude [all])
  (:require [clojure.pprint :refer [pprint]]
            [clojure.core.async.impl.protocols :as impl]
            [clojure.core.async.impl.dispatch :as dispatch]
            [cljs.analyzer :as cljs])
  (:import [java.util.concurrent.locks Lock]))

(defn debug [x]
  (binding [*out* *err*]
    (pprint x))
  x)


(def ^:dynamic *symbol-translations* {})
(def ^:dynamic *local-env* nil)

(defonce ^:private ^java.util.concurrent.atomic.AtomicLong block-id-gen (java.util.concurrent.atomic.AtomicLong.))

(def ^:const FN-IDX 0)
(def ^:const STATE-IDX 1)
(def ^:const VALUE-IDX 2)
(def ^:const ACTION-IDX 3)
(def ^:const BINDINGS-IDX 4)
(def ^:const USER-START-IDX 5)

(defn aset-object [^objects arr idx ^Object o]
  (aset arr idx o))

(defmacro aset-all!
  [arr & more]
  (let [bindings (partition 2 more)
        arr-sym (gensym "statearr-")]
    `(let [~arr-sym ~arr]
       ~@(map
          (fn [[idx val]]
            `(aset ~arr-sym ~idx ~val))
          bindings)
       ~arr-sym)))

;; State monad stuff, used only in SSA construction

(defn- with-bind [id expr psym body]
  `(fn [~psym]
     (let [[~id ~psym] ( ~expr ~psym)]
       (assert ~psym "Nill plan")
       ~body)))

(defmacro gen-plan
  "Allows a user to define a state monad binding plan.

  (gen-plan
    [_ (assoc-in-plan [:foo :bar] 42)
     val (get-in-plan [:foo :bar])]
    val)"
  [binds id-expr]
  (let [binds (partition 2 binds)
        psym (gensym "plan_")
        f (reduce
           (fn [acc [id expr]]
             `(~(with-bind id expr psym acc)
               ~psym))
           `[~id-expr ~psym]
           (reverse binds))]
    `(fn [~psym]
       ~f)))

(defn get-plan
  "Returns the final [id state] from a plan. "
  [f]
  (f {}))

(defn push-binding
  "Sets the binding 'key' to value. This operation can be undone via pop-bindings.
   Bindings are stored in the state hashmap."
  [key value]
  (fn [plan]
    [nil (update-in plan [:bindings key] conj value)]))

(defn push-alter-binding
  "Pushes the result of (apply f old-value args) as current value of binding key"
  [key f & args]
  (fn [plan]
    [nil (update-in plan [:bindings key]
                  #(conj % (apply f (first %) args)))]))

(defn get-binding
  "Gets the value of the current binding for key"
  [key]
  (fn [plan]
    [(first (get-in plan [:bindings key])) plan]))

(defn pop-binding
  "Removes the most recent binding for key"
  [key]
  (fn [plan]
    [(first (get-in plan [:bindings key]))
     (update-in plan [:bindings key] pop)]))

(defn no-op
  "This function can be used inside a gen-plan when no operation is to be performed"
  []
  (fn [plan]
    [nil plan]))

(defn all
  "Assumes that itms is a list of state monad function results, threads the state map
  through all of them. Returns a vector of all the results."
  [itms]
  (fn [plan]
    (reduce
     (fn [[ids plan] f]
       (let [[id plan] (f plan)]
         [(conj ids id) plan]))
     [[] plan]
     itms)))

(defn assoc-in-plan
  "Same as assoc-in, but for state hash map"
  [path val]
  (fn [plan]
    [val (assoc-in plan path val)]))

(defn update-in-plan
  "Same as update-in, but for a state hash map"
  [path f & args]
  (fn [plan]
    [nil (apply update-in plan path f args)]))

(defn get-in-plan
  "Same as get-in, but for a state hash map"
  [path]
  (fn [plan]
    [(get-in plan path) plan]))

(defn print-plan []
  (fn [plan]
    (pprint plan)
    [nil plan]))

(defn set-block
  "Sets the current block being written to by the functions. The next add-instruction call will append to this block"
  [block-id]
  (fn [plan]
    [block-id (assoc plan :current-block block-id)]))

(defn get-block
  "Gets the current block"
  []
  (fn [plan]
    [(:current-block plan) plan]))

(defn add-block
  "Adds a new block, returns its id, but does not change the current block (does not call set-block)."
  []
  (let [blk-id (.incrementAndGet block-id-gen)]
    (gen-plan
     [cur-blk (get-block)
      _ (assoc-in-plan [:blocks blk-id] [])
      _ (if-not cur-blk
          (assoc-in-plan [:start-block] blk-id)
          (no-op))]
     blk-id)))


(defn instruction? [x]
  (::instruction (meta x)))

(defn add-instruction
  "Appends an instruction to the current block. "
  [inst]
  (let [inst-id (with-meta (gensym "inst_")
                  {::instruction true})
        inst (assoc inst :id inst-id)]
    (gen-plan
     [blk-id (get-block)
      _ (update-in-plan [:blocks blk-id] (fnil conj []) inst)]
     inst-id)))

;;

;; We're going to reduce Clojure expressions to a ssa format,
;; and then translate the instructions for this
;; virtual-virtual-machine back into Clojure data.

;; Here we define the instructions:

(defprotocol IInstruction
  (reads-from [this] "Returns a list of instructions this instruction reads from")
  (writes-to [this] "Returns a list of instructions this instruction writes to")
  (block-references [this] "Returns all the blocks this instruction references")
  (emit-instruction [this state-sym] "Returns the clojure code that this instruction represents"))

(defrecord Const [value]
  IInstruction
  (reads-from [this] [value])
  (writes-to [this] [(:id this)])
  (block-references [this] [])
  (emit-instruction [this state-sym]
    (if (= value ::value)
      `[~(:id this) (aget ~state-sym ~VALUE-IDX)]
      `[~(:id this) ~value])))

(defrecord Set [set-id value]
  IInstruction
  (reads-from [this] [value])
  (writes-to [this] [set-id])
  (block-references [this] [])
  (emit-instruction [this state-sym]
    `[~set-id ~value]))

(defrecord Call [refs]
  IInstruction
  (reads-from [this] refs)
  (writes-to [this] [(:id this)])
  (block-references [this] [])
  (emit-instruction [this state-sym]
    `[~(:id this) ~(seq refs)]))

(defrecord Case [val-id test-vals jmp-blocks default-block]
  IInstruction
  (reads-from [this] [val-id])
  (writes-to [this] [])
  (block-references [this] [])
  (emit-instruction [this state-sym]
    `(case ~val-id
       ~@(concat (mapcat (fn [test blk]
                           `[~test (recur (aset-all! ~state-sym
                                                     ~STATE-IDX ~blk))])
                         test-vals jmp-blocks)
                 (when default-block
                   `[(recur (aset-all! ~state-sym ~STATE-IDX ~default-block))])))))

(defrecord Fn [fn-expr local-names local-refs]
  IInstruction
  (reads-from [this] local-refs)
  (writes-to [this] [(:id this)])
  (block-references [this] [])
  (emit-instruction [this state-sym]
    `[~(:id this)
      (let [~@(interleave local-names local-refs)]
        ~@fn-expr)]))

(defrecord Jmp [value block]
  IInstruction
  (reads-from [this] [value])
  (writes-to [this] [])
  (block-references [this] [block])
  (emit-instruction [this state-sym]
    `(recur (aset-all! ~state-sym ~VALUE-IDX ~value ~STATE-IDX ~block))))

(defrecord Return [value]
  IInstruction
  (reads-from [this] [value])
  (writes-to [this] [])
  (block-references [this] [])
  (emit-instruction [this state-sym]
    `(aset-all! ~state-sym
                ~VALUE-IDX ~value
                ~ACTION-IDX ::return
                ~STATE-IDX :finished)))

(defrecord Put! [channel value block]
  IInstruction
  (reads-from [this] [channel value])
  (writes-to [this] [])
  (block-references [this] [block])
  (emit-instruction [this state-sym]
    `(aset-all! ~state-sym
                ~VALUE-IDX [~channel ~value]
                ~ACTION-IDX ::put!
                ~STATE-IDX ~block)))

(defrecord Take! [channel block]
  IInstruction
  (reads-from [this] [channel])
  (writes-to [this] [])
  (block-references [this] [block])
  (emit-instruction [this state-sym]
    `(aset-all! ~state-sym
                ~VALUE-IDX ~channel
                ~ACTION-IDX ::take!
                ~STATE-IDX ~block)))

(defrecord Pause [value block]
  IInstruction
  (reads-from [this] [value])
  (writes-to [this] [])
  (block-references [this] [block])
  (emit-instruction [this state-sym]
    `(aset-all! ~state-sym
                ~VALUE-IDX ~value
                ~ACTION-IDX ::pause
                ~STATE-IDX ~block)))

(defrecord CondBr [test then-block else-block]
  IInstruction
  (reads-from [this] [test])
  (writes-to [this] [])
  (block-references [this] [then-block else-block])
  (emit-instruction [this state-sym]
    `(if ~test
       (recur (aset-all! ~state-sym
                         ~STATE-IDX ~then-block))
       (recur (aset-all! ~state-sym
                         ~STATE-IDX ~else-block)))))

;; Dispatch clojure forms based on data type
(defmulti -item-to-ssa (fn [x]
                         (cond
                          (symbol? x) :symbol
                          (seq? x) :list
                          (map? x) :map
                          (set? x) :set
                          (vector? x) :vector
                          :else :default)))

(defn item-to-ssa [x]
  (-item-to-ssa x))

(def specials (into #{} (keys clojure.lang.Compiler/specials)))

(defn var-name [^clojure.lang.Var var]
  (symbol (name (ns-name (.ns var)))
          (name (.sym var))))

(defn symbol-translation [x]
  (if (contains? *local-env* x)
    nil
    (if (specials x)
      x
      (if-let [unqualified-translation (*symbol-translations* x)]
        unqualified-translation
        (if-let [var (resolve *local-env* x)]
          (let [resolved-sym (var-name var)]
            (*symbol-translations* resolved-sym resolved-sym))
          x)))))

;; given an sexpr, dispatch on the first item 
(defmulti sexpr-to-ssa (fn [[x & _]]
                         (symbol-translation x)))


(defmethod sexpr-to-ssa :default
  [args]
  (gen-plan
   [args-ids (all (map item-to-ssa args))
    inst-id (add-instruction (->Call args-ids))]
   inst-id))

(defn let-binding-to-ssa
  [[sym bind]]
  (gen-plan
   [bind-id (item-to-ssa bind)
    _ (push-alter-binding :locals assoc sym bind-id)]
   bind-id))

;; NOTE: CLJS uses let and loop instead of let* and loop*

(defmethod sexpr-to-ssa 'let*
  [[_ binds & body]]
  (let [parted (partition 2 binds)]
    (gen-plan
     [let-ids (all (map let-binding-to-ssa parted))
      body-ids (all (map item-to-ssa body))
      _ (all (map (fn [x]
                    (pop-binding :locals))
                  (range (count parted))))]
     (last body-ids))))

(defmethod sexpr-to-ssa 'loop*
  [[_ locals & body]]
  (let [parted (partition 2 locals)
        syms (map first parted)
        inits (map second parted)]
    (gen-plan
     [local-val-ids (all (map item-to-ssa inits))
      local-ids (all (map (comp add-instruction ->Const) local-val-ids))
      body-blk (add-block)
      final-blk (add-block)
      _ (add-instruction (->Jmp nil body-blk))

      _ (set-block body-blk)
      _ (push-alter-binding :locals merge (zipmap syms local-ids))
      _ (push-binding :recur-point body-blk)
      _ (push-binding :recur-nodes local-ids)

      body-ids (all (map item-to-ssa body))

      _ (pop-binding :recur-nodes)
      _ (pop-binding :recur-point)
      _ (pop-binding :locals)
      _ (if (not= (last body-ids) ::terminated)
          (add-instruction (->Jmp (last body-ids) final-blk))
          (no-op))

      _ (set-block final-blk)
      ret-id (add-instruction (->Const ::value))]
     ret-id)))

(defmethod sexpr-to-ssa 'do
  [[_ & body]]
  (gen-plan
   [ids (all (map item-to-ssa body))]
   (last ids)))

(defmethod sexpr-to-ssa 'case
  [[_ val & body]]
  (let [clauses (partition 2 body)
        default (when (odd? (count body))
                  (last body))]
    (gen-plan
     [end-blk (add-block)
      start-blk (get-block)
      clause-blocks (all (map (fn [expr]
                                (gen-plan
                                 [blk-id (add-block)
                                  _ (set-block blk-id)
                                  expr-id (item-to-ssa expr)
                                  _ (add-instruction (->Jmp expr-id end-blk))]
                                 blk-id))
                              (map second clauses)))
      default-block (if default
                      (gen-plan
                       [blk-id (add-block)
                        _ (set-block blk-id)
                        expr-id (item-to-ssa default)
                        _ (add-instruction (->Jmp expr-id end-blk))]
                       blk-id)
                      (no-op))
      _ (set-block start-blk)
      val-id (item-to-ssa val)
      case-id (add-instruction (->Case val-id (map first clauses) clause-blocks default-block))
      _ (set-block end-blk)
      ret-id (add-instruction (->Const ::value))]
     ret-id)))

(defmethod sexpr-to-ssa 'recur
  [[_ & vals]]
  (gen-plan
   [val-ids (all (map item-to-ssa vals))
    recurs (get-binding :recur-nodes)
    _ (all (map #(add-instruction (->Set %1 %2))
                recurs
                val-ids))
    recur-point (get-binding :recur-point)
    
    _ (add-instruction (->Jmp nil recur-point))]
   ::terminated))

(defmethod sexpr-to-ssa 'if
  [[_ test then else]]
  (gen-plan
   [test-id (item-to-ssa test)
    then-blk (add-block)
    else-blk (add-block)
    final-blk (add-block)
    _ (add-instruction (->CondBr test-id then-blk else-blk))
    
    _ (set-block then-blk)
    then-id (item-to-ssa then)
    _ (if (not= then-id ::terminated)
        (gen-plan
         [_ (add-instruction (->Jmp then-id final-blk))]
         then-id)
        (no-op))

    _ (set-block else-blk)
    else-id (item-to-ssa else)
    _ (if (not= else-id ::terminated)
        (gen-plan
         [_ (add-instruction (->Jmp else-id final-blk))]
         then-id)
        (no-op))

    _ (set-block final-blk)
    val-id (add-instruction (->Const ::value))]
   val-id))
(defmethod sexpr-to-ssa 'fn*
  [& fn-expr]
  ;; For fn expressions we just want to record the expression as well
  ;; as a list of all known renamed locals
  (gen-plan
   [locals (get-binding :locals)
    fn-id (add-instruction (->Fn fn-expr (keys locals) (vals locals)))]
   fn-id))

(defmethod sexpr-to-ssa 'clojure.core.async.ioc-macros/pause
  [[_ expr]]
  (gen-plan
   [next-blk (add-block)
    expr-id (item-to-ssa expr)
    jmp-id (add-instruction (->Pause expr-id next-blk))
    _ (set-block next-blk)
    val-id (add-instruction (->Const ::value))]
   val-id))

(defmethod sexpr-to-ssa 'clojure.core.async/<!
  [[_ chan]]
  (gen-plan
   [next-blk (add-block)
    chan-id (item-to-ssa chan)
    jmp-id (add-instruction (->Take! chan-id next-blk))
    _ (set-block next-blk)
    val-id (add-instruction (->Const ::value))]
   val-id))

(defmethod sexpr-to-ssa 'clojure.core.async/>!
  [[_ chan expr]]
  (gen-plan
   [next-blk (add-block)
    chan-id (item-to-ssa chan)
    expr-id (item-to-ssa expr)
    jmp-id (add-instruction (->Put! chan-id expr-id next-blk))
    _ (set-block next-blk)
    val-id (add-instruction (->Const ::value))]
   val-id))


(defn expand-1 [env form]
  (if (seq? form)
    (cljs/macroexpand-1 env form)
    form))

(defn expand [env form]
  (let [form' (expand-1 env form)]
    (if (not= form form')
      (recur env form')
      form')))

(defmethod -item-to-ssa :list
  [lst]
  (fn [p]
    (let [[locals p] ((get-binding :locals) p)
          env (merge *local-env* locals)]
      (binding [*local-env* env]
        (if (and (*symbol-translations* (first lst))
                 (not (contains? env (first lst))))
          ((sexpr-to-ssa lst) p)
          (let [result (expand env lst)]
            ((if (seq? result)
               (sexpr-to-ssa result)
               (item-to-ssa result))
             p)))))))

(defmethod -item-to-ssa :default
  [x]
  (fn [plan]
    [x plan]))

(defmethod -item-to-ssa :symbol
  [x]
  (gen-plan
   [locals (get-binding :locals)
    inst-id (if (contains? locals x)
              (fn [p]
                [(locals x) p])
              (fn [p]
                [x p])
              #_(add-instruction (->Const x)))]
   inst-id))

(defmethod -item-to-ssa :map
  [x]
  (-item-to-ssa `(hash-map ~@(mapcat identity x))))

(defmethod -item-to-ssa :vector
  [x]
  (-item-to-ssa `(vector ~@x)))

(defmethod -item-to-ssa :set
  [x]
  (-item-to-ssa `(hash-set ~@x)))

(defn parse-to-state-machine
  "Takes an sexpr and returns a hashmap that describes the execution flow of the sexpr as
   a series of SSA style blocks."
  [body]
  (-> (gen-plan
       [blk (add-block)
        _ (set-block blk)
        ids (all (map item-to-ssa body))
        term-id (add-instruction (->Return (last ids)))]
       term-id)
      get-plan))


(defn index-instruction [blk-id idx inst]
  (let [idx (reduce
             (fn [acc id]
               (update-in acc [id :read-in] (fnil conj #{}) blk-id))
             idx
             (filter instruction? (reads-from inst)))
        idx (reduce
             (fn [acc id]
               (update-in acc [id :written-in] (fnil conj #{}) blk-id))
             idx
             (filter instruction? (writes-to inst)))]
    idx))

(defn index-block [idx [blk-id blk]]
  (reduce (partial index-instruction blk-id) idx blk))

(defn index-state-machine [machine]
  (reduce index-block {} (:blocks machine)))

(defn id-for-inst [m sym] ;; m :: symbols -> integers
  (if-let [i (get @m sym)]
    i
    (let [next-idx (get @m ::next-idx)]
      (swap! m assoc sym next-idx)
      (swap! m assoc ::next-idx (inc next-idx))
      next-idx)))

(defn persistent-value?
  "Returns true if this value should be saved in the state hash map"
  [index value]
  (or (not= (-> index value :read-in)
            (-> index value :written-in))
      (-> index value :read-in count (> 1))))

(defn count-persistent-values
  [index]
  (->> (keys index)
       (filter instruction?)
       (filter (partial persistent-value? index))
       count))

(defn- build-block-preamble [local-map idx state-sym blk]
  (let [args (->> (mapcat reads-from blk)
                  (filter instruction?)
                  (filter (partial persistent-value? idx))
                  set
                  vec)]
    (if (empty? args)
      []
      (mapcat (fn [sym]
             `[~sym (aget ~state-sym ~(id-for-inst local-map sym))])
              args))))

(defn- build-block-body [state-sym blk]
  (mapcat
   #(emit-instruction % state-sym)
   (butlast blk)))

(defn- build-new-state [local-map idx state-sym blk]
  (let [results (->> blk
                     (mapcat writes-to)
                     (filter instruction?)
                     (filter (partial persistent-value? idx))
                     set
                     vec)
        results (interleave (map (partial id-for-inst local-map) results) results)]
    (if-not (empty? results)
      `(aset-all! ~state-sym ~@results)
      state-sym)))


(defn- emit-state-machine [machine num-user-params]
  (let [index (index-state-machine machine)
        state-sym (with-meta (gensym "state_")
                    {:tag 'objects})
        local-start-idx (+ num-user-params USER-START-IDX)
        state-arr-size (+ local-start-idx (count-persistent-values index))
        local-map (atom {::next-idx local-start-idx})]
    `(fn state-machine#
       ([] (aset-all! ^objects (make-array ~state-arr-size)
                      ~FN-IDX state-machine#
                      ~STATE-IDX ~(:start-block machine)))
       ([~state-sym]
          (loop [~state-sym ~state-sym]
            (case (aget ~state-sym ~STATE-IDX)
              ~@(mapcat
                 (fn [[id blk]]
                   `(~id
                     (let [~@(concat (build-block-preamble local-map index state-sym blk)
                                     (build-block-body state-sym blk))
                           ~state-sym ~(build-new-state local-map index state-sym blk)]
                       ~(emit-instruction (last blk) state-sym))))
                 (:blocks machine))))))))

(defn finished?
  "Returns true if the machine is in a finished state"
  [^objects state-array]
  (identical? (aget state-array STATE-IDX) :finished))

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

(defn async-chan-wrapper
  "State machine wrapper that uses the async library. Has to be in this file do to dependency issues. "
  ([^objects state]
     (let [state ((aget state FN-IDX) state)
           value (aget ^objects state VALUE-IDX)]
       (case (aget ^objects state ACTION-IDX)
         ::take!
         (when-let [cb (impl/take! value (fn-handler
                                          (fn [x]
                                            (->> x
                                                 (aset-all! state VALUE-IDX)
                                                 async-chan-wrapper))))]
           (recur (aset-all! state VALUE-IDX @cb)))
         ::put!
         (let [[chan value] value]
           (when-let [cb (impl/put! chan value (fn-handler (fn []
                                                             (->> nil
                                                                  (aset-all! state VALUE-IDX)
                                                                  async-chan-wrapper))))]
             (recur (aset-all! state VALUE-IDX @cb))))
         
         ::return
         (let [c (aget ^objects state USER-START-IDX)]
           (when-not (nil? value)
             (impl/put! c value (fn-handler (fn [] nil))))
           (impl/close! c)
           c)

                                        ; Default case, return nil
         nil))))


(defn state-machine [body num-user-params]
  (-> (parse-to-state-machine body)
      second
      (emit-state-machine num-user-params)))
