;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns cljs.core.async.test-runner
  (:require [cljs.test :refer-macros [run-tests]]
            [cljs.core.async.buffer-tests]
            [cljs.core.async.pipeline-test]
            [cljs.core.async.timers-test]
            [cljs.core.async.interop-tests]
            [cljs.core.async.tests]
            [cljs.core.async.runner-tests]))

(run-tests
  'cljs.core.async.runner-tests
  'cljs.core.async.pipeline-test
  'cljs.core.async.buffer-tests
  'cljs.core.async.timers-test
  'cljs.core.async.interop-tests
  'cljs.core.async.tests)
