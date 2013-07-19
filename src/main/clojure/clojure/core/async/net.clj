(ns clojure.core.async.net
  (:require [clojure.core.async :refer :all]))



(defprotocol IRemoteChannelManager
  (net-send [this nm data ack-chan])
  (register-chan [this nm c]))

(defprotocol ITransport
  (get-put-chan [this])
  (get-take-chan [this]))

(defn channel-transports []
  (let [to-remote (chan)
        from-remote (chan)]
    [(reify ITransport
       (get-put-chan [this] to-remote)
       (get-take-chan [this] from-remote))
     (reify ITransport
       (get-put-chan [this] from-remote)
       (get-take-chan [this] to-remote))]))


(defn simulated-connection
  "Returns a pair of [local remote] IRemoteChannelManagers"
  [local-transport remote-transport]
  (let [
        ack-id (atom 0)
        
        remote-channels (atom {})
        remote (reify IRemoteChannelManager
                 (register-chan [this nm c]
                   (assert (not (@remote-channels nm)) "A channel with that name aready exists")
                   (swap! remote-channels assoc nm c))
                 (net-send [this nm data ack-chan]
                   (assert false "Not implemented")))

        local-acks (atom {})
        local (reify IRemoteChannelManager
                (register-chan [this nm c]
                  (assert false "Not implemented"))
                (net-send [this nm data ack-chan]
                  (let [ack (swap! ack-id inc)]
                    (swap! local-acks assoc ack ack-chan)
                    (go (>! (get-put-chan local-transport)
                            {:ACK {:id ack
                                   :type :ACK}
                             :type :DATA
                             :name nm
                             :data data})))))]
    (go
     (while true
       (let [data (<! (get-take-chan remote-transport))]
         (case (:type data)
           :DATA (go (when-let [c (@remote-channels (:name data))]
                       (>! c (:data data))
                       (>! (get-put-chan remote-transport) (:ACK data))))
           :ACK (assert false "Not Implemented ACK")))))
    (go
     (while true
       (let [data (<! (get-take-chan local-transport))]
         (case (:type data)
           :ACK (go (if-let [loc-ack (@local-acks (:id data))]
                      (>! loc-ack :ACK)))
           :DATA (assert false "Not implemented DATA")))))
    [local remote]))


(defn duct-> [c manager remote-name]
  (let [ack-chan (chan)]
    (go
     (while true
       (let [data (<! c)]
         (net-send manager remote-name data ack-chan)
         (assert (= (<! ack-chan) :ACK) "Unknown reponse on ack-chan"))))))
