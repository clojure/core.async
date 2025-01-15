;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns clojure.core.async.flow.impl
  (:require [clojure.core.async :as async]
            [clojure.core.async.flow :as-alias flow]
            [clojure.core.async.flow.spi :as spi]
            [clojure.core.async.flow.impl.graph :as graph])
  (:import [java.util.concurrent Future Executors ExecutorService TimeUnit]
           [java.util.concurrent.locks ReentrantLock]))

(set! *warn-on-reflection* true)

;;TODO - something specific, e.g. make aware of JDK version and vthreads
(defonce mixed-exec clojure.lang.Agent/soloExecutor)
(defonce io-exec clojure.lang.Agent/soloExecutor)
(defonce compute-exec clojure.lang.Agent/pooledExecutor)

(defn futurize ^Future [f {:keys [exec]}]
  (fn [& args]
    (^[Callable] ExecutorService/.submit
     (case exec
       :compute compute-exec
       :io io-exec
       :mixed mixed-exec
       exec)
     #(apply f args))))

(defn prep-proc [ret pid {:keys [proc, args, chan-opts] :or {chan-opts {}}}]
  (let [{:keys [ins outs]} (spi/describe proc)
        copts (fn [cs]
                (zipmap (keys cs) (map #(chan-opts %) (keys cs))))
        inopts (copts ins)
        outopts (copts outs)]
    (when (or (some (partial contains? inopts) (keys outopts))
              (some (partial contains? outopts) (keys inopts)))
      (throw (ex-info ":ins and :outs cannot share ids within a process"
                      {:pid pid :ins (keys inopts) :outs (keys outopts)})))
    (assoc ret pid {:pid pid :proc proc :ins inopts :outs outopts :args args})))

(defn create-flow
  "see lib ns for docs"
  [{:keys [procs conns mixed-exec io-exec compute-exec]
    :or {mixed-exec mixed-exec, io-exec io-exec, compute-exec compute-exec}
    :as desc}]
  (let [lock (ReentrantLock.)
        chans (atom nil)
        execs {:mixed mixed-exec :io io-exec :compute compute-exec}
        _ (assert (every? #(instance? ExecutorService %) (vals execs))
                  "mixed-exe, io-exec and compute-exec must be ExecutorServices")
        pdescs (reduce-kv prep-proc {} procs)
        allopts (fn [iok] (into {} (mapcat #(map (fn [[k opts]] [[(:pid %) k] opts]) (iok %)) (vals pdescs))))
        inopts (allopts :ins)
        outopts (allopts :outs)
        set-conj (fnil conj #{})
        ;;out-coord->#{in-coords}
        conn-map (reduce (fn [ret [out in :as conn]]
                           (if (and (contains? outopts out)
                                    (contains? inopts in))
                             (update ret out set-conj in)
                             (throw (ex-info "invalid connection" {:conn conn}))))
                         {} conns)
        running-chans #(or (deref chans) (throw (Exception. "flow not running")))
        send-command (fn [command to]
                       (let [{:keys [control]} (running-chans)]
                         (async/>!! control #::flow{:command command :to to})))]
    (reify
     clojure.core.async.flow.impl.graph.Graph
      (start [_]
        (.lock lock)
        (try
          (if-let [{:keys [report error]} @chans]
            {:report-chan report :error-chan error :already-running true}
            (let [control-chan (async/chan 10)
                  control-mult (async/mult control-chan)
                  report-chan (async/chan (async/sliding-buffer 100))
                  error-chan (async/chan (async/sliding-buffer 100))
                  make-chan (fn [[[pid cid]{:keys [buf-or-n xform]}]]
                              (if xform
                                (async/chan
                                 buf-or-n xform
                                 (fn [ex]
                                   (async/put! error-chan
                                               #::flow{:ex ex, :pid pid, :cid cid, :xform xform})
                                   nil))
                                (async/chan (or buf-or-n 10))))
                  in-chans (zipmap (keys inopts) (map make-chan inopts))
                  out-chans (zipmap (keys outopts)
                                    (map (fn [[coord opts :as co]]
                                           (let [conns (conn-map coord)]
                                             (cond
                                               (empty? conns) nil
                                               ;;direct connect 1:1
                                               (= 1 (count conns)) (in-chans (first conns))
                                               :else (make-chan co)))) 
                                         outopts))
                  ;;mults
                  _  (doseq [[out ins] conn-map]
                       (when (< 1 (count ins))
                         (let [m (async/mult (out-chans out))]
                           (doseq [in ins]
                             (async/tap m (in-chans in))))))
                  write-chan #(if-let [[_ c] (or (find in-chans %) (find out-chans %))]
                                c
                                (throw (ex-info "can't resolve channel with coord" {:coord %})))
                  resolver (reify spi/Resolver
                             (get-write-chan [_ coord]
                               (write-chan coord))
                             (get-exec [_ context] (execs context)))
                  start-proc
                  (fn [{:keys [pid proc args ins outs]}]
                    (try
                      (let [chan-map (fn [ks coll] (zipmap (keys ks) (map #(coll [pid %]) (keys ks))))
                            control-tap (async/chan 10)]
                        (async/tap control-mult control-tap)
                        (spi/start proc {:pid pid :args args :resolver resolver
                                         :ins (assoc (chan-map ins in-chans)
                                                     ::flow/control control-tap)
                                         :outs (assoc (chan-map outs out-chans)
                                                      ::flow/error error-chan
                                                      ::flow/report report-chan)}))
                      (catch Throwable ex
                        (async/>!! control-chan #::flow{:command ::flow/stop :to ::flow/all})
                        (throw ex))))]
              (doseq [p (vals pdescs)]
                (start-proc p))
              ;;the only connection to a running flow is via channels
              (reset! chans {:control control-chan :resolver resolver
                             :report report-chan, :error error-chan
                             :ins in-chans, :outs out-chans})
              {:report-chan report-chan :error-chan error-chan}))
          (finally (.unlock lock))))
      (stop [_]
        (.lock lock)
        (try
          (when-let [{:keys [report error]} @chans]
            (send-command ::flow/stop ::flow/all)
            (async/close! error)
            (async/close! report)
            (reset! chans nil)
            true)
          (finally (.unlock lock))))
      (pause [_] (send-command ::flow/pause ::flow/all))
      (resume [_] (send-command ::flow/resume ::flow/all))
      (ping [_] (send-command ::flow/ping ::flow/all))

      (pause-proc [_ pid] (send-command ::flow/pause pid))
      (resume-proc [_ pid] (send-command ::flow/resume pid))
      (ping-proc [_ pid] (send-command ::flow/ping pid))
      (command-proc [_ pid command kvs]
        (assert (and (namespace command) (not= (namespace ::flow/command) (namespace command)))
                "extension commands must be in your own namespace")
        (let [{:keys [control]} (running-chans)]
          (async/>!! control (merge kvs #::flow{:command command :to pid}))))
          
      (inject [_ coord msgs]
        (let [{:keys [resolver]} (running-chans)
              chan (spi/get-write-chan resolver coord)]
          (doseq [m msgs]
            (async/>!! chan m)))))))

(defn handle-command
  [pid pong status cmd]
  (let [transition #::flow{:stop :exit, :resume :running, :pause :paused}
        {::flow/keys [to command]} cmd]
    (if (#{::flow/all pid} to)
      (do
        (when (= command ::flow/ping) (pong))
        (or (transition command) status))
      status)))

(defn handle-transition
  "when transition, returns maybe new state"
  [transition status nstatus state]
  (if (and transition (not= status nstatus))
    (transition state nstatus)
    state))

(defn send-outputs [status state outputs outs resolver control handle-command transition]
  (loop [nstatus status, nstate state, outputs (seq outputs)]
    (if (or (nil? outputs) (= nstatus :exit))
      [nstatus nstate]
      (let [[output msgs] (first outputs)]
        (if-let [outc (or (outs output) (spi/get-write-chan resolver output))]
          (let [[nstatus nstate]
                (loop [nstatus nstatus, nstate nstate, msgs (seq msgs)]
                  (if (or (nil? msgs) (= nstatus :exit))
                    [nstatus nstate]
                    (let [[v c] (async/alts!!
                                 [control [outc (first msgs)]]
                                 :priority true)]
                      (if (= c control)
                        (let [nnstatus (handle-command nstatus v)
                              nnstate (handle-transition transition nstatus nnstatus nstate)]
                          (recur nnstatus nnstate msgs))
                        (recur nstatus nstate (next msgs))))))]
            (recur nstatus nstate (next outputs)))
          (recur nstatus nstate  (next outputs)))))))

(defn proc
  "see lib ns for docs"
  [{:keys [describe init transition transform inject] :as impl} {:keys [exec compute-timeout-ms]}]
  ;;validate the preconditions
  (assert (= 1 (count (keep identity [transform inject]))) "must provide exactly one of :transform or :inject")
  (assert (not (and inject (= exec :compute))) "can't specify :inject and :compute")
  (reify
    clojure.core.protocols/Datafiable
    (datafy [_]
      (let [{:keys [params ins outs]} (describe)]
        {:impl impl :params (-> params keys vec) :ins (-> ins keys vec) :outs (-> outs keys vec)}))
    spi/ProcLauncher
    (describe [_]
      (let [{:keys [params ins] :as desc} (describe)]
        (assert (not (and ins inject)) "can't specify :ins when :inject")
        (assert (or (not params) init) "must have :init if :params")
        desc))
    (start [_ {:keys [pid args ins outs resolver]}]
      (let [comp? (= exec :compute)
            transform (cond-> transform (= exec :compute)
                              #(.get (futurize transform {:exec (spi/get-exec resolver :compute)})
                                     compute-timeout-ms TimeUnit/MILLISECONDS))
            exs (spi/get-exec resolver (if (= exec :mixed) :mixed :io))
            io-id (zipmap (concat (vals ins) (vals outs)) (concat (keys ins) (keys outs)))
            control (::flow/control ins)
            ;;TODO rotate/randomize after control per normal alts?
            read-chans (into [control] (-> ins (dissoc ::flow/control) vals))
            run
            #(loop [status :paused, state (when init (init args)), count 0]
               (let [pong (fn [] (async/>!! (outs ::flow/report)
                                            #::flow{:report :ping, :pid pid, :status status
                                                     :state state, :count count}))
                     handle-command (partial handle-command pid pong)
                     [nstatus nstate count]
                     (try
                       (if (= status :paused)
                         (let [nstatus (handle-command status (async/<!! control))
                               nstate (handle-transition transition status nstatus state)]
                           [nstatus nstate count])
                         ;;:running
                         (let [[msg c] (if transform
                                         (async/alts!! read-chans :priority true)
                                         ;;inject
                                         (when-let [msg (async/poll! control)]
                                           [msg control]))
                               cid (io-id c)]
                           (if (= c control)
                             (let [nstatus (handle-command status msg)
                                   nstate (handle-transition transition status nstatus state)]
                               [nstatus nstate count])
                             (try
                               (let [[nstate outputs] (if transform
                                                        (transform state cid msg)
                                                        (inject state))
                                     [nstatus nstate]
                                     (send-outputs status nstate outputs outs resolver control handle-command transition)]
                                 [nstatus nstate (inc count)])
                               (catch Throwable ex
                                 (async/>!! (outs ::flow/error)
                                            #::flow{:pid pid, :status status, :state state,
                                                     :count (inc count), :cid cid, :msg msg :op :step, :ex ex})
                                 [status state count])))))
                       (catch Throwable ex
                         (async/>!! (outs ::flow/error)
                                    #::flow{:pid pid, :status status, :state state, :count (inc count), :ex ex})
                         [status state count]))]
                 (when-not (= nstatus :exit) ;;fall out
                   (recur nstatus nstate (long count)))))]
        ((futurize run {:exec exs}))))))
