;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns core.async.impl.channels
  (:require [core.async.impl.protocols :as impl]
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
         (when-not (impl/active? taker)
           (.remove iter))
         (when (.hasNext iter)
           (recur (.next iter))))))
   (when-not (.isEmpty puts)
     (let [iter (.iterator puts)]
       (loop [[putter] (.next iter)]
         (when-not (impl/active? putter)
           (.remove iter))
         (when (.hasNext iter)
           (recur (.next iter)))))))

  impl/WritePort
  (put!
   [this val handler]
   (impl/lock mutex)
   (cleanup this)
   (if @closed
     (do (impl/unlock mutex)
         (throw (IllegalStateException. "put! on closed channel")))
     (let [iter (.iterator takes)
           [put-cb take-cb] (when (.hasNext iter)
                              (loop [taker (.next iter)]
                                (if (< (impl/lock-id handler) (impl/lock-id taker))
                                  (do (impl/lock handler) (impl/lock taker))
                                  (do (impl/lock taker) (impl/lock handler)))
                                (let [ret (when (and (impl/active? handler) (impl/active? taker))
                                            [(impl/commit handler) (impl/commit taker)])]
                                  (impl/unlock handler)
                                  (impl/unlock taker)
                                  (if ret
                                    ret
                                    (when (.hasNext iter)
                                      (recur (.next iter)))))))]
       (if (and put-cb take-cb)
         (do
           (impl/unlock mutex)
           (dispatch/run (fn [] (take-cb val)))
           put-cb)
         (if (and buf (not (impl/full? buf)))
           (do
             (impl/lock handler)
             (let [put-cb (and (impl/active? handler) (impl/commit handler))]
               (impl/unlock handler)
               (if put-cb
                 (do (impl/add! buf val)
                     (impl/unlock mutex)
                     put-cb)
                 (impl/unlock mutex))))
           (do
             (.add puts [handler val])
             (impl/unlock mutex)
             nil))))))
  
  impl/ReadPort
  (take!
   [this handler]
   (impl/lock mutex)
   (cleanup this)
   (let [commit-handler (fn []
                          (impl/lock handler)
                          (let [take-cb (and (impl/active? handler) (impl/commit handler))]
                            (impl/unlock handler)
                            take-cb))]
     (if (and buf (pos? (count buf)))
       (do
         (if-let [take-cb (commit-handler)]
           (let [val (impl/remove! buf)]
             (impl/unlock mutex)
             (fn [] (take-cb val)))
           (do (impl/unlock mutex)
               nil)))
       (let [iter (.iterator puts)
             [take-cb put-cb val]
             (when (.hasNext iter)
               (loop [[putter val] (.next iter)]
                 (if (< (impl/lock-id handler) (impl/lock-id putter))
                   (do (impl/lock handler) (impl/lock putter))
                   (do (impl/lock putter) (impl/lock handler)))
                 (let [ret (when (and (impl/active? handler) (impl/active? putter))
                             [(impl/commit handler) (impl/commit putter) val])]
                   (impl/unlock handler)
                   (impl/unlock putter)
                   (if ret
                     ret
                     (when (.hasNext iter)
                       (recur (.next iter)))))))]
         (if (and put-cb take-cb)
           (do
             (impl/unlock mutex)
             (dispatch/run put-cb)
             (fn [] (take-cb val)))
           (if @closed
             (do
               (impl/unlock mutex)
               (if-let [take-cb (commit-handler)]
                 (fn [] (take-cb nil))
                 nil))
             (do
               (.add takes handler)
               (impl/unlock mutex)
               nil)))))))

  impl/Channel
  (close!
   [this]
   (impl/lock mutex)
   (cleanup this)
   (if @closed
     (do
       (impl/unlock mutex)
       nil)
     (do
       (reset! closed true)
       (let [iter (.iterator takes)]
         (when (.hasNext iter)
           (loop [taker (.next iter)]
             (impl/lock taker)
             (let [take-cb (and (impl/active? taker) (impl/commit taker))]
               (impl/unlock taker)
               (if take-cb
                 (dispatch/run (fn [] (take-cb nil)))
                 (when (.hasNext iter)
                   (recur (.next iter))))))))
       (impl/unlock mutex)
       nil))))

(defn- mutex []
  (let [m (Mutex.)]
    (reify
     impl/Locking
     (lock [_] (.lock m))
     (unlock [_] (.unlock m)))))

(defn chan [buf]
 (ManyToManyChannel. (LinkedList.) (LinkedList.) buf (atom nil) (mutex)))

