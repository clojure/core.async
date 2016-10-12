{:namespaces
 ({:doc
   "Facilities for async programming and communication.\n\ngo blocks are dispatched over an internal thread pool, which\ndefaults to 8 threads. The size of this pool can be modified using\nthe Java system property `clojure.core.async.pool-size`.",
   :name "clojure.core.async",
   :wiki-url "http://clojure.github.io/core.async/index.html",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj"}
  {:doc
   "core.async HIGHLY EXPERIMENTAL feature exploration\n\nCaveats:\n\n1. Everything defined in this namespace is experimental, and subject\nto change or deletion without warning.\n\n2. Many features provided by this namespace are highly coupled to\nimplementation details of core.async. Potential features which\noperate at higher levels of abstraction are suitable for inclusion\nin the examples.\n\n3. Features provided by this namespace MAY be promoted to\nclojure.core.async at a later point in time, but there is no\nguarantee any of them will.",
   :name "clojure.core.async.lab",
   :wiki-url
   "http://clojure.github.io/core.async/index.html#clojure.core.async.lab",
   :source-url
   "https://github.com/clojure/core.async/blob/ad7ca68a831a1e469b6f30334903279b2e4e43f7/src/main/clojure/clojure/core/async/lab.clj"}),
 :vars
 ({:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "<!",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L116",
   :line 116,
   :var-type "function",
   :arglists ([port]),
   :doc
   "takes a val from port. Must be called inside a (go ...) block. Will\nreturn nil if closed. Will park if nothing is available.",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/<!"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "<!!",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L106",
   :line 106,
   :var-type "function",
   :arglists ([port]),
   :doc
   "takes a val from port. Will return nil if closed. Will block\nif nothing is available.",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/<!!"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name ">!",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L147",
   :line 147,
   :var-type "function",
   :arglists ([port val]),
   :doc
   "puts a val into port. nil values are not allowed. Must be called\ninside a (go ...) block. Will park if no buffer space is available.\nReturns true unless port is already closed.",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/>!"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name ">!!",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L137",
   :line 137,
   :var-type "function",
   :arglists ([port val]),
   :doc
   "puts a val into port. nil values are not allowed. Will block if no\nbuffer space is available. Returns true unless port is already closed.",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/>!!"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "admix",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L799",
   :line 799,
   :var-type "function",
   :arglists ([mix ch]),
   :doc "Adds ch as an input to the mix",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/admix"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "alt!",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L347",
   :line 347,
   :var-type "macro",
   :arglists ([& clauses]),
   :doc
   "Makes a single choice between one of several channel operations,\nas if by alts!, returning the value of the result expr corresponding\nto the operation completed. Must be called inside a (go ...) block.\n\nEach clause takes the form of:\n\nchannel-op[s] result-expr\n\nwhere channel-ops is one of:\n\ntake-port - a single port to take\n[take-port | [put-port put-val] ...] - a vector of ports as per alts!\n:default | :priority - an option for alts!\n\nand result-expr is either a list beginning with a vector, whereupon that\nvector will be treated as a binding for the [val port] return of the\noperation, else any other expression.\n\n(alt!\n  [c t] ([val ch] (foo ch val))\n  x ([v] v)\n  [[out val]] :wrote\n  :default 42)\n\nEach option may appear at most once. The choice and parking\ncharacteristics are those of alts!.",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/alt!"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "alt!!",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L340",
   :line 340,
   :var-type "macro",
   :arglists ([& clauses]),
   :doc
   "Like alt!, except as if by alts!!, will block until completed, and\nnot intended for use in (go ...) blocks.",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/alt!!"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "alts!",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L276",
   :line 276,
   :var-type "function",
   :arglists ([ports & {:as opts}]),
   :doc
   "Completes at most one of several channel operations. Must be called\ninside a (go ...) block. ports is a vector of channel endpoints,\nwhich can be either a channel to take from or a vector of\n[channel-to-put-to val-to-put], in any combination. Takes will be\nmade as if by <!, and puts will be made as if by >!. Unless\nthe :priority option is true, if more than one port operation is\nready a non-deterministic choice will be made. If no operation is\nready and a :default value is supplied, [default-val :default] will\nbe returned, otherwise alts! will park until the first operation to\nbecome ready completes. Returns [val port] of the completed\noperation, where val is the value taken for takes, and a\nboolean (true unless already closed, as per put!) for puts.\n\nopts are passed as :key val ... Supported options:\n\n:default val - the value to use if none of the operations are immediately ready\n:priority true - (default nil) when true, the operations will be tried in order.\n\nNote: there is no guarantee that the port exps or val exprs will be\nused, nor in what order should they be, so they should not be\ndepended upon for side effects.",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/alts!"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "alts!!",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L265",
   :line 265,
   :var-type "function",
   :arglists ([ports & {:as opts}]),
   :doc
   "Like alts!, except takes will be made as if by <!!, and puts will\nbe made as if by >!!, will block until completed, and not intended\nfor use in (go ...) blocks.",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/alts!!"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "buffer",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L50",
   :line 50,
   :var-type "function",
   :arglists ([n]),
   :doc
   "Returns a fixed buffer of size n. When full, puts will block/park.",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/buffer"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "chan",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L74",
   :line 74,
   :var-type "function",
   :arglists
   ([] [buf-or-n] [buf-or-n xform] [buf-or-n xform ex-handler]),
   :doc
   "Creates a channel with an optional buffer, an optional transducer\n(like (map f), (filter p) etc or a composition thereof), and an\noptional exception-handler.  If buf-or-n is a number, will create\nand use a fixed buffer of that size. If a transducer is supplied a\nbuffer must be specified. ex-handler must be a fn of one argument -\nif an exception occurs during transformation it will be called with\nthe Throwable as an argument, and any non-nil return value will be\nplaced in the channel.",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/chan"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "close!",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L177",
   :line 177,
   :var-type "function",
   :arglists ([chan]),
   :doc
   "Closes a channel. The channel will no longer accept any puts (they\nwill be ignored). Data in the channel remains available for taking, until\nexhausted, after which takes will return nil. If there are any\npending takes, they will be dispatched with nil. Closing a closed\nchannel is a no-op. Returns nil.\n\nLogically closing happens after all puts have been delivered. Therefore, any\nblocked or parked puts will remain blocked/parked until a taker releases them.",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/close!"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "do-alts",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L236",
   :line 236,
   :var-type "function",
   :arglists ([fret ports opts]),
   :doc "returns derefable [val port] if immediate, nil if enqueued",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/do-alts"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "dropping-buffer",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L55",
   :line 55,
   :var-type "function",
   :arglists ([n]),
   :doc
   "Returns a buffer of size n. When full, puts will complete but\nval will be dropped (no transfer).",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/dropping-buffer"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "filter<",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L1064",
   :line 1064,
   :deprecated "0.1.319.0-6b1aca-alpha",
   :var-type "function",
   :arglists ([p ch] [p ch buf-or-n]),
   :doc
   "Deprecated - this function will be removed. Use transducer instead",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/filter<"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "filter>",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L1040",
   :line 1040,
   :deprecated "0.1.319.0-6b1aca-alpha",
   :var-type "function",
   :arglists ([p ch]),
   :doc
   "Deprecated - this function will be removed. Use transducer instead",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/filter>"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "go",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L403",
   :line 403,
   :var-type "macro",
   :arglists ([& body]),
   :doc
   "Asynchronously executes the body, returning immediately to the\ncalling thread. Additionally, any visible calls to <!, >! and alt!/alts!\nchannel operations within the body will block (if necessary) by\n'parking' the calling thread rather than tying up an OS thread (or\nthe only JS thread when in ClojureScript). Upon completion of the\noperation, the body will be resumed.\n\nReturns a channel which will receive the result of the body when\ncompleted",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/go"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "go-loop",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L455",
   :line 455,
   :var-type "macro",
   :arglists ([bindings & body]),
   :doc "Like (go (loop ...))",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/go-loop"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "into",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L965",
   :line 965,
   :var-type "function",
   :arglists ([coll ch]),
   :doc
   "Returns a channel containing the single (collection) result of the\nitems taken from the channel conjoined to the supplied\ncollection. ch must close before into produces a result.",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/into"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "map",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L911",
   :line 911,
   :var-type "function",
   :arglists ([f chs] [f chs buf-or-n]),
   :doc
   "Takes a function and a collection of source channels, and returns a\nchannel which contains the values produced by applying f to the set\nof first items taken from each source channel, followed by applying\nf to the set of second items from each channel, until any one of the\nchannels is closed, at which point the output channel will be\nclosed. The returned channel will be unbuffered by default, or a\nbuf-or-n can be supplied",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/map"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "map<",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L992",
   :line 992,
   :deprecated "0.1.319.0-6b1aca-alpha",
   :var-type "function",
   :arglists ([f ch]),
   :doc
   "Deprecated - this function will be removed. Use transducer instead",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/map<"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "map>",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L1024",
   :line 1024,
   :deprecated "0.1.319.0-6b1aca-alpha",
   :var-type "function",
   :arglists ([f ch]),
   :doc
   "Deprecated - this function will be removed. Use transducer instead",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/map>"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "mapcat<",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L1095",
   :line 1095,
   :deprecated "0.1.319.0-6b1aca-alpha",
   :var-type "function",
   :arglists ([f in] [f in buf-or-n]),
   :doc
   "Deprecated - this function will be removed. Use transducer instead",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/mapcat<"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "mapcat>",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L1104",
   :line 1104,
   :deprecated "0.1.319.0-6b1aca-alpha",
   :var-type "function",
   :arglists ([f out] [f out buf-or-n]),
   :doc
   "Deprecated - this function will be removed. Use transducer instead",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/mapcat>"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "merge",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L947",
   :line 947,
   :var-type "function",
   :arglists ([chs] [chs buf-or-n]),
   :doc
   "Takes a collection of source channels and returns a channel which\ncontains all values taken from them. The returned channel will be\nunbuffered by default, or a buf-or-n can be supplied. The channel\nwill close after all the source channels have closed.",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/merge"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "mix",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L729",
   :line 729,
   :var-type "function",
   :arglists ([out]),
   :doc
   "Creates and returns a mix of one or more input channels which will\nbe put on the supplied out channel. Input sources can be added to\nthe mix with 'admix', and removed with 'unmix'. A mix supports\nsoloing, muting and pausing multiple inputs atomically using\n'toggle', and can solo using either muting or pausing as determined\nby 'solo-mode'.\n\nEach channel can have zero or more boolean modes set via 'toggle':\n\n:solo - when true, only this (ond other soloed) channel(s) will appear\n        in the mix output channel. :mute and :pause states of soloed\n        channels are ignored. If solo-mode is :mute, non-soloed\n        channels are muted, if :pause, non-soloed channels are\n        paused.\n\n:mute - muted channels will have their contents consumed but not included in the mix\n:pause - paused channels will not have their contents consumed (and thus also not included in the mix)",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/mix"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "mult",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L662",
   :line 662,
   :var-type "function",
   :arglists ([ch]),
   :doc
   "Creates and returns a mult(iple) of the supplied channel. Channels\ncontaining copies of the channel can be created with 'tap', and\ndetached with 'untap'.\n\nEach item is distributed to all taps in parallel and synchronously,\ni.e. each tap must accept before the next item is distributed. Use\nbuffering/windowing to prevent slow taps from holding up the mult.\n\nItems received when there are no taps get dropped.\n\nIf a tap puts to a closed channel, it will be removed from the mult.",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/mult"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "offer!",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L389",
   :line 389,
   :var-type "function",
   :arglists ([port val]),
   :doc
   "Puts a val into port if it's possible to do so immediately.\nnil values are not allowed. Never blocks. Returns true if offer succeeds.",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/offer!"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "onto-chan",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L631",
   :line 631,
   :var-type "function",
   :arglists ([ch coll] [ch coll close?]),
   :doc
   "Puts the contents of coll into the supplied channel.\n\nBy default the channel will be closed after the items are copied,\nbut can be determined by the close? parameter.\n\nReturns a channel which will close after the items are copied.",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/onto-chan"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "partition",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L1131",
   :line 1131,
   :deprecated "0.1.319.0-6b1aca-alpha",
   :var-type "function",
   :arglists ([n ch] [n ch buf-or-n]),
   :doc
   "Deprecated - this function will be removed. Use transducer instead",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/partition"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "partition-by",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L1156",
   :line 1156,
   :deprecated "0.1.319.0-6b1aca-alpha",
   :var-type "function",
   :arglists ([f ch] [f ch buf-or-n]),
   :doc
   "Deprecated - this function will be removed. Use transducer instead",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/partition-by"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "pipe",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L460",
   :line 460,
   :var-type "function",
   :arglists ([from to] [from to close?]),
   :doc
   "Takes elements from the from channel and supplies them to the to\nchannel. By default, the to channel will be closed when the from\nchannel closes, but can be determined by the close?  parameter. Will\nstop consuming the from channel if the to channel closes",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/pipe"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "pipeline",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L534",
   :line 534,
   :var-type "function",
   :arglists
   ([n to xf from]
    [n to xf from close?]
    [n to xf from close? ex-handler]),
   :doc
   "Takes elements from the from channel and supplies them to the to\nchannel, subject to the transducer xf, with parallelism n. Because\nit is parallel, the transducer will be applied independently to each\nelement, not across elements, and may produce zero or more outputs\nper input.  Outputs will be returned in order relative to the\ninputs. By default, the to channel will be closed when the from\nchannel closes, but can be determined by the close?  parameter. Will\nstop consuming the from channel if the to channel closes. Note this\nshould be used for computational parallelism. If you have multiple\nblocking operations to put in flight, use pipeline-blocking instead,\nIf you have multiple asynchronous operations to put in flight, use\npipeline-async instead.",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/pipeline"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "pipeline-async",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L557",
   :line 557,
   :var-type "function",
   :arglists ([n to af from] [n to af from close?]),
   :doc
   "Takes elements from the from channel and supplies them to the to\nchannel, subject to the async function af, with parallelism n. af\nmust be a function of two arguments, the first an input value and\nthe second a channel on which to place the result(s). af must close!\nthe channel before returning.  The presumption is that af will\nreturn immediately, having launched some asynchronous operation\n(i.e. in another thread) whose completion/callback will manipulate\nthe result channel. Outputs will be returned in order relative to\nthe inputs. By default, the to channel will be closed when the from\nchannel closes, but can be determined by the close?  parameter. Will\nstop consuming the from channel if the to channel closes. See also\npipeline, pipeline-blocking.",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/pipeline-async"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "pipeline-blocking",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L551",
   :line 551,
   :var-type "function",
   :arglists
   ([n to xf from]
    [n to xf from close?]
    [n to xf from close? ex-handler]),
   :doc "Like pipeline, for blocking operations.",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/pipeline-blocking"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "poll!",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L396",
   :line 396,
   :var-type "function",
   :arglists ([port]),
   :doc
   "Takes a val from port if it's possible to do so immediately.\nNever blocks. Returns value if successful, nil otherwise.",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/poll!"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "promise-chan",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L90",
   :line 90,
   :var-type "function",
   :arglists ([] [xform] [xform ex-handler]),
   :doc
   "Creates a promise channel with an optional transducer, and an optional\nexception-handler. A promise channel can take exactly one value that consumers\nwill receive. Once full, puts complete but val is dropped (no transfer).\nConsumers will block until either a value is placed in the channel or the\nchannel is closed. See chan for the semantics of xform and ex-handler.",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/promise-chan"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "pub",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L836",
   :line 836,
   :var-type "function",
   :arglists ([ch topic-fn] [ch topic-fn buf-fn]),
   :doc
   "Creates and returns a pub(lication) of the supplied channel,\npartitioned into topics by the topic-fn. topic-fn will be applied to\neach value on the channel and the result will determine the 'topic'\non which that value will be put. Channels can be subscribed to\nreceive copies of topics using 'sub', and unsubscribed using\n'unsub'. Each topic will be handled by an internal mult on a\ndedicated channel. By default these internal channels are\nunbuffered, but a buf-fn can be supplied which, given a topic,\ncreates a buffer with desired properties.\n\nEach item is distributed to all subs in parallel and synchronously,\ni.e. each sub must accept before the next item is distributed. Use\nbuffering/windowing to prevent slow subs from holding up the pub.\n\nItems received when there are no matching subs get dropped.\n\nNote that if buf-fns are used then each topic is handled\nasynchronously, i.e. if a channel is subscribed to more than one\ntopic it should not expect them to be interleaved identically with\nthe source.",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/pub"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "put!",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L157",
   :line 157,
   :var-type "function",
   :arglists ([port val] [port val fn1] [port val fn1 on-caller?]),
   :doc
   "Asynchronously puts a val into port, calling fn1 (if supplied) when\ncomplete, passing false iff port is already closed. nil values are\nnot allowed. If on-caller? (default true) is true, and the put is\nimmediately accepted, will call fn1 on calling thread.  Returns\ntrue unless port is already closed.",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/put!"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "reduce",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L594",
   :line 594,
   :var-type "function",
   :arglists ([f init ch]),
   :doc
   "f should be a function of 2 arguments. Returns a channel containing\nthe single result of applying f to init and the first item from the\nchannel, then applying f to that result and the 2nd item, etc. If\nthe channel closes without yielding items, returns init and f is not\ncalled. ch must close before reduce produces a result.",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/reduce"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "remove<",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L1079",
   :line 1079,
   :deprecated "0.1.319.0-6b1aca-alpha",
   :var-type "function",
   :arglists ([p ch] [p ch buf-or-n]),
   :doc
   "Deprecated - this function will be removed. Use transducer instead",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/remove<"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "remove>",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L1058",
   :line 1058,
   :deprecated "0.1.319.0-6b1aca-alpha",
   :var-type "function",
   :arglists ([p ch]),
   :doc
   "Deprecated - this function will be removed. Use transducer instead",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/remove>"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "sliding-buffer",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L61",
   :line 61,
   :var-type "function",
   :arglists ([n]),
   :doc
   "Returns a buffer of size n. When full, puts will complete, and be\nbuffered, but oldest elements in buffer will be dropped (not\ntransferred).",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/sliding-buffer"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "solo-mode",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L826",
   :line 826,
   :var-type "function",
   :arglists ([mix mode]),
   :doc
   "Sets the solo mode of the mix. mode must be one of :mute or :pause",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/solo-mode"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "split",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L573",
   :line 573,
   :var-type "function",
   :arglists ([p ch] [p ch t-buf-or-n f-buf-or-n]),
   :doc
   "Takes a predicate and a source channel and returns a vector of two\nchannels, the first of which will contain the values for which the\npredicate returned true, the second those for which it returned\nfalse.\n\nThe out channels will be unbuffered by default, or two buf-or-ns can\nbe supplied. The channels will close after the source channel has\nclosed.",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/split"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "sub",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L891",
   :line 891,
   :var-type "function",
   :arglists ([p topic ch] [p topic ch close?]),
   :doc
   "Subscribes a channel to a topic of a pub.\n\nBy default the channel will be closed when the source closes,\nbut can be determined by the close? parameter.",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/sub"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "take",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L973",
   :line 973,
   :var-type "function",
   :arglists ([n ch] [n ch buf-or-n]),
   :doc
   "Returns a channel that will return, at most, n items from ch. After n items\n have been returned, or ch has been closed, the return channel will close.\n\nThe output channel is unbuffered by default, unless buf-or-n is given.",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/take"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "take!",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L122",
   :line 122,
   :var-type "function",
   :arglists ([port fn1] [port fn1 on-caller?]),
   :doc
   "Asynchronously takes a val from port, passing to fn1. Will pass nil\nif closed. If on-caller? (default true) is true, and value is\nimmediately available, will call fn1 on calling thread.\nReturns nil.",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/take!"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "tap",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L705",
   :line 705,
   :var-type "function",
   :arglists ([mult ch] [mult ch close?]),
   :doc
   "Copies the mult source onto the supplied channel.\n\nBy default the channel will be closed when the source closes,\nbut can be determined by the close? parameter.",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/tap"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "thread",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L446",
   :line 446,
   :var-type "macro",
   :arglists ([& body]),
   :doc
   "Executes the body in another thread, returning immediately to the\ncalling thread. Returns a channel which will receive the result of\nthe body when completed.",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/thread"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "thread-call",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L428",
   :line 428,
   :var-type "function",
   :arglists ([f]),
   :doc
   "Executes f in another thread, returning immediately to the calling\nthread. Returns a channel which will receive the result of calling\nf when completed.",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/thread-call"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "timeout",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L101",
   :line 101,
   :var-type "function",
   :arglists ([msecs]),
   :doc "Returns a channel that will close after msecs",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/timeout"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "to-chan",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L646",
   :line 646,
   :var-type "function",
   :arglists ([coll]),
   :doc
   "Creates and returns a channel which contains the contents of coll,\nclosing when exhausted.",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/to-chan"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "toggle",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L814",
   :line 814,
   :var-type "function",
   :arglists ([mix state-map]),
   :doc
   "Atomically sets the state(s) of one or more channels in a mix. The\nstate map is a map of channels -> channel-state-map. A\nchannel-state-map is a map of attrs -> boolean, where attr is one or\nmore of :mute, :pause or :solo. Any states supplied are merged with\nthe current state.\n\nNote that channels can be added to a mix via toggle, which can be\nused to add channels in a particular (e.g. paused) state.",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/toggle"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "transduce",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L610",
   :line 610,
   :var-type "function",
   :arglists ([xform f init ch]),
   :doc
   "async/reduces a channel with a transformation (xform f).\nReturns a channel containing the result.  ch must close before\ntransduce produces a result.",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/transduce"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "unblocking-buffer?",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L68",
   :line 68,
   :var-type "function",
   :arglists ([buff]),
   :doc
   "Returns true if a channel created with buff will never block. That is to say,\nputs into this buffer will never cause the buffer to be full. ",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/unblocking-buffer?"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "unique",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L1113",
   :line 1113,
   :deprecated "0.1.319.0-6b1aca-alpha",
   :var-type "function",
   :arglists ([ch] [ch buf-or-n]),
   :doc
   "Deprecated - this function will be removed. Use transducer instead",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/unique"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "unmix",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L804",
   :line 804,
   :var-type "function",
   :arglists ([mix ch]),
   :doc "Removes ch as an input to the mix",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/unmix"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "unmix-all",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L809",
   :line 809,
   :var-type "function",
   :arglists ([mix]),
   :doc "removes all inputs from the mix",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/unmix-all"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "unsub",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L899",
   :line 899,
   :var-type "function",
   :arglists ([p topic ch]),
   :doc "Unsubscribes a channel from a topic of a pub",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/unsub"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "unsub-all",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L904",
   :line 904,
   :var-type "function",
   :arglists ([p] [p topic]),
   :doc "Unsubscribes all channels from a pub, or a topic of a pub",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/unsub-all"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "untap",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L713",
   :line 713,
   :var-type "function",
   :arglists ([mult ch]),
   :doc "Disconnects a target channel from a mult",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/untap"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj",
   :name "untap-all",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/2afc2dc5102f60713135ffca6fab993fb35809f0/src/main/clojure/clojure/core/async.clj#L718",
   :line 718,
   :var-type "function",
   :arglists ([mult]),
   :doc "Disconnects all target channels from a mult",
   :namespace "clojure.core.async",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async/untap-all"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/ad7ca68a831a1e469b6f30334903279b2e4e43f7/src/main/clojure/clojure/core/async/lab.clj",
   :name "->BroadcastingWritePort",
   :file "src/main/clojure/clojure/core/async/lab.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/ad7ca68a831a1e469b6f30334903279b2e4e43f7/src/main/clojure/clojure/core/async/lab.clj#L93",
   :line 93,
   :var-type "function",
   :arglists ([write-ports]),
   :doc
   "Positional factory function for class clojure.core.async.lab.BroadcastingWritePort.",
   :namespace "clojure.core.async.lab",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async.lab/->BroadcastingWritePort"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/ad7ca68a831a1e469b6f30334903279b2e4e43f7/src/main/clojure/clojure/core/async/lab.clj",
   :name "->MultiplexingReadPort",
   :file "src/main/clojure/clojure/core/async/lab.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/ad7ca68a831a1e469b6f30334903279b2e4e43f7/src/main/clojure/clojure/core/async/lab.clj#L33",
   :line 33,
   :var-type "function",
   :arglists ([mutex read-ports]),
   :doc
   "Positional factory function for class clojure.core.async.lab.MultiplexingReadPort.",
   :namespace "clojure.core.async.lab",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async.lab/->MultiplexingReadPort"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/ad7ca68a831a1e469b6f30334903279b2e4e43f7/src/main/clojure/clojure/core/async/lab.clj",
   :name "broadcast",
   :file "src/main/clojure/clojure/core/async/lab.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/ad7ca68a831a1e469b6f30334903279b2e4e43f7/src/main/clojure/clojure/core/async/lab.clj#L99",
   :line 99,
   :var-type "function",
   :arglists ([& ports]),
   :doc
   "Returns a broadcasting write port which, when written to, writes\nthe value to each of ports.\n\nWrites to the broadcasting port will park until the value is written\nto each of the ports used to create it. For this reason, it is\nstrongly advised that each of the underlying ports support buffered\nwrites.",
   :namespace "clojure.core.async.lab",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async.lab/broadcast"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/ad7ca68a831a1e469b6f30334903279b2e4e43f7/src/main/clojure/clojure/core/async/lab.clj",
   :name "multiplex",
   :file "src/main/clojure/clojure/core/async/lab.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/ad7ca68a831a1e469b6f30334903279b2e4e43f7/src/main/clojure/clojure/core/async/lab.clj#L69",
   :line 69,
   :var-type "function",
   :arglists ([& ports]),
   :doc
   "Returns a multiplexing read port which, when read from, produces a\nvalue from one of ports.\n\nIf at read time only one port is available to be read from, the\nmultiplexing port will return that value. If multiple ports are\navailable to be read from, the multiplexing port will return one\nvalue from a port chosen non-deterministicly. If no port is\navailable to be read from, parks execution until a value is\navailable.",
   :namespace "clojure.core.async.lab",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async.lab/multiplex"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/ad7ca68a831a1e469b6f30334903279b2e4e43f7/src/main/clojure/clojure/core/async/lab.clj",
   :name "spool",
   :file "src/main/clojure/clojure/core/async/lab.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/ad7ca68a831a1e469b6f30334903279b2e4e43f7/src/main/clojure/clojure/core/async/lab.clj#L112",
   :line 112,
   :var-type "function",
   :arglists ([s c] [s]),
   :doc
   "Take a sequence and puts each value on a channel and returns the channel.\nIf no channel is provided, an unbuffered channel is created. If the\nsequence ends, the channel is closed.",
   :namespace "clojure.core.async.lab",
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async.lab/spool"}
  {:name "BroadcastingWritePort",
   :var-type "type",
   :namespace "clojure.core.async.lab",
   :arglists nil,
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async.lab/BroadcastingWritePort",
   :source-url nil,
   :raw-source-url nil,
   :file nil}
  {:name "MultiplexingReadPort",
   :var-type "type",
   :namespace "clojure.core.async.lab",
   :arglists nil,
   :wiki-url
   "http://clojure.github.io/core.async//index.html#clojure.core.async.lab/MultiplexingReadPort",
   :source-url nil,
   :raw-source-url nil,
   :file nil})}
