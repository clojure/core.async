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
            [clojure.core.async.impl.protocols :as impl]
            [clojure.core.async.impl.dispatch :as dispatch]
            [clojure.set :refer (intersection)]
            [clojure.tools.analyzer.jvm :as an-jvm]
            [clojure.tools.analyzer.ast :as ast]
            [clojure.tools.analyzer :as an])
  (:import [java.util.concurrent.locks Lock]
           [java.util.concurrent.atomic AtomicReferenceArray]))

(defn debug [x]
  (pprint x)
  x)


(def ^:const FN-IDX 0)
(def ^:const STATE-IDX 1)
(def ^:const VALUE-IDX 2)
(def ^:const BINDINGS-IDX 3)
(def ^:const EXCEPTION-FRAMES 4)
(def ^:const CURRENT-EXCEPTION 5)
(def ^:const USER-START-IDX 6)

(defn aset-object [^AtomicReferenceArray arr idx ^Object o]
  (.set arr idx o))

(defn aget-object [^AtomicReferenceArray arr idx]
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
   [_ (update-in-plan [:block-id] (fnil inc -1))
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

(defrecord ExceptionFrame [catch-block
                           ^Class catch-exception
                           finally-block
                           continue-block
                           prev])

(defn add-exception-frame [state catch-block catch-exception finally-block continue-block]
  (aset-all! state
             EXCEPTION-FRAMES
             (->ExceptionFrame catch-block
                               catch-exception
                               finally-block
                               continue-block
                               (aget-object state EXCEPTION-FRAMES))))

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

(defrecord Try [catch-block catch-exception finally-block continue-block]
  IInstruction
  (reads-from [this] [])
  (writes-to [this] [])
  (block-references [this] [catch-block finally-block continue-block])
  IEmittableInstruction
  (emit-instruction [this state-sym]
    `[~'_ (add-exception-frame ~state-sym ~catch-block ~catch-exception ~finally-block ~continue-block)]))

(defrecord ProcessExceptionWithValue [value]
  IInstruction
  (reads-from [this] [value])
  (writes-to [this] [])
  (block-references [this] [])
  ITerminator
  (terminate-block [this state-sym _]
    `(do (aset-all! ~state-sym
                    VALUE-IDX
                    ~value)
         (process-exception ~state-sym)
         :recur)))

(defrecord EndCatchFinally []
  IInstruction
  (reads-from [this] [])
  (writes-to [this] [])
  (block-references [this] [])
  ITerminator
  (terminate-block [this state-sym _]
    `(do (process-exception ~state-sym)
         :recur)))

(defn process-exception [state]
  (let [exception-frame (aget-object state EXCEPTION-FRAMES)
        catch-block (:catch-block exception-frame)
        catch-exception (:catch-exception exception-frame)
        exception (aget-object state CURRENT-EXCEPTION)]
    (cond
     (and exception
          (not exception-frame))
     (throw exception)

     (and exception
          catch-block
          (instance? catch-exception exception))
     (aset-all! state
                STATE-IDX
                catch-block
                VALUE-IDX
                exception
                CURRENT-EXCEPTION
                nil
                EXCEPTION-FRAMES
                (assoc exception-frame
                  :catch-block nil
                  :catch-exception nil))


     (and exception
          (not catch-block)
          (not (:finally-block exception-frame)))

     (do (aset-all! state
                    EXCEPTION-FRAMES
                    (:prev exception-frame))
         (recur state))

     (and exception
          (not catch-block)
          (:finally-block exception-frame))
     (aset-all! state
                STATE-IDX
                (:finally-block exception-frame)
                EXCEPTION-FRAMES
                (assoc exception-frame
                  :finally-block nil))

     (and (not exception)
          (:finally-block exception-frame))
     (do (aset-all! state
                    STATE-IDX
                    (:finally-block exception-frame)
                    EXCEPTION-FRAMES
                    (assoc exception-frame
                      :finally-block nil)))

     (and (not exception)
          (not (:finally-block exception-frame)))
     (do (aset-all! state
                   STATE-IDX
                   (:continue-block exception-frame)
                   EXCEPTION-FRAMES
                   (:prev exception-frame))))))

(defmulti -emit :op)

(defn emit [x]
  (if (= (:ssa-transform? x) false)
    (gen-plan
     [resid (add-instruction {:op :ssa-inline-expr
                              :expr x})]
     resid)
    (-emit x)))


(defmethod -emit :const
  [{:as op}]
  (add-instruction op))

(defmethod -emit :if
  [{:keys [test then else] :as op}]
  (println (keys op))
  (gen-plan
   [then-block (add-block)
    else-block (add-block)
    end-block (add-block)

    test-result (emit test)

    _ (add-instruction {:op :cond-jmp
                        :test test-result
                        :true then-block
                        :false else-block
                        :terminator? true})
    _ (set-block then-block)

    then-val (emit then)
    _ (add-instruction {:op :jmp
                        :block end-block
                        :terminator? true})
    _ (set-block else-block)

    else-val (emit else)
    _ (add-instruction {:op :jmp
                        :block end-block
                        :terminator? true})
    _ (set-block end-block)

    phi-val (add-instruction {:op :phi
                              :inbound {then-block then-val
                                        else-block else-val}})]
   phi-val))

(defmethod -emit :binding
  [{:keys [name init] :as op}]
  (println (keys op) "binding" init)
  (gen-plan
   [v (emit init)
    _ (push-alter-binding :locals assoc name v)]
   v))

(defmethod -emit :let
  [{:keys [bindings body] :as op}]
  (println (keys op) body)
  (gen-plan
   [bindings (all (map emit bindings))
    body-ids (emit body)
    _ (all (map (fn [_](pop-binding :locals))
                (range (count bindings))))]
   body-ids))

(defmethod -emit :local
  [{:keys [name] :as op}]
  (println (keys op) name)
  (gen-plan
   [locals (get-binding :locals)]
   (get locals name)))

(defmethod -emit :invoke
  [{:keys [args] :as op}]
  (println (keys op) (:fn op))
  (gen-plan
   [fn-id (emit (:fn op))
    args-ids (all (map emit args))
    result (add-instruction (assoc op :fn fn-id :args args-ids))]
   result))

(defn debug [x]
  (clojure.pprint/pprint x)
  x)

(defn emit-ssa [ast]
  (->> (gen-plan
        [start-blk (add-block)
         _ (set-block start-blk)
         resid (emit ast)]
        resid)
       (get-plan)
       second
       :blocks
       (assoc {:op :ssa
               :children [:blocks]} :blocks)
       debug))

(def ^:dynamic *dfn* 32)

(defn mark-ssa-transform-limits [transform? ast]
  (if (or (transform? ast)
          (some :ssa-transform? (ast/children ast)))
    (assoc ast :ssa-transform? true)
    (assoc ast :ssa-transform? false)))


(defn analyze
  [form env]
  (binding [an/macroexpand-1 an-jvm/macroexpand-1
            an/create-var    an-jvm/create-var
            an/parse         an-jvm/parse
            an/var?          var?]
    (an/analyze form env)))

(defn ^:terminator foo [x y]
  (+ x y))







(def -ast->state :b)
(defmulti -ast->state (fn [{:keys [op]} _]
                      op))

(defprotocol ISSAState
  (state-set-block! [this ^long block-id])
  (state-get-block ^long [this])
  (state-set-last-block! [this ^long block-id])
  (state-get-last-block ^long [this])
  (state-set-object! [this ^int idx object])
  (state-get-object [this ^int idx])
  (state-set-long! [this ^long idx ^long val])
  (state-get-long [this ^long idx])
  (state-set-double! [this ^long idx ^double val])
  (state-get-double [this ^long idx]))

(def -ast-clj :b)
(defmulti -ast->clj (fn [{:keys [op]} _]
                      op))

(defn ast->clj [ast state]
  (-ast->clj ast state))

(defn ast->state [ast state]
  (-ast->state ast state))






(defmethod -ast->state :phi
  [{:keys [inbound id]} {:keys [state-sym] :as state}]
  `[~id (case (state-get-last-block ~state-sym)
          ~@(mapcat
             (fn [[id inst]]
               [id inst])
             inbound))])


(defmethod -ast->state :ssa-inline-expr
  [{:keys [id expr]} state]
  [id (ast->clj expr state)])

(defmethod -ast->state :invoke
  [{:keys [id fn args]} _]
  `[id (~fn ~@args)])

(defmethod -ast->state :const
  [{:keys [id val]} _]
  [id val])

(defmethod -ast->state :var
  [{:keys [id var]} state]
  [id var])



(defmethod -ast->state :ssa
  [{:keys [blocks]} {:keys [state-sym] :as state}]
  `(loop []
     (let [result# (case (get-block ~state-sym)
                     ~@(mapcat
                        (fn [[blockid insts]]
                          [blockid `(let [~@(mapcat (fn [x]
                                                      (ast->state x (assoc state :block-id blockid)))
                                                    (butlast insts))])])
                        blocks))]
       (if (identical? ::recur result#)
         (recur)
         result#))))










(-> (an-jvm/analyze '(if true (let [x 42] (foo x)) 43) (an-jvm/empty-env))
    (ast/postwalk (partial mark-ssa-transform-limits
                           (fn [x]
                             (-> x :fn :var meta :terminator))))
    (debug)
    emit-ssa
    (ast->clj {:state-sym ::statesym})
    (clojure.pprint/pprint))
