(ns clojure.core.async.net
  (:require [clojure.core.async :refer :all]
            [clojure.java.io :refer [reader writer]])
  (:import [java.net ServerSocket Socket]))

;; This is a highly experimental implementation of networked channels.
;; This code is not to be considered as finalized, well thought
;; through or even working. It's most likely full of hacks and poorly
;; thought out code.

;; What this is, is a playground to think about how network channels
;; might be implemented with core.async. 


;; This system de-couples data transport from multiplexing. Therefore,
;; any protocol that can send and receive data, and can serialize
;; clojure data in some form is able to be used as a transport. In
;; this code we present two such transports, a in-process transport
;; based on Clojure channels, and a interprocess transport based on
;; TCP. 


;; Each end of a connection has a IMultiplexer
(defprotocol IMultiplexer
  (register-chan [this nm c] "registers a channel with the manager, under the given name")
  (unregister-chan [this nm] "removes a channel from the registry")
  (net-send [this nm data ack-chan] "sends data to the remote end of the connection and enqueues it into the named channel
                                     once the operation is complete, ack-chan will receive either a :ACK response or an error"))

;; In order to decouple multiplexing from data
;; transport/serialization, we present a single ITransport protocol
(defprotocol ITransport
  (get-put-chan [this] "Returns a channel that supports put!. Values put into this channel
                        will be transported to the remote end")
  (get-take-chan [this] "Returns a channel that supports get! Values that are put! on the
                         remote put channel will be enqueued into this channel"))

(defn channel-transports
  "Creates a mock network transport by implementing local and remote ITransports over channels"
  []
  (let [to-remote (chan)
        from-remote (chan)]
    [(reify ITransport
       (get-put-chan [this] to-remote)
       (get-take-chan [this] from-remote))
     (reify ITransport
       (get-put-chan [this] from-remote)
       (get-take-chan [this] to-remote))]))


(defn multiplexer
  "Generates a IMultiplexer from a transport"
  [transport]
  (let [ack-id-a (atom 0)
        acks (atom {})
        channels (atom {})]

    ;; This go reads from the transport and dispatches to channels or
    ;; releases channels with a :ACK
    (go
     (while true
       (let [data (<! (get-take-chan transport))]
         (case (:type data)
           :DATA (go (when-let [c (@channels (:name data))]
                       (>! c (:data data))
                       (>! (get-put-chan transport) (:ACK data))))
           :ACK (go (if-let [ack-c (@acks (:id data))]
                      (>! ack-c :ACK)))))))

    ;; The end-user interface library fns should be coded against this
    (reify IMultiplexer
      (register-chan [this nm c]
        (assert (not (@channels nm)) "A channel with that name aready exists")
        (swap! channels assoc nm c))
      (unregister-chan [this nm]
        (swap! channels dissoc nm))
      (net-send [this nm data ack-chan]
        (let [ack-id (swap! ack-id-a inc)]
          (swap! acks assoc ack-id ack-chan)
          (go (>! (get-put-chan transport)
                  {:ACK {:id ack-id
                         :type :ACK}
                   :type :DATA
                   :name nm
                   :data data})))))))

(defn simulated-connection
  "Returns a pair of [local remote] IRemoteChannelManagers that use the given transports.
  Only really useful for testing on a local machine, as that is about the only time you'll have
  both transports on a single JVM"
  [local-transport remote-transport]
  [(multiplexer local-transport)
   (multiplexer remote-transport)])


(defn duct->
  "Creates a go routine that will take data from c and send it over the manager to the channel with remote-name"
  [c manager remote-name]
  (let [ack-chan (chan)]
    (go
     (while true
       (let [data (<! c)]
         (net-send manager remote-name data ack-chan)
         (assert (= (<! ack-chan) :ACK) "Unknown reponse on ack-chan"))))))




;; TCP ITransports
(defn- localhost-tcp-server [port]
  (future (let [take-chan (chan)
                put-chan (chan)
                server (ServerSocket. port)
                socket (.accept server)
                out (writer (.getOutputStream socket))
                in (java.io.PushbackReader. (reader (.getInputStream socket)))]
            (thread
             (while true
               (>!! take-chan (read in))))
            (thread
             (while true
               (binding [*out* out]
                 (prn (<!! put-chan))
                 (flush))))
            (reify ITransport
              (get-put-chan [this] put-chan)
              (get-take-chan [this] take-chan)))))

(defn- localhost-tcp-client [port]
  (future (let [take-chan (chan)
                put-chan (chan)
                socket (Socket. "localhost" port)
                out (writer (.getOutputStream socket))
                in (java.io.PushbackReader. (reader (.getInputStream socket)))]
            (thread
             (while true
               (>!! take-chan (read in))))
            (thread
             (while true
               (binding [*out* out]
                 (prn (<!! put-chan))
                 (flush))))
            (reify ITransport
              (get-put-chan [this] put-chan)
              (get-take-chan [this] take-chan)))))

(defn localhost-tcp-transports
  "Same as channel-transports, but uses TCP over localhost:port"
  [port]
  (let [remote (localhost-tcp-server port)
        local (localhost-tcp-client port)]
    [@local @remote]))
