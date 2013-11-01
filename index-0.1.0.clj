{:namespaces
 ({:source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
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
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L87",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/<!",
   :doc
   "takes a val from port. Must be called inside a (go ...) block. Will\nreturn nil if closed. Will park if nothing is available.",
   :var-type "function",
   :line 87,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([port]),
   :name "<!!",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L77",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/<!!",
   :doc
   "takes a val from port. Will return nil if closed. Will block\nif nothing is available.",
   :var-type "function",
   :line 77,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([port val]),
   :name ">!",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L118",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/>!",
   :doc
   "puts a val into port. nil values are not allowed. Must be called\ninside a (go ...) block. Will park if no buffer space is available.",
   :var-type "function",
   :line 118,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([port val]),
   :name ">!!",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L108",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/>!!",
   :doc
   "puts a val into port. nil values are not allowed. Will block if no\nbuffer space is available. Returns nil.",
   :var-type "function",
   :line 108,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([mix ch]),
   :name "admix",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L770",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/admix",
   :doc "Adds ch as an input to the mix",
   :var-type "function",
   :line 770,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([& clauses]),
   :name "alt!",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L304",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/alt!",
   :doc
   "Makes a single choice between one of several channel operations,\nas if by alts!, returning the value of the result expr corresponding\nto the operation completed. Must be called inside a (go ...) block.\n\nEach clause takes the form of:\n\nchannel-op[s] result-expr\n\nwhere channel-ops is one of:\n\ntake-port - a single port to take\n[take-port | [put-port put-val] ...] - a vector of ports as per alts!\n:default | :priority - an option for alts!\n\nand result-expr is either a list beginning with a vector, whereupon that\nvector will be treated as a binding for the [val port] return of the\noperation, else any other expression.\n\n(alt!\n  [c t] ([val ch] (foo ch val))\n  x ([v] v)\n  [[out val]] :wrote\n  :default 42)\n\nEach option may appear at most once. The choice and parking\ncharacteristics are those of alts!.",
   :var-type "macro",
   :line 304,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([& clauses]),
   :name "alt!!",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L297",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/alt!!",
   :doc
   "Like alt!, except as if by alts!!, will block until completed, and\nnot intended for use in (go ...) blocks.",
   :var-type "macro",
   :line 297,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([ports & {:as opts}]),
   :name "alts!",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L234",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/alts!",
   :doc
   "Completes at most one of several channel operations. Must be called\ninside a (go ...) block. ports is a vector of channel endpoints, which\ncan be either a channel to take from or a vector of\n[channel-to-put-to val-to-put], in any combination. Takes will be\nmade as if by <!, and puts will be made as if by >!. Unless\nthe :priority option is true, if more than one port operation is\nready a non-deterministic choice will be made. If no operation is\nready and a :default value is supplied, [default-val :default] will\nbe returned, otherwise alts! will park until the first operation to\nbecome ready completes. Returns [val port] of the completed\noperation, where val is the value taken for takes, and nil for puts.\n\nopts are passed as :key val ... Supported options:\n\n:default val - the value to use if none of the operations are immediately ready\n:priority true - (default nil) when true, the operations will be tried in order.\n\nNote: there is no guarantee that the port exps or val exprs will be\nused, nor in what order should they be, so they should not be\ndepended upon for side effects.",
   :var-type "function",
   :line 234,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([ports & {:as opts}]),
   :name "alts!!",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L223",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/alts!!",
   :doc
   "Like alts!, except takes will be made as if by <!!, and puts will\nbe made as if by >!!, will block until completed, and not intended\nfor use in (go ...) blocks.",
   :var-type "function",
   :line 223,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([n]),
   :name "buffer",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L42",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/buffer",
   :doc
   "Returns a fixed buffer of size n. When full, puts will block/park.",
   :var-type "function",
   :line 42,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([] [buf-or-n]),
   :name "chan",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L66",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/chan",
   :doc
   "Creates a channel with an optional buffer. If buf-or-n is a number,\nwill create and use a fixed buffer of that size.",
   :var-type "function",
   :line 66,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([chan]),
   :name "close!",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L141",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/close!",
   :doc
   "Closes a channel. The channel will no longer accept any puts (they\nwill be ignored). Data in the channel remains available for taking, until\nexhausted, after which takes will return nil. If there are any\npending takes, they will be dispatched with nil. Closing a closed\nchannel is a no-op. Returns nil.",
   :var-type "function",
   :line 141,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([fret ports opts]),
   :name "do-alts",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L194",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/do-alts",
   :doc "returns derefable [val port] if immediate, nil if enqueued",
   :var-type "function",
   :line 194,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([n]),
   :name "dropping-buffer",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L47",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/dropping-buffer",
   :doc
   "Returns a buffer of size n. When full, puts will complete but\nval will be dropped (no transfer).",
   :var-type "function",
   :line 47,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([p ch] [p ch buf-or-n]),
   :name "filter<",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L473",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/filter<",
   :doc
   "Takes a predicate and a source channel, and returns a channel which\ncontains only the values taken from the source channel for which the\npredicate returns true. The returned channel will be unbuffered by\ndefault, or a buf-or-n can be supplied. The channel will close\nwhen the source channel closes.",
   :var-type "function",
   :line 473,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([p ch]),
   :name "filter>",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L448",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/filter>",
   :doc
   "Takes a predicate and a target channel, and returns a channel which\nsupplies only the values for which the predicate returns true to the\ntarget channel.",
   :var-type "function",
   :line 448,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([& body]),
   :name "go",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L347",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/go",
   :doc
   "Asynchronously executes the body, returning immediately to the\ncalling thread. Additionally, any visible calls to <!, >! and alt!/alts!\nchannel operations within the body will block (if necessary) by\n'parking' the calling thread rather than tying up an OS thread (or\nthe only JS thread when in ClojureScript). Upon completion of the\noperation, the body will be resumed.\n\nReturns a channel which will receive the result of the body when\ncompleted",
   :var-type "macro",
   :line 347,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([bindings & body]),
   :name "go-loop",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L443",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/go-loop",
   :doc "Like (go (loop ...))",
   :var-type "macro",
   :line 443,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([coll ch]),
   :name "into",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L938",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/into",
   :doc
   "Returns a channel containing the single (collection) result of the\nitems taken from the channel conjoined to the supplied\ncollection. ch must close before into produces a result.",
   :var-type "function",
   :line 938,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([f chs] [f chs buf-or-n]),
   :name "map",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L884",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/map",
   :doc
   "Takes a function and a collection of source channels, and returns a\nchannel which contains the values produced by applying f to the set\nof first items taken from each source channel, followed by applying\nf to the set of second items from each channel, until any one of the\nchannels is closed, at which point the output channel will be\nclosed. The returned channel will be unbuffered by default, or a\nbuf-or-n can be supplied",
   :var-type "function",
   :line 884,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([f ch]),
   :name "map<",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L397",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/map<",
   :doc
   "Takes a function and a source channel, and returns a channel which\ncontains the values produced by applying f to each value taken from\nthe source channel",
   :var-type "function",
   :line 397,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([f ch]),
   :name "map>",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L428",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/map>",
   :doc
   "Takes a function and a target channel, and returns a channel which\napplies f to each value before supplying it to the target channel.",
   :var-type "function",
   :line 428,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([f in] [f in buf-or-n]),
   :name "mapcat<",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L510",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/mapcat<",
   :doc
   "Takes a function and a source channel, and returns a channel which\ncontains the values in each collection produced by applying f to\neach value taken from the source channel. f must return a\ncollection.\n\nThe returned channel will be unbuffered by default, or a buf-or-n\ncan be supplied. The channel will close when the source channel\ncloses.",
   :var-type "function",
   :line 510,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([f out] [f out buf-or-n]),
   :name "mapcat>",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L525",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/mapcat>",
   :doc
   "Takes a function and a target channel, and returns a channel which\napplies f to each value put, then supplies each element of the result\nto the target channel. f must return a collection.\n\nThe returned channel will be unbuffered by default, or a buf-or-n\ncan be supplied. The target channel will be closed when the source\nchannel closes.",
   :var-type "function",
   :line 525,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([chs] [chs buf-or-n]),
   :name "merge",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L920",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/merge",
   :doc
   "Takes a collection of source channels and returns a channel which\ncontains all values taken from them. The returned channel will be\nunbuffered by default, or a buf-or-n can be supplied. The channel\nwill close after all the source channels have closed.",
   :var-type "function",
   :line 920,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([out]),
   :name "mix",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L701",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/mix",
   :doc
   "Creates and returns a mix of one or more input channels which will\nbe put on the supplied out channel. Input sources can be added to\nthe mix with 'admix', and removed with 'unmix'. A mix supports\nsoloing, muting and pausing multiple inputs atomically using\n'toggle', and can solo using either muting or pausing as determined\nby 'solo-mode'.\n\nEach channel can have zero or more boolean modes set via 'toggle':\n\n:solo - when true, only this (ond other soloed) channel(s) will appear\n        in the mix output channel. :mute and :pause states of soloed\n        channels are ignored. If solo-mode is :mute, non-soloed\n        channels are muted, if :pause, non-soloed channels are\n        paused.\n\n:mute - muted channels will have their contents consumed but not included in the mix\n:pause - paused channels will not have their contents consumed (and thus also not included in the mix)",
   :var-type "function",
   :line 701,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([ch]),
   :name "mult",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L632",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/mult",
   :doc
   "Creates and returns a mult(iple) of the supplied channel. Channels\ncontaining copies of the channel can be created with 'tap', and\ndetached with 'untap'.\n\nEach item is distributed to all taps in parallel and synchronously,\ni.e. each tap must accept before the next item is distributed. Use\nbuffering/windowing to prevent slow taps from holding up the mult.\n\nItems received when there are no taps get dropped.\n\nIf a tap put throws an exception, it will be removed from the mult.",
   :var-type "function",
   :line 632,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([ch coll] [ch coll close?]),
   :name "onto-chan",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L600",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/onto-chan",
   :doc
   "Puts the contents of coll into the supplied channel.\n\nBy default the channel will be closed after the items are copied,\nbut can be determined by the close? parameter.\n\nReturns a channel which will close after the items are copied.",
   :var-type "function",
   :line 600,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([n ch] [n ch buf-or-n]),
   :name "partition",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L985",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/partition",
   :doc
   "Returns a channel that will contain vectors of n items taken from ch. The\nfinal vector in the return channel may be smaller than n if ch closed before\nthe vector could be completely filled.\n\nThe output channel is unbuffered by default, unless buf-or-n is given",
   :var-type "function",
   :line 985,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([f ch] [f ch buf-or-n]),
   :name "partition-by",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L1013",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/partition-by",
   :doc
   "Returns a channel that will contain vectors of items taken from ch. New\n vectors will be created whenever (f itm) returns a value that differs from\n the previous item's (f itm).\n\nThe output channel is unbuffered, unless buf-or-n is given",
   :var-type "function",
   :line 1013,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([from to] [from to close?]),
   :name "pipe",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L540",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/pipe",
   :doc
   "Takes elements from the from channel and supplies them to the to\nchannel. By default, the to channel will be closed when the\nfrom channel closes, but can be determined by the close?\nparameter.",
   :var-type "function",
   :line 540,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([ch topic-fn] [ch topic-fn buf-fn]),
   :name "pub",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L807",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/pub",
   :doc
   "Creates and returns a pub(lication) of the supplied channel,\npartitioned into topics by the topic-fn. topic-fn will be applied to\neach value on the channel and the result will determine the 'topic'\non which that value will be put. Channels can be subscribed to\nreceive copies of topics using 'sub', and unsubscribed using\n'unsub'. Each topic will be handled by an internal mult on a\ndedicated channel. By default these internal channels are\nunbuffered, but a buf-fn can be supplied which, given a topic,\ncreates a buffer with desired properties.\n\nEach item is distributed to all subs in parallel and synchronously,\ni.e. each sub must accept before the next item is distributed. Use\nbuffering/windowing to prevent slow subs from holding up the pub.\n\nItems received when there are no matching subs get dropped.\n\nNote that if buf-fns are used then each topic is handled\nasynchronously, i.e. if a channel is subscribed to more than one\ntopic it should not expect them to be interleaved identically with\nthe source.",
   :var-type "function",
   :line 807,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([port val] [port val fn0] [port val fn0 on-caller?]),
   :name "put!",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L126",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/put!",
   :doc
   "Asynchronously puts a val into port, calling fn0 (if supplied) when\ncomplete. nil values are not allowed. Will throw if closed. If\non-caller? (default true) is true, and the put is immediately\naccepted, will call fn0 on calling thread.  Returns nil.",
   :var-type "function",
   :line 126,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([f init ch]),
   :name "reduce",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L576",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/reduce",
   :doc
   "f should be a function of 2 arguments. Returns a channel containing\nthe single result of applying f to init and the first item from the\nchannel, then applying f to that result and the 2nd item, etc. If\nthe channel closes without yielding items, returns init and f is not\ncalled. ch must close before reduce produces a result.",
   :var-type "function",
   :line 576,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([p ch] [p ch buf-or-n]),
   :name "remove<",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L491",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/remove<",
   :doc
   "Takes a predicate and a source channel, and returns a channel which\ncontains only the values taken from the source channel for which the\npredicate returns false. The returned channel will be unbuffered by\ndefault, or a buf-or-n can be supplied. The channel will close\nwhen the source channel closes.",
   :var-type "function",
   :line 491,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([p ch]),
   :name "remove>",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L466",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/remove>",
   :doc
   "Takes a predicate and a target channel, and returns a channel which\nsupplies only the values for which the predicate returns false to the\ntarget channel.",
   :var-type "function",
   :line 466,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([n]),
   :name "sliding-buffer",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L53",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/sliding-buffer",
   :doc
   "Returns a buffer of size n. When full, puts will complete, and be\nbuffered, but oldest elements in buffer will be dropped (not\ntransferred).",
   :var-type "function",
   :line 53,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([mix mode]),
   :name "solo-mode",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L797",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/solo-mode",
   :doc
   "Sets the solo mode of the mix. mode must be one of :mute or :pause",
   :var-type "function",
   :line 797,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([p ch] [p ch t-buf-or-n f-buf-or-n]),
   :name "split",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L555",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/split",
   :doc
   "Takes a predicate and a source channel and returns a vector of two\nchannels, the first of which will contain the values for which the\npredicate returned true, the second those for which it returned\nfalse.\n\nThe out channels will be unbuffered by default, or two buf-or-ns can\nbe supplied. The channels will close after the source channel has\nclosed.",
   :var-type "function",
   :line 555,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([p topic ch] [p topic ch close?]),
   :name "sub",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L864",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/sub",
   :doc
   "Subscribes a channel to a topic of a pub.\n\nBy default the channel will be closed when the source closes,\nbut can be determined by the close? parameter.",
   :var-type "function",
   :line 864,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([n ch] [n ch buf-or-n]),
   :name "take",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L946",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/take",
   :doc
   "Returns a channel that will return, at most, n items from ch. After n items\n have been returned, or ch has been closed, the return chanel will close.\n\nThe output channel is unbuffered by default, unless buf-or-n is given.",
   :var-type "function",
   :line 946,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([port fn1] [port fn1 on-caller?]),
   :name "take!",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L93",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/take!",
   :doc
   "Asynchronously takes a val from port, passing to fn1. Will pass nil\nif closed. If on-caller? (default true) is true, and value is\nimmediately available, will call fn1 on calling thread.\nReturns nil.",
   :var-type "function",
   :line 93,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([mult ch] [mult ch close?]),
   :name "tap",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L677",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/tap",
   :doc
   "Copies the mult source onto the supplied channel.\n\nBy default the channel will be closed when the source closes,\nbut can be determined by the close? parameter.",
   :var-type "function",
   :line 677,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([& body]),
   :name "thread",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L388",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/thread",
   :doc
   "Executes the body in another thread, returning immediately to the\ncalling thread. Returns a channel which will receive the result of\nthe body when completed.",
   :var-type "macro",
   :line 388,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([f]),
   :name "thread-call",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L372",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/thread-call",
   :doc
   "Executes f in another thread, returning immediately to the calling\nthread. Returns a channel which will receive the result of calling\nf when completed.",
   :var-type "function",
   :line 372,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([msecs]),
   :name "timeout",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L72",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/timeout",
   :doc "Returns a channel that will close after msecs",
   :var-type "function",
   :line 72,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([coll]),
   :name "to-chan",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L616",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/to-chan",
   :doc
   "Creates and returns a channel which contains the contents of coll,\nclosing when exhausted.",
   :var-type "function",
   :line 616,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([mix state-map]),
   :name "toggle",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L785",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/toggle",
   :doc
   "Atomically sets the state(s) of one or more channels in a mix. The\nstate map is a map of channels -> channel-state-map. A\nchannel-state-map is a map of attrs -> boolean, where attr is one or\nmore of :mute, :pause or :solo. Any states supplied are merged with\nthe current state.\n\nNote that channels can be added to a mix via toggle, which can be\nused to add channels in a particular (e.g. paused) state.",
   :var-type "function",
   :line 785,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([buff]),
   :name "unblocking-buffer?",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L60",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/unblocking-buffer?",
   :doc
   "Returns true if a channel created with buff will never block. That is to say,\nputs into this buffer will never cause the buffer to be full. ",
   :var-type "function",
   :line 60,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([ch] [ch buf-or-n]),
   :name "unique",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L965",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/unique",
   :doc
   "Returns a channel that will contain values from ch. Consecutive duplicate\n values will be dropped.\n\nThe output channel is unbuffered by default, unless buf-or-n is given.",
   :var-type "function",
   :line 965,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([mix ch]),
   :name "unmix",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L775",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/unmix",
   :doc "Removes ch as an input to the mix",
   :var-type "function",
   :line 775,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([mix]),
   :name "unmix-all",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L780",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/unmix-all",
   :doc "removes all inputs from the mix",
   :var-type "function",
   :line 780,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([p topic ch]),
   :name "unsub",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L872",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/unsub",
   :doc "Unsubscribes a channel from a topic of a pub",
   :var-type "function",
   :line 872,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([p] [p topic]),
   :name "unsub-all",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L877",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/unsub-all",
   :doc "Unsubscribes all channels from a pub, or a topic of a pub",
   :var-type "function",
   :line 877,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([mult ch]),
   :name "untap",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L685",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/untap",
   :doc "Disconnects a target channel from a mult",
   :var-type "function",
   :line 685,
   :file "src/main/clojure/clojure/core/async.clj"}
  {:arglists ([mult]),
   :name "untap-all",
   :namespace "clojure.core.async",
   :source-url
   "https://github.com/clojure/core.async/blob/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj#L690",
   :raw-source-url
   "https://github.com/clojure/core.async/raw/76317035d386ce2a1d98c2c349da9b898b480c55/src/main/clojure/clojure/core/async.clj",
   :wiki-url
   "http://clojure.github.com/core.async//clojure.core.async-api.html#clojure.core.async/untap-all",
   :doc "Disconnects all target channels from a mult",
   :var-type "function",
   :line 690,
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
