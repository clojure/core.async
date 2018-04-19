(ns cljs.core.async.macros
  #?(:clj  (:require [cljs.core.async :as async])
     :cljs (:require-macros [cljs.core.async])))

;; Idea taken from Potemkin: https://github.com/ztellman/potemkin/blob/master/src/potemkin/namespaces.clj
#?(:clj
   (defn- setup-macro-shim
     [src dst]
     (alter-meta! dst merge (dissoc (meta src) :name))
     (.setMacro dst)
     (add-watch src dst
       (fn [_ src old new]
         (alter-var-root dst (constantly @src))
         (alter-meta! dst merge (dissoc (meta src) :name))))))

#?(:clj
   (do
     (def go #'async/go)
     (setup-macro-shim #'async/go #'go))
   :cljs
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
     `(cljs.core.async/go ~@body)))

#?(:clj
   (do
     (def go-loop #'async/go-loop)
     (setup-macro-shim #'async/go-loop #'go-loop))
   :cljs
   (defmacro go-loop
     "Like (go (loop ...))"
     [bindings & body]
     `(cljs.core.async/go-loop ~bindings ~@body)))

#?(:clj
   (do
     (def alt! #'async/alt!)
     (setup-macro-shim #'async/alt! #'alt!))
   :cljs
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
     `(cljs.core.async/alt! ~@clauses)))
