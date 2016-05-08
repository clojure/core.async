(ns cljs.core.async
  (:require [cljs.core.async.macros :as m]))

(defmacro alt! [& x] `(m/alt! ~@x))
(defmacro go [& x] `(m/go ~@x))
(defmacro go-loop [& x] `(m/go-loop ~@x))
