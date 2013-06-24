{:namespaces
 ({:source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async/clojure.core.async-api.html",
   :name "clojure.core.async",
   :doc nil}
  {:source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/buffers.clj",
   :wiki-url
   "http://clojure.github.com/core.async/clojure.core.async.impl.buffers-api.html",
   :name "clojure.core.async.impl.buffers",
   :doc nil}
  {:source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/channels.clj",
   :wiki-url
   "http://clojure.github.com/core.async/clojure.core.async.impl.channels-api.html",
   :name "clojure.core.async.impl.channels",
   :doc nil}
  {:source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/concurrent.clj",
   :wiki-url
   "http://clojure.github.com/core.async/clojure.core.async.impl.concurrent-api.html",
   :name "clojure.core.async.impl.concurrent",
   :doc nil}
  {:source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/dispatch.clj",
   :wiki-url
   "http://clojure.github.com/core.async/clojure.core.async.impl.dispatch-api.html",
   :name "clojure.core.async.impl.dispatch",
   :doc nil}
  {:source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_alt.clj",
   :wiki-url
   "http://clojure.github.com/core.async/clojure.core.async.impl.ioc-alt-api.html",
   :name "clojure.core.async.impl.ioc-alt",
   :doc nil}
  {:source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async/clojure.core.async.impl.ioc-macros-api.html",
   :name "clojure.core.async.impl.ioc-macros",
   :doc nil}
  {:source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/protocols.clj",
   :wiki-url
   "http://clojure.github.com/core.async/clojure.core.async.impl.protocols-api.html",
   :name "clojure.core.async.impl.protocols",
   :doc nil}
  {:source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/timers.clj",
   :wiki-url
   "http://clojure.github.com/core.async/clojure.core.async.impl.timers-api.html",
   :name "clojure.core.async.impl.timers",
   :doc nil}
  {:source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/lab.clj",
   :wiki-url
   "http://clojure.github.com/core.async/clojure.core.async.lab-api.html",
   :name "clojure.core.async.lab",
   :doc
   "core.async HIGHLY EXPERIMENTAL feature exploration\n\nCaveats:\n\n1. Everything defined in this namespace is experimental, and subject\nto change or deletion without warning.\n\n2. Many features provided by this namespace are highly coupled to\nimplementation details of core.async. Potential features which\noperate at higher levels of abstraction are suitable for inclusion\nin the examples.\n\n3. Features provided by this namespace MAY be promoted to\nclojure.core.async at a later point in time, but there is no\nguarantee any of them will."}),
 :vars
 ({:arglists ([port]),
   :name "<!",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj#L77",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/<!",
   :doc
   "takes a val from port. Must be called inside a (go ...) block. Will\nreturn nil if closed. Will park if nothing is available.",
   :var-type "function",
   :line 77,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([port]),
   :name "<!!",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj#L67",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/<!!",
   :doc
   "takes a val from port. Will return nil if closed. Will block\nif nothing is available.",
   :var-type "function",
   :line 67,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([port val]),
   :name ">!",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj#L108",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/>!",
   :doc
   "puts a val into port. nil values are not allowed. Must be called\ninside a (go ...) block. Will park if no buffer space is available.",
   :var-type "function",
   :line 108,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([port val]),
   :name ">!!",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj#L98",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/>!!",
   :doc
   "puts a val into port. nil values are not allowed. Will block if no\nbuffer space is available. Returns nil.",
   :var-type "function",
   :line 98,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([& clauses]),
   :name "alt!",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj#L291",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/alt!",
   :doc
   "Makes a single choice between one of several channel operations,\nas if by alts!, returning the value of the result expr corresponding\nto the operation completed. Must be called inside a (go ...) block.\n\nEach clause takes the form of:\n\nchannel-op[s] result-expr\n\nwhere channel-ops is one of:\n\ntake-port - a single port to take\n[take-port | [put-port put-val] ...] - a vector of ports as per alts!\n:default | :priority - an option for alts!\n\nand result-expr is either a list beginning with a vector, whereupon that\nvector will be treated as a binding for the [val port] return of the\noperation, else any other expression.\n\n(alt!\n  [c t] ([val ch] (foo ch val))\n  x ([v] v)\n  [[out val]] :wrote\n  :default 42)\n\nEach option may appear at most once. The choice and parking\ncharacteristics are those of alts!.",
   :var-type "macro",
   :line 291,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([& clauses]),
   :name "alt!!",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj#L284",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/alt!!",
   :doc
   "Like alt!, except as if by alts!!, will block until completed, and\nnot intended for use in (go ...) blocks.",
   :var-type "macro",
   :line 284,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([ports & {:as opts}]),
   :name "alts!",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj#L221",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/alts!",
   :doc
   "Completes at most one of several channel operations. Must be called\ninside a (go ...) block. ports is a set of channel endpoints, which\ncan be either a channel to take from or a vector of\n[channel-to-put-to val-to-put], in any combination. Takes will be\nmade as if by <!, and puts will be made as if by >!. Unless\nthe :priority option is true, if more than one port operation is\nready a non-deterministic choice will be made. If no operation is\nready and a :default value is supplied, [default-val :default] will\nbe returned, otherwise alts! will park until the first operation to\nbecome ready completes. Returns [val port] of the completed\noperation, where val is the value taken for takes, and nil for puts.\n\nopts are passed as :key val ... Supported options:\n\n:default val - the value to use if none of the operations are immediately ready\n:priority true - (default nil) when true, the operations will be tried in order.\n\nNote: there is no guarantee that the port exps or val exprs will be\nused, nor in what order should they be, so they should not be\ndepended upon for side effects.",
   :var-type "function",
   :line 221,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([ports & {:as opts}]),
   :name "alts!!",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj#L210",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/alts!!",
   :doc
   "Like alts!, except takes will be made as if by <!!, and puts will\nbe made as if by >!!, will block until completed, and not intended\nfor use in (go ...) blocks.",
   :var-type "function",
   :line 210,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([n]),
   :name "buffer",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj#L38",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/buffer",
   :doc
   "Returns a fixed buffer of size n. When full, puts will block/park.",
   :var-type "function",
   :line 38,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([] [buf-or-n]),
   :name "chan",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj#L56",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/chan",
   :doc
   "Creates a channel with an optional buffer. If buf-or-n is a number,\nwill create and use a fixed buffer of that size.",
   :var-type "function",
   :line 56,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([chan]),
   :name "close!",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj#L128",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/close!",
   :doc
   "Closes a channel. The channel will no longer accept any puts (they\nwill be ignored). Data in the channel remains available for taking, until\nexhausted, after which takes will return nil. If there are any\npending takes, they will be dispatched with nil. Closing a closed\nchannel is a no-op. Returns nil.",
   :var-type "function",
   :line 128,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([fret ports opts]),
   :name "do-alts",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj#L181",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/do-alts",
   :doc "returns derefable [val port] if immediate, nil if enqueued",
   :var-type "function",
   :line 181,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([n]),
   :name "dropping-buffer",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj#L43",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/dropping-buffer",
   :doc
   "Returns a buffer of size n. When full, puts will complete but\nval will be dropped (no transfer).",
   :var-type "function",
   :line 43,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([& body]),
   :name "go",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj#L322",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/go",
   :doc
   "Asynchronously executes the body, returning immediately to the\ncalling thread. Additionally, any visible calls to <!, >! and alt!/alts!\nchannel operations within the body will block (if necessary) by\n'parking' the calling thread rather than tying up an OS thread (or\nthe only JS thread when in ClojureScript). Upon completion of the\noperation, the body will be resumed.\n\nReturns a channel which will receive the result of the body when\ncompleted",
   :var-type "macro",
   :line 322,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([port val fn0] [port val fn0 on-caller?]),
   :name "put!",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj#L114",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/put!",
   :doc
   "Asynchronously puts a val into port, calling fn0 when complete. nil\nvalues are not allowed. Will throw if closed. If\non-caller? (default true) is true, and the put is immediately\naccepted, will call fn0 on calling thread.  Returns nil.",
   :var-type "function",
   :line 114,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([n]),
   :name "sliding-buffer",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj#L49",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/sliding-buffer",
   :doc
   "Returns a buffer of size n. When full, puts will complete, and be\nbuffered, but oldest elements in buffer will be dropped (not\ntransferred).",
   :var-type "function",
   :line 49,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([port fn1] [port fn1 on-caller?]),
   :name "take!",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj#L83",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/take!",
   :doc
   "Asynchronously takes a val from port, passing to fn1. Will pass nil\nif closed. If on-caller? (default true) is true, and value is\nimmediately available, will call fn1 on calling thread.\nReturns nil.",
   :var-type "function",
   :line 83,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([& body]),
   :name "thread",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj#L367",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/thread",
   :doc
   "Executes the body in another thread, returning immediately to the\ncalling thread. Returns a channel which will receive the result of\nthe body when completed.",
   :var-type "macro",
   :line 367,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([f]),
   :name "thread-call",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj#L351",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/thread-call",
   :doc
   "Executes f in another thread, returning immediately to the calling\nthread. Returns a channel which will receive the result of calling\nf when completed.",
   :var-type "function",
   :line 351,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([msecs]),
   :name "timeout",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj#L62",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/timeout",
   :doc "Returns a channel that will close after msecs",
   :var-type "function",
   :line 62,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([buf n]),
   :name "->DroppingBuffer",
   :namespace "clojure.core.async.impl.buffers",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/buffers.clj#L32",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/buffers.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.buffers/->DroppingBuffer",
   :doc
   "Positional factory function for class clojure.core.async.impl.buffers.DroppingBuffer.",
   :var-type "function",
   :line 32,
   :file "src/main/clojure/clojure/core/async/impl/buffers.clj"}
  {:arglists ([buf n]),
   :name "->FixedBuffer",
   :namespace "clojure.core.async.impl.buffers",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/buffers.clj#L15",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/buffers.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.buffers/->FixedBuffer",
   :doc
   "Positional factory function for class clojure.core.async.impl.buffers.FixedBuffer.",
   :var-type "function",
   :line 15,
   :file "src/main/clojure/clojure/core/async/impl/buffers.clj"}
  {:arglists ([buf n]),
   :name "->SlidingBuffer",
   :namespace "clojure.core.async.impl.buffers",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/buffers.clj#L48",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/buffers.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.buffers/->SlidingBuffer",
   :doc
   "Positional factory function for class clojure.core.async.impl.buffers.SlidingBuffer.",
   :var-type "function",
   :line 48,
   :file "src/main/clojure/clojure/core/async/impl/buffers.clj"}
  {:file nil,
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.buffers/DroppingBuffer",
   :namespace "clojure.core.async.impl.buffers",
   :var-type "type",
   :name "DroppingBuffer"}
  {:file nil,
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.buffers/FixedBuffer",
   :namespace "clojure.core.async.impl.buffers",
   :var-type "type",
   :name "FixedBuffer"}
  {:file nil,
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.buffers/SlidingBuffer",
   :namespace "clojure.core.async.impl.buffers",
   :var-type "type",
   :name "SlidingBuffer"}
  {:arglists ([takes puts buf closed mutex]),
   :name "->ManyToManyChannel",
   :namespace "clojure.core.async.impl.channels",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/channels.clj#L25",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/channels.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.channels/->ManyToManyChannel",
   :doc
   "Positional factory function for class clojure.core.async.impl.channels.ManyToManyChannel.",
   :var-type "function",
   :line 25,
   :file "src/main/clojure/clojure/core/async/impl/channels.clj"}
  {:file nil,
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.channels/ManyToManyChannel",
   :namespace "clojure.core.async.impl.channels",
   :var-type "type",
   :name "ManyToManyChannel"}
  {:arglists ([name-format daemon]),
   :name "counted-thread-factory",
   :namespace "clojure.core.async.impl.concurrent",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/concurrent.clj#L14",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/concurrent.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.concurrent/counted-thread-factory",
   :doc
   "Create a ThreadFactory that maintains a counter for naming Threads.\nname-format specifies thread names - use %d to include counter\ndaemon is a flag for whether threads are daemons or not",
   :var-type "function",
   :line 14,
   :file "src/main/clojure/clojure/core/async/impl/concurrent.clj"}
  {:file "src/main/clojure/clojure/core/async/impl/concurrent.clj",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/concurrent.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/concurrent.clj#L27",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.concurrent/processors",
   :namespace "clojure.core.async.impl.concurrent",
   :line 27,
   :var-type "var",
   :doc "Number of processors reported by the JVM",
   :name "processors"}
  {:arglists ([fn0]),
   :name "run",
   :namespace "clojure.core.async.impl.dispatch",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/dispatch.clj#L21",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/dispatch.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.dispatch/run",
   :doc "Runs fn0 in a thread pool thread",
   :var-type "function",
   :line 21,
   :file "src/main/clojure/clojure/core/async/impl/dispatch.clj"}
  {:arglists ([ids cont-block]),
   :name "->Park",
   :namespace "clojure.core.async.impl.ioc-alt",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_alt.clj#L6",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_alt.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-alt/->Park",
   :doc
   "Positional factory function for class clojure.core.async.impl.ioc_alt.Park.",
   :var-type "function",
   :line 6,
   :file "src/main/clojure/clojure/core/async/impl/ioc_alt.clj"}
  {:arglists ([m__5818__auto__]),
   :name "map->Park",
   :namespace "clojure.core.async.impl.ioc-alt",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_alt.clj#L6",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_alt.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-alt/map->Park",
   :doc
   "Factory function for class clojure.core.async.impl.ioc_alt.Park, taking a map of keywords to field values.",
   :var-type "function",
   :line 6,
   :file "src/main/clojure/clojure/core/async/impl/ioc_alt.clj"}
  {:file nil,
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-alt/Park",
   :namespace "clojure.core.async.impl.ioc-alt",
   :var-type "record",
   :name "Park"}
  {:arglists ([refs]),
   :name "->Call",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L227",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/->Call",
   :doc
   "Positional factory function for class clojure.core.async.impl.ioc_macros.Call.",
   :var-type "function",
   :line 227,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([val-id test-vals jmp-blocks default-block]),
   :name "->Case",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L235",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/->Case",
   :doc
   "Positional factory function for class clojure.core.async.impl.ioc_macros.Case.",
   :var-type "function",
   :line 235,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([test then-block else-block]),
   :name "->CondBr",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L312",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/->CondBr",
   :doc
   "Positional factory function for class clojure.core.async.impl.ioc_macros.CondBr.",
   :var-type "function",
   :line 312,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([value]),
   :name "->Const",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L209",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/->Const",
   :doc
   "Positional factory function for class clojure.core.async.impl.ioc_macros.Const.",
   :var-type "function",
   :line 209,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([fn-expr local-names local-refs]),
   :name "->Fn",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L250",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/->Fn",
   :doc
   "Positional factory function for class clojure.core.async.impl.ioc_macros.Fn.",
   :var-type "function",
   :line 250,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([value block]),
   :name "->Jmp",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L260",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/->Jmp",
   :doc
   "Positional factory function for class clojure.core.async.impl.ioc_macros.Jmp.",
   :var-type "function",
   :line 260,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([value block]),
   :name "->Pause",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L301",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/->Pause",
   :doc
   "Positional factory function for class clojure.core.async.impl.ioc_macros.Pause.",
   :var-type "function",
   :line 301,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([channel value block]),
   :name "->Put!",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L279",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/->Put!",
   :doc
   "Positional factory function for class clojure.core.async.impl.ioc_macros.Put!.",
   :var-type "function",
   :line 279,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([value]),
   :name "->Return",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L268",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/->Return",
   :doc
   "Positional factory function for class clojure.core.async.impl.ioc_macros.Return.",
   :var-type "function",
   :line 268,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([set-id value]),
   :name "->Set",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L219",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/->Set",
   :doc
   "Positional factory function for class clojure.core.async.impl.ioc_macros.Set.",
   :var-type "function",
   :line 219,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([channel block]),
   :name "->Take!",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L290",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/->Take!",
   :doc
   "Positional factory function for class clojure.core.async.impl.ioc_macros.Take!.",
   :var-type "function",
   :line 290,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([]),
   :name "add-block",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L166",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/add-block",
   :doc
   "Adds a new block, returns its id, but does not change the current block (does not call set-block).",
   :var-type "function",
   :line 166,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([inst]),
   :name "add-instruction",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L184",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/add-instruction",
   :doc "Appends an instruction to the current block. ",
   :var-type "function",
   :line 184,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([itms]),
   :name "all",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L119",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/all",
   :doc
   "Assumes that itms is a list of state monad function results, threads the state map\nthrough all of them. Returns a vector of all the results.",
   :var-type "function",
   :line 119,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([path val]),
   :name "assoc-in-plan",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L131",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/assoc-in-plan",
   :doc "Same as assoc-in, but for state hash map",
   :var-type "function",
   :line 131,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([state]),
   :name "async-chan-wrapper",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L791",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/async-chan-wrapper",
   :doc
   "State machine wrapper that uses the async library. Has to be in this file do to dependency issues. ",
   :var-type "function",
   :line 791,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([state-array]),
   :name "finished?",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L774",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/finished?",
   :doc "Returns true if the machine is in a finished state",
   :var-type "function",
   :line 774,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([binds id-expr]),
   :name "gen-plan",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L62",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/gen-plan",
   :doc
   "Allows a user to define a state monad binding plan.\n\n(gen-plan\n  [_ (assoc-in-plan [:foo :bar] 42)\n   val (get-in-plan [:foo :bar])]\n  val)",
   :var-type "macro",
   :line 62,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([key]),
   :name "get-binding",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L100",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/get-binding",
   :doc "Gets the value of the current binding for key",
   :var-type "function",
   :line 100,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([]),
   :name "get-block",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L160",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/get-block",
   :doc "Gets the current block",
   :var-type "function",
   :line 160,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([path]),
   :name "get-in-plan",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L143",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/get-in-plan",
   :doc "Same as get-in, but for a state hash map",
   :var-type "function",
   :line 143,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([f]),
   :name "get-plan",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L81",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/get-plan",
   :doc "Returns the final [id state] from a plan. ",
   :var-type "function",
   :line 81,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([m__5818__auto__]),
   :name "map->Call",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L227",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/map->Call",
   :doc
   "Factory function for class clojure.core.async.impl.ioc_macros.Call, taking a map of keywords to field values.",
   :var-type "function",
   :line 227,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([m__5818__auto__]),
   :name "map->Case",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L235",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/map->Case",
   :doc
   "Factory function for class clojure.core.async.impl.ioc_macros.Case, taking a map of keywords to field values.",
   :var-type "function",
   :line 235,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([m__5818__auto__]),
   :name "map->CondBr",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L312",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/map->CondBr",
   :doc
   "Factory function for class clojure.core.async.impl.ioc_macros.CondBr, taking a map of keywords to field values.",
   :var-type "function",
   :line 312,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([m__5818__auto__]),
   :name "map->Const",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L209",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/map->Const",
   :doc
   "Factory function for class clojure.core.async.impl.ioc_macros.Const, taking a map of keywords to field values.",
   :var-type "function",
   :line 209,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([m__5818__auto__]),
   :name "map->Fn",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L250",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/map->Fn",
   :doc
   "Factory function for class clojure.core.async.impl.ioc_macros.Fn, taking a map of keywords to field values.",
   :var-type "function",
   :line 250,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([m__5818__auto__]),
   :name "map->Jmp",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L260",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/map->Jmp",
   :doc
   "Factory function for class clojure.core.async.impl.ioc_macros.Jmp, taking a map of keywords to field values.",
   :var-type "function",
   :line 260,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([m__5818__auto__]),
   :name "map->Pause",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L301",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/map->Pause",
   :doc
   "Factory function for class clojure.core.async.impl.ioc_macros.Pause, taking a map of keywords to field values.",
   :var-type "function",
   :line 301,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([m__5818__auto__]),
   :name "map->Put!",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L279",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/map->Put!",
   :doc
   "Factory function for class clojure.core.async.impl.ioc_macros.Put!, taking a map of keywords to field values.",
   :var-type "function",
   :line 279,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([m__5818__auto__]),
   :name "map->Return",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L268",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/map->Return",
   :doc
   "Factory function for class clojure.core.async.impl.ioc_macros.Return, taking a map of keywords to field values.",
   :var-type "function",
   :line 268,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([m__5818__auto__]),
   :name "map->Set",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L219",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/map->Set",
   :doc
   "Factory function for class clojure.core.async.impl.ioc_macros.Set, taking a map of keywords to field values.",
   :var-type "function",
   :line 219,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([m__5818__auto__]),
   :name "map->Take!",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L290",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/map->Take!",
   :doc
   "Factory function for class clojure.core.async.impl.ioc_macros.Take!, taking a map of keywords to field values.",
   :var-type "function",
   :line 290,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([]),
   :name "no-op",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L113",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/no-op",
   :doc
   "This function can be used inside a gen-plan when no operation is to be performed",
   :var-type "function",
   :line 113,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([body]),
   :name "parse-to-state-machine",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L648",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/parse-to-state-machine",
   :doc
   "Takes an sexpr and returns a hashmap that describes the execution flow of the sexpr as\na series of SSA style blocks.",
   :var-type "function",
   :line 648,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([index value]),
   :name "persistent-value?",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L688",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/persistent-value?",
   :doc
   "Returns true if this value should be saved in the state hash map",
   :var-type "function",
   :line 688,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([key]),
   :name "pop-binding",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L106",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/pop-binding",
   :doc "Removes the most recent binding for key",
   :var-type "function",
   :line 106,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([key f & args]),
   :name "push-alter-binding",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L93",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/push-alter-binding",
   :doc
   "Pushes the result of (apply f old-value args) as current value of binding key",
   :var-type "function",
   :line 93,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([key value]),
   :name "push-binding",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L86",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/push-binding",
   :doc
   "Sets the binding 'key' to value. This operation can be undone via pop-bindings.\nBindings are stored in the state hashmap.",
   :var-type "function",
   :line 86,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([block-id]),
   :name "set-block",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L154",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/set-block",
   :doc
   "Sets the current block being written to by the functions. The next add-instruction call will append to this block",
   :var-type "function",
   :line 154,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:arglists ([path f & args]),
   :name "update-in-plan",
   :namespace "clojure.core.async.impl.ioc-macros",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L137",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/update-in-plan",
   :doc "Same as update-in, but for a state hash map",
   :var-type "function",
   :line 137,
   :file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj"}
  {:file nil,
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/Call",
   :namespace "clojure.core.async.impl.ioc-macros",
   :var-type "record",
   :name "Call"}
  {:file nil,
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/Case",
   :namespace "clojure.core.async.impl.ioc-macros",
   :var-type "record",
   :name "Case"}
  {:file nil,
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/CondBr",
   :namespace "clojure.core.async.impl.ioc-macros",
   :var-type "record",
   :name "CondBr"}
  {:file nil,
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/Const",
   :namespace "clojure.core.async.impl.ioc-macros",
   :var-type "record",
   :name "Const"}
  {:file nil,
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/Fn",
   :namespace "clojure.core.async.impl.ioc-macros",
   :var-type "record",
   :name "Fn"}
  {:file nil,
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/Jmp",
   :namespace "clojure.core.async.impl.ioc-macros",
   :var-type "record",
   :name "Jmp"}
  {:file nil,
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/Pause",
   :namespace "clojure.core.async.impl.ioc-macros",
   :var-type "record",
   :name "Pause"}
  {:file nil,
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/Put!",
   :namespace "clojure.core.async.impl.ioc-macros",
   :var-type "record",
   :name "Put!"}
  {:file nil,
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/Return",
   :namespace "clojure.core.async.impl.ioc-macros",
   :var-type "record",
   :name "Return"}
  {:file nil,
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/Set",
   :namespace "clojure.core.async.impl.ioc-macros",
   :var-type "record",
   :name "Set"}
  {:file nil,
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/Take!",
   :namespace "clojure.core.async.impl.ioc-macros",
   :var-type "record",
   :name "Take!"}
  {:file "src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/ioc_macros.clj#L203",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/IInstruction",
   :namespace "clojure.core.async.impl.ioc-macros",
   :line 203,
   :var-type "protocol",
   :doc nil,
   :name "IInstruction"}
  {:file nil,
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/block-references",
   :namespace "clojure.core.async.impl.ioc-macros",
   :var-type "function",
   :arglists ([this]),
   :doc "Returns all the blocks this instruction references",
   :name "block-references"}
  {:file nil,
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/emit-instruction",
   :namespace "clojure.core.async.impl.ioc-macros",
   :var-type "function",
   :arglists ([this state-sym]),
   :doc "Returns the clojure code that this instruction represents",
   :name "emit-instruction"}
  {:file nil,
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/reads-from",
   :namespace "clojure.core.async.impl.ioc-macros",
   :var-type "function",
   :arglists ([this]),
   :doc "Returns a list of instructions this instruction reads from",
   :name "reads-from"}
  {:file nil,
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.ioc-macros/writes-to",
   :namespace "clojure.core.async.impl.ioc-macros",
   :var-type "function",
   :arglists ([this]),
   :doc "Returns a list of instructions this instruction writes to",
   :name "writes-to"}
  {:file "src/main/clojure/clojure/core/async/impl/protocols.clj",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/protocols.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/protocols.clj#L20",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.protocols/Handler",
   :namespace "clojure.core.async.impl.protocols",
   :line 20,
   :var-type "protocol",
   :doc nil,
   :name "Handler"}
  {:file "src/main/clojure/clojure/core/async/impl/protocols.clj",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/protocols.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/protocols.clj#L11",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.protocols/ReadPort",
   :namespace "clojure.core.async.impl.protocols",
   :line 11,
   :var-type "protocol",
   :doc nil,
   :name "ReadPort"}
  {:file "src/main/clojure/clojure/core/async/impl/protocols.clj",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/protocols.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/protocols.clj#L14",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.protocols/WritePort",
   :namespace "clojure.core.async.impl.protocols",
   :line 14,
   :var-type "protocol",
   :doc nil,
   :name "WritePort"}
  {:file nil,
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.protocols/active?",
   :namespace "clojure.core.async.impl.protocols",
   :var-type "function",
   :arglists ([h]),
   :doc "returns true if has callback. Must work w/o lock",
   :name "active?"}
  {:file nil,
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.protocols/commit",
   :namespace "clojure.core.async.impl.protocols",
   :var-type "function",
   :arglists ([h]),
   :doc
   "commit to fulfilling its end of the transfer, returns cb. Must be called within lock",
   :name "commit"}
  {:file nil,
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.protocols/lock-id",
   :namespace "clojure.core.async.impl.protocols",
   :var-type "function",
   :arglists ([h]),
   :doc "a unique id for lock acquisition order, 0 if no lock",
   :name "lock-id"}
  {:file nil,
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.protocols/take!",
   :namespace "clojure.core.async.impl.protocols",
   :var-type "function",
   :arglists ([port fn1-handler]),
   :doc "derefable val if taken, nil if take was enqueued",
   :name "take!"}
  {:file nil,
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.protocols/put!",
   :namespace "clojure.core.async.impl.protocols",
   :var-type "function",
   :arglists ([port val fn0-handler]),
   :doc
   "derefable nil if put, nil if put was enqueued. Must throw on nil val.",
   :name "put!"}
  {:arglists ([channel timestamp]),
   :name "->TimeoutQueueEntry",
   :namespace "clojure.core.async.impl.timers",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/timers.clj#L24",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/timers.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.timers/->TimeoutQueueEntry",
   :doc
   "Positional factory function for class clojure.core.async.impl.timers.TimeoutQueueEntry.",
   :var-type "function",
   :line 24,
   :file "src/main/clojure/clojure/core/async/impl/timers.clj"}
  {:arglists ([msecs]),
   :name "timeout",
   :namespace "clojure.core.async.impl.timers",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/timers.clj#L42",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/impl/timers.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.timers/timeout",
   :doc "returns a channel that will close after msecs",
   :var-type "function",
   :line 42,
   :file "src/main/clojure/clojure/core/async/impl/timers.clj"}
  {:file nil,
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.impl.timers/TimeoutQueueEntry",
   :namespace "clojure.core.async.impl.timers",
   :var-type "type",
   :name "TimeoutQueueEntry"}
  {:arglists ([write-ports mutex]),
   :name "->BroadcastingWritePort",
   :namespace "clojure.core.async.lab",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/lab.clj#L92",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/lab.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.lab/->BroadcastingWritePort",
   :doc
   "Positional factory function for class clojure.core.async.lab.BroadcastingWritePort.",
   :var-type "function",
   :line 92,
   :file "src/main/clojure/clojure/core/async/lab.clj"}
  {:arglists ([mutex read-ports]),
   :name "->MultiplexingReadPort",
   :namespace "clojure.core.async.lab",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/lab.clj#L33",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/lab.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.lab/->MultiplexingReadPort",
   :doc
   "Positional factory function for class clojure.core.async.lab.MultiplexingReadPort.",
   :var-type "function",
   :line 33,
   :file "src/main/clojure/clojure/core/async/lab.clj"}
  {:arglists ([& ports]),
   :name "broadcast",
   :namespace "clojure.core.async.lab",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/lab.clj#L100",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/lab.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.lab/broadcast",
   :doc
   "Returns a broadcasting write port which, when written to, writes\nthe value to each of ports.\n\nWrites to the broadcasting port will park until the value is written\nto each of the ports used to create it. For this reason, it is\nstrongly advised that each of the underlying ports support buffered\nwrites.",
   :var-type "function",
   :line 100,
   :file "src/main/clojure/clojure/core/async/lab.clj"}
  {:arglists ([& ports]),
   :name "multiplex",
   :namespace "clojure.core.async.lab",
   :source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/lab.clj#L69",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async/lab.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.lab/multiplex",
   :doc
   "Returns a multiplexing read port which, when read from, produces a\nvalue from one of ports.\n\nIf at read time only one port is available to be read from, the\nmultiplexing port will return that value. If multiple ports are\navailable to be read from, the multiplexing port will return one\nvalue from a port chosen non-deterministicly. If no port is\navailable to be read from, parks execution until a value is\navailable.",
   :var-type "function",
   :line 69,
   :file "src/main/clojure/clojure/core/async/lab.clj"}
  {:file nil,
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.lab/BroadcastingWritePort",
   :namespace "clojure.core.async.lab",
   :var-type "type",
   :name "BroadcastingWritePort"}
  {:file nil,
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.lab/MultiplexingReadPort",
   :namespace "clojure.core.async.lab",
   :var-type "type",
   :name "MultiplexingReadPort"})}
