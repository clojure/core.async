;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns core.async.channels
  (:require [core.async.protocols :as proto])
  (:import [java.util LinkedList Queue Iterator]))

(set! *warn-on-reflection* true)

(defn- exec
  "Run fn0 in the default executor"
  [fn0]
  )

(defprotocol MMC
  (cleanup [_]))

(deftype ManyToManyChannel [^Queue takes ^Queue puts ^Queue buf closed mutex]
  MMC
  (cleanup
   [_]
   (when-not (.isEmpty takes)
     (let [iter (.iterator takes)]
       (loop [taker (.next iter)]
         (when-not (active? taker)
           (.remove iter))
         (when (.hasNext iter)
           (recur (.next iter))))))
   (when-not (.isEmpty puts)
     (let [iter (.iterator puts)]
       (loop [[putter] (.next iter)]
         (when-not (active? putter)
           (.remove iter))
         (when (.hasNext iter)
           (recur (.next iter)))))))

  proto/WritePort
  (put!
   [this val handler]
   (proto/lock mutex)
   (cleanup this)
   (if @closed
     (do (proto/unlock mutex)
         (throw (IllegalStateException. "put! on closed channel")))
     (let [iter (.iterator takes)
           [put-cb take-cb] (when (.hasNext iter)
                              (loop [taker (.next iter)]
                                (if (< (proto/lock-id handler) (proto/lock-id taker))
                                  (do (proto/lock handler) (proto/lock taker))
                                  (do (proto/lock taker) (proto/lock handler)))
                                (let [ret (when (and (proto/active? handler) (proto/active? taker))
                                            [(proto/commit handler) (proto/commit taker)])]
                                  (proto/unlock handler)
                                  (proto/unlock taker)
                                  (if ret
                                    ret
                                    (when (.hasNext iter)
                                      (recur (.next iter)))))))]
       (if (and put-cb take-cb)
         (do
           (proto/unlock mutex)
           (exec (fn [] (take-cb val)))
           put-cb)
         (if (and buf (.offer buf val))
           (do
             (proto/lock handler)
             (let [put-cb (and (proto/active? handler) (proto/commit handler))]
               (proto/unlock handler)
               (if put-cb
                 (do (proto/unlock mutex)
                     put-cb)
                 (do (.removeLast buf)
                     (proto/unlock mutex)
                     nil))))
           (do
             (.add puts [handler val])
             (proto/unlock mutex)
             nil)))))))

(defn chan [buf]
 (ManyToManyChannel. (LinkedList.) (LinkedList.) buf (atom nil) ))

