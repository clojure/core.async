(ns cljs.core.async.impl.dispatch)

(defn run [f]
  (js/setTimeout f 0))

(defn queue-delay [f delay]
  (js/setTimeout f delay))

