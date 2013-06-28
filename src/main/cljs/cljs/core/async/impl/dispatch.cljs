(ns cljs.core.async.impl.dispatch)


(defn run [f]
  (js/setTimeout f 0))

