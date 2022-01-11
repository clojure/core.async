;   Copyright (c) Rich Hickey. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

;; by Timothy Baldridge
;; April 13, 2013

(ns ^{:skip-wiki true}
  clojure.core.async.impl.ioc-macros
  (:refer-clojure :exclude [all])
  (:require [clojure.pprint :refer [pprint]]
            [clojure.tools.analyzer :as an]
            [clojure.tools.analyzer.ast :as ast]
            [clojure.tools.analyzer.env :as env]
            [clojure.tools.analyzer.passes :refer [schedule]]
            [clojure.tools.analyzer.passes.jvm.annotate-loops :refer [annotate-loops]]
            [clojure.tools.analyzer.passes.jvm.warn-on-reflection :refer [warn-on-reflection]]
            [clojure.tools.analyzer.jvm :as an-jvm]
            [clojure.core.async.impl.protocols :as impl]
            [clojure.core.async.impl.dispatch :as dispatch]
            [clojure.set :refer (intersection union difference)])
  (:import [java.util.concurrent.locks Lock]
           [java.util.concurrent.atomic AtomicReferenceArray]))

(defn debug [x]
  (pprint x)
  x)

(def ^{:const true :tag 'long} FN-IDX 0)
(def ^{:const true :tag 'long} STATE-IDX 1)
(def ^{:const true :tag 'long} VALUE-IDX 2)
(def ^{:const true :tag 'long} BINDINGS-IDX 3)
(def ^{:const true :tag 'long} EXCEPTION-FRAMES 4)
(def ^{:const true :tag 'long} USER-START-IDX 5)

(defn aset-object [^AtomicReferenceArray arr ^long idx o]
  (.set arr idx o))

(defn aget-object [^AtomicReferenceArray arr ^long idx]
  (.get arr idx))

(defmacro aset-all!
  [arr & more]
  (assert (even? (count more)) "Must give an even number of args to aset-all!")
  (let [bindings (partition 2 more)
        arr-sym (gensym "statearr-")]
    `(let [~arr-sym ~arr]
       ~@(map
          (fn [[idx val]]
            `(aset-object ~arr-sym ~idx ~val))
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
      `[~(:id this) (aget-object ~state-sym ~VALUE-IDX)]
      `[~(:id this) ~value])))

(defrecord RawCode [ast locals]
  IInstruction
  (reads-from [this]
    (for [local (map :name (-> ast :env :locals vals))
          :when (contains? locals local)]
      (get locals local)))
  (writes-to [this] [(:id this)])
  (block-references [this] [])
  IEmittableInstruction
  (emit-instruction [this state-sym]
    (if (not-empty (reads-from this))
      `[~@(into []
            (comp
              (map #(select-keys % [:op :name :form]))
              (filter (fn [local]
                        (contains? locals (:name local))))
              (distinct)
              (mapcat
                (fn [local]
                  `[~(:form local) ~(get locals (:name local))])))
            (-> ast :env :locals vals))
        ~(:id this) ~(:form ast)]
      `[~(:id this) ~(:form ast)])))

(defrecord CustomTerminator [f blk values meta]
  IInstruction
  (reads-from [this] values)
  (writes-to [this] [])
  (block-references [this] [])
  ITerminator
  (terminate-block [this state-sym _]
    (with-meta `(~f ~state-sym ~blk ~@values)
      meta)))

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

(defrecord StaticCall [class method refs]
  IInstruction
  (reads-from [this] refs)
  (writes-to [this] [(:id this)])
  (block-references [this] [])
  IEmittableInstruction
  (emit-instruction [this state-sym]
    `[~(:id this) (. ~class ~method ~@(seq refs))]))

(defrecord InstanceInterop [instance-id op refs]
  IInstruction
  (reads-from [this] (cons instance-id refs))
  (writes-to [this] [(:id this)])
  (block-references [this] [])
  IEmittableInstruction
  (emit-instruction [this state-sym]
    `[~(:id this) (. ~instance-id ~op ~@(seq refs))]))

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

(defrecord Dot [cls-or-instance method args]
  IInstruction
  (reads-from [this] `[~cls-or-instance ~method ~@args])
  (writes-to [this] [(:id this)])
  (block-references [this] [])
  IEmittableInstruction
  (emit-instruction [this state-sym]
    `[~(:id this) (. ~cls-or-instance ~method ~@args)]))

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
                      ~STATE-IDX ::finished)
           nil))))

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

(defrecord PushTry [catch-block]
  IInstruction
  (reads-from [this] [])
  (writes-to [this] [])
  (block-references [this] [catch-block])
  IEmittableInstruction
  (emit-instruction [this state-sym]
    `[~'_ (aset-all! ~state-sym ~EXCEPTION-FRAMES (cons ~catch-block (aget-object ~state-sym ~EXCEPTION-FRAMES)))]))

(defrecord PopTry []
  IInstruction
  (reads-from [this] [])
  (writes-to [this] [])
  (block-references [this] [])
  IEmittableInstruction
  (emit-instruction [this state-sym]
    `[~'_ (aset-all! ~state-sym ~EXCEPTION-FRAMES (rest (aget-object ~state-sym ~EXCEPTION-FRAMES)))]))

(defrecord CatchHandler [catches]
  IInstruction
  (reads-from [this] [])
  (writes-to [this] [])
  (block-references [this] (map first catches))
  ITerminator
  (terminate-block [this state-sym _]
    (let [ex (gensym 'ex)]
      `(let [~ex (aget-object ~state-sym ~VALUE-IDX)]
         (cond
          ~@(for [[handler-idx type] catches
                  i [`(instance? ~type ~ex) `(aset-all! ~state-sym ~STATE-IDX ~handler-idx)]]
              i)
          :else (throw ~ex))
         :recur))))

(defrecord EndFinally [exception-local]
  IInstruction
  (reads-from [this] [exception-local])
  (writes-to [this] [])
  (block-references [this] [])
  IEmittableInstruction
  (emit-instruction [this state-sym]
    `[~'_ (throw ~exception-local)]))

;; Dispatch clojure forms based on :op
(def -item-to-ssa nil) ;; for help in the repl
(defmulti -item-to-ssa :op)

(defmethod -item-to-ssa :default
  [ast]
  (gen-plan
   [locals (get-binding :locals)
    id (add-instruction (->RawCode ast locals))]
   id))

(defn item-to-ssa [ast]
  (if (or (::transform? ast)
          (contains? #{:local :const :quote} (:op ast)))
    (-item-to-ssa ast)
    (gen-plan
     [locals (get-binding :locals)
      id (add-instruction (->RawCode ast locals))]
     id)))

(defmethod -item-to-ssa :invoke
  [{f :fn args :args}]
  (gen-plan
   [arg-ids (all (map item-to-ssa (cons f args)))
    inst-id (add-instruction (->Call arg-ids))]
   inst-id))

(defmethod -item-to-ssa :keyword-invoke
  [{f :keyword target :target}]
  (gen-plan
   [arg-ids (all (map item-to-ssa (list f target)))
    inst-id (add-instruction (->Call arg-ids))]
   inst-id))

(defmethod -item-to-ssa :protocol-invoke
  [{f :protocol-fn target :target args :args}]
  (gen-plan
   [arg-ids (all (map item-to-ssa (list* f target args)))
    inst-id (add-instruction (->Call arg-ids))]
   inst-id))

(defmethod -item-to-ssa :instance?
  [{:keys [class target]}]
  (gen-plan
   [arg-id (item-to-ssa target)
    inst-id (add-instruction (->Call (list `instance? class arg-id)))]
   inst-id))

(defmethod -item-to-ssa :prim-invoke
  [{f :fn args :args}]
  (gen-plan
   [arg-ids (all (map item-to-ssa (cons f args)))
    inst-id (add-instruction (->Call arg-ids))]
   inst-id))

(defmethod -item-to-ssa :instance-call
  [{:keys [instance method args]}]
  (gen-plan
   [arg-ids (all (map item-to-ssa args))
    instance-id (item-to-ssa instance)
    inst-id (add-instruction (->InstanceInterop instance-id method arg-ids))]
   inst-id))

(defmethod -item-to-ssa :instance-field
  [{:keys [instance field]}]
  (gen-plan
   [instance-id (item-to-ssa instance)
    inst-id (add-instruction (->InstanceInterop instance-id (symbol (str "-" field)) ()))]
   inst-id))

(defmethod -item-to-ssa :host-interop
  [{:keys [target m-or-f]}]
  (gen-plan
   [instance-id (item-to-ssa target)
    inst-id (add-instruction (->InstanceInterop instance-id m-or-f ()))]
   inst-id))

(defmethod -item-to-ssa :static-call
  [{:keys [class method args]}]
  (gen-plan
   [arg-ids (all (map item-to-ssa args))
    inst-id (add-instruction (->StaticCall class method arg-ids))]
   inst-id))

(defmethod -item-to-ssa :set!
  [{:keys [val target]}]
  (gen-plan
   [arg-ids (all (map item-to-ssa (list target val)))
    inst-id (add-instruction (->Call (cons 'set! arg-ids)))]
   inst-id))

(defn var-name [v]
  (let [nm (:name (meta v))
        nsp (.getName ^clojure.lang.Namespace (:ns (meta v)))]
    (symbol (name nsp) (name nm))))


(defmethod -item-to-ssa :var
  [{:keys [var]}]
  (gen-plan
   []
   (var-name var)))

(defmethod -item-to-ssa :const
  [{:keys [form]}]
  (gen-plan
   []
   form))

(defn let-binding-to-ssa
  [{:keys [name init form]}]
  (gen-plan
   [bind-id (item-to-ssa init)
    _ (push-alter-binding :locals assoc (vary-meta name merge (meta form)) bind-id)]
   bind-id))

(defmethod -item-to-ssa :let
  [{:keys [bindings body]}]
  (gen-plan
   [let-ids (all (map let-binding-to-ssa bindings))
    _ (all (map (fn [_] (pop-binding :locals)) bindings))

    local-ids (all (map (comp add-instruction ->Const) let-ids))
    _ (push-alter-binding :locals merge (into {} (map (fn [id {:keys [name form]}]
                                                        [name (vary-meta id merge (meta form))])
                                                      local-ids bindings)))

    body-id (item-to-ssa body)
    _ (pop-binding :locals)]
   body-id))

(defmethod -item-to-ssa :loop
  [{:keys [body bindings] :as ast}]
  (gen-plan
   [local-val-ids (all (map let-binding-to-ssa bindings))
    _ (all (for [_ bindings]
             (pop-binding :locals)))
    local-ids (all (map (comp add-instruction ->Const) local-val-ids))
    body-blk (add-block)
    final-blk (add-block)
    _ (add-instruction (->Jmp nil body-blk))

    _ (set-block body-blk)
    _ (push-alter-binding :locals merge (into {} (map (fn [id {:keys [name form]}]
                                                        [name (vary-meta id merge (meta form))])
                                                      local-ids bindings)))
    _ (push-binding :recur-point body-blk)
    _ (push-binding :recur-nodes local-ids)

    ret-id (item-to-ssa body)

    _ (pop-binding :recur-nodes)
    _ (pop-binding :recur-point)
    _ (pop-binding :locals)
    _ (if (not= ret-id ::terminated)
        (add-instruction (->Jmp ret-id final-blk))
        (no-op))
    _ (set-block final-blk)
    ret-id (add-instruction (->Const ::value))]
   ret-id))

(defmethod -item-to-ssa :do
  [{:keys [statements ret] :as ast}]
  (gen-plan
   [_ (all (map item-to-ssa statements))
    ret-id (item-to-ssa ret)]
   ret-id))

(defmethod -item-to-ssa :case
  [{:keys [test tests thens default] :as ast}]
  (gen-plan
   [end-blk (add-block)
    start-blk (get-block)
    clause-blocks (all (map (fn [expr]
                              (assert expr)
                              (gen-plan
                               [blk-id (add-block)
                                _ (set-block blk-id)
                                expr-id (item-to-ssa expr)
                                _ (if (not= expr-id ::terminated)
                                    (add-instruction (->Jmp expr-id end-blk))
                                    (no-op))]
                               blk-id))
                            (map :then thens)))
    default-block (if default
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
    val-id (item-to-ssa test)
    case-id (add-instruction (->Case val-id (map (comp :form :test) tests)
                                     clause-blocks
                                     default-block))
    _ (set-block end-blk)
    ret-id (add-instruction (->Const ::value))]
   ret-id))

(defmethod -item-to-ssa :quote
  [{:keys [form]}]
  (gen-plan
   [ret-id (add-instruction (->Const form))]
   ret-id))

(defmethod -item-to-ssa :try
  [{:keys [catches body finally] :as ast}]
  (let [make-finally (fn [exit-block rethrow?]
                       (if finally
                         (gen-plan
                          [cur-blk (get-block)
                           finally-blk (add-block)
                           _ (set-block finally-blk)
                           ;; catch block has to pop itself off of
                           ;; EXCEPTION-FRAMES.  every try/catch pushes at
                           ;; least 1 frame on to EXCEPTION-FRAMES,
                           ;; try/catch/finally pushes 2. The exception
                           ;; handling machinery around the state machine
                           ;; pops one off when handling an exception.
                           _ (add-instruction (->PopTry))
                           result-id (add-instruction (->Const ::value))
                           _ (item-to-ssa finally)
                           ;; rethrow exception on exception path
                           _  (if rethrow?
                                (add-instruction (->EndFinally result-id))
                                (no-op))
                           _ (add-instruction (->Jmp result-id exit-block))
                           _ (set-block cur-blk)]
                          finally-blk)
                         (gen-plan [] exit-block)))]
    (gen-plan
     [body-block (add-block)
      exit-block (add-block)
      ;; Two routes to the finally block, via normal execution and
      ;; exception execution
      finally-blk (make-finally exit-block false)
      exception-finally-blk (make-finally exit-block true)
      catch-blocks (all
                    (for [{ex-bind :local {ex :val} :class catch-body :body} catches]
                      (gen-plan
                       [cur-blk (get-block)
                        catch-blk (add-block)
                        _ (set-block catch-blk)
                        ex-id (add-instruction (->Const ::value))
                        _ (push-alter-binding :locals assoc (:name ex-bind)
                                              (vary-meta ex-id merge (when (:tag ex-bind)
                                                                       {:tag (.getName ^Class (:tag ex-bind))})))
                        result-id (item-to-ssa catch-body)
                        ;; if there is a finally, jump to it after
                        ;; handling the exception, if not jump to exit
                        _ (add-instruction (->Jmp result-id finally-blk))
                        _ (pop-binding :locals)
                        _ (set-block cur-blk)]
                       [catch-blk ex])))
      ;; catch block handler routes exceptions to the correct handler,
      ;; rethrows if there is no match
      catch-handler-block (add-block)
      cur-blk (get-block)
      _ (set-block catch-handler-block)
      _ (add-instruction (->PopTry)) ; pop catch-handler-block
      _ (add-instruction (->CatchHandler catch-blocks))
      _ (set-block cur-blk)
      _ (add-instruction (->Jmp nil body-block))
      _ (set-block body-block)
      ;; the finally gets pushed on to the exception handler stack, so
      ;; it will be executed if there is an exception
      _ (if finally
          (add-instruction (->PushTry exception-finally-blk))
          (no-op))
      _ (add-instruction (->PushTry catch-handler-block))
      body (item-to-ssa body)
      _ (add-instruction (->PopTry)) ; pop catch-handler-block
      ;; if the body finishes executing normally, jump to the finally
      ;; block, if it exists
      _ (add-instruction (->Jmp body finally-blk))
      _ (set-block exit-block)
      ret (add-instruction (->Const ::value))]
     ret)))

(defmethod -item-to-ssa :throw
  [{:keys [exception] :as ast}]
  (gen-plan
   [exception-id (item-to-ssa exception)
    ret-id (add-instruction (->Call ['throw exception-id]))]
   ret-id))

(defmethod -item-to-ssa :new
  [{:keys [args class] :as ast}]
  (gen-plan
   [arg-ids (all (map item-to-ssa args))
    ret-id (add-instruction (->Call (list* 'new (:val class) arg-ids)))]
   ret-id))

(defmethod -item-to-ssa :recur
  [{:keys [exprs] :as ast}]
  (gen-plan
   [val-ids (all (map item-to-ssa exprs))
    recurs (get-binding :recur-nodes)
    _ (do (assert (= (count val-ids)
                     (count recurs))
                  "Wrong number of arguments to recur")
          (no-op))
    _ (add-instruction (->Recur recurs val-ids))

    recur-point (get-binding :recur-point)

    _ (add-instruction (->Jmp nil recur-point))]
   ::terminated))

(defmethod -item-to-ssa :if
  [{:keys [test then else]}]
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

(defmethod -item-to-ssa :transition
  [{:keys [name args form]}]
  (gen-plan
   [blk (add-block)
    vals (all (map item-to-ssa args))
    val (add-instruction (->CustomTerminator name blk vals (meta form)))
    _ (set-block blk)
    res (add-instruction (->Const ::value))]
   res))

(defmethod -item-to-ssa :local
  [{:keys [name form]}]
  (gen-plan
   [locals (get-binding :locals)
    inst-id (if (contains? locals name)
              (fn [p]
                [(locals name) p])
              (fn [p]
                [form p]))]
   inst-id))

(defmethod -item-to-ssa :map
  [{:keys [keys vals]}]
  (gen-plan
   [keys-ids (all (map item-to-ssa keys))
    vals-ids (all (map item-to-ssa vals))
    id (add-instruction (->Call (cons 'clojure.core/hash-map
                             (interleave keys-ids vals-ids))))]
   id))

(defmethod -item-to-ssa :with-meta
  [{:keys [expr meta]}]
  (gen-plan
   [meta-id (item-to-ssa meta)
    expr-id (item-to-ssa expr)
    id (add-instruction (->Call (list 'clojure.core/with-meta expr-id meta-id)))]
   id))

(defmethod -item-to-ssa :record
  [x]
  (-item-to-ssa `(~(symbol (.getName (class x)) "create")
                  (hash-map ~@(mapcat identity x)))))

(defmethod -item-to-ssa :vector
  [{:keys [items]}]
  (gen-plan
   [item-ids (all (map item-to-ssa items))
    id (add-instruction (->Call (cons 'clojure.core/vector
                                      item-ids)))]
   id))

(defmethod -item-to-ssa :set
  [{:keys [items]}]
  (gen-plan
   [item-ids (all (map item-to-ssa items))
    id (add-instruction (->Call (cons 'clojure.core/hash-set
                                      item-ids)))]
   id))

(defn parse-to-state-machine
  "Takes an sexpr and returns a hashmap that describes the execution flow of the sexpr as
   a series of SSA style blocks."
  [body terminators]
  (-> (gen-plan
       [_ (push-binding :terminators terminators)
        blk (add-block)
        _ (set-block blk)
        id (item-to-ssa body)
        term-id (add-instruction (->Return id))
        _ (pop-binding :terminators)]
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
  (transduce
    (comp (filter instruction?) (filter (partial persistent-value? index)))
    (completing (fn [acc _] (inc acc))) 0 (keys index)))

(defn- build-block-preamble [local-map idx state-sym blk]
  (let [args (into [] (comp
                        (mapcat reads-from)
                        (filter instruction?)
                        (filter (partial persistent-value? idx))
                        (distinct))
               blk)]
    (if (empty? args)
      []
      (mapcat (fn [sym]
             `[~sym (aget-object ~state-sym ~(id-for-inst local-map sym))])
              args))))

(defn- build-block-body [state-sym blk]
  (mapcat
   #(emit-instruction % state-sym)
   (butlast blk)))

(defn- build-new-state [local-map idx state-sym blk]
  (let [results (into [] (comp
                           (mapcat writes-to)
                           (filter instruction?)
                           (filter (partial persistent-value? idx))
                           (distinct))
                    blk)
        results (interleave (map (partial id-for-inst local-map) results) results)]
    (if-not (empty? results)
      [state-sym `(aset-all! ~state-sym ~@results)]
      [])))

(defn- emit-state-machine [machine num-user-params custom-terminators]
  (let [index (index-state-machine machine)
        state-sym (with-meta (gensym "state_")
                    {:tag 'objects})
        local-start-idx (+ num-user-params USER-START-IDX)
        state-arr-size (+ local-start-idx (count-persistent-values index))
        local-map (atom {::next-idx local-start-idx})
        block-catches (:block-catches machine)]
    `(fn state-machine#
       ([] (aset-all! (AtomicReferenceArray. ~state-arr-size)
                      ~FN-IDX state-machine#
                      ~STATE-IDX ~(:start-block machine)))
       ([~state-sym]
          (let [old-frame# (clojure.lang.Var/getThreadBindingFrame)
                ret-value# (try
                             (clojure.lang.Var/resetThreadBindingFrame (aget-object ~state-sym ~BINDINGS-IDX))
                             (loop []
                               (let [result# (case (int (aget-object ~state-sym ~STATE-IDX))
                                               ~@(mapcat
                                                  (fn [[id blk]]
                                                    [id `(let [~@(concat (build-block-preamble local-map index state-sym blk)
                                                                         (build-block-body state-sym blk))
                                                               ~@(build-new-state local-map index state-sym blk)]
                                                           ~(terminate-block (last blk) state-sym custom-terminators))])
                                                  (:blocks machine)))]
                                 (if (identical? result# :recur)
                                   (recur)
                                   result#)))
                             (catch Throwable ex#
                               (aset-all! ~state-sym ~VALUE-IDX ex#)
                               (if (seq (aget-object ~state-sym ~EXCEPTION-FRAMES))
                                 (aset-all! ~state-sym ~STATE-IDX (first (aget-object ~state-sym ~EXCEPTION-FRAMES)))
                                 (throw ex#))
                               :recur)
                             (finally
                               (aset-object ~state-sym ~BINDINGS-IDX (clojure.lang.Var/getThreadBindingFrame))
                               (clojure.lang.Var/resetThreadBindingFrame old-frame#)))]
            (if (identical? ret-value# :recur)
              (recur ~state-sym)
              ret-value#))))))

(defn finished?
  "Returns true if the machine is in a finished state"
  [state-array]
  (identical? (aget-object state-array STATE-IDX) ::finished))

(defn- fn-handler
  [f]
  (reify
   Lock
   (lock [_])
   (unlock [_])

   impl/Handler
   (active? [_] true)
   (blockable? [_] true)
   (lock-id [_] 0)
   (commit [_] f)))


(defn run-state-machine [state]
  ((aget-object state FN-IDX) state))

(defn run-state-machine-wrapped [state]
  (try
    (run-state-machine state)
    (catch Throwable ex
      (impl/close! (aget-object state USER-START-IDX))
      (throw ex))))

(defn take! [state blk c]
  (if-let [cb (impl/take! c (fn-handler
                                   (fn [x]
                                     (aset-all! state VALUE-IDX x STATE-IDX blk)
                                     (run-state-machine-wrapped state))))]
    (do (aset-all! state VALUE-IDX @cb STATE-IDX blk)
        :recur)
    nil))

(defn put! [state blk c val]
  (if-let [cb (impl/put! c val (fn-handler (fn [ret-val]
                                             (aset-all! state VALUE-IDX ret-val STATE-IDX blk)
                                             (run-state-machine-wrapped state))))]
    (do (aset-all! state VALUE-IDX @cb STATE-IDX blk)
        :recur)
    nil))

(defn return-chan [state value]
  (let [c (aget-object state USER-START-IDX)]
           (when-not (nil? value)
             (impl/put! c value (fn-handler (fn [_] nil))))
           (impl/close! c)
           c))

(def async-custom-terminators
  {'clojure.core.async/<! `take!
   'clojure.core.async/>! `put!
   'clojure.core.async/alts! 'clojure.core.async/ioc-alts!
   :Return `return-chan})

(defn mark-transitions
  {:pass-info {:walk :post :depends #{} :after an-jvm/default-passes}}
  [{:keys [op fn] :as ast}]
  (let [transitions (-> (env/deref-env) :passes-opts :mark-transitions/transitions)]
    (if (and (= op :invoke)
             (= (:op fn) :var)
             (contains? transitions (var-name (:var fn))))
      (merge ast
             {:op   :transition
              :name (get transitions (var-name (:var fn)))})
      ast)))

(defn propagate-transitions
  {:pass-info {:walk :post :depends #{#'mark-transitions}}}
  [{:keys [op] :as ast}]
  (if (or (= op :transition)
          (some #(or (= (:op %) :transition)
                     (::transform? %))
                (ast/children ast)))
    (assoc ast ::transform? true)
    ast))

(defn propagate-recur
  {:pass-info {:walk :post :depends #{#'annotate-loops #'propagate-transitions}}}
  [ast]
  (if (and (= (:op ast) :loop)
           (::transform? ast))
    ;; If we are a loop and we need to transform, and
    ;; one of our children is a recur, then we must transform everything
    ;; that has a recur
    (let [loop-id (:loop-id ast)]
      (ast/postwalk ast #(if (contains? (:loops %) loop-id)
                           (assoc % ::transform? true)
                           %)))
    ast))

(defn nested-go? [env]
  (-> env vals first map?))

(defn make-env [input-env crossing-env]
  (assoc (an-jvm/empty-env)
         :locals (into {}
                       (if (nested-go? input-env)
                         (for [[l expr] input-env
                               :let [local (get crossing-env l)]]
                           [local (-> expr
                                      (assoc :form local)
                                      (assoc :name local))])
                         (for [l (keys input-env)
                               :let [local (get crossing-env l)]]
                           [local {:op :local
                                   :form local
                                   :name local}])))))

(defn pdebug [x]
  (clojure.pprint/pprint x)
  (println "----")
  x)

(def passes (into (disj an-jvm/default-passes #'warn-on-reflection)
                  #{#'propagate-recur
                    #'propagate-transitions
                    #'mark-transitions}))

(def run-passes
  (schedule passes))

(defn emit-hinted [local tag env]
  (let [tag (or tag (-> local meta :tag))
        init (list (get env local))]
    (if-let [prim-fn (case (cond-> tag (string? tag) symbol)
                       int `int
                       long `long
                       char `char
                       float `float
                       double `double
                       byte `byte
                       short `short
                       boolean `boolean
                       nil)]
      [(vary-meta local dissoc :tag) (list prim-fn init)]
      [(vary-meta local merge (when tag {:tag tag})) init])))

(defn state-machine [body num-user-params [crossing-env env] user-transitions]
  (binding [an-jvm/run-passes run-passes]
    (-> (an-jvm/analyze `(let [~@(if (nested-go? env)
                                   (mapcat (fn [[l {:keys [tag]}]]
                                             (emit-hinted l tag crossing-env))
                                           env)
                                   (mapcat (fn [[l ^clojure.lang.Compiler$LocalBinding lb]]
                                             (emit-hinted l (when (.hasJavaClass lb)
                                                              (some-> lb .getJavaClass .getName))
                                                          crossing-env))
                                           env))]
                           ~body)
                        (make-env env crossing-env)
                        {:passes-opts (merge an-jvm/default-passes-opts
                                             {:uniquify/uniquify-env true
                                              :mark-transitions/transitions user-transitions})})
        (parse-to-state-machine user-transitions)
        second
        (emit-state-machine num-user-params user-transitions))))
