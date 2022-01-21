(ns build
  (:refer-clojure :exclude [compile])
  (:require [clojure.tools.build.api :as b]))

(def class-dir "target/classes")
(def basis (b/create-basis {:project "deps.edn"}))

(defn compile
  [_]
  (b/delete {:path "target"})
  (b/compile-clj {:basis basis, :src-dirs ["src/main/clojure"], :class-dir class-dir,
                  :filter-nses '[clojure.core.async]
                  :ns-compile '[clojure.core.async.impl.exec.threadpool
                                clojure.core.async.impl.protocols
                                clojure.core.async.impl.mutex
                                clojure.core.async.impl.concurrent
                                clojure.core.async.impl.dispatch
                                clojure.core.async.impl.ioc-macros
                                clojure.core.async.impl.buffers
                                clojure.core.async.impl.channels
                                clojure.core.async.impl.timers
                                clojure.core.async]}))

