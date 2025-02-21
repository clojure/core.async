;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

;;;; flow config

(ns clojure.core.async.flow.specs
  (:require
    [clojure.spec.alpha :as s]))

;; process identifier
(s/def ::pid keyword?)

;; proc in/out identifier
(s/def ::ioid keyword?)

;; channel end defined by tuple of pid and ioid
(s/def ::port (s/tuple ::pid ::ioid))

;; proc function - symbol resolving to a fn or a fn
(s/def ::proc (s/or symbol? ifn?))

;; proc arg map - arbitrary, keys defined by proc fn
(s/def ::args map?)

;; chan config - buffer symbol or fixed buffer size
(s/def ::buf-or-n (s/or symbol? int?))

;; chan definition - chan config + xform
(s/def ::chan-def
  (s/keys :opt-un [::buf-or-n ::xform]))

;; channel opts for a proc, map of ioid to channel definition
(s/def ::chan-opts
  (s/map-of ::ioid ::chan-def))

;; process definition
(s/def ::proc-def
  (s/keys :opt-un [::proc ::args ::chan-opts]))

;; map of pid to proc def in flow
(s/def ::procs
  (s/map-of ::pid ::proc-def))

;; connection is a tuple of from-port and to-port
(s/def ::conn
  (s/tuple ::port ::port))

;; connections
(s/def ::conns
  (s/coll-of ::conn))

;; flow config consists of procs and conns
;; the exec options take something that resolves to an ExecutorService
(s/def ::flow-config
  (s/keys :opt-un [::procs ::conns
                   ::mixed-exec ::io-exec ::compute-exec]))

;;;; process description

;; defines the parameters a proc takes, kw->docstring
(s/def ::params
  (s/map-of keyword? string?))

;; defines the in channels a proc takes, kw->docstring
(s/def ::ins
  (s/map-of keyword? string?))

;; defines the out channels a proc takes, kw->docstring
(s/def ::outs
  (s/map-of keyword? string?))

;; proc workload type
(s/def ::workload #{:mixed :io :compute})

;; returned by the proc :describe function
(s/def ::proc-description
  (s/keys
    :opt-un [::params ::ins ::outs ::workload]))

