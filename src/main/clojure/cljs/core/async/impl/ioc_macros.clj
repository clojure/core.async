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
            [clojure.set :refer (intersection)]
            [clojure.core.async.impl.protocols :as impl]
            [clojure.core.async.impl.dispatch :as dispatch]
            [cljs.analyzer :as cljs])
  (:import [java.util.concurrent.locks Lock]))

(defn debug [x]
  (binding [*out* *err*]
    (pprint x))
  x)

(def ^:const FN-IDX 0)
(def ^:const STATE-IDX 1)
(def ^:const VALUE-IDX 2)
(def ^:const BINDINGS-IDX 3)
(def ^:const EXCEPTION-FRAMES 4)
(def ^:const CURRENT-EXCEPTION 5)
(def ^:const USER-START-IDX 6)

(defmacro aset-all!
  [arr & more]
  (assert (even? (count more)) "Must give an even number of args to aset-all!")
  (let [bindings (partition 2 more)
        arr-sym (gensym "statearr-")]
    `(let [~arr-sym ~arr]
       ~@(map
          (fn [[idx val]]
            `(aset ~arr-sym ~idx ~val))
          bindings)
       ~arr-sym)))

;; State monad stuff, used only in SSA construction

(defmacro gen-plan
  "Allows a user to define a state monad binding plan.

  (gen-plan
    [_ (assoc-in-plan [:foo :bar] 42)
     val (get-in-plan [:foo :bar])]
    val)"
  [binds id-expr]
  (let [binds (partition 2 binds)
        psym (gensym "plan_")
        forms (reduce
               (fn [acc [id expr]]
                 (concat acc `[[~id ~psym] (~expr ~psym)]))
               []
               binds)]
    `(fn [~psym]
       (let [~@forms]
         [~id-expr ~psym]))))

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
  (gen-plan
   [_ (update-in-plan [:block-id] (fnil inc 0))
    blk-id (get-in-plan [:block-id])
    cur-blk (get-block)
    _ (assoc-in-plan [:blocks blk-id] [])
    catches (get-binding :catch)
    _ (assoc-in-plan [:block-catches blk-id] catches)
    _ (if-not cur-blk
        (assoc-in-plan [:start-block] blk-id)
        (no-op))]
   blk-id))


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
  (block-references [this] "Returns all the blocks this instruction references"))

(defprotocol IEmittableInstruction
  (emit-instruction [this state-sym] "Returns the clojure code that this instruction represents"))

(defprotocol ITerminator
  (terminator-code [this] "Returns a unique symbol for this instruction")
  (terminate-block [this state-sym custom-terminators] "Emites the code to terminate a given block"))

(defrecord Const [value]
  IInstruction
  (reads-from [this] [value])
  (writes-to [this] [(:id this)])
  (block-references [this] [])
  IEmittableInstruction
  (emit-instruction [this state-sym]
    (if (= value ::value)
      `[~(:id this) (aget ~state-sym ~VALUE-IDX)]
      `[~(:id this) ~value])))

(defrecord CustomTerminator [f blk values]
  IInstruction
  (reads-from [this] values)
  (writes-to [this] [])
  (block-references [this] [])
  ITerminator
  (terminate-block [this state-sym _]
    `(~f ~state-sym ~blk ~@values)))

(defn- emit-clashing-binds
  [recur-nodes ids clashes]
  (let [temp-binds (reduce
                    (fn [acc i]
                      (assoc acc i (gensym "tmp")))
                    {} clashes)]
    (concat
     (mapcat (fn [i]
            `[~(temp-binds i) ~i])
          clashes)
     (mapcat (fn [node id]
               `[~node ~(get temp-binds id id)])
             recur-nodes
             ids))))

(defrecord Recur [recur-nodes ids]
  IInstruction
  (reads-from [this] ids)
  (writes-to [this] recur-nodes)
  (block-references [this] [])
  IEmittableInstruction
  (emit-instruction [this state-sym]
    (if-let [overlap (seq (intersection (set recur-nodes) (set ids)))]
      (emit-clashing-binds recur-nodes ids overlap)
      (mapcat (fn [r i]
                `[~r ~i]) recur-nodes ids))))

(defrecord Call [refs]
  IInstruction
  (reads-from [this] refs)
  (writes-to [this] [(:id this)])
  (block-references [this] [])
  IEmittableInstruction
  (emit-instruction [this state-sym]
    `[~(:id this) ~(seq refs)]))

(defrecord Case [val-id test-vals jmp-blocks default-block]
  IInstruction
  (reads-from [this] [val-id])
  (writes-to [this] [])
  (block-references [this] [])
  ITerminator
  (terminate-block [this state-sym _]
    `(do (case ~val-id
           ~@(concat (mapcat (fn [test blk]
                               `[~test (aset-all! ~state-sym
                                                  ~STATE-IDX ~blk)])
                             test-vals jmp-blocks)
                     (when default-block
                       `[(do (aset-all! ~state-sym ~STATE-IDX ~default-block)
                             :recur)])))
         :recur)))

(defrecord Fn [fn-expr local-names local-refs]
  IInstruction
  (reads-from [this] local-refs)
  (writes-to [this] [(:id this)])
  (block-references [this] [])
  IEmittableInstruction
  (emit-instruction [this state-sym]
    `[~(:id this)
      (let [~@(interleave local-names local-refs)]
        ~@fn-expr)]))

(defrecord Dot [target method args]
  IInstruction
  (reads-from [this] `[~target ~method ~@args])
  (writes-to [this] [(:id this)])
  (block-references [this] [])
  IEmittableInstruction
  (emit-instruction [this state-sym]
    `[~(:id this) (. ~target ~(cons method args))]))

(defrecord Jmp [value block]
  IInstruction
  (reads-from [this] [value])
  (writes-to [this] [])
  (block-references [this] [block])
  ITerminator
  (terminate-block [this state-sym _]
    `(do (aset-all! ~state-sym ~VALUE-IDX ~value ~STATE-IDX ~block)
         :recur)))

(defrecord Return [value]
  IInstruction
  (reads-from [this] [value])
  (writes-to [this] [])
  (block-references [this] [])
  ITerminator
  (terminator-code [this] :Return)
  (terminate-block [this state-sym custom-terminators]
    (if-let [f (get custom-terminators (terminator-code this))]
      `(~f ~state-sym ~value)
      `(do (aset-all! ~state-sym
                      ~VALUE-IDX ~value
                      ~STATE-IDX :finished)
           nil))))

(defrecord Set! [field object val]
  IInstruction
  (reads-from [this] [object val])
  (writes-to [this] [(:id this)])
  (block-references [this] [])
  IEmittableInstruction
  (emit-instruction [this state-sym]
    `[~(:id this)
      (set! (~field ~object) ~val)]))

(defrecord CondBr [test then-block else-block]
  IInstruction
  (reads-from [this] [test])
  (writes-to [this] [])
  (block-references [this] [then-block else-block])
  ITerminator
  (terminate-block [this state-sym _]
    `(do (if ~test
           (aset-all! ~state-sym
                      ~STATE-IDX ~then-block)
           (aset-all! ~state-sym
                      ~STATE-IDX ~else-block))
         :recur)))


(defrecord Try [catch-block catch-exception finally-block continue-block]
  IInstruction
  (reads-from [this] [])
  (writes-to [this] [])
  (block-references [this] [catch-block finally-block continue-block])
  IEmittableInstruction
  (emit-instruction [this state-sym]
    `[~'_ (cljs.core.async.impl.ioc-helpers/add-exception-frame ~state-sym
                                                                ~catch-block
                                                                ~catch-exception
                                                                ~finally-block
                                                                ~continue-block)]))

(defrecord ProcessExceptionWithValue [value]
  IInstruction
  (reads-from [this] [value])
  (writes-to [this] [])
  (block-references [this] [])
  ITerminator
  (terminate-block [this state-sym _]
    `(do (aset-all! ~state-sym
                    ~VALUE-IDX
                    ~value)
         (cljs.core.async.impl.ioc-helpers/process-exception ~state-sym)
         :recur)))

(defrecord EndCatchFinally []
  IInstruction
  (reads-from [this] [])
  (writes-to [this] [])
  (block-references [this] [])
  ITerminator
  (terminate-block [this state-sym _]
    `(do (cljs.core.async.impl.ioc-helpers/process-exception ~state-sym)
         :recur)))



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

;; given an sexpr, dispatch on the first item
(defmulti sexpr-to-ssa (fn [[x & _]]
                         x))

(defn is-special? [x]
  (let [^clojure.lang.MultiFn mfn sexpr-to-ssa]
    (.getMethod mfn x)))



(defn default-sexpr [args]
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
     [local-val-ids (all (map ; parallel bind
                          (fn [sym init]
                            (gen-plan
                             [itm-id (item-to-ssa init)
                              _ (push-alter-binding :locals assoc sym itm-id)]
                             itm-id))
                          syms
                          inits))
      _ (all (for [x syms]
               (pop-binding :locals)))
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

(defmethod sexpr-to-ssa 'set!
  [[_ [field obj] val]]
  (gen-plan
   [obj-id (item-to-ssa obj)
    val-id (item-to-ssa val)
    ret-id (add-instruction (->Set! field obj-id val-id))]
   ret-id))

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
                                  _ (if (not= expr-id ::terminated)
                                      (add-instruction (->Jmp expr-id end-blk))
                                      (no-op))]
                                 blk-id))
                              (map second clauses)))
      default-block (if (odd? (count body))
                      (gen-plan
                       [blk-id (add-block)
                        _ (set-block blk-id)
                        expr-id (item-to-ssa default)
                        _ (if (not= expr-id ::terminated)
                            (add-instruction (->Jmp expr-id end-blk))
                            (no-op))]
                       blk-id)
                      (no-op))
      _ (set-block start-blk)
      val-id (item-to-ssa val)
      case-id (add-instruction (->Case val-id (map first clauses) clause-blocks default-block))
      _ (set-block end-blk)
      ret-id (add-instruction (->Const ::value))]
     ret-id)))

(defmethod sexpr-to-ssa 'quote
  [expr]
  (gen-plan
   [ret-id (add-instruction (->Const expr))]
   ret-id))

(defmethod sexpr-to-ssa '.
  [[_ target method & args]]
  (let [args (if (seq? method)
               (next method)
               args)
        method (if (seq? method)
                 (first method)
                 method)]
    (gen-plan
     [target-id (item-to-ssa target)
      args-ids (all (map item-to-ssa args))
      ret-id (add-instruction (->Dot target-id method args-ids))]
     ret-id)))

(defmethod sexpr-to-ssa 'try
  [[_ & body]]
  (let [finally-fn (every-pred seq? (comp (partial = 'finally) first))
        catch-fn (every-pred seq? (comp (partial = 'catch) first))
        finally (next (first (filter finally-fn body)))
        body (remove finally-fn body)
        catch (next (first (filter catch-fn body)))
        [ex ex-bind & catch-body] catch
        body (remove catch-fn body)]
    (gen-plan
     [end-blk (add-block)
      finally-blk (if finally
                    (gen-plan
                     [cur-blk (get-block)
                      blk (add-block)
                      _ (set-block blk)
                      value-id (add-instruction (->Const ::value))
                      _ (all (map item-to-ssa finally))
                      _ (add-instruction (->EndCatchFinally))
                      _ (set-block cur-blk)]
                     blk)
                    (no-op))
      catch-blk (if catch
                  (gen-plan
                   [cur-blk (get-block)
                    blk (add-block)
                    _ (set-block blk)
                    ex-id (add-instruction (->Const ::value))
                    _ (push-alter-binding :locals assoc ex-bind ex-id)
                    ids (all (map item-to-ssa catch-body))
                    _ (add-instruction (->ProcessExceptionWithValue (last ids)))
                    _ (pop-binding :locals)
                    _ (set-block cur-blk)
                    _ (push-alter-binding :catch (fnil conj []) [ex blk])]
                   blk)
                  (no-op))
      body-blk (add-block)
      _ (add-instruction (->Jmp nil body-blk))
      _ (set-block body-blk)
      _ (add-instruction (->Try catch-blk ex finally-blk end-blk))
      ids (all (map item-to-ssa body))
      _ (if catch
          (pop-binding :catch)
          (no-op))
      _ (add-instruction (->ProcessExceptionWithValue (last ids)))
      _ (set-block end-blk)
      ret (add-instruction (->Const ::value))]
     ret)))

(defmethod sexpr-to-ssa 'recur
  [[_ & vals]]
  (gen-plan
   [val-ids (all (map item-to-ssa vals))
    recurs (get-binding :recur-nodes)
    _ (do (assert (= (count val-ids)
                     (count recurs))
                  "Wrong number of arguments to recur")
          (no-op))
    _ (add-instruction (->Recur recurs val-ids))

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


(def special-override? '#{case clojure.core/case
                          try clojure.core/try})

(defn expand [locals env form]
  (loop [form form]
    (if-not (seq? form)
      form
      (let [[s & r] form]
        (if (symbol? s)
          (if (or (get locals s)
                  (special-override? s))
            form
            (let [new-env (update-in env [:locals] merge locals)
                  expanded (cljs/macroexpand-1 new-env form)]
              (if (= expanded form)
                form
                (recur expanded))))
          form)))))

(defn terminate-custom [vals term]
  (gen-plan
   [blk (add-block)
    vals (all (map item-to-ssa vals))
    val (add-instruction (->CustomTerminator term blk vals))
    _ (set-block blk)
    res (add-instruction (->Const ::value))]
   res))

(defn fixup-aliases [sym env]
  (let [aliases (ns-aliases *ns*)]
    (if-not (namespace sym)
      sym
      (if-let [ns (or (get-in env [:ns :requires-macros (symbol (namespace sym))])
                      (get-in env [:ns :requires (symbol (namespace sym))]))]
        (symbol (name ns) (name sym))
        sym))))

(defmethod -item-to-ssa :list
  [lst]
  (gen-plan
   [env (get-binding :env)
    locals (get-binding :locals)
    terminators (get-binding :terminators)
    val (let [exp (expand locals env lst)]
          (if (seq? exp)
            (if (symbol? (first exp))
              (let [f (fixup-aliases (first exp) env)]
                (cond
                 (is-special? f) (sexpr-to-ssa exp)
                 (get locals f) (default-sexpr exp)
                 (get terminators f) (terminate-custom (next exp) (get terminators f))
                 :else (default-sexpr exp)))
              (default-sexpr exp))
            (item-to-ssa exp)))]
   val))

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
  [body env terminators]
  (-> (gen-plan
       [_ (push-binding :env env)
        _ (push-binding :locals (zipmap (:locals (keys env)) (:locals (keys env))))
        _ (push-binding :terminators terminators)
        blk (add-block)
        _ (set-block blk)
        ids (all (map item-to-ssa body))
        term-id (add-instruction (->Return (last ids)))
        _ (pop-binding :terminators)
        _ (pop-binding :locals)
        _ (pop-binding :env)]
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

(defn- emit-state-machine [machine num-user-params custom-terminators]
  (let [index (index-state-machine machine)
        state-sym (with-meta (gensym "state_")
                    {:tag 'objects})
        local-start-idx (+ num-user-params USER-START-IDX)
        state-arr-size (+ local-start-idx (count-persistent-values index))
        local-map (atom {::next-idx local-start-idx})
        block-catches (:block-catches machine)
        state-val-sym (gensym "state_val_")]
    `(let [switch# (fn [~state-sym]
                     (let [~state-val-sym (aget ~state-sym ~STATE-IDX)]
                       (cond
                        ~@(mapcat
                           (fn [[id blk]]
                             [`(== ~state-val-sym ~id)
                              `(let [~@(concat (build-block-preamble local-map index state-sym blk)
                                               (build-block-body state-sym blk))
                                     ~state-sym ~(build-new-state local-map index state-sym blk)]
                                 ~(terminate-block (last blk) state-sym custom-terminators))])
                           (:blocks machine)))))]
       (fn state-machine#
         ([] (aset-all! (make-array ~state-arr-size)
                        ~FN-IDX state-machine#
                        ~STATE-IDX ~(:start-block machine)))
         ([~state-sym]
            (let [ret-value# (try (loop []
                                    (let [result# (switch# ~state-sym)]
                                      (if (cljs.core/keyword-identical? result# :recur)
                                        (recur)
                                        result#)))
                                  (catch js/Object ex#
                                    (aset-all! ~state-sym ~CURRENT-EXCEPTION ex#)
                                    (cljs.core.async.impl.ioc-helpers/process-exception ~state-sym)
                                    :recur))]
              (if (cljs.core/keyword-identical? ret-value# :recur)
                (recur ~state-sym)
                ret-value#)))))))


(def async-custom-terminators
  {'<! 'cljs.core.async.impl.ioc-helpers/take!
   'cljs.core.async/<! 'cljs.core.async.impl.ioc-helpers/take!
   '>! 'cljs.core.async.impl.ioc-helpers/put!
   'cljs.core.async/>! 'cljs.core.async.impl.ioc-helpers/put!
   'alts! 'cljs.core.async.impl.ioc-helpers/ioc-alts!
   'cljs.core.async/alts! 'cljs.core.async.impl.ioc-helpers/ioc-alts!
   :Return 'cljs.core.async.impl.ioc-helpers/return-chan})


(defn state-machine [body num-user-params env user-transitions]
  (-> (parse-to-state-machine body env user-transitions)
      second
      (emit-state-machine num-user-params user-transitions)))
