# Walkthrough

## Getting started

The core.async library supports asynchronous programming through the use of channels.

To use core.async, declare a dependency on Clojure 1.10.0 or higher and the latest core.async library:

```clojure
{:deps
 {org.clojure/clojure {:mvn/version "1.12.0"}
  org.clojure/core.async {:mvn/version "1.8.741"}}}
```

To start working with core.async, require the `clojure.core.async` namespace at the REPL:

```clojure
(require '[clojure.core.async :as a :refer [<!! >!! <! >!]])
```

Or include it in your namespace:

```clojure
(ns my.ns
  (:require [clojure.core.async :as a :refer [<!! >!! <! >!]]))
```

## Channels

Values are conveyed on queue-like channels. By default channels are unbuffered (0-length) - they require producer and consumer to rendezvous for the transfer of a value through the channel.

Use `chan` to make an unbuffered channel:

```clojure
(a/chan)
```

Pass a number to create a channel with a fixed buffer size:

```clojure
(a/chan 10)
```

`close!` a channel to stop accepting puts. Remaining values are still available to take. Drained channels return nil on take. Nils may not be sent over a channel explicitly!

```clojure
(let [c (a/chan)]
  (a/close! c))
```

Channels can also use custom buffers that have different policies for the "full" case.  Two useful examples are provided in the API.

```clojure
;; Use `dropping-buffer` to drop newest values when the buffer is full:
(a/chan (a/dropping-buffer 10))

;; Use `sliding-buffer` to drop oldest values when the buffer is full:
(a/chan (a/sliding-buffer 10))
```

## Threads

In ordinary threads, we use `>!!` (blocking put) and `<!!` (blocking take) to communicate via channels.

```clojure
(let [c (a/chan 10)]
  (>!! c "hello")
  (assert (= "hello" (<!! c)))
  (a/close! c))
```

Because these are blocking calls, if we try to put on an unbuffered channel, we will block the main thread. We can use `thread` (like `future`) to execute a body in a pool thread and return a channel with the result. Here we launch a background task to put "hello" on a channel, then read that value in the current thread.

```clojure
(let [c (a/chan)]
  (a/thread (>!! c "hello"))
  (assert (= "hello" (<!! c)))
  (a/close! c))
```

## Go Blocks and IOC Threads

The `go` macro asynchronously executes its body in a special pool of threads. Channel operations that would block will pause execution instead, blocking no threads. This mechanism encapsulates the inversion of control that is external in event/callback systems. Inside `go` blocks, we use `>!` (put) and `<!` (take).

Here we convert our prior channel example to use go blocks:

```clojure
(let [c (a/chan)]
  (a/go (>! c "hello"))
  (assert (= "hello" (<!! (a/go (<! c)))))
  (a/close! c))
```

Instead of the explicit thread and blocking call, we use a go block for the producer. The consumer uses a go block to take, then returns a result channel, from which we do a blocking take.

== Alts

One killer feature for channels over queues is the ability to wait on many channels at the same time (like a socket select). This is done with `alts!!` (ordinary threads) or `alts!` in go blocks.

We can create a background thread with alts that combines inputs on either of two channels. `alts!!` takes a set of operations to perform - either a channel to take from or a channel value to put and returns the value (nil for put) and channel that succeeded:

```clojure
(let [c1 (a/chan)
      c2 (a/chan)]
  (a/thread (while true
              (let [[v ch] (a/alts!! [c1 c2])]
                (println "Read" v "from" ch))))
  (>!! c1 "hi")
  (>!! c2 "there"))
```

Prints (on stdout, possibly not visible at your repl):

```
Read hi from #object[clojure.core.async.impl.channels.ManyToManyChannel ...]
Read there from #object[clojure.core.async.impl.channels.ManyToManyChannel ...]
```

We can use alts! to do the same thing with go blocks:

```clojure
(let [c1 (a/chan)
      c2 (a/chan)]
  (a/go (while true
          (let [[v ch] (a/alts! [c1 c2])]
            (println "Read" v "from" ch))))
  (a/go (>! c1 "hi"))
  (a/go (>! c2 "there")))
```

Since go blocks are lightweight processes not bound to threads, we can have LOTS of them! Here we create 1000 go blocks that say hi on 1000 channels. We use alts!! to read them as they're ready.

```clojure
(let [n 1000
      cs (repeatedly n a/chan)
      begin (System/currentTimeMillis)]
  (doseq [c cs] (a/go (>! c "hi")))
  (dotimes [i n]
    (let [[v c] (a/alts!! cs)]
      (assert (= "hi" v))))
  (println "Read" n "msgs in" (- (System/currentTimeMillis) begin) "ms"))
```

`timeout` creates a channel that waits for a specified ms, then closes:

```clojure
(let [t (a/timeout 100)
      begin (System/currentTimeMillis)]
  (<!! t)
  (println "Waited" (- (System/currentTimeMillis) begin)))
```

We can combine timeout with `alts!` to do timed channel waits.  Here we wait for 100 ms for a value to arrive on the channel, then give up:

```clojure
(let [c (a/chan)
      begin (System/currentTimeMillis)]
  (a/alts!! [c (a/timeout 100)])
  (println "Gave up after" (- (System/currentTimeMillis) begin)))
```
