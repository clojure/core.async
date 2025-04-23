# Rationale

*June 28, 2013*

[core.async](https://github.com/clojure/core.async) is a new contrib library for Clojure that adds support for asynchronous programming using channels.

There comes a time in all good programs when components or subsystems must stop communicating directly with one another. This is often achieved via the introduction of queues between the producers of data and the consumers/processors of that data. This architectural indirection ensures that important decisions can be made with some degree of independence, and leads to systems that are easier to understand, manage, monitor and change, and make better use of computational resources, etc.

On the JVM, the [java.util.concurrent](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/package-summary.html) package provides some good concurrent blocking queues, and they are a viable and popular choice for Clojure programs. However, in order to use the queues one must dedicate one or more actual threads to their consumption. Per-thread stack allocation and task-switching overheads limit the number of threads that can be used in practice. Another limitation of j.u.c. queues is there is no way to block waiting on a set of alternatives.

On JavaScript engines, there are no threads and no queues.

Thread overheads or lack of threads often cause people to move to systems based upon events/callbacks, in the pursuit of greater efficiency (often under the misnomer 'scalability', which doesn't apply since you can't scale a single machine). Events complect communication and flow of control. While there are various mechanisms to make events/callbacks cleaner (FRP, Rx/Observables) they don't change their fundamental nature, which is that upon an event an arbitrary amount of other code is run, possibly on the same thread, leading to admonitions such as "don't do too much work in your handler", and phrases like "callback hell".

The objectives of core.async are:

* To provide facilities for independent threads of activity, communicating via queue-like _channels_
* To support both real threads and shared use of thread pools (in any combination), as well as ClojureScript on JS engines
* To build upon the work done on CSP and its derivatives

It is our hope that async channels will greatly simplify efficient server-side Clojure programs, and offer simpler and more robust techniques for front-end programming in ClojureScript.

## History

The roots of this style go back at least as far as [Hoare's Communicating Sequential Processes (CSP)](https://en.wikipedia.org/wiki/Communicating_sequential_processes), followed by realizations and extensions in e.g. [occam](https://en.wikipedia.org/wiki/Occam_programming_language), [Java CSP](https://www.cs.kent.ac.uk/projects/ofa/jcsp/) and the [Go programming language](https://golang.org/).

In modern incarnations, the notion of a channel becomes first class, and in doing so provides us the indirection and independence we seek.

A key characteristic of channels is that they are blocking. In the most primitive form, an unbuffered channel acts as a rendezvous, any reader will await a writer and vice-versa. Buffering can be introduced, but unbounded buffering is discouraged, as bounded buffering with blocking can be an important tool coordinating pacing and back pressure, ensuring a system doesn't take on more work than it can achieve.

## Details

### Just a library

*core.async* is a library. It doesn't modify Clojure. It is designed to support Clojure 1.5+.

### Creating channels

You can create a channel with the [chan](https://clojure.github.io/core.async/clojure.core.async.html#var-chan) function. This will return a channel that supports multiple writers and readers. By default, the channel is unbuffered, but you can supply a number to indicate a buffer size, or supply a buffer object created via [buffer](https://clojure.github.io/core.async/clojure.core.async.html#var-buffer), [dropping-buffer](https://clojure.github.io/core.async/clojure.core.async.html#var-dropping-buffer) or [sliding-buffer](https://clojure.github.io/core.async/clojure.core.async.html#var-sliding-buffer).

The fundamental operations on channels are putting and taking values. Both of those operations potentially block, but the nature of the blocking depends on the nature of the thread of control in which the operation is performed. core.async supports two kinds of threads of control - ordinary threads and IOC (inversion of control) 'threads'. Ordinary threads can be created in any manner, but IOC threads are created via [go blocks](https://clojure.github.io/core.async/clojure.core.async.html#var-go). Because JS does not have threads, only `go` blocks and IOC threads are supported in ClojureScript.

### go blocks and IOC 'threads'

`go` is a macro that takes its body and examines it for any channel operations. It will turn the body into a state machine. Upon reaching any blocking operation, the state machine will be 'parked' and the actual thread of control will be released. This approach is similar to that used in [C# async](https://msdn.microsoft.com/en-us/library/vstudio/hh191443.aspx). When the blocking operation completes, the code will be resumed (on a thread-pool thread, or the sole thread in a JS VM). In this way the inversion of control that normally leaks into the program itself with event/callback systems is encapsulated by the mechanism, and you are left with straightforward sequential code. It will also provide the illusion of threads, and more important, separable sequential subsystems, to ClojureScript.

The primary channel operations within go blocks are [>!](https://clojure.github.io/core.async/clojure.core.async.html#var-.3E.21) (_put_) and [<!](https://clojure.github.io/core.async/clojure.core.async.html#var-.3C.21) (_take_). The go block itself immediately returns a channel, on which it will eventually put the value of the last expression of the body (if non-nil), and then close.

### Channel on ordinary threads

There are analogous operations for use on ordinary threads - [>!!](https://clojure.github.io/core.async/clojure.core.async.html#var-.3E.21.21) (_put blocking_) and [<!!](https://clojure.github.io/core.async/clojure.core.async.html#var-.3C.21.21) (_take blocking_), which will block the thread on which they are called, until complete. While you can use these operations on threads created with e.g. future, there is also a macro, [thread](https://clojure.github.io/core.async/clojure.core.async.html#var-thread), analogous to `go`, that will launch a first-class thread and similarly return a channel, and should be preferred over `future` for channel work.

### Mixing modes

You can put on a channel from either flavor of `>!`/`>!!` and similarly take with either of `<!`/`<!!` in any combination, i.e. the channel is oblivious to the nature of the threads which use it.

### alt

It is often desirable to be able to wait for any one (and only one) of a set of channel operations to complete. This powerful facility is made available through the [alts!](https://clojure.github.io/core.async/clojure.core.async.html#var-alts.21) function (for use in `go` blocks), and the analogous [alts!!](https://clojure.github.io/core.async/clojure.core.async.html#var-alts.21.21) (_alts blocking_). If more than one operation is available to complete, one can be chosen at random or by priority (i.e. in the order they are supplied). There are corresponding [alt!](https://clojure.github.io/core.async/clojure.core.async.html#var-alt.21) and [alt!!](https://clojure.github.io/core.async/clojure.core.async.html#var-alt.21.21) macros that combine the choice with conditional evaluation of expressions.

### Timeouts

Timeouts are just channels that automatically close after a period of time. You can create one with the [timeout](https://clojure.github.io/core.async/clojure.core.async.html#var-timeout) function, then just include the timeout in an `alt` variant. A nice aspect of this is that timeouts can be shared between threads of control, e.g. in order to have a set of activities share a bound.

### The value of values

As with STM, the pervasive use of persistent data structures offers particular benefits for CSP-style channels. In particular, it is always safe and efficient to put a Clojure data structure on a channel, without fear of its subsequent use by either the producer or consumer.

### Contrasting Go language channels

core.async has obvious similarities to Go channels. Some differences with Go are:

* All of the operations are expressions (not statements)
* This is a library, not syntax
* `alts!` is a function (and supports a runtime-variable number of operations)
* Priority is supported in `alt`

Finally, Clojure is hosted, i.e. we are bringing these facilities to existing platforms, not needing a custom runtime. The flip-side is we don't have the underpinnings we would with a custom runtime. Reaching existing platforms remains a core Clojure value proposition.

### Whither actors?

I remain unenthusiastic about actors. They still couple the producer with the consumer. Yes, one can emulate or implement certain kinds of queues with actors (and, notably, people often do), but since any actor mechanism already incorporates a queue, it seems evident that queues are more primitive. It should be noted that Clojure's mechanisms for concurrent use of state remain viable, and channels are oriented towards the flow aspects of a system.

### Deadlocks

Note that, unlike other Clojure concurrency constructs, channels, like all communications, are subject to deadlocks, the simplest being waiting for a message that will never arrive, which must be dealt with manually via timeouts etc. CSP proper is amenable to certain kinds of automated correctness analysis. No work has been done on that front for core.async as yet.

Also note that async channels are not intended for fine-grained computational parallelism, though you might see examples in that vein.

## Future directions

Networks channels and distribution are interesting areas for attention. We will also being doing performance tuning and refining the APIs.

### Team

I'd like to thank the original team that helped bring core.async to life:

* Timothy Baldridge
* Ghadi Shayban
* Alex Miller
* Alex Redington
* Sam Umbach

I hope that these async channels will help you build simpler and more robust programs.

Rich

