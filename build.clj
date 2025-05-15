(ns build
  (:refer-clojure :exclude [compile])
  (:require [clojure.tools.build.api :as b]))

(def class-dir "target/classes")
(def basis (b/create-basis {:project "deps.edn"
                            :aliases [:dev]}))

(def comp-test-nses '[clojure.core.async-test
                      clojure.core.pipeline-test
                      clojure.core.async.buffers-test
                      clojure.core.async.concurrent-test
                      clojure.core.async.exceptions-test
                      clojure.core.async.timers-test])

;; clj -T:build compile-tests
(defn compile-tests
  [_]
  (b/delete {:path "target"})
  (b/compile-clj {:basis basis
                  :src-dirs ["src/test/clojure"]
                  :class-dir class-dir,
                  :ns-compile comp-test-nses}))

;; clj -T:build compile-tests-vthreads
(defn compile-tests-vthreads
  [_]
  (b/delete {:path "target"})
  (b/compile-clj {:basis (b/create-basis {:project "deps.edn" :aliases [:dev :vthreads]})
                  :src-dirs ["src/test/clojure"]
                  :class-dir class-dir,
                  :filter-nses '[clojure.core.async]
                  :ns-compile comp-test-nses})
  (println "DONE " (-> "target/classes/clojure/core/" clojure.java.io/file .list vec)))

;; clj -T:build compile-tests-no-vthreads
(defn compile-tests-no-vthreads
  [_]
  (b/delete {:path "target"})
  (b/compile-clj {:basis (b/create-basis {:project "deps.edn" :aliases [:dev :no-vthreads]})
                  :src-dirs ["src/test/clojure"]
                  :class-dir class-dir,
                  :filter-nses '[clojure.core.async]
                  :ns-compile comp-test-nses}))

(defn compile
  [_]
  (b/delete {:path "target"})
  (b/compile-clj {:basis basis
                  :src-dirs ["src/main/clojure"]
                  :class-dir class-dir
                  :filter-nses '[clojure.core.async]
                  :ns-compile '[clojure.core.async.impl.protocols
                                clojure.core.async.impl.mutex
                                clojure.core.async.impl.dispatch
                                clojure.core.async.impl.ioc-macros
                                clojure.core.async.impl.buffers
                                clojure.core.async.impl.channels
                                clojure.core.async.impl.timers
                                clojure.core.async]}))

