;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns clojure.core.async.impl.exec.forkjoin
  (:require [clojure.core.async.impl.protocols :as impl])
  (:import [clojure.jsr166y ForkJoinPool ForkJoinTask]))

(set! *warn-on-reflection* true)

;; The last "true" flag sets asyncMode and is recommended when using
;; only tasks that do not join (basically as an executor)
(defonce ^ForkJoinPool the-pool
  (ForkJoinPool. (.availableProcessors (Runtime/getRuntime))
                 ForkJoinPool/defaultForkJoinWorkerThreadFactory
                 nil
                 true))

(defn fork-join-executor
  ([] (fork-join-executor the-pool))
  ([pool] (reify impl/Executor
            (impl/exec [this r]
              (let [fjtask (ForkJoinTask/adapt ^Runnable r)]
                (.execute ^ForkJoinPool pool fjtask))))))
