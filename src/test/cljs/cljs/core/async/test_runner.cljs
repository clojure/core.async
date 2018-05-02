(ns cljs.core.async.test-runner
  (:require [cljs.test :refer-macros [run-tests]]
            [cljs.core.async.buffer-tests]
            [cljs.core.async.pipeline-test]
            [cljs.core.async.timers-test]
            [cljs.core.async.tests]
            [cljs.core.async.runner-tests]))

(run-tests
  'cljs.core.async.runner-tests
  'cljs.core.async.pipeline-test
  'cljs.core.async.buffer-tests
  'cljs.core.async.timers-test
  'cljs.core.async.tests)
