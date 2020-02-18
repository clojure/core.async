# core.async

A Clojure library providing facilities for async programming and communication.


## Releases and Dependency Information

Latest release: 1.0.567

* [All Released Versions](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22org.clojure%22%20AND%20a%3A%22core.async%22)

[deps.edn](https://clojure.org/reference/deps_and_cli) dependency information:

```clj
  org.clojure/core.async {:mvn/version "1.0.567"}
 ```

[Leiningen](https://github.com/technomancy/leiningen) dependency information:

```clj
 [org.clojure/core.async "1.0.567"]
```

[Maven](http://maven.apache.org/) dependency information:

```xml
<dependency>
  <groupId>org.clojure</groupId>
  <artifactId>core.async</artifactId>
  <version>1.0.567</version>
</dependency>
```

## Documentation

* [Rationale](https://clojure.org/news/2013/06/28/clojure-clore-async-channels)
* [API docs](https://clojure.github.io/core.async/)
* [Code walkthrough](https://github.com/clojure/core.async/blob/master/examples/walkthrough.clj)

## Presentations

* [Rich Hickey on core.async](http://www.infoq.com/presentations/clojure-core-async)
* [Tim Baldridge on core.async](http://www.youtube.com/watch?v=enwIIGzhahw) from Clojure/conj 2013 ([code](https://github.com/halgari/clojure-conj-2013-core.async-examples)).
* Tim Baldridge on go macro internals - [part 1](https://www.youtube.com/watch?v=R3PZMIwXN_g) [part 2](https://www.youtube.com/watch?v=SI7qtuuahhU)

## Contributing 

[Contributing to Clojure projects](https://clojure.org/community/contributing) requires a signed Contributor Agreement. Pull requests and GitHub issues are not accepted; please use the [core.async JIRA project](https://clojure.atlassian.net/browse/ASYNC) to report problems or enhancements.

To run the ClojureScript tests:

* lein cljsbuild once
* open script/runtests.html
* View JavaScript console for test results

## License

Copyright © 2017-2020 Rich Hickey and contributors

Distributed under the Eclipse Public License, the same as Clojure.

## Changelog

* Release 1.0.567 on 2020.02.18
* Release 0.7.559 on 2020.01.10
  * [ASYNC-198](https://clojure.atlassian.net/browse/ASYNC-198) (CLJ) Fix exception rewriting in go can replace return value
  * [ASYNC-220](https://clojure.atlassian.net/browse/ASYNC-220) (CLJ) Fix exception in go finally swallows exception of outer try
  * [ASYNC-229](https://clojure.atlassian.net/browse/ASYNC-229) (CLJ) Fix go finally block executed twice
  * [ASYNC-212](https://clojure.atlassian.net/browse/ASYNC-212) (CLJ) Fix go fails to compile expressions with literal nil as let value
  * [ASYNC-145](https://clojure.atlassian.net/browse/ASYNC-145) (CLJ, CLJS) Fix mix throws error when many channels added
  * [ASYNC-170](https://clojure.atlassian.net/browse/ASYNC-170) (CLJ) Fix binding in go block throws assertion error
  * [ASYNC-127](https://clojure.atlassian.net/browse/ASYNC-127) (CLJ, CLJS) Fix mult to work as doc'ed with all taps accepting before next
  * [ASYNC-210](https://clojure.atlassian.net/browse/ASYNC-210) (CLJ) Fix puts allowed when buffer still full from expanding transducer
* Release 0.6.532 on 2019.12.02
  * Bump tools.analyzer.jvm dep to 0.7.3
* Release 0.5.527 on 2019.11.12
  * Add system property clojure.core.async.go-checking that will throw if core.async blocking ops (>!!, <!!, alts!!, alt!!) are used in a go block
  * Fix use of blocking op and thread constraints in `pipeline` - will now match `pipeline-blocking` in using N cached threads.
* Release 0.4.500 on 2019.06.11
  * [ASYNC-227](https://clojure.atlassian.net/browse/ASYNC-227) cljs alts! isn't non-deterministic
  * [ASYNC-224](https://clojure.atlassian.net/browse/ASYNC-224) Fix bad putter unwrapping in channel abort
  * [ASYNC-226](https://clojure.atlassian.net/browse/ASYNC-226) Fix bad cljs test code
* Release 0.4.490 on 2018.11.19
  * [ASYNC-216](https://clojure.atlassian.net/browse/ASYNC-216) Delay start of timeout thread
  * [ASYNC-218](https://clojure.atlassian.net/browse/ASYNC-218) Fix docstring for put!
  * [ASYNC-213](https://clojure.atlassian.net/browse/ASYNC-213) Small addition to promise-chan docstring
* Release 0.4.474 on 2018.01.08
  * Fix typo in error message
  * Remove Java code, depend on Java 1.7+
  * Add deps.edn, can now be used as a git dependency
* Release 0.3.465 on 2017.11.17
  * [ASYNC-119](https://clojure.atlassian.net/browse/ASYNC-119) Move macros to cljs.core.async ns (CLJS)
  * [ASYNC-201](https://clojure.atlassian.net/browse/ASYNC-201) Out-of-bounds index values passed in timers (CLJS)
* Release 0.3.443 on 2017.05.26
  * [ASYNC-159](https://clojure.atlassian.net/browse/ASYNC-159) - promise-chan in ClojureScript is broken 
* Release 0.3.442 on 2017.03.14
  * Fix bad `:refer-clojure` clause that violates new spec in Clojure 1.9.0-alpha15
* Release 0.3.441 on 2017.02.23
  * [ASYNC-187](https://clojure.atlassian.net/browse/ASYNC-187) - Tag metadata is lost in local closed over by a loop
    * Related: [ASYNC-188](https://clojure.atlassian.net/browse/ASYNC-188)
  * [ASYNC-185](https://clojure.atlassian.net/browse/ASYNC-185) - `thread` prevents clearing of body locals
  * [ASYNC-186](https://clojure.atlassian.net/browse/ASYNC-186) - NPE when `go` closes over a local variable bound to nil
* Release 0.3.426 on 2017.02.22
  * [ASYNC-169](https://clojure.atlassian.net/browse/ASYNC-169) - handling of catch and finally inside go blocks was broken, causing a number of issues. Related: [ASYNC-100](https://clojure.atlassian.net/browse/ASYNC-100), [ASYNC-173](https://clojure.atlassian.net/browse/ASYNC-173), [ASYNC-180](https://clojure.atlassian.net/browse/ASYNC-180), [ASYNC-179](https://clojure.atlassian.net/browse/ASYNC-179), [ASYNC-122](https://clojure.atlassian.net/browse/ASYNC-122), [ASYNC-78](https://clojure.atlassian.net/browse/ASYNC-78), [ASYNC-168](https://clojure.atlassian.net/browse/ASYNC-168)
  * [ASYNC-138](https://clojure.atlassian.net/browse/ASYNC-138) - go blocks do not allow closed over locals to be cleared which can lead to a memory leak. Related: [ASYNC-32](https://clojure.atlassian.net/browse/ASYNC-32)
  * [ASYNC-155](https://clojure.atlassian.net/browse/ASYNC-155) - preserve loop binding metadata when inside a go block
  * [ASYNC-54](https://clojure.atlassian.net/browse/ASYNC-54) - fix bad type hint on MAX-QUEUE-SIZE
  * [ASYNC-177](https://clojure.atlassian.net/browse/ASYNC-177) - fix typo in Buffer protocol full? method
  * [ASYNC-70](https://clojure.atlassian.net/browse/ASYNC-70) - docstring change in thread, thread-call
  * [ASYNC-143](https://clojure.atlassian.net/browse/ASYNC-143) - assert that fixed buffers must have size > 0
  * Update tools.analyzer.jvm dependency
* Release 0.2.395 on 2016.10.12
  * Add async version of transduce
* Release 0.2.391 on 2016.09.09
  * Fix redefinition warning for bounded-count (added in Clojure 1.9)
  * Add :deprecated meta to the deprecated functions
* Release 0.2.385 on 2016.06.17
  * Updated tools.analyzer.jvm version
* Release 0.2.382 on 2016.06.13
  * Important: Change default dispatch thread pool size to 8.
  * Add Java system property `clojure.core.async.pool-size` to set the dispatch thread pool size
  * [ASYNC-152](https://clojure.atlassian.net/browse/ASYNC-152) - disable t.a.jvm's warn-on-reflection pass 
* Release 0.2.374 on 2015.11.11
  * [ASYNC-149](https://clojure.atlassian.net/browse/ASYNC-149) - fix error compiling recur inside case in a go block
  * Updated tools.analyzer.jvm version (and other upstream deps)
  * Updated to latest clojurescript and cljsbuild versions
* Release 0.2.371 on 2015.10.28
  * [ASYNC-124](https://clojure.atlassian.net/browse/ASYNC-124) - dispatch multiple pending takers from expanding transducer
  * [ASYNC-103](https://clojure.atlassian.net/browse/ASYNC-103) - NEW promise-chan
  * [ASYNC-104](https://clojure.atlassian.net/browse/ASYNC-104) - NEW non-blocking offer!, poll!
  * [ASYNC-101](https://clojure.atlassian.net/browse/ASYNC-101) - async/reduce now respects reduced
  * [ASYNC-112](https://clojure.atlassian.net/browse/ASYNC-112) - replace "transformer" with "transducer" in deprecation messages
  * [ASYNC-6](https://clojure.atlassian.net/browse/ASYNC-6) - alts! docs updated to explicitly state ports is a vector
  * Support (try (catch :default)) in CLJS exception handling
  * Use cljs.test
  * Updated tools.analyzer.jvm version (and other upstream deps)
* Release 0.1.346.0-17112a-alpha on 2014.09.22
  * cljs nextTick relies on goog.async.nextTick
  * Updated docstring for put! re result on closed channel
* Release 0.1.338.0-5c5012-alpha on 2014.08.19
  * Add cljs transducers support
* Release 0.1.319.0-6b1aca-alpha on 2014.08.06
  * Add transducers support
  * NEW pipeline
* Release 0.1.303.0-886421-alpha on 2014.05.08
* Release 0.1.301.0-deb34a-alpha on 2014.04.29
* Release 0.1.298.0-2a82a1-alpha on 2014.04.25
* Release 0.1.278.0-76b25b-alpha on 2014.02.07
* Release 0.1.267.0-0d7780-alpha on 2013.12.11
* Release 0.1.262.0-151b23-alpha on 2013.12.10
* Release 0.1.256.0-1bf8cf-alpha on 2013.11.07
* Release 0.1.242.0-44b1e3-alpha on 2013.09.27
* Release 0.1.222.0-83d0c2-alpha on 2013.09.12
