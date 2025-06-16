# core.async and virtual threads
## Rationale

Java 21 saw the inclusion of [virtual threads](https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html). Virtual threads were added to address the limitations of platform threads for handling highly-concurrent, I/O heavy workloads. While core.async provided mechanisms for mapping operations to platform threads, their limitations were absorbed by the library itself. That is, the granularity of workflow profiles that core.async could directly support were limited by those supported by platform threads and its internal IOC mechanisms. By enhancing core.async to use virtual threads for workloads in which they excel, we can open up new execution modes supported by the best-fit threading modes in the JVM.

## Workload types

Traditionally, core.async implicitly supported two workload types:

- Channel dispatch workloads implicit to the `go` block IOC completion handling and thunk processing, backed by a user-configurable bounded thread-pool
- User-specific workloads backed by platform threads created with the `clojure.core.async/thread` macro

As a first step toward integrating virtual threads, we changed the underlying workflow model within core.async to support a richer set of types, each described via related keywords:

- `:io` - used in the new `clojure.core.async/io-thread` macro for `:io` workloads in flow/process, and for dispatch handling if no explicit dispatch handler is provided (see below)
- `:mixed` - used by `clojure.core.async/thread` and for `:mixed` workloads in flow/process
- `:compute` - used for `:compute` workloads in flow/process
- `:core-async-dispatch` - used for completion fn handling (e.g. in `clojure.core.aync/put!` and `take!`, as well as go block IOC thunk processing) throughout core.async.

The defaults and mechanisms for customization around these workloads are described later.

## core.async's use of virtual threads

By default, whenever core.aync runs in an environment that supports virtual threads (i.e. a version 21+ JVM), it will use virtual threads to service all `:io` workloads executed with the `io-thread` macro. Additionally, all `go` IOC dispatching will use virtual threads under the hood.

### Specifying a factory for custom thread `ExecutorService` instances

However, to support a graceful upgrade path for core.async users, we've added mechanisms to customize if and how virtual threads are used. First, the Java system property `clojure.core.async.executor-factory` specifies a function that will provide `java.util.concurrent.ExecutorService`s for application-wide use by core.async in lieu of its defaults. The property value should name a fully qualified var. The function will be passed workflow type keywords `:io`, `:mixed`, `:compute`, or `:core-async-dispatch`, and should return either an ExecutorService, or nil to signal to core.async to use its default. Results per keyword will be cached and used for the remainder of the application.

### Targeting or avoiding virtual threads

Users can also set the Java system property `clojure.core.async.vthreads` to control how core.async uses JDK 21+ virtual threads. The property can be one of the following values:

- unset - Always default to IOC when AOT compiling, and use virtual threads for `io-thread` blocks if available at runtime
- `"target"` - Always target virtual threads when compiling `go` blocks and require them at runtime in `io-thread` blocks
- `"avoid"` - Always use IOC when compiling `go` blocks (will work regardless), and do not use virtual threads for `io-thread` blocks

There is one circumstance that needs special attention. That is, users can choose to AOT compile their applications/libraries and target virtual threads using the `"target"` flag. However, users may run that compiled code on a JVM without virtual threads support. By using "target" to compile code, you've fixed an expectation of that the runtime environment support virtual threads. When users run compiled code targeting virtual threads in a runtime environment without them then `go` blocks will not guarantee non-blocking semantics anymore. In this particular circumstance, core.async will throw an error when the compiled `"target"` expectation does not match the runtime capability.
