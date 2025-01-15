;;   Copyright (c) Rich Hickey and contributors. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns ^{:author "Rich Hickey"}
  clojure.core.async.flow
  "A library for building concurrent, event driven data processing
  flows out of communication-free functions, while centralizing
  control, reporting, execution and error handling. Built on core.async.

  The top-level construct is the flow, comprising:
  a set of processes (generally, threads) - concurrent activities
  a set of channels flowing data into and out of the processes
  a set of channels for centralized control, reporting, error-handling,
    and execution of the processes

  A flow is constructed from flow definition data which defines a
  directed graph of processes and the connections between
  them. Processes describe their I/O requirements and the
  flow (library) itself creates channels and passes them to the
  processes that requested them. See 'create-flow' for the
  details. The flow definition provides a centralized place for policy
  decisions regarding configuration, threading, buffering etc. 

  It is expected that applications will rarely define processes but
  instead use the API functions here, 'process' and 'step-process',
  that implement the process protocol in terms of calls to ordinary
  functions that include no communication or core.async code. In this
  way the library helps you achieve a strict separation of your
  application logic from its execution, communication, lifecycle and
  monitoring.

  Note that at several points the library calls upon the user to
  supply ids for processes, inputs, outputs etc. These should be
  keywords. When a namespaced keyword is required it is explicitly
  stated. This documentation refers to various keywords utilized by
  the library itself as ::flow/xyz, where ::flow is an alias for
  clojure.core.async.flow

  A process is represented in the flow definition by an implementation
  of spi/ProcLauncher that starts it. See the spi docs for
  details."

  (:require
   [clojure.core.async.flow.impl :as impl]
   [clojure.core.async.flow.impl.graph :as g]))

(set! *warn-on-reflection* true)

(defn create-flow
  "Creates a flow from the supplied definition: a map containing the
  keys :procs and :conns, and optionally :mixed-exec/:io-exec/:compute-exec

  :procs - a map of pid->proc-def
  where proc-def is a map with keys :proc, :args, :chan-opts

  :proc - a function that starts a process
  :args - a map of param->val which will be passed to the process ctor
  :chan-opts - a map of in-or-out-id->{:keys [buf-or-n xform]}, where buf-or-n
               and xform have their meanings per core.async/chan
               the default is {:buf-or-n 10}
  
  :conns - a collection of [[from-pid outid] [to-pid inid]] tuples.

  Inputs and outputs support mutliple connections. When an output is
  connected multiple times every connection will get every message,
  as per a core.async/mult.

  :mixed-exec/:io-exec/:compute-exec -> ExecutorService
  These can be used to specify the ExecutorService to use for the
  corresonding context, in lieu of the lib defaults

  N.B. The flow is not started. See 'start'"
  [def] (impl/create-flow def))

(defn start
  "starts the entire flow from init values. The processes start paused.
  Call 'resume' or 'resume-proc' to start flow.  returns a map with keys:
  
  ::flow/report-chan - a core.async chan for reading.'ping' reponses
  will show up here, as will any explicit ::flow/report outputs
  from :transform/:inject
  
  ::flow/error-chan - a core.async chan for reading. Any (and only)
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
  "pings all processes, which will put their status and state on the
  report channel returned from start"
  [g] (g/ping g))

(defn pause-proc
  "pauses a process"
  [g pid] (g/pause-proc g pid))

(defn resume-proc
  "resumes a process"
  [g pid] (g/resume-proc g pid))

(defn ping-proc
  "pings the process, which will put its status and state on the report
  channel returned from start"
  [g pid] (g/ping-proc g pid))

(defn command-proc
  "synchronously sends a process-specific command with the given id and
  additional kvs to the process. The cmd-id must be ns-qualified with
  a ns you own."
  [g pid cmd-id more-kvs] (g/command-proc g pid cmd-id more-kvs))
  
(defn inject
  "synchronously puts the messages on the channel corresponding to the
  input or output of the process"
  [g [pid io-id :as coord] msgs] (g/inject g coord msgs))

(defn process
  "Given a map of functions (described below), returns a launcher that
  creates a process compliant with the process protocol (see the
  spi/ProcLauncher doc). The possible entries for process-impl-map
  are :describe, :init, :transition, :transform and :inject. This is
  the core facility for defining the logic for processes via ordinary
  functions.

  :describe - required, () -> desc
  where desc is a map with keys :params :ins and :outs, each of which
  in turn is a map of keyword to doc string
  
  :params describes the initial arguments to setup the state for the function.
  :ins enumerates the input[s], for which the flow will create channels
  :outs enumerates the output[s], for which the flow may create channels.
  
  No key may be present in both :ins and :outs The ins/outs/params of f
  will be the ins/outs/params of the process. describe may be called
  by users to understand how to use the proc. It will also be called
  by the impl in order to discover what channels are needed.

  :init - optional, (arg-map) -> initial-state
  
  init will be called once by the process to establish any
  initial state. The arg-map will be a map of param->val, as supplied
  in the flow def. init must be provided if 'describe' returns :params.

  :transition - optional, (state transition) -> state'

  transition will be called when the process makes a state transition,
  transition being one of ::flow/resume, ::flow/pause or ::flow/stop

  With this fn a process impl can track changes and coordinate
  resources, especially cleaning up any resources on :stop, since the
  process will no longer be used following that. See the SPI for
  details. state' will be the state supplied to subsequent calls.

  Exactly one of either :transform or :inject are required.

  :transform - (state in-name msg) -> [state' output]
  where output is a map of outid->[msgs*]

  The transform fn will be called every time a message arrives at any
  of the inputs. Output can be sent to none, any or all of the :outs
  enumerated, and/or an input named by a [pid inid] tuple (e.g. for
  reply-to), and/or to the ::flow/report output. A step need not
  output at all (output or msgs can be empyt/nil), however an output _message_
  may never be nil (per core.async channels). state' will be the state
  supplied to subsequent calls.

  :inject - (state) -> [state' output]
  where output is a map of outid->[msgs*], per :transform
 
  The inject fn is used for sources - proc-impls that inject new data
  into the flow by doing I/O with something external to the flow and
  feeding that data to its outputs. A proc-impl specifying :inject may not
  specify any :ins in its descriptor, as none but the ::flow/control channel
  will be read. Instead, inject will be called every time through the
  process loop, and will presumably do blocking or paced I/O to get
  new data to return via its outputs. If it does blocking I/O it
  should do so with a timeout so it can regularly return to the
  process loop which can then look for control messages - it's fine
  for inject to return with no output. Do not spin poll in the inject
  fn.

  proc accepts an option map with keys:
  :exec - one of :mixed, :io or :compute, default :mixed
  :compute-timeout-ms - if :exec is :compute, this timeout (default 5000 msec)
                will be used when getting the return from the future - see below

  The :compute context is not allowed for proc impls that
  provide :inject (as I/O is presumed).

  In the :exec context of :mixed or :io, this dictates the type of
  thread in which the process loop will run, _including its calls to
  transform/inject_. 

  When :io is specified transform/inject should not do extensive computation.

  When :compute is specified (only allowed for :transform), each call
  to transform will be run in a separate thread. The process loop will
  run in an :io context (since it no longer directly calls transform,
  all it does is I/O) and it will submit transform to the :compute
  executor then await (blocking, for compute-timeout-ms) the
  completion of the future returned by the executor. If the future
  times out it will be reported on ::flow/error.

  When :compute is specified transform must not block!"
  ([process-impl-map] (process process-impl-map nil))
  ([process-impl-map {:keys [exec timeout-ms]
                      :or {exec :mixed, timeout-ms 5000} :as opts}]
   (impl/proc process-impl-map opts)))

(defn step-process
  "Given a (e.g. communication-free) step function f of three
  arities (described below), and the same opts as 'process', returns a
  launcher that creates a process compliant with the process
  protocol (see 'process').

  The arities of f are:

  ()->desc
  a function matching the semantics of process' :describe

  (arg-map)->initial-state
  a function matching the semantics of process' :init
  
  (state in-name msg)->[state' output]
  a function matching the semantics of process' :transform"
  ([f] (step-process f nil))
  ([f opts]
   (process {:describe f, :init f, :transform f} opts)))

(defn futurize
  "Takes a fn f and returns a fn that takes the same arguments as f and
  immediately returns a future, having starting a thread of the
  indicated type, or via the supplied executor, that invokes f with
  those args and completes that future with its return.

  futurize accepts kwarg options:
  :exec - one of :mixed, :io, :compute
          or a j.u.c.ExecutorService object,
          default :mixed"
  [f & {:keys [exec]
        :or {exec :mixed} :as opts}]
  (impl/futurize f opts))

(defn lift*->step
  "given a fn f taking one arg and returning a collection of non-nil
  values, create a 'step' fn as needed by step-process, with one input
  and one output (named :in and :out), and no state."
  [f]
  (fn
    ([] {:params {}
         :ins {:in (str "the argument to " f)}
         :outs {:out (str "the return of " f)}})
    ([_] nil)
    ([_ _ msg] [nil {:out (f msg)}])))

(defn lift1->step
  "like lift*->step except taking a fn returning one value, which, when
  nil, will yield no output."
  [f]
  (lift*->step #(when-some [m (f %)] (vector m))))
