# Flow

## Rationale

The [rationale](https://clojure.github.io/core.async/rationale.html) for **core.async** says "There comes a time in all good programs when components or subsystems must stop communicating directly with one another." And core.async provides fundamental tools (channels) for doing that.

But using core.async well, e.g. keeping your I/O out of your computational logic, requires discipline and architectural savvy, and to do so consistently throughout an application or organization, conventions. Given channels, many architectural decisions remain regarding thread execution, backpressure, error handling etc. And often the topology of your network of communicating processes *emerges* out of the flow of control of your program as various pieces of code create threads and wire channels together, interleaved with computation, making it difficult to see the topology or administer it in one place.

The fundamental objective of __core.async.flow__ is to enable a strict separation of your application logic from its topology, execution, communication, lifecycle, monitoring and error handling, all of which are provided by and centralized in, c.a.flow, yielding more consistent, robust, testable, observable and operable systems.

## Overview

__core.async.flow__ provides *concrete* implementations of two more abstractions - the '__process__' - a thread of activity, and the '__flow__' - a directed graph of processes communicating via channels. A single data structure describes your flow topology, and has all of the settings for threads, channels etc. A process accepts data from and provides data to channels. The process implementation in c.a.flow handles all channel I/O, thread lifecycle and coordination with the flow graph.

All you need to do in your application is:

1. Define ordinary, often pure, data->data functions that the processes will run in their inner loop to do the *computational* part of processing messages (aka 'step' functions). These functions do not handle channels or threads or lifecycle, and do not even know they are running in a flow. They can be tested in isolation, and hot-reloaded. If they encounter a problem they can, and should, just throw an exception. The process will take care of it from there.

2. Define a flow by creating a data structure that enumerates the processes and the connections between their inputs and outputs, as well as various configuration settings for both.

<a href="https://github.com/clojure/core.async/blob/master/doc/img/flow-concerns.png?raw=true"><img src="https://github.com/clojure/core.async/blob/master/doc/img/flow-concerns.png?raw=true" alt="core.async.flow concerns" width="700"/></a>

With these application inputs, c.a.flow does the rest. It inquires of the processes what channels they require, creates those channels, then instantiates the processes making all of the channel connections between them. The processes in turn start threads (in fully user-configurable thread pools), await inputs, monitor the admin control channel, and when inputs arrive make data->data calls to your application logic, taking the return from that and sending it to the designated output channels. The processes follow a protocol used by the flow to do lifecycle management and error handling.

Once you've created a flow, the API provides functions to start/stop(shutdown) the flow, and to pause/resume both the flow and individual processes, to ping processes to get their state and that of their connected channels, to inject data into any point in the graph etc. The flow provides channels containing the ordinary monitoring/reporting stream and, separately, the error stream. 

The library provides many more details and features, including the ability to create, via ordinary functions, processes that act as __sources__ (of data from outside the flow) or __sinks__ (to recipients outside the flow) so you can situate your flow in a broader context while still coordinating resource management with the flow lifecycle.

I hope __core.async.flow__ enables you to write more robust and smaller applications, with more focus on your domain logic and less on plumbing.

Rich Hickey 
1/2025
