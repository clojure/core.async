# API Reference

## Channels

Channels are queues that carry values and support multiple writers and readers. Channels are created with [chan](https://clojure.github.io/core.async/clojure.core.async.html#var-chan). Values in a channel are stored in a buffer. Buffers are never unbounded and there are several provided buffer types:

* Unbuffered - `(chan)` - no buffer is used, and a rendezvous is required to pass a value through the channel from writer to reader
* Fixed size - `(chan 10)`
* Dropping - `(chan (dropping-buffer 10))` - fixed size, and when full drop newest value
* Sliding - `(chan (sliding-buffer 10))` - fixed size, and when full drop oldest value

Channels are first-class values that can be passed around like any other value.

Channels may optionally be supplied with a [transducer](https://clojure.org/reference/transducers) and an exception handler. The transducer will be applied to values that pass through the channel. If a transducer is supplied, the channel *must* be buffered (transducers can create intermediate values that must be stored somewhere). Channel transducers must not block, whether by issuing i/o operations or by externally synchronizing, else risk impeding or deadlocking go blocks.

The `ex-handler` is a function of one argument (a Throwable). If an exception occurs while applying the transducer, the `ex-handler` will be invoked, and any non-nil return value will be placed in the channel. If no `ex-handler` is supplied, exceptions will flow and be handled where they occur (note that this may in either the writer or reader thread depending on the operation and the state of the buffer).

* Creating channels: [chan](https://clojure.github.io/core.async/clojure.core.async.html#var-chan)
* Buffers: [buffer](https://clojure.github.io/core.async/clojure.core.async.html#var-buffer) [dropping-buffer](https://clojure.github.io/core.async/clojure.core.async.html#var-dropping-buffer) [sliding-buffer](https://clojure.github.io/core.async/clojure.core.async.html#var-sliding-buffer) [unblocking-buffer?](https://clojure.github.io/core.async/clojure.core.async.html#var-unblocking-buffer.3F)

### Put and take

Any value can be placed on a channel, except `nil`. The primary operations on channels are _put_ and _take_, which are provided in several variants:

* Blocking: [>!!](https://clojure.github.io/core.async/clojure.core.async.html#var-.3E.21.21), [<!!](https://clojure.github.io/core.async/clojure.core.async.html#var-.3C.21.21)
* Parking: [>!](https://clojure.github.io/core.async/clojure.core.async.html#var-.3E.21), [<!](https://clojure.github.io/core.async/clojure.core.async.html#var-.3C.21)
* Async: [put!](https://clojure.github.io/core.async/clojure.core.async.html#var-put.21), [take!](https://clojure.github.io/core.async/clojure.core.async.html#var-take.21)
* Non-blocking: [offer!](https://clojure.github.io/core.async/clojure.core.async.html#var-offer.21), [poll!](https://clojure.github.io/core.async/clojure.core.async.html#var-poll.21)

NOTE: As a mnemonic, the `<` or `>` points in the direction the value travels relative to the channel arg. For example, in `(>!! chan val)` the `>` points into the channel (_put_) and `(<!! chan)` points out of the channel (_take_).

The use case dictates the variant to use. Parking operations are only valid in `go` blocks (see below for more) and never valid outside the lexical scope of a `go`. Conversely, blocking operations should only be used outside `go` blocks.

The async and non-blocking forms are less common but may be used in either context. Use the async variants to specify a channel and a function that is called when the take or put succeeds. The `take!` and `put!` functions also take an optional flag `on-caller?` to indicate whether the fn can be called on the current thread. The non-blocking `offer!` and `poll!` will either complete or return immediately.

Channels are closed with [close!](https://clojure.github.io/core.async/clojure.core.async.html#var-close.21). When a channel is closed, no values may be added, but values already in the channel may be taken. When all values are drained from a closed channel, take operations will return `nil` (these are not valid values and serve as a marker).


* Closing channels:  [close!](https://clojure.github.io/core.async/clojure.core.async.html#var-close.21)
* Buffers: [buffer](https://clojure.github.io/core.async/clojure.core.async.html#var-buffer) [dropping-buffer](https://clojure.github.io/core.async/clojure.core.async.html#var-dropping-buffer) [sliding-buffer](https://clojure.github.io/core.async/clojure.core.async.html#var-sliding-buffer) [unblocking-buffer?](https://clojure.github.io/core.async/clojure.core.async.html#var-unblocking-buffer.3F)
* Blocking ops: [>!!](https://clojure.github.io/core.async/clojure.core.async.html#var-.3E.21.21) [<!!](https://clojure.github.io/core.async/clojure.core.async.html#var-.3C.21.21)
* Parking ops: [>!](https://clojure.github.io/core.async/clojure.core.async.html#var-.3E.21) [<!](https://clojure.github.io/core.async/clojure.core.async.html#var-.3C.21)
* Async ops: [put!](https://clojure.github.io/core.async/clojure.core.async.html#var-put.21) [take!](https://clojure.github.io/core.async/clojure.core.async.html#var-take.21)
* Non-blocking ops: [offer!](https://clojure.github.io/core.async/clojure.core.async.html#var-offer.21) [poll!](https://clojure.github.io/core.async/clojure.core.async.html#var-poll.21)

### alts

`alts!` (parking) and `alts!!` (blocking) can be used to wait on a set of channel operations until one succeeds. Channel operations can be either a put (with a value) or a take. By default, if more than one operation becomes available, they are chosen in random order, but set `:priority true` to order a preference. Only one of the operations will occur. If no operation is available and a `:default val` is specified, the default value will be returned instead.

Since it is common to combine an `alts` with a conditional return based on the action chosen, `alt!` (parking) and `alt!!` (blocking) combine an `alts!` select with destructuring of the channel and value and a result expression.


* Blocking: [alt!!](https://clojure.github.io/core.async/clojure.core.async.html#var-alt.21.21) [alts!!](https://clojure.github.io/core.async/clojure.core.async.html#var-alts.21.21)
* Parking: [alt!](https://clojure.github.io/core.async/clojure.core.async.html#var-alt.21) [alts!](https://clojure.github.io/core.async/clojure.core.async.html#var-alts.21)
* Timeouts: [timeout](https://clojure.github.io/core.async/clojure.core.async.html#var-timeout)

### Promise channels

Promise channels are special channels that will accept only a single value. Once a value is put to a promise channel, all pending and future consumers will receive only that value. Future puts complete but drop the value. When the channel is closed, consumers will receive either the value (if a put occurred) or nil (if no put occurred) forever.

Promise channels: [promise-chan](https://clojure.github.io/core.async/clojure.core.async.html#var-promise-chan)

## Managing processes

### go blocks and threads

"Processes", in the most general sense, are represented either as go blocks or threads. Go blocks model a lightweight computation that can be "parked" (paused) without consuming a thread. Go blocks communicate externally via channels. Any core.async parking operation (`>!`, `<!`, `alt!`, `alts!`) that cannot be immediately completed will cause the block to park and it will be automatically resumed when the operation can complete (when data has arrived on a channel to allow it).

Note that go blocks are multiplexed over a finite number of threads and should never be blocked, either by the use of a core.async blocking operation (like `<!!`) or by calling a blockable I/O operation like a network call. Doing so may effectively block all of the threads in the go block pool and prevent further progress.

core.async provides the helper functions `thread` and `thread-call` (analogous to `future` and `future-call`) to execute a process asynchronously in a separate thread. As these threads are not limited, they are suitable for blocking operations and can communicate with other processes via channels. However, note that these threads are not special - you can create and manage your own threads in any way you like and use core.async channels from those threads to communicate.


* Go blocks: [go](https://clojure.github.io/core.async/clojure.core.async.html#var-go) [go-loop](https://clojure.github.io/core.async/clojure.core.async.html#var-go-loop)
* Threads: [thread](https://clojure.github.io/core.async/clojure.core.async.html#var-thread)
[thread-call](https://clojure.github.io/core.async/clojure.core.async.html#var-thread-call)

### Multi-threaded pipelines

The `pipeline` function (and variants) are designed for modeling your work as a pipeline of multi-threaded processing stages. The stages are connected by channels and each stage has N threads performing transducer xf as values flow from the from channel to the to channel. The variants are:

* `pipeline` - the work performed in the xf must not block (designed for computational parallelism). The transducer will be applied independently to each value, in parallel, so stateful transducer functions will likely not be useful.
* `pipeline-blocking` - the work performed in the xf may block, for example on network operations.
* `pipeline-async` - this variant triggers asynchronous work in another system or thread and expects another thread to place the results on a return channel.


* Pipeline ops: [pipeline](https://clojure.github.io/core.async/clojure.core.async.html#var-pipeline) [pipeline-blocking](https://clojure.github.io/core.async/clojure.core.async.html#var-pipeline-blocking) [pipeline-async](https://clojure.github.io/core.async/clojure.core.async.html#var-pipeline-async)

## Working with channels

### Operations on channels


* Collections: [into](https://clojure.github.io/core.async/clojure.core.async.html#var-into) [onto-chan!](https://clojure.github.io/core.async/clojure.core.async.html#var-onto-chan.21) [onto-chan!!](https://clojure.github.io/core.async/clojure.core.async.html#var-onto-chan.21.21)  [to-chan](https://clojure.github.io/core.async/clojure.core.async.html#var-to-chan)
* Functions: [map](https://clojure.github.io/core.async/clojure.core.async.html#var-map) [take](https://clojure.github.io/core.async/clojure.core.async.html#var-take)
* Reducing: [reduce](https://clojure.github.io/core.async/clojure.core.async.html#var-reduce) [transduce](https://clojure.github.io/core.async/clojure.core.async.html#var-transduce)

### Channel connectors

* Connecting channels: [pipe](https://clojure.github.io/core.async/clojure.core.async.html#var-pipe)
* Merging channels: [merge](https://clojure.github.io/core.async/clojure.core.async.html#var-merge)
* Splitting channels: [split](https://clojure.github.io/core.async/clojure.core.async.html#var-split)

### Mults

* Mults: [mult](https://clojure.github.io/core.async/clojure.core.async.html#var-mult) [tap](https://clojure.github.io/core.async/clojure.core.async.html#var-tap) [untap](https://clojure.github.io/core.async/clojure.core.async.html#var-untap) [untap-all](https://clojure.github.io/core.async/clojure.core.async.html#var-untap-all)

### Pub/sub

* Pub/sub: [pub](https://clojure.github.io/core.async/clojure.core.async.html#var-pub) [sub](https://clojure.github.io/core.async/clojure.core.async.html#var-sub) [unsub](https://clojure.github.io/core.async/clojure.core.async.html#var-unsub) [unsub-all](https://clojure.github.io/core.async/clojure.core.async.html#var-unsub-all)

### Mixes

* Mixes: [mix](https://clojure.github.io/core.async/clojure.core.async.html#var-mix) [admix](https://clojure.github.io/core.async/clojure.core.async.html#var-admix) [toggle](https://clojure.github.io/core.async/clojure.core.async.html#var-toggle) [unmix](https://clojure.github.io/core.async/clojure.core.async.html#var-unmix) [unmix-all](https://clojure.github.io/core.async/clojure.core.async.html#var-unmix-all) [solo-mode](https://clojure.github.io/core.async/clojure.core.async.html#var-solo-mode)

## Configuration

### `go` checking

Because the core.async go block thread pool is fixed size, blocking IO operations should never be done in go blocks. If all go threads are blocked on blocking operations, you may experience either deadlock or lack of progress. 

One common issue is the use of core.async blocking operations inside go blocks. core.async includes a debugging facility to detect this situation (other kinds of blocking operation cannot be detected so this covers only part of the problem). To enable go checking, set the Java system property `clojure.core.async.go-checking=true`. This property is read once, at namespace load time, and should be used in development or testing, not in production.

When go checking is active, invalid blocking calls in a go block will throw in go block threads. By default, these will likely throw to the go block thread's uncaught exception handler and be printed, but you can use `Thread/setDefaultUncaughtExceptionHandler` to change the default behavior (or depending on your system, you may have one already that routes to logging).

## More information

See the following for more information:

* [Rationale](https://clojure.github.io/core.async/rationale.html)
* [API](https://clojure.github.io/core.async)
* [Source](https://github.com/clojure/core.async)
