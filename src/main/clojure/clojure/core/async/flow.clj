;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns ^{:author "Rich Hickey"}
  clojure.core.async.flow
  "
  Note - Alpha, work-in-progress, names and other details are in flux 

  A library for building concurrent, event driven data processing
  flows out of communication-free functions, while centralizing
  control, reporting, execution and error handling. Built on core.async.

  The top-level construct is the flow, comprising:
  a set of processes (generally, threads) - concurrent activities
  a set of channels flowing data into and out of the processes
  a set of channels for centralized control, reporting, error-handling,
    and execution of the processes

  The flow library itself constructs processes, channels and flows. The
  user provides configuration data and process logic (step-fns) that
  specify how the flow should work.

  A flow is constructed from flow configuration data which defines a
  directed graph of processes and the connections between
  them. Processes describe their I/O requirements and the
  flow (library) itself creates channels and passes them to the
  processes that requested them. See 'create-flow' for the
  details. The flow configuration provides a centralized place for
  policy decisions regarding process settings, threading, buffering etc.

  Flow also provides a subsystem for broadcast communication of
  out-of-band messages without explicit connections or
  declarations. This could for example be used to communicate the
  passage of (real or virtual) time. Broadcast messages are associated
  with (otherwise undeclared) signal-ids, and will be received by
  processes selecting those ids. Broadcasts messages will arrive along
  with messages from process inputs, so signal-ids must not conflict
  with any process input-id. Thus namespaced keywords, UUIDs etc or
  tuples thereof are recommended as signal-ids. See process
  describe/transform and inject below for details.
  
  It is expected that applications will rarely define instances of the
  process protocol but instead use the API function 'process' that
  implements the process protocol in terms of calls to ordinary
  functions (step-fns) that might include no communication or
  core.async code. In this way the library helps you achieve a strict
  separation of your application logic from its execution,
  communication, lifecycle, error handling and monitoring.

  Note that at several points the library calls upon the user to
  supply ids for processes, inputs, outputs etc. These should be
  keywords. When a namespaced keyword is required it is explicitly
  stated. This documentation refers to various keywords utilized by
  the library itself as ::flow/xyz, where ::flow is an alias for
  clojure.core.async.flow

  Flows support the Clojure 'datafy' protocol to support
  observability. See also the 'ping' and 'ping-proc' fns for a live
  view of processes.

  A process is represented in the flow definition by an implementation
  of spi/ProcLauncher that starts it. See the spi docs for
  details."

  (:require
   [clojure.core.async.flow.impl :as impl]
   [clojure.core.async.flow.impl.graph :as g]))

(set! *warn-on-reflection* true)

(defn create-flow
  "Creates a flow from the supplied configuration: a map containing the
  keys :procs and :conns, and optionally :mixed-exec/:io-exec/:compute-exec

  :procs - a map of pid->proc-def
  where proc-def is a map with keys :proc, :args, :chan-opts

  :proc - a function that starts a process
  :args - a map of param->val which will be passed to the process ctor
  :chan-opts - a map of io-id->{:keys [buf-or-n xform]},
               where io-id is an input/output name, and buf-or-n
               and xform have their meanings per core.async/chan
               the default for inputs and outputs is {:buf-or-n 10}
  
  :conns - a collection of [[from-pid outid] [to-pid inid]] tuples.

  Inputs and outputs support multiple connections. When an output is
  connected multiple times every connection will get every message, as
  per a core.async/mult. Note that non-multed outputs do not have
  corresponding channels and thus any chan-opts will be ignored.

  Broadcast signals are conveyed to a process via a channel with an
  async/sliding-buffer of size 100, thus signals not handled in a
  timely manner will be dropped in favor of later arriving signals.
  
  :mixed-exec/:io-exec/:compute-exec -> ExecutorService
  These can be used to specify the ExecutorService to use for the
  corresonding workload, in lieu of the lib defaults.

  N.B. The flow is not started. See 'start'"
  [config] (impl/create-flow config))

(defn start
  "starts the entire flow from init values. The processes start paused.
  Call 'resume' or 'resume-proc' to start flow.  Returns a map with keys:
  
  :report-chan - a core.async chan for reading.'ping' responses
  will show up here, as will any explicit ::flow/report outputs
  from :transform
  
  :error-chan - a core.async chan for reading. Any (and only)
  exceptions thrown anywhere on any thread inside a flow will appear
  in maps sent here. There will at least be a ::flow/ex entry with the
  exception, and may be additional keys for pid, state, status etc
  depending on the context of the error."
  [g] (g/start g))

(defn stop
  "shuts down the flow, stopping all procsesses and closing the error
  and report channels. The flow can be started again"
  [g] (g/stop g))

(defn pause
  "pauses a running flow"
  [g] (g/pause g))

(defn resume
  "resumes a paused flow"
  [g] (g/resume g))

(defn ping
  "pings all processes, returning a map of pid -> proc status and
  state, for those procs that reply within timeout-ms (default 1000)"
  [g & {:keys [timeout-ms] :or {timeout-ms 1000}}]
  (g/ping g timeout-ms))

(defn pause-proc
  "pauses a process"
  [g pid] (g/pause-proc g pid))

(defn resume-proc
  "resumes a process"
  [g pid] (g/resume-proc g pid))

(defn ping-proc
  "like ping, but just pings the specified process"
  [g pid & {:keys [timeout-ms] :or {timeout-ms 1000}}]
  (g/ping-proc g pid timeout-ms))
  
(defn inject
  "asynchronously puts the messages on the channel corresponding to
  the input or output of the process, returning a future that will
  complete when done. You can broadcast messages on a signal using the
  special coord [::flow/cast a-signal-id]. Note that signals cannot be
  sent to a particular process."
  [g [pid io-id :as coord] msgs] (g/inject g coord msgs))

(defn process
  "Given a function of four arities (0-3), aka the 'step-fn',
  returns a launcher that creates a process compliant with the process
  protocol (see the spi/ProcLauncher doc).

  The possible arities for the step-fn are

  0 - 'describe',   () -> description
  1 - 'init',       (arg-map) -> initial-state
  2 - 'transition', (state transition) -> state'
  3 - 'transform',  (state input msg) -> [state' output-map]

  This is the core facility for defining the logic for processes via
  ordinary functions. Using a var holding a fn as the 'step-fn' is the
  preferred method for defining a proc, as it enables
  hot-code-reloading of the proc logic in a flow, and better names in
  datafy.

  arity 0 - 'describe', () -> description
  where description is a map with possible keys:
  :params :ins and :outs, each of which in turn is a map of keyword to doc string
  :signal-select - a predicate of a signal-id. Messages on approved
                   signals will appear in the transform arity (see below)
                   For the simple case of enumerated signal-ids, use a set,
                   e.g. #{:this/signal :that/signal}
                   If no :signal-select is provided, no signals will be received
  :workload with possible values of :mixed :io :compute.
  All entries in the describe return map are optional.
  
  :params describes the initial arguments to setup the state for the function.
  :ins enumerates the process input[s], for which the flow will create channels
  :outs enumerates the process output[s], for which the flow _may_ create channels.
  :workload - describes the nature of the workload, one of :mixed :io or :compute
          an :io workload should not do extended computation
          a :compute workload should never block
  
  No io-id key may be present in both :ins and :outs, allowing for a
  uniform channel coordinate system of [:process-id :channel-id]. The
  ins/outs/params returned will be the ins/outs/params of the
  process. describe may be called by users to understand how to use
  the proc. It will also be called by the impl in order to discover
  what channels are needed.

  arity 1 - 'init', (arg-map) -> initial-state
  
  The init arity will be called once by the process to establish any initial
  state. The arg-map will be a map of param->val, as supplied in the
  flow def. The key ::flow/pid will be added, mapped to the pid
  associated with the process (useful e.g. if the process wants to
  refer to itself in reply-to coordinates). 

  Optionally, a returned init state may contain the
  keys ::flow/in-ports and/or ::flow/out-ports. These should be maps
  of cid -> a core.async.channel. The cids must not conflict with the
  in/out ids. These channels will become part of the input/output set
  of the process, but are not otherwise visible/resolvable within the
  flow. Ports are a way to allow data to enter or exit the flow from
  outside of it. Use :transition to coordinate the lifecycle of these
  external channels.

  Optionally, _any_ returned state, whether from init, transition
  or transform, may contain the key ::flow/input-filter, a predicate
  of cid. Only inputs (including in-ports) satisfying the predicate
  will be part of the next channel read set. In the absence of this
  predicate all inputs are read.

  arity 2 - 'transition', (state transition) -> state'

  The transition arity will be called when the process makes a state
  transition, transition being one of ::flow/resume, ::flow/pause
  or ::flow/stop

  With this a process impl can track changes and coordinate
  resources, especially cleaning up any resources on :stop, since the
  process will no longer be used following that. See the SPI for
  details. state' will be the state supplied to subsequent calls.

  arity 3 - 'transform', (state in-or-signal-id msg) -> [state' output]
  where output is a map of outid->[msgs*]

  The transform arity will be called every time a message arrives at
  any of the inputs or signals (selected via :signal-select in
  describe), identified by the id. Output can be sent to none, any or
  all of the :outs enumerated, and/or an input named by a [pid in-id]
  coord tuple (e.g. for reply-to), and/or to the ::flow/report
  output.

  You can broadcast output to all processes selecting a signal via
  the special coord [::flow/cast a-signal-id] Note that signals cannot
  be sent to a particular process.

  A step need not output at all (output or msgs can be empty/nil),
  however an output _message_ may never be nil (per core.async
  channels). state' will be the state supplied to subsequent calls.

  process also accepts an option map with keys:
  :workload - one of :mixed, :io or :compute
  :compute-timeout-ms - if :workload is :compute, this timeout (default 5000 msec)
                will be used when getting the return from the future - see below

  A :workload supplied as an option to process will override
  any :workload returned by the :describe fn of the process. If neither
  are provded the default is :mixed.

  In the :workload context of :mixed or :io, this dictates the type of
  thread in which the process loop will run, _including its calls to
  transform_. 

  When :io is specified, transform should not do extensive computation.

  When :compute is specified, each call to transform will be run in a
  separate thread. The process loop will run in an :io context (since
  it no longer directly calls transform, all it does is I/O) and it
  will submit transform to the :compute executor then await (blocking,
  for compute-timeout-ms) the completion of the future returned by the
  executor. If the future times out it will be reported
  on ::flow/error.

  When :compute is specified transform must not block!"
  ([step-fn] (process step-fn nil))
  ([step-fn {:keys [workload compute-timeout-ms] :as opts}]
   (impl/proc step-fn opts)))

(defn map->step
  "given a map of functions corresponding to step fn arities (see
  'process'), returns a step fn suitable for passing to 'process'. You
  can use this map form to compose the proc logic from disparate
  functions or to leverage the optionality of some of the entry
  points.

  The keys in the map are:
  :describe, arity 0 - required
  :init, arity 1 - optional, but should be provided if 'describe' returns :params.
  :transition, arity 2 - optional
  :transform, arity 3 - required"
  [{:keys [describe init transition transform]}]
  (assert (and describe transform) "must provide :describe and :transform")
  (fn
    ([] (describe))
    ([arg-map] (when init (init arg-map)))
    ([state trans] (if transition (transition state trans) state))
    ([state input msg] (transform state input msg))))

(defn lift*->step
  "given a fn f taking one arg and returning a collection of non-nil
  values, creates a step fn as needed by process, with one input
  and one output (named :in and :out), and no state."
  [f]
  (fn
    ([] {:ins {:in (str "the argument to " f)}
         :outs {:out (str "the return of " f)}})
    ([arg-map] nil)
    ([state transition] nil)
    ([state input msg] [nil {:out (f msg)}])))

(defn lift1->step
  "like lift*->step except taking a fn returning one value, which when
  nil will yield no output."
  [f]
  (fn
    ([] {:ins {:in (str "the argument to " f)}
         :outs {:out (str "the return of " f)}})
    ([arg-map] nil)
    ([state transition] nil)
    ([state input msg] [nil (when-some [m (f msg)] {:out (vector m)})])))

(defn futurize
  "Takes a fn f and returns a fn that takes the same arguments as f
  and immediately returns a future, having started a thread for the
  indicated workload, or via the supplied executor, that invokes f
  with those args and completes that future with its return.

  futurize accepts kwarg options:
  :exec - one of the workloads :mixed, :io, :compute
          or a j.u.c.ExecutorService object,
          default :mixed"
  [f & {:keys [exec]
        :or {exec :mixed} :as opts}]
  (impl/futurize f opts))
