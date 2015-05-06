{:namespaces
 ({:source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async/clojure.core.async-api.html",
   :name "clojure.core.async",
   :doc nil}
  {:source-url
   "https://github.com/clojure/core.async/blob/ad7ca68a831a1e469b6f30334903279b2e4e43f7/src/main/clojure/clojure/core/async/lab.clj",
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
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L100",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/<!",
   :doc
   "takes a val from port. Must be called inside a (go ...) block. Will\nreturn nil if closed. Will park if nothing is available.",
   :var-type "function",
   :line 100,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([port]),
   :name "<!!",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L90",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/<!!",
   :doc
   "takes a val from port. Will return nil if closed. Will block\nif nothing is available.",
   :var-type "function",
   :line 90,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([port val]),
   :name ">!",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L131",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/>!",
   :doc
   "puts a val into port. nil values are not allowed. Must be called\ninside a (go ...) block. Will park if no buffer space is available.\nReturns true unless port is already closed.",
   :var-type "function",
   :line 131,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([port val]),
   :name ">!!",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L121",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/>!!",
   :doc
   "puts a val into port. nil values are not allowed. Will block if no\nbuffer space is available. Returns true unless port is already closed.",
   :var-type "function",
   :line 121,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([mix ch]),
   :name "admix",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L773",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/admix",
   :doc "Adds ch as an input to the mix",
   :var-type "function",
   :line 773,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([& clauses]),
   :name "alt!",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L331",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/alt!",
   :doc
   "Makes a single choice between one of several channel operations,\nas if by alts!, returning the value of the result expr corresponding\nto the operation completed. Must be called inside a (go ...) block.\n\nEach clause takes the form of:\n\nchannel-op[s] result-expr\n\nwhere channel-ops is one of:\n\ntake-port - a single port to take\n[take-port | [put-port put-val] ...] - a vector of ports as per alts!\n:default | :priority - an option for alts!\n\nand result-expr is either a list beginning with a vector, whereupon that\nvector will be treated as a binding for the [val port] return of the\noperation, else any other expression.\n\n(alt!\n  [c t] ([val ch] (foo ch val))\n  x ([v] v)\n  [[out val]] :wrote\n  :default 42)\n\nEach option may appear at most once. The choice and parking\ncharacteristics are those of alts!.",
   :var-type "macro",
   :line 331,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([& clauses]),
   :name "alt!!",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L324",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/alt!!",
   :doc
   "Like alt!, except as if by alts!!, will block until completed, and\nnot intended for use in (go ...) blocks.",
   :var-type "macro",
   :line 324,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([ports & {:as opts}]),
   :name "alts!",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L260",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/alts!",
   :doc
   "Completes at most one of several channel operations. Must be called\ninside a (go ...) block. ports is a vector of channel endpoints,\nwhich can be either a channel to take from or a vector of\n[channel-to-put-to val-to-put], in any combination. Takes will be\nmade as if by <!, and puts will be made as if by >!. Unless\nthe :priority option is true, if more than one port operation is\nready a non-deterministic choice will be made. If no operation is\nready and a :default value is supplied, [default-val :default] will\nbe returned, otherwise alts! will park until the first operation to\nbecome ready completes. Returns [val port] of the completed\noperation, where val is the value taken for takes, and a\nboolean (true unless already closed, as per put!) for puts.\n\nopts are passed as :key val ... Supported options:\n\n:default val - the value to use if none of the operations are immediately ready\n:priority true - (default nil) when true, the operations will be tried in order.\n\nNote: there is no guarantee that the port exps or val exprs will be\nused, nor in what order should they be, so they should not be\ndepended upon for side effects.",
   :var-type "function",
   :line 260,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([ports & {:as opts}]),
   :name "alts!!",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L249",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/alts!!",
   :doc
   "Like alts!, except takes will be made as if by <!!, and puts will\nbe made as if by >!!, will block until completed, and not intended\nfor use in (go ...) blocks.",
   :var-type "function",
   :line 249,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([n]),
   :name "buffer",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L45",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/buffer",
   :doc
   "Returns a fixed buffer of size n. When full, puts will block/park.",
   :var-type "function",
   :line 45,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists
   ([] [buf-or-n] [buf-or-n xform] [buf-or-n xform ex-handler]),
   :name "chan",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L69",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/chan",
   :doc
   "Creates a channel with an optional buffer, an optional transducer\n(like (map f), (filter p) etc or a composition thereof), and an\noptional exception-handler.  If buf-or-n is a number, will create\nand use a fixed buffer of that size. If a transducer is supplied a\nbuffer must be specified. ex-handler must be a fn of one argument -\nif an exception occurs during transformation it will be called with\nthe Throwable as an argument, and any non-nil return value will be\nplaced in the channel.",
   :var-type "function",
   :line 69,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([chan]),
   :name "close!",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L161",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/close!",
   :doc
   "Closes a channel. The channel will no longer accept any puts (they\nwill be ignored). Data in the channel remains available for taking, until\nexhausted, after which takes will return nil. If there are any\npending takes, they will be dispatched with nil. Closing a closed\nchannel is a no-op. Returns nil.\n\nLogically closing happens after all puts have been delivered. Therefore, any\nblocked or parked puts will remain blocked/parked until a taker releases them.",
   :var-type "function",
   :line 161,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([fret ports opts]),
   :name "do-alts",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L220",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/do-alts",
   :doc "returns derefable [val port] if immediate, nil if enqueued",
   :var-type "function",
   :line 220,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([n]),
   :name "dropping-buffer",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L50",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/dropping-buffer",
   :doc
   "Returns a buffer of size n. When full, puts will complete but\nval will be dropped (no transfer).",
   :var-type "function",
   :line 50,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([p ch] [p ch buf-or-n]),
   :name "filter<",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L1034",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/filter<",
   :doc
   "Deprecated - this function will be removed. Use transducer instead",
   :var-type "function",
   :line 1034,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([p ch]),
   :name "filter>",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L1012",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/filter>",
   :doc
   "Deprecated - this function will be removed. Use transducer instead",
   :var-type "function",
   :line 1012,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([& body]),
   :name "go",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L387",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/go",
   :doc
   "Asynchronously executes the body, returning immediately to the\ncalling thread. Additionally, any visible calls to <!, >! and alt!/alts!\nchannel operations within the body will block (if necessary) by\n'parking' the calling thread rather than tying up an OS thread (or\nthe only JS thread when in ClojureScript). Upon completion of the\noperation, the body will be resumed.\n\nReturns a channel which will receive the result of the body when\ncompleted",
   :var-type "macro",
   :line 387,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([bindings & body]),
   :name "go-loop",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L439",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/go-loop",
   :doc "Like (go (loop ...))",
   :var-type "macro",
   :line 439,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([coll ch]),
   :name "into",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L939",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/into",
   :doc
   "Returns a channel containing the single (collection) result of the\nitems taken from the channel conjoined to the supplied\ncollection. ch must close before into produces a result.",
   :var-type "function",
   :line 939,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([f chs] [f chs buf-or-n]),
   :name "map",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L885",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/map",
   :doc
   "Takes a function and a collection of source channels, and returns a\nchannel which contains the values produced by applying f to the set\nof first items taken from each source channel, followed by applying\nf to the set of second items from each channel, until any one of the\nchannels is closed, at which point the output channel will be\nclosed. The returned channel will be unbuffered by default, or a\nbuf-or-n can be supplied",
   :var-type "function",
   :line 885,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([f ch]),
   :name "map<",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L966",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/map<",
   :doc
   "Deprecated - this function will be removed. Use transducer instead",
   :var-type "function",
   :line 966,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([f ch]),
   :name "map>",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L997",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/map>",
   :doc
   "Deprecated - this function will be removed. Use transducer instead",
   :var-type "function",
   :line 997,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([f in] [f in buf-or-n]),
   :name "mapcat<",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L1063",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/mapcat<",
   :doc
   "Deprecated - this function will be removed. Use transducer instead",
   :var-type "function",
   :line 1063,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([f out] [f out buf-or-n]),
   :name "mapcat>",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L1071",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/mapcat>",
   :doc
   "Deprecated - this function will be removed. Use transducer instead",
   :var-type "function",
   :line 1071,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([chs] [chs buf-or-n]),
   :name "merge",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L921",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/merge",
   :doc
   "Takes a collection of source channels and returns a channel which\ncontains all values taken from them. The returned channel will be\nunbuffered by default, or a buf-or-n can be supplied. The channel\nwill close after all the source channels have closed.",
   :var-type "function",
   :line 921,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([out]),
   :name "mix",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L703",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/mix",
   :doc
   "Creates and returns a mix of one or more input channels which will\nbe put on the supplied out channel. Input sources can be added to\nthe mix with 'admix', and removed with 'unmix'. A mix supports\nsoloing, muting and pausing multiple inputs atomically using\n'toggle', and can solo using either muting or pausing as determined\nby 'solo-mode'.\n\nEach channel can have zero or more boolean modes set via 'toggle':\n\n:solo - when true, only this (ond other soloed) channel(s) will appear\n        in the mix output channel. :mute and :pause states of soloed\n        channels are ignored. If solo-mode is :mute, non-soloed\n        channels are muted, if :pause, non-soloed channels are\n        paused.\n\n:mute - muted channels will have their contents consumed but not included in the mix\n:pause - paused channels will not have their contents consumed (and thus also not included in the mix)",
   :var-type "function",
   :line 703,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([ch]),
   :name "mult",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L636",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/mult",
   :doc
   "Creates and returns a mult(iple) of the supplied channel. Channels\ncontaining copies of the channel can be created with 'tap', and\ndetached with 'untap'.\n\nEach item is distributed to all taps in parallel and synchronously,\ni.e. each tap must accept before the next item is distributed. Use\nbuffering/windowing to prevent slow taps from holding up the mult.\n\nItems received when there are no taps get dropped.\n\nIf a tap puts to a closed channel, it will be removed from the mult.",
   :var-type "function",
   :line 636,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([port val]),
   :name "offer!",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L373",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/offer!",
   :doc
   "Puts a val into port if it's possible to do so immediately.\nnil values are not allowed. Never blocks. Returns true if offer succeeds.",
   :var-type "function",
   :line 373,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([ch coll] [ch coll close?]),
   :name "onto-chan",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L605",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/onto-chan",
   :doc
   "Puts the contents of coll into the supplied channel.\n\nBy default the channel will be closed after the items are copied,\nbut can be determined by the close? parameter.\n\nReturns a channel which will close after the items are copied.",
   :var-type "function",
   :line 605,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([n ch] [n ch buf-or-n]),
   :name "partition",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L1097",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/partition",
   :doc
   "Deprecated - this function will be removed. Use transducer instead",
   :var-type "function",
   :line 1097,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([f ch] [f ch buf-or-n]),
   :name "partition-by",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L1121",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/partition-by",
   :doc
   "Deprecated - this function will be removed. Use transducer instead",
   :var-type "function",
   :line 1121,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([from to] [from to close?]),
   :name "pipe",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L444",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/pipe",
   :doc
   "Takes elements from the from channel and supplies them to the to\nchannel. By default, the to channel will be closed when the from\nchannel closes, but can be determined by the close?  parameter. Will\nstop consuming the from channel if the to channel closes",
   :var-type "function",
   :line 444,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists
   ([n to xf from]
    [n to xf from close?]
    [n to xf from close? ex-handler]),
   :name "pipeline",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L518",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/pipeline",
   :doc
   "Takes elements from the from channel and supplies them to the to\nchannel, subject to the transducer xf, with parallelism n. Because\nit is parallel, the transducer will be applied independently to each\nelement, not across elements, and may produce zero or more outputs\nper input.  Outputs will be returned in order relative to the\ninputs. By default, the to channel will be closed when the from\nchannel closes, but can be determined by the close?  parameter. Will\nstop consuming the from channel if the to channel closes. Note this\nshould be used for computational parallelism. If you have multiple\nblocking operations to put in flight, use pipeline-blocking instead,\nIf you have multiple asynchronous operations to put in flight, use\npipeline-async instead.",
   :var-type "function",
   :line 518,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([n to af from] [n to af from close?]),
   :name "pipeline-async",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L541",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/pipeline-async",
   :doc
   "Takes elements from the from channel and supplies them to the to\nchannel, subject to the async function af, with parallelism n. af\nmust be a function of two arguments, the first an input value and\nthe second a channel on which to place the result(s). af must close!\nthe channel before returning.  The presumption is that af will\nreturn immediately, having launched some asynchronous operation\n(i.e. in another thread) whose completion/callback will manipulate\nthe result channel. Outputs will be returned in order relative to\nthe inputs. By default, the to channel will be closed when the from\nchannel closes, but can be determined by the close?  parameter. Will\nstop consuming the from channel if the to channel closes. See also\npipeline, pipeline-blocking.",
   :var-type "function",
   :line 541,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists
   ([n to xf from]
    [n to xf from close?]
    [n to xf from close? ex-handler]),
   :name "pipeline-blocking",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L535",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/pipeline-blocking",
   :doc "Like pipeline, for blocking operations.",
   :var-type "function",
   :line 535,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([port]),
   :name "poll!",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L380",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/poll!",
   :doc
   "Takes a val from port if it's possible to do so immediately.\nNever blocks. Returns value if successful, nil otherwise.",
   :var-type "function",
   :line 380,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([ch topic-fn] [ch topic-fn buf-fn]),
   :name "pub",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L810",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/pub",
   :doc
   "Creates and returns a pub(lication) of the supplied channel,\npartitioned into topics by the topic-fn. topic-fn will be applied to\neach value on the channel and the result will determine the 'topic'\non which that value will be put. Channels can be subscribed to\nreceive copies of topics using 'sub', and unsubscribed using\n'unsub'. Each topic will be handled by an internal mult on a\ndedicated channel. By default these internal channels are\nunbuffered, but a buf-fn can be supplied which, given a topic,\ncreates a buffer with desired properties.\n\nEach item is distributed to all subs in parallel and synchronously,\ni.e. each sub must accept before the next item is distributed. Use\nbuffering/windowing to prevent slow subs from holding up the pub.\n\nItems received when there are no matching subs get dropped.\n\nNote that if buf-fns are used then each topic is handled\nasynchronously, i.e. if a channel is subscribed to more than one\ntopic it should not expect them to be interleaved identically with\nthe source.",
   :var-type "function",
   :line 810,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([port val] [port val fn1] [port val fn1 on-caller?]),
   :name "put!",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L141",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/put!",
   :doc
   "Asynchronously puts a val into port, calling fn1 (if supplied) when\ncomplete, passing false iff port is already closed. nil values are\nnot allowed. If on-caller? (default true) is true, and the put is\nimmediately accepted, will call fn1 on calling thread.  Returns\ntrue unless port is already closed.",
   :var-type "function",
   :line 141,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([f init ch]),
   :name "reduce",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L578",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/reduce",
   :doc
   "f should be a function of 2 arguments. Returns a channel containing\nthe single result of applying f to init and the first item from the\nchannel, then applying f to that result and the 2nd item, etc. If\nthe channel closes without yielding items, returns init and f is not\ncalled. ch must close before reduce produces a result.",
   :var-type "function",
   :line 578,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([p ch] [p ch buf-or-n]),
   :name "remove<",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L1048",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/remove<",
   :doc
   "Deprecated - this function will be removed. Use transducer instead",
   :var-type "function",
   :line 1048,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([p ch]),
   :name "remove>",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L1029",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/remove>",
   :doc
   "Deprecated - this function will be removed. Use transducer instead",
   :var-type "function",
   :line 1029,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([n]),
   :name "sliding-buffer",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L56",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/sliding-buffer",
   :doc
   "Returns a buffer of size n. When full, puts will complete, and be\nbuffered, but oldest elements in buffer will be dropped (not\ntransferred).",
   :var-type "function",
   :line 56,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([mix mode]),
   :name "solo-mode",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L800",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/solo-mode",
   :doc
   "Sets the solo mode of the mix. mode must be one of :mute or :pause",
   :var-type "function",
   :line 800,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([p ch] [p ch t-buf-or-n f-buf-or-n]),
   :name "split",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L557",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/split",
   :doc
   "Takes a predicate and a source channel and returns a vector of two\nchannels, the first of which will contain the values for which the\npredicate returned true, the second those for which it returned\nfalse.\n\nThe out channels will be unbuffered by default, or two buf-or-ns can\nbe supplied. The channels will close after the source channel has\nclosed.",
   :var-type "function",
   :line 557,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([p topic ch] [p topic ch close?]),
   :name "sub",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L865",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/sub",
   :doc
   "Subscribes a channel to a topic of a pub.\n\nBy default the channel will be closed when the source closes,\nbut can be determined by the close? parameter.",
   :var-type "function",
   :line 865,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([n ch] [n ch buf-or-n]),
   :name "take",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L947",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/take",
   :doc
   "Returns a channel that will return, at most, n items from ch. After n items\n have been returned, or ch has been closed, the return channel will close.\n\nThe output channel is unbuffered by default, unless buf-or-n is given.",
   :var-type "function",
   :line 947,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([port fn1] [port fn1 on-caller?]),
   :name "take!",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L106",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/take!",
   :doc
   "Asynchronously takes a val from port, passing to fn1. Will pass nil\nif closed. If on-caller? (default true) is true, and value is\nimmediately available, will call fn1 on calling thread.\nReturns nil.",
   :var-type "function",
   :line 106,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([mult ch] [mult ch close?]),
   :name "tap",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L679",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/tap",
   :doc
   "Copies the mult source onto the supplied channel.\n\nBy default the channel will be closed when the source closes,\nbut can be determined by the close? parameter.",
   :var-type "function",
   :line 679,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([& body]),
   :name "thread",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L430",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/thread",
   :doc
   "Executes the body in another thread, returning immediately to the\ncalling thread. Returns a channel which will receive the result of\nthe body when completed.",
   :var-type "macro",
   :line 430,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([f]),
   :name "thread-call",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L412",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/thread-call",
   :doc
   "Executes f in another thread, returning immediately to the calling\nthread. Returns a channel which will receive the result of calling\nf when completed.",
   :var-type "function",
   :line 412,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([msecs]),
   :name "timeout",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L85",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/timeout",
   :doc "Returns a channel that will close after msecs",
   :var-type "function",
   :line 85,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([coll]),
   :name "to-chan",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L620",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/to-chan",
   :doc
   "Creates and returns a channel which contains the contents of coll,\nclosing when exhausted.",
   :var-type "function",
   :line 620,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([mix state-map]),
   :name "toggle",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L788",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/toggle",
   :doc
   "Atomically sets the state(s) of one or more channels in a mix. The\nstate map is a map of channels -> channel-state-map. A\nchannel-state-map is a map of attrs -> boolean, where attr is one or\nmore of :mute, :pause or :solo. Any states supplied are merged with\nthe current state.\n\nNote that channels can be added to a mix via toggle, which can be\nused to add channels in a particular (e.g. paused) state.",
   :var-type "function",
   :line 788,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([buff]),
   :name "unblocking-buffer?",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L63",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/unblocking-buffer?",
   :doc
   "Returns true if a channel created with buff will never block. That is to say,\nputs into this buffer will never cause the buffer to be full. ",
   :var-type "function",
   :line 63,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([ch] [ch buf-or-n]),
   :name "unique",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L1080",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/unique",
   :doc
   "Deprecated - this function will be removed. Use transducer instead",
   :var-type "function",
   :line 1080,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([mix ch]),
   :name "unmix",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L778",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/unmix",
   :doc "Removes ch as an input to the mix",
   :var-type "function",
   :line 778,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([mix]),
   :name "unmix-all",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L783",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/unmix-all",
   :doc "removes all inputs from the mix",
   :var-type "function",
   :line 783,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([p topic ch]),
   :name "unsub",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L873",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/unsub",
   :doc "Unsubscribes a channel from a topic of a pub",
   :var-type "function",
   :line 873,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([p] [p topic]),
   :name "unsub-all",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L878",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/unsub-all",
   :doc "Unsubscribes all channels from a pub, or a topic of a pub",
   :var-type "function",
   :line 878,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([mult ch]),
   :name "untap",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L687",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/untap",
   :doc "Disconnects a target channel from a mult",
   :var-type "function",
   :line 687,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([mult]),
   :name "untap-all",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj#L692",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/d073896192fa55fab992eb4c9ea57b86ec5cf076/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/untap-all",
   :doc "Disconnects all target channels from a mult",
   :var-type "function",
   :line 692,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([write-ports]),
   :name "->BroadcastingWritePort",
   :namespace "clojure.core.async.lab",
   :source-url
   "https://github.com/clojure/core.async/blob/ad7ca68a831a1e469b6f30334903279b2e4e43f7/src/main/clojure/clojure/core/async/lab.clj#L93",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/ad7ca68a831a1e469b6f30334903279b2e4e43f7/src/main/clojure/clojure/core/async/lab.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.lab/->BroadcastingWritePort",
   :doc
   "Positional factory function for class clojure.core.async.lab.BroadcastingWritePort.",
   :var-type "function",
   :line 93,
   :file "src/main/clojure/clojure/core/async/lab.clj"}
  {:arglists ([mutex read-ports]),
   :name "->MultiplexingReadPort",
   :namespace "clojure.core.async.lab",
   :source-url
   "https://github.com/clojure/core.async/blob/ad7ca68a831a1e469b6f30334903279b2e4e43f7/src/main/clojure/clojure/core/async/lab.clj#L33",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/ad7ca68a831a1e469b6f30334903279b2e4e43f7/src/main/clojure/clojure/core/async/lab.clj",
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
   "https://github.com/clojure/core.async/blob/ad7ca68a831a1e469b6f30334903279b2e4e43f7/src/main/clojure/clojure/core/async/lab.clj#L99",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/ad7ca68a831a1e469b6f30334903279b2e4e43f7/src/main/clojure/clojure/core/async/lab.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.lab/broadcast",
   :doc
   "Returns a broadcasting write port which, when written to, writes\nthe value to each of ports.\n\nWrites to the broadcasting port will park until the value is written\nto each of the ports used to create it. For this reason, it is\nstrongly advised that each of the underlying ports support buffered\nwrites.",
   :var-type "function",
   :line 99,
   :file "src/main/clojure/clojure/core/async/lab.clj"}
  {:arglists ([& ports]),
   :name "multiplex",
   :namespace "clojure.core.async.lab",
   :source-url
   "https://github.com/clojure/core.async/blob/ad7ca68a831a1e469b6f30334903279b2e4e43f7/src/main/clojure/clojure/core/async/lab.clj#L69",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/ad7ca68a831a1e469b6f30334903279b2e4e43f7/src/main/clojure/clojure/core/async/lab.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.lab/multiplex",
   :doc
   "Returns a multiplexing read port which, when read from, produces a\nvalue from one of ports.\n\nIf at read time only one port is available to be read from, the\nmultiplexing port will return that value. If multiple ports are\navailable to be read from, the multiplexing port will return one\nvalue from a port chosen non-deterministicly. If no port is\navailable to be read from, parks execution until a value is\navailable.",
   :var-type "function",
   :line 69,
   :file "src/main/clojure/clojure/core/async/lab.clj"}
  {:arglists ([s c] [s]),
   :name "spool",
   :namespace "clojure.core.async.lab",
   :source-url
   "https://github.com/clojure/core.async/blob/ad7ca68a831a1e469b6f30334903279b2e4e43f7/src/main/clojure/clojure/core/async/lab.clj#L112",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/ad7ca68a831a1e469b6f30334903279b2e4e43f7/src/main/clojure/clojure/core/async/lab.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async.lab/spool",
   :doc
   "Take a sequence and puts each value on a channel and returns the channel.\nIf no channel is provided, an unbuffered channel is created. If the\nsequence ends, the channel is closed.",
   :var-type "function",
   :line 112,
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
