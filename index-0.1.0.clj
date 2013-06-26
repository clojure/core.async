{:namespaces
 ({:source-url
   "https://github.com/clojure/core.async/blob/d466ba28fbea0a7421f9dfba7288aeb96f1ad289/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async/clojure.core.async-api.html",
   :name "clojure.core.async",
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
