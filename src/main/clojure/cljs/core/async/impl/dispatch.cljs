(ns cljs.core.async.impl.dispatch
  (:require [cljs.core.async.impl.buffers :as buffers]))

(def message-channel nil)
(def tasks (buffers/ring-buffer 32))
(def ^:boolean running? false)
(def ^:boolean queued? false)

(def TASK_BATCH_SIZE 1024)

(declare queue-dispatcher)

(defn process-messages []
  (set! running? true)
  (set! queued? false)
  (loop [count 0]
    (let [m (.pop tasks)]
      (when-not (nil? m)
        (m)
        (when (< count TASK_BATCH_SIZE)
          (recur (inc count))))))
  (set! running? false)
  (when (> (.-length tasks) 0)
    (queue-dispatcher)))

(when (exists? js/MessageChannel)
  (set! message-channel (js/MessageChannel.))
  (set! (.. message-channel -port1 -onmessage)
        (fn [msg]
          (process-messages))))

(defn queue-dispatcher []
  (when-not ^boolean (and ^boolean queued?
                          running?)
    (set! queued? true)
    (cond
     (exists? js/MessageChannel) (.postMessage (.-port2 message-channel) 0)
     (exists? js/setImmediate) (js/setImmediate process-messages)
     :else (js/setTimeout process-messages 0))))

(defn run [f]
  (.unbounded-unshift tasks f)
  (queue-dispatcher))

(defn queue-delay [f delay]
  (js/setTimeout f delay))

