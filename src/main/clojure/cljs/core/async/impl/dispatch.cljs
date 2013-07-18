(ns cljs.core.async.impl.dispatch)

(def message-channel nil)
(def tasks nil)

(when (.-MessageChannel js/window)
  (set! message-channel (js/MessageChannel.))
  (set! tasks (array))
  (set! (.. message-channel -port1 -onmessage)
    (fn [msg]
      ((.shift tasks)))))

(defn queue-task [f]
  (.push tasks f)
  (.postMessage (.-port2 message-channel) 0))

(defn run [f]
  (if-not (nil? message-channel)
    (queue-task f)
    (js/setTimeout f 0)))

(defn queue-delay [f delay]
  (js/setTimeout f delay))

