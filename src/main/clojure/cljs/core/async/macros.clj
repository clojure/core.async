(ns cljs.core.async.macros
  (:require [cljs.core.async :as async]))

;; Idea taken from Potemkin: https://github.com/ztellman/potemkin/blob/master/src/potemkin/namespaces.clj
(defn- setup-macro-shim
  [src dst]
  (alter-meta! dst merge (dissoc (meta src) :name))
  (.setMacro dst)
  (add-watch src dst
    (fn [_ src old new]
      (alter-var-root dst (constantly @src))
      (alter-meta! dst merge (dissoc (meta src) :name)))))

(def go #'async/go)
(setup-macro-shim #'async/go #'go)

(def go-loop #'async/go-loop)
(setup-macro-shim #'async/go-loop #'go-loop)

(def alt! #'async/alt!)
(setup-macro-shim #'async/alt! #'alt!)
