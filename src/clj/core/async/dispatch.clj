;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns core.async.dispatch
  (:require [core.async.protocols :as proto])
  (:import [java.util.concurrent Executor ThreadPoolExecutor]))

(set! *warn-on-reflection* true)

(defn run
  "runs fn0 in a thread pool thread"
  [fn0])