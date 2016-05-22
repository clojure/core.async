(require '[cljs.build.api]
         '[clojure.java.io :as io])

(cljs.build.api/build "src/test/self-host"
  {:main       'test.runner
   :output-to  "target/out-self-host/main.js"
   :output-dir "target/out-self-host"
   :target     :nodejs})

(defn copy-source
  [filename]
  (spit (str "target/out-self-host/" filename)
    (slurp (io/resource filename))))

(copy-source "cljs/test.cljc")
(copy-source "cljs/analyzer/api.cljc")
(copy-source "clojure/template.clj")
