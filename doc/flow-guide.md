# Flow Guide

## Getting started

The [flow](https://clojure.github.io/core.async/flow.html) library enables a strict separation application logic from the deployment concerns of topology, execution, communication, lifecycle, monitoring and error handling.

## Step fns and processes

You provide logic to flow in the form of _step-fns_, which are wrapped into running processes, executing in a loop. Flow manages the life cycle of the process and handles incoming and outgoing messages by putting or taking them on channels. Step-fns do not access channels directly or hold state, making them easy to test in isolation and reuse.

Step functions have four arities:

<a href="https://github.com/clojure/core.async/blob/master/doc/img/step-fn-arities.png?raw=true"><img src="https://github.com/clojure/core.async/blob/master/doc/img/step-fn-arities.png?raw=true" alt="step-fn arities" width="700"/></a>

### describe (0 arity)

The describe arity must return a static description of the step-fn's :params, :ins, and :outs. Each of these is a map of name (a keyword) to docstring.

For example, the describe arity might return this description for a simple step-fn:

```clojure
{:params {:size "Max size"}       ;; step-fn params
 :ins {:in "Input channel"}       ;; input channels
 :outs {:out "Output channel"}}   ;; output channels
```

The names used for input and output channels should be distinct (no overlap).

### init (1 arity)

The init arity is called once by the process to takes a set of args from the flow def (corresponding to the params returned from the describe arity) and returns the init state of the process.

### Process state

The process state is a map. It can contain any keys needed by the step-fn transition and transform arities. In addition, there are some flow-specific keys, described here. 

`::flow/pid` is added to the state by the process based on the name supplied in the flow def.

`::flow/in-ports` and `::flow/out-ports` are maps of cid to external channel, optionally returned in the initial state from the init arity. The in-ports and out-ports are used to connect source and sink processes to external channels. These channels must be provided by the step-fn and returned in the init arity map, either by creating the channel or using a channel passed in via the flow def init args for the process. The flow does not manage the lifecycle of these channels.

`::flow/input-filter`, a predicate of cid, can be returned in the state from any arity to indicate a filter on the process input channel read set. For example, a step-fn that is waiting for a response from multiple inputs might remove the channels that have already responded from the read-set until responses have been received from all.

### transition (2 arity)

The transition arity is called any time the flow or process undergoes a lifecycle transition (::flow/start, ::flow/stop, ::flow/pause, ::flow/resume). The description arity takes the current state and returns an updated state to be used for subsequent calls.

The step-fn should use the transition arity to coordinate the creation, pausing, and shutdown of external resources in a process.

### transform (3 arity)

The transform arity is called in a loop by the process for every message received on an input channel and returns a new state and a map of output cids to messages to return. The process will take care of sending these messages to the output channels. Output can be sent to none, any or all of the :outsenumerated, and/or an input named by a [pid inid] tuple (e.g. for reply-to), and/or to the ::flow/report output. A step need not output at all (output or msgs can be empyt/nil), however an output _message_ may never be nil (per core.async channels).

The step-fn may throw excepitons from any arity and they will be handled by flow. Exceptions thrown from the transition or transform arities, the exception will be logged on the flow's :error-chan.

### step-fn helpers

Some additional helpers exist to create step-fns from other forms:

* `lift*->step` - given a fn f taking one arg and returning a collection of non-nil values, creates a step-fn as needed by a process, with one input and one output (named :in and :out), and no state
* `lift1->step` - like `lift*->step` but for functions that return a single value (when `nil`, yield no output)
* `map->step` - given a map with keys `:describe`, `:init`, `:transition`, `:transform` corresponding to the arities above, create a step-fn.

### Creating a process

Processes can be created using the [process](https://clojure.github.io/core.async/clojure.core.async.flow.html#var-process) function, which takes a step-fn, and an option map with keys:

* `::workload` - one of `:mixed`, `:io` or `:compute`
* `:compute-timeout-ms` - if :workload is :compute, this timeout (default 5000 msec) will be used when getting the return from the future - see below

A :workload supplied as an option to process will override any :workload returned by the :describe fn of the process. If neither are provded the default is :mixed.

In the :workload context of :mixed or :io, this dictates the type of thread in which the process loop will run, _including its calls to transform_. 

When :io is specified, transform should not do extensive computation.

When :compute is specified, each call to transform will be run in a separate thread. The process loop will run in an :io context (since it no longer directly calls transform, all it does is I/O) and it will submit transform to the :compute executor then await (blocking, for compute-timeout-ms) the completion of the future returned by the executor. If the future times out it will be reported on ::flow/error.

When :compute is specified transform must not block!

### Reloading

Because the step-fn is called in a loop, it is a good practice to define the step-fn in a var and use the var (`#'the-fn`) instead of the function value itself (`the-fn`). This practice supports interactive development by allowing the var to be rebound from the repl while the flow is running.

## Flow def

The step-fns are how you supply code for each process in the flow. The other thing you must supply is the flow configuration that ties together the procs and the connections between them.

This flow definition is supplied to the [create-flow](https://clojure.github.io/core.async/clojure.core.async.flow.html#var-create-flow) function and consists of a map with `:procs`, `:conns`, and optionally some workflow executors.

The `:procs` is a map of pid -> proc-def. The proc-def is a map with `:proc` (the process function), the `:args` (passed to the init arity of the step-fn), and the `:chan-opts` which can be used to specify channel properties.

The `:conns` is a collection of `[[from-pid outid] [to-pid inid]]` tuples. Inputs and outputs support multiple connections. When an output is connected multiple times, every connection will get every message, per `core.async/mult`.

An example flow definition might look like this for a flow with two procs where the in-chan and out-chan are being passed through the source and sink args:

```clojure
{:procs {:source-proc {:proc (process #'source-fn)
                       :args {:source-chan in-chan}}
         :sink-proc   {:proc (process #'sink-fn)
                       :args {:sink-chan out-chan}}}
 :conns [ [[:source-proc :out] [:sink-proc :in]] ]}
````

The flow is created by passing the flow definition to [create-flow](https://clojure.github.io/core.async/clojure.core.async.flow.html#var-create-flow).

The returned flow object can be passed to the lifecycle methods (see next). In addition the flow can be used with [datafy](https://clojure.github.io/clojure/clojure.datafy-api.html#clojure.datafy/datafy) to get a datafied description of the flow. This is a static view - see `ping` described later for a dynamic view.

## Flow lifecycle

When a flow is created, it starts in the resumed state. The following flow functions can be used to change the flow lifecycle state:

* [start](https://clojure.github.io/core.async/clojure.core.async.flow.html#var-start) - Starts all procs in the flow, return a map of with `:report-chan` and `:error-chan`
* [stop](https://clojure.github.io/core.async/clojure.core.async.flow.html#var-stop) - Stops all procs in the flow
* [pause](https://clojure.github.io/core.async/clojure.core.async.flow.html#var-pause) - Pauses all procs in the flow
* [resume](https://clojure.github.io/core.async/clojure.core.async.flow.html#var-resume) - Resumes all procs in the flow
* [pause-proc](https://clojure.github.io/core.async/clojure.core.async.flow.html#var-pause-proc) - Pauses a single proc
* [resume-proc](https://clojure.github.io/core.async/clojure.core.async.flow.html#var-resume-proc) - Resumes a single proc

You can also use these functions to ping the running processes are return their current state and status:

* [ping](https://clojure.github.io/core.async/clojure.core.async.flow.html#var-ping) - Pings all procs and returns a map of their status
* [ping-proc](https://clojure.github.io/core.async/clojure.core.async.flow.html#var-ping-proc) - Pings a single proce by pid and returns a map of status

This function can be used to inject a message to an arbitrary `[pid cid]` channel:

* [inject](https://clojure.github.io/core.async/clojure.core.async.flow.html#var-inject) - Inject messages to any coord in the flow

The map returned from `start` has the flow's report and error channels. Procs can output messages to the `:report-chan` for unified logging across the flow. Exceptions thrown by a step-fn or procs in the flow are all logged to the `:error-chan`.

## Flow monitor

See [core.async.flow-monitor](https://github.com/clojure/core.async.flow-monitor/) for how to use the flow-monitor tool.