;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns core.async.impl.buffers
  (:require [core.async.impl.protocols :as proto])
  (:import [java.util LinkedList Queue]))

(set! *warn-on-reflection* true)

(deftype FixedBuffer [^LinkedList buf ^long n]
  proto/Buffer
  (full? [this]
    (= (.size buf) n))
  (remove! [this]
    (.removeLast buf))
  (add! [this itm]
    (assert (not (proto/full? this)) "Can't add to a full buffer")
    (.addFirst buf itm))
  clojure.lang.Counted
  (count [this]
    (.size buf)))

(defn fixed-buffer [^long n]
  (FixedBuffer. (LinkedList.) n))


(deftype DroppingBuffer [^LinkedList buf ^long n]
  proto/Buffer
  (full? [this]
    false)
  (remove! [this]
    (.removeLast buf))
  (add! [this itm]
    (when-not (= (.size buf) n)
      (.addFirst buf itm)))
  clojure.lang.Counted
  (count [this]
    (.size buf)))

(defn dropping-buffer [n]
  (DroppingBuffer. (LinkedList.) n))

(deftype SlidingBuffer [^LinkedList buf ^long n]
  proto/Buffer
  (full? [this]
    false)
  (remove! [this]
    (.removeLast buf))
  (add! [this itm]
    (when (= (.size buf) n)
      (proto/remove! this))
    (.addFirst buf itm))
  clojure.lang.Counted
  (count [this]
    (.size buf)))

(defn sliding-buffer [n]
  (SlidingBuffer. (LinkedList.) n))
