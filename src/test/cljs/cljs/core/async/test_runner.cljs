(ns cljs.core.async.test-runner
  (:require [cljs.test :refer-macros [run-tests]]
            [cljs.core.async.buffer-tests]
            [cljs.core.async.pipeline-test]
            [cljs.core.async.tests]))

(run-tests
  'cljs.core.async.pipeline-test
  'cljs.core.async.buffer-tests
  'cljs.core.async.tests)
