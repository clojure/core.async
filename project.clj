(defproject org.clojure/core.async "0.1.0-SNAPSHOT"
  :description "Facilities for async programming and communication in Clojure"
  :url "https://github.com/clojure/core.async"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :parent [org.clojure/pom.contrib "0.1.2"]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.analyzer.jvm "0.0.1-SNAPSHOT"]
                 [org.clojure/clojurescript "0.0-2138" :scope "provided"]]
  :global-vars {*warn-on-reflection* true}
  :source-paths ["src/main/clojure"]
  :test-paths ["src/test/clojure"]
  :jvm-opts ^:replace ["-Xmx1g" "-server"]
  :java-source-paths ["src/main/java"]
  :profiles {:dev {:source-paths ["examples"]}}

  :plugins [[lein-cljsbuild "1.0.0"]]

  :cljsbuild
  {:builds
   [{:id "simple"
     :source-paths ["src/test/cljs" "src/main/clojure/cljs"]
     :compiler {:optimizations :simple
                :pretty-print true
                :static-fns true
                :output-to "tests.js"}}
    {:id "adv"
     :source-paths ["src/test/cljs" "src/main/clojure/cljs"]
     :compiler {:optimizations :advanced
                :pretty-print false
                :static-fns true
                :output-dir "out"
                :output-to "tests.js"
                :source-map "tests.js.map"}}]})
