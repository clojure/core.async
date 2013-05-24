(ns core.async.impl.ioc-alt
  (:require [core.async.impl.ioc-macros :refer :all :as m]
            [core.async.impl.protocols :as impl]
            [core.async.impl.dispatch :as dispatch]
            [core.async.impl.alt-helpers :refer [alt-handler alt-flag random-array]]))

(defn handle-take [label val state block-id]
  (let [new-state (assoc state ::m/value [label val] ::m/state block-id)]
    (async-chan-wrapper new-state)))

(defn handle-put [label state block-id]
  (let [new-state (assoc state ::m/value [label] ::m/state block-id)]
    (async-chan-wrapper new-state)))


(defrecord Alt [clauses default next-block-id default-block-id]
  IInstruction
  (reads-from [this] (remove nil? (mapcat (juxt :port-id :arg-id) clauses)))
  (writes-to [this] [])
  (block-references [this] [])
  (emit-instruction [this state-sym]
    (let [gflag (gensym)
          ops (map (fn [{:keys [label op port-id arg-id]}]
                     (case (name op)
                       "<!" `(fn [] (impl/take! ~port-id (alt-handler ~gflag (fn [val#] (handle-take ~label
                                                                                                     val#
                                                                                                     ~state-sym
                                                                                                     ~next-block-id)))))
                       ">!" `(fn [] (impl/put! ~port-id  ~arg-id (alt-handler ~gflag (fn [] (handle-put ~label
                                                                                                        ~state-sym
                                                                                                        ~next-block-id)))))))
                   clauses)
          defops (when default
                   `((impl/lock ~gflag)
                     (let [got# (and (impl/active? ~gflag) (impl/commit ~gflag))]
                       (impl/unlock ~gflag)
                       (when got#
                         (async-chan-wrapper (assoc ~state-sym ::m/value nil ::m/state ~default-block-id))))))]
      `(let [~gflag (alt-flag)
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
         ::m/queued))))


(defmethod sexpr-to-ssa 'core.async.impl.ioc-alt/alt
  [[_ & clauses]]
  (let [clauses (partition 2 clauses)
        default (first (filter #(= :default (first %)) clauses))
        clauses (remove #(= :default (first %)) clauses)
        clauses (for [[label [op port arg]] clauses]
                  {:label label
                   :op op
                   :port port
                   :arg arg})]
    (assert (every? keyword? (map :label clauses)) "alt clauses must begin with keywords")
    (assert (every? #{"<!" ">!"} (map (comp name :op) clauses)) "alt exprs must be <! or >!")
    (gen-plan
     [default-blk (add-block)
      end-blk (add-block)
      alt-blk (get-block)
      clause-data (all (for [clause clauses]
                         (gen-plan
                          [port-id (item-to-ssa (:port clause))
                           arg-id (if (:arg clause)
                                    (item-to-ssa (:arg clause))
                                    (no-op))]
                          (assoc clause :port-id port-id :arg-id arg-id))))
      
      alt-id (add-instruction (->Alt clause-data default end-blk default-blk))

      _ (set-block default-blk)
      default-id (if default
                   (item-to-ssa (vec default))
                   (no-op))
      _ (add-instruction (->Jmp default-id end-blk))
      _ (set-block end-blk)
      val-id (add-instruction (->Const ::m/value))]
     val-id)))

