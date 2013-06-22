(ns cljs.core.async.macros
  (:require [cljs.core.async.impl.ioc-macros :as ioc]))

(defmacro runner
  "Creates a runner block. The code inside the body of this macro will be translated
  into a state machine. At run time the body will be run as normal. This transform is
  only really useful for testing."
  [& body]
  (binding [ioc/*symbol-translations* '{pause clojure.core.async.ioc-macros/pause
                                        case case}]
    `(cljs.core.async/runner-wrapper ~(ioc/state-machine body 0))))


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
  (binding [ioc/*symbol-translations* '{alts! alts!
                                        case case}
            ioc/*local-env* &env]
    `(let [c# (cljs.core.async/chan 1)]
       (cljs.core.async.impl.dispatch/run
        (fn []
          (let [f# ~(ioc/state-machine body 1)
                state# (-> (f#)
                           (ioc/aset-all! cljs.core.async.impl.ioc-helpers/USER-START-IDX c#))]
            (cljs.core.async.impl.ioc-helpers/async-chan-wrapper state#))))
       c#)))


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
  (do-alt 'alts! clauses))
