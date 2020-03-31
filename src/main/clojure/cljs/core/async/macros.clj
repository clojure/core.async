;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

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
