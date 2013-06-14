;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns cljs.core.async.impl.buffers
  (:require [cljs.core.async.impl.protocols :as impl]))

(deftype FixedBuffer [buf n]
  impl/Buffer
  (full? [this]
    (= (.-length buf) n))
  (remove! [this]
    (.pop buf))
  (add! [this itm]
    (assert (not (impl/full? this)) "Can't add to a full buffer")
    (.unshift buf itm))
  cljs.core.ICounted
  (-count [this]
    (.-length buf)))

(defn fixed-buffer [n]
  (FixedBuffer. (make-array 0) n))


(deftype DroppingBuffer [buf n]
  impl/Buffer
  (full? [this]
    false)
  (remove! [this]
    (.pop buf))
  (add! [this itm]
    (when-not (= (.-length buf) n)
      (.unshift buf itm)))
  cljs.core.ICounted
  (-count [this]
    (.-length buf)))

(defn dropping-buffer [n]
  (DroppingBuffer. (make-array 0) n))

(deftype SlidingBuffer [buf n]
  impl/Buffer
  (full? [this]
    false)
  (remove! [this]
    (.pop buf))
  (add! [this itm]
    (when (= (.-length buf) n)
      (impl/remove! this))
    (.unshift buf itm))
  cljs.core.ICounted
  (-count [this]
    (.-length buf)))

(defn sliding-buffer [n]
  (SlidingBuffer. (make-array 0) n))
         
