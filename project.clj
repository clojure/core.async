(defproject core.async "0.1.0-SNAPSHOT"
  :description "Facilities for async programming and communication in Clojure"
  :url "https://github.com/clojure/core.async"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :parent [org.clojure/pom.contrib "0.1.2"]
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :warn-on-reflection true
  :source-paths ["src/clj"]
  :test-paths ["test/clojure"]
  :jvm-opts ^:replace ["-Xmx1g" "-server"]
  :java-source-paths ["src/java"]
  :profiles {:dev {:source-paths ["examples"]}}

  :plugins [[lein-cljsbuild "0.3.0"]]

  :cljsbuild
  {:builds
   [{:id "simple"
     :source-paths ["test/cljs" "src/cljs"]
     :compiler {:optimizations :simple
                :pretty-print true
                :static-fns true
                :output-to "tests.js"}}
    {:id "adv"
     :source-paths ["test/cljs" "src/cljs"]
     :compiler {:optimizations :advanced
                :pretty-print true
                :output-to "tests.js"}}]})
