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
            [clojure.core.async.flow.impl.graph :as graph]
            [clojure.walk :as walk]
            [clojure.datafy :as datafy])
  (:import [java.util.concurrent Future Executors ExecutorService TimeUnit]
           [java.util.concurrent.locks ReentrantLock]))

(set! *warn-on-reflection* true)

;;TODO - something specific, e.g. make aware of JDK version and vthreads
(defonce mixed-exec clojure.lang.Agent/soloExecutor)
(defonce io-exec clojure.lang.Agent/soloExecutor)
(defonce compute-exec clojure.lang.Agent/pooledExecutor)

(defn datafy [x]
  (condp instance? x
    clojure.lang.Fn (-> x str symbol)
    ExecutorService (str x)
    clojure.lang.Var (symbol x)
    (datafy/datafy x)))

(defn futurize ^Future [f {:keys [exec]}]
  (fn [& args]
    (let [^ExecutorService e (case exec
                                   :compute compute-exec
                                   :io io-exec
                                   :mixed mixed-exec
                                   exec)]
      (.submit e ^Callable #(apply f args)))))

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
    :or {mixed-exec mixed-exec, io-exec io-exec, compute-exec compute-exec}}]
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
        send-command (fn sc
                       ([cmap]
                        (let [{:keys [control]} (running-chans)]
                          (async/>!! control cmap)))
                       ([command to] (sc #::flow{:command command :to to})))
        handle-ping (fn [to timeout-ms]
                      (let [reply-chan (async/chan (count procs))
                            ret-chan (async/take (if (= to ::flow/all) (count procs) 1) reply-chan)
                            timeout (async/timeout timeout-ms)
                            _ (send-command #::flow{:command ::flow/ping, :to to, :reply-chan reply-chan})
                            ret (loop [ret nil]
                                  (let [[{::flow/keys [pid] :as m} c] (async/alts!! [ret-chan timeout])]
                                    (if (some? m)
                                      (recur (assoc ret pid m))
                                      ret)))]
                        (if (= to ::flow/all) ret (-> ret vals first))))]
    (reify
      clojure.core.protocols/Datafiable
      (datafy [_]
        (walk/postwalk datafy {:procs procs, :conns conns, :execs execs
                               :chans (select-keys @chans [:ins :outs :error :report])}))

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
                        (spi/start proc {:pid pid :args (assoc args ::flow/pid pid) :resolver resolver
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
      (ping [_ timeout-ms] (handle-ping ::flow/all timeout-ms))

      (pause-proc [_ pid] (send-command ::flow/pause pid))
      (resume-proc [_ pid] (send-command ::flow/resume pid))
      (ping-proc [_ pid timeout-ms] (handle-ping pid timeout-ms))
      (command-proc [_ pid command kvs]
        (assert (and (namespace command) (not= (namespace ::flow/command) (namespace command)))
                "extension commands must be in your own namespace")
        (let [{:keys [control]} (running-chans)]
          (async/>!! control (merge kvs #::flow{:command command :to pid}))))
          
      (inject [_ coord msgs]
        (let [{:keys [resolver]} (running-chans)
              chan (spi/get-write-chan resolver coord)]
          ((futurize #(doseq [m msgs]
                        (async/>!! chan m))
                     {:exec :io})))))))

(defn handle-command
  [pid pong status cmd]
  (let [transition #::flow{:stop :exit, :resume :running, :pause :paused}
        {::flow/keys [to command reply-chan]} cmd]
    (if (#{::flow/all pid} to)
      (do
        (when (= command ::flow/ping) (pong reply-chan))
        (or (transition command) status))
      status)))

(defn handle-transition
  "when transition, returns maybe new state"
  [transition status nstatus state]
  (if (and transition (not= status nstatus))
    (transition state (case nstatus
                            :exit ::flow/stop
                            :running ::flow/resume
                            :paused ::flow/pause))
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
  [fm {:keys [workload compute-timeout-ms]}]
  (let [{:keys [describe init transition transform] :as impl}
        (if (map? fm) fm {:describe fm :init fm :transition fm :transform fm})
        {:keys [params ins] :as desc} (describe)
        workload (or workload (:workload desc) :mixed)]
    (assert transform "must provide :transform")
    (assert (or (not params) init) "must have :init if :params")
    (reify
      clojure.core.protocols/Datafiable
      (datafy [_]
        (let [{:keys [params ins outs]} desc]
          (walk/postwalk datafy {:impl fm :params (-> params keys vec)
                                 :ins (-> ins keys vec) :outs (-> outs keys vec)})))
      spi/ProcLauncher
      (describe [_] desc)
      (start [_ {:keys [pid args ins outs resolver]}]
        (assert (or (not params) args) "must provide :args if :params")
        (let [comp? (= workload :compute)
              transform (cond-> transform (= workload :compute)
                                #(.get (futurize transform {:exec (spi/get-exec resolver :compute)})
                                       compute-timeout-ms TimeUnit/MILLISECONDS))
              exs (spi/get-exec resolver (if (= workload :mixed) :mixed :io))
              state (when init (init args))
              ins (into (or ins {}) (::flow/in-ports state))
              outs (into (or outs {}) (::flow/out-ports state))
              io-id (zipmap (concat (vals ins) (vals outs)) (concat (keys ins) (keys outs)))
              control (::flow/control ins)
              read-ins (dissoc ins ::flow/control)
              run
              #(loop [status :paused, state state, count 0, read-ins read-ins]
                 (let [pong (fn [c]
                              (let [pins (dissoc ins ::flow/control)
                                    pouts (dissoc outs ::flow/error ::flow/report)]
                                (async/>!! c (walk/postwalk datafy
                                                #::flow{:pid pid, :status status
                                                        :state state, :count count
                                                        :ins pins :outs pouts}))))
                       handle-command (partial handle-command pid pong)
                       [nstatus nstate count read-ins]
                       (try
                         (if (= status :paused)
                           (let [nstatus (handle-command status (async/<!! control))
                                 nstate (handle-transition transition status nstatus state)]
                             [nstatus nstate count read-ins])
                           ;;:running
                           (let [ ;;TODO rotate/randomize after control per normal alts?
                                 read-chans (let [ipred (or (::flow/input-filter state) identity)]
                                              (reduce-kv (fn [ret cid chan]
                                                           (if (ipred cid)
                                                             (conj ret chan)
                                                             ret))
                                                         [control] read-ins))
                                 [msg c] (async/alts!! read-chans :priority true)
                                 cid (io-id c)]
                             (if (= c control)
                               (let [nstatus (handle-command status msg)
                                     nstate (handle-transition transition status nstatus state)]
                                 [nstatus nstate count read-ins])
                               (try
                                 (let [[nstate outputs] (transform state cid msg)
                                       [nstatus nstate]
                                       (send-outputs status nstate outputs outs
                                                     resolver control handle-command transition)]
                                   [nstatus nstate (inc count) (if (some? msg)
                                                                 read-ins
                                                                 (dissoc read-ins cid))])
                                 (catch Throwable ex
                                   (async/>!! (outs ::flow/error)
                                              #::flow{:pid pid, :status status, :state state,
                                                      :count (inc count), :cid cid, :msg msg :op :step, :ex ex})
                                   [status state count read-ins])))))
                         (catch Throwable ex
                           (async/>!! (outs ::flow/error)
                                      #::flow{:pid pid, :status status, :state state, :count (inc count), :ex ex})
                           [status state count read-ins]))]
                   (when-not (= nstatus :exit) ;;fall out
                     (recur nstatus nstate (long count) read-ins))))]
          ((futurize run {:exec exs})))))))
