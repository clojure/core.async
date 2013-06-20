;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns clojure.core.async.impl.dispatch
  (:require [clojure.core.async.impl.protocols :as impl]
            [clojure.core.async.impl.exec.forkjoin :as fj]))

(set! *warn-on-reflection* true)

;; Set default executor to be jsr166 ForkJoinPool
;; Could also do something more interesting here based on JDK version
;; or other capabilities to dynamically pick an executor
(def ^:dynamic *executor* (fj/fork-join-executor))

(defn run
  "Runs fn0 in a thread pool thread"
  [^Runnable task]
  (impl/exec *executor* task))
