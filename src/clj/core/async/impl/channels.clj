;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns core.async.impl.channels
  (:require [core.async.impl.protocols :as proto]
            [core.async.impl.dispatch :as dispatch])
  (:import [java.util LinkedList Queue Iterator]
           [core.async Mutex]))

(set! *warn-on-reflection* true)

(defprotocol MMC
  (cleanup [_]))

(deftype ManyToManyChannel [^Queue takes ^Queue puts ^Queue buf closed mutex]
  MMC
  (cleanup
   [_]
   (when-not (.isEmpty takes)
     (let [iter (.iterator takes)]
       (loop [taker (.next iter)]
         (when-not (proto/active? taker)
           (.remove iter))
         (when (.hasNext iter)
           (recur (.next iter))))))
   (when-not (.isEmpty puts)
     (let [iter (.iterator puts)]
       (loop [[putter] (.next iter)]
         (when-not (proto/active? putter)
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
           (dispatch/run (fn [] (take-cb val)))
           put-cb)
         (if (and buf (not (proto/full? buf)))
           (do
             (proto/lock handler)
             (let [put-cb (and (proto/active? handler) (proto/commit handler))]
               (proto/unlock handler)
               (if put-cb
                 (do (proto/add! buf val)
                     (proto/unlock mutex)
                     put-cb)
                 (proto/unlock mutex))))
           (do
             (.add puts [handler val])
             (proto/unlock mutex)
             nil))))))
  
  proto/ReadPort
  (take!
   [this handler]
   (proto/lock mutex)
   (cleanup this)
   (let [commit-handler (fn []
                          (proto/lock handler)
                          (let [take-cb (and (proto/active? handler) (proto/commit handler))]
                            (proto/unlock handler)
                            take-cb))]
     (if (and buf (pos? (count buf)))
       (do
         (if-let [take-cb (commit-handler)]
           (let [val (proto/remove! buf)]
             (proto/unlock mutex)
             (fn [] (take-cb val)))
           (do (proto/unlock mutex)
               nil)))
       (let [iter (.iterator puts)
             [take-cb put-cb val]
             (when (.hasNext iter)
               (loop [[putter val] (.next iter)]
                 (if (< (proto/lock-id handler) (proto/lock-id putter))
                   (do (proto/lock handler) (proto/lock putter))
                   (do (proto/lock putter) (proto/lock handler)))
                 (let [ret (when (and (proto/active? handler) (proto/active? putter))
                             [(proto/commit handler) (proto/commit putter) val])]
                   (proto/unlock handler)
                   (proto/unlock putter)
                   (if ret
                     ret
                     (when (.hasNext iter)
                       (recur (.next iter)))))))]
         (if (and put-cb take-cb)
           (do
             (proto/unlock mutex)
             (dispatch/run put-cb)
             (fn [] (take-cb val)))
           (if @closed
             (do
               (proto/unlock mutex)
               (if-let [take-cb (commit-handler)]
                 (fn [] (take-cb nil))
                 nil))
             (do
               (.add takes handler)
               (proto/unlock mutex)
               nil)))))))

  proto/Channel
  (close!
   [this]
   (proto/lock mutex)
   (cleanup this)
   (if @closed
     (do
       (proto/unlock mutex)
       nil)
     (do
       (reset! closed true)
       (let [iter (.iterator takes)]
         (when (.hasNext iter)
           (loop [taker (.next iter)]
             (proto/lock taker)
             (let [take-cb (and (proto/active? taker) (proto/commit taker))]
               (proto/unlock taker)
               (if take-cb
                 (dispatch/run (fn [] (take-cb nil)))
                 (when (.hasNext iter)
                   (recur (.next iter))))))))
       (proto/unlock mutex)
       nil))))

(defn- mutex []
  (let [m (Mutex.)]
    (reify
     proto/Locking
     (lock [_] (.lock m))
     (unlock [_] (.unlock m)))))

(defn chan [buf]
 (ManyToManyChannel. (LinkedList.) (LinkedList.) buf (atom nil) (mutex)))

