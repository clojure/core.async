;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns ^{:skip-wiki true}
  clojure.core.async.impl.buffers
  (:require [clojure.core.async.impl.protocols :as impl])
  (:import [java.util LinkedList]
           [clojure.lang Counted]))

(set! *warn-on-reflection* true)


(deftype FixedBuffer [^LinkedList buf ^long n]
  impl/Buffer
  (full? [_this]
    (>= (.size buf) n))
  (remove! [_this]
    (.removeLast buf))
  (add!* [this itm]
    (.addFirst buf itm)
    this)
  (close-buf! [_this])
  Counted
  (count [_this]
    (.size buf))
  impl/Capacity
  (capacity [_this]
    n))

(defn fixed-buffer [^long n]
  (FixedBuffer. (LinkedList.) n))

(deftype DroppingBuffer [^LinkedList buf ^long n]
  impl/UnblockingBuffer
  impl/Buffer
  (full? [_this]
    false)
  (remove! [_this]
    (.removeLast buf))
  (add!* [this itm]
    (when-not (>= (.size buf) n)
      (.addFirst buf itm))
    this)
  (close-buf! [_this])
  Counted
  (count [_this]
    (.size buf))
  impl/Capacity
  (capacity [_this]
    n))

(defn dropping-buffer [n]
  (DroppingBuffer. (LinkedList.) n))

(deftype SlidingBuffer [^LinkedList buf ^long n]
  impl/UnblockingBuffer
  impl/Buffer
  (full? [_this]
    false)
  (remove! [_this]
    (.removeLast buf))
  (add!* [this itm]
    (when (= (.size buf) n)
      (impl/remove! this))
    (.addFirst buf itm)
    this)
  (close-buf! [_this])
  Counted
  (count [_this]
    (.size buf))
  impl/Capacity
  (capacity [_this]
    n))

(defn sliding-buffer [n]
  (SlidingBuffer. (LinkedList.) n))

(defonce ^:private NO-VAL (Object.))
(defn- undelivered? [val]
  (identical? NO-VAL val))

(deftype PromiseBuffer [^:unsynchronized-mutable val]
  impl/UnblockingBuffer
  impl/Buffer
  (full? [_]
    false)
  (remove! [_]
    val)
  (add!* [this itm]
    (when (undelivered? val)
      (set! val itm))
    this)
  (close-buf! [_]
    (when (undelivered? val)
      (set! val nil)))
  Counted
  (count [_]
    (if (undelivered? val) 0 1))
  impl/Capacity
  (capacity [_this]
    1))

(defn promise-buffer []
  (PromiseBuffer. NO-VAL))

(defn datafy-buffer [buffer]
  {:type (-> buffer class .getSimpleName symbol)
   :count (count buffer)
   :capacity (impl/capacity buffer)})

(extend-protocol clojure.core.protocols/Datafiable
  FixedBuffer
  (datafy [b] (datafy-buffer b))
  DroppingBuffer
  (datafy [b] (datafy-buffer b))
  SlidingBuffer
  (datafy [b] (datafy-buffer b))
  PromiseBuffer
  (datafy [b] (datafy-buffer b)))
