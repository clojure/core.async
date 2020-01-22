{:namespaces
 ({:doc
   "Facilities for async programming and communication.\n\ngo blocks are dispatched over an internal thread pool, which\ndefaults to 8 threads. The size of this pool can be modified using\nthe Java system property `clojure.core.async.pool-size`.\n\nSet Java system property `clojure.core.async.go-checking` to true\nto validate go blocks do not invoke core.async blocking operations.\nProperty is read once, at namespace load time. Recommended for use\nprimarily during development. Invalid blocking calls will throw in\ngo block threads - use Thread.setDefaultUncaughtExceptionHandler()\nto catch and handle.",
   :name "clojure.core.async",
   :wiki-url "https://clojure.github.io/core.async/index.html",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj"}),
 :vars
 ({:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "<!",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L138",
   :line 138,
   :var-type "function",
   :arglists ([port]),
   :doc
   "takes a val from port. Must be called inside a (go ...) block. Will\nreturn nil if closed. Will park if nothing is available.",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/<!"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "<!!",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L125",
   :line 125,
   :var-type "function",
   :arglists ([port]),
   :doc
   "takes a val from port. Will return nil if closed. Will block\nif nothing is available.\nNot intended for use in direct or transitive calls from (go ...) blocks.\nUse the clojure.core.async.go-checking flag to detect invalid use (see\nnamespace docs).",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/<!!"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name ">!",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L177",
   :line 177,
   :var-type "function",
   :arglists ([port val]),
   :doc
   "puts a val into port. nil values are not allowed. Must be called\ninside a (go ...) block. Will park if no buffer space is available.\nReturns true unless port is already closed.",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/>!"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name ">!!",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L164",
   :line 164,
   :var-type "function",
   :arglists ([port val]),
   :doc
   "puts a val into port. nil values are not allowed. Will block if no\nbuffer space is available. Returns true unless port is already closed.\nNot intended for use in direct or transitive calls from (go ...) blocks.\nUse the clojure.core.async.go-checking flag to detect invalid use (see\nnamespace docs).",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/>!!"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "admix",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L844",
   :line 844,
   :var-type "function",
   :arglists ([mix ch]),
   :doc "Adds ch as an input to the mix",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/admix"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "alt!",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L385",
   :line 385,
   :var-type "macro",
   :arglists ([& clauses]),
   :doc
   "Makes a single choice between one of several channel operations,\nas if by alts!, returning the value of the result expr corresponding\nto the operation completed. Must be called inside a (go ...) block.\n\nEach clause takes the form of:\n\nchannel-op[s] result-expr\n\nwhere channel-ops is one of:\n\ntake-port - a single port to take\n[take-port | [put-port put-val] ...] - a vector of ports as per alts!\n:default | :priority - an option for alts!\n\nand result-expr is either a list beginning with a vector, whereupon that\nvector will be treated as a binding for the [val port] return of the\noperation, else any other expression.\n\n(alt!\n  [c t] ([val ch] (foo ch val))\n  x ([v] v)\n  [[out val]] :wrote\n  :default 42)\n\nEach option may appear at most once. The choice and parking\ncharacteristics are those of alts!.",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/alt!"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "alt!!",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L378",
   :line 378,
   :var-type "macro",
   :arglists ([& clauses]),
   :doc
   "Like alt!, except as if by alts!!, will block until completed, and\nnot intended for use in (go ...) blocks.",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/alt!!"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "alts!",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L314",
   :line 314,
   :var-type "function",
   :arglists ([ports & {:as opts}]),
   :doc
   "Completes at most one of several channel operations. Must be called\ninside a (go ...) block. ports is a vector of channel endpoints,\nwhich can be either a channel to take from or a vector of\n[channel-to-put-to val-to-put], in any combination. Takes will be\nmade as if by <!, and puts will be made as if by >!. Unless\nthe :priority option is true, if more than one port operation is\nready a non-deterministic choice will be made. If no operation is\nready and a :default value is supplied, [default-val :default] will\nbe returned, otherwise alts! will park until the first operation to\nbecome ready completes. Returns [val port] of the completed\noperation, where val is the value taken for takes, and a\nboolean (true unless already closed, as per put!) for puts.\n\nopts are passed as :key val ... Supported options:\n\n:default val - the value to use if none of the operations are immediately ready\n:priority true - (default nil) when true, the operations will be tried in order.\n\nNote: there is no guarantee that the port exps or val exprs will be\nused, nor in what order should they be, so they should not be\ndepended upon for side effects.",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/alts!"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "alts!!",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L301",
   :line 301,
   :var-type "function",
   :arglists ([ports & opts]),
   :doc
   "Like alts!, except takes will be made as if by <!!, and puts will\nbe made as if by >!!, will block until completed.\nNot intended for use in direct or transitive calls from (go ...) blocks.\nUse the clojure.core.async.go-checking flag to detect invalid use (see\nnamespace docs).",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/alts!!"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "buffer",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L56",
   :line 56,
   :var-type "function",
   :arglists ([n]),
   :doc
   "Returns a fixed buffer of size n. When full, puts will block/park.",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/buffer"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "chan",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L81",
   :line 81,
   :var-type "function",
   :arglists
   ([] [buf-or-n] [buf-or-n xform] [buf-or-n xform ex-handler]),
   :doc
   "Creates a channel with an optional buffer, an optional transducer\n(like (map f), (filter p) etc or a composition thereof), and an\noptional exception-handler.  If buf-or-n is a number, will create\nand use a fixed buffer of that size. If a transducer is supplied a\nbuffer must be specified. ex-handler must be a fn of one argument -\nif an exception occurs during transformation it will be called with\nthe Throwable as an argument, and any non-nil return value will be\nplaced in the channel.",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/chan"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "close!",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L212",
   :line 212,
   :var-type "function",
   :arglists ([chan]),
   :doc
   "Closes a channel. The channel will no longer accept any puts (they\nwill be ignored). Data in the channel remains available for taking, until\nexhausted, after which takes will return nil. If there are any\npending takes, they will be dispatched with nil. Closing a closed\nchannel is a no-op. Returns nil.\n\nLogically closing happens after all puts have been delivered. Therefore, any\nblocked or parked puts will remain blocked/parked until a taker releases them.",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/close!"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "do-alts",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L271",
   :line 271,
   :var-type "function",
   :arglists ([fret ports opts]),
   :doc "returns derefable [val port] if immediate, nil if enqueued",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/do-alts"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "dropping-buffer",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L62",
   :line 62,
   :var-type "function",
   :arglists ([n]),
   :doc
   "Returns a buffer of size n. When full, puts will complete but\nval will be dropped (no transfer).",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/dropping-buffer"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "go",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L441",
   :line 441,
   :var-type "macro",
   :arglists ([& body]),
   :doc
   "Asynchronously executes the body, returning immediately to the\ncalling thread. Additionally, any visible calls to <!, >! and alt!/alts!\nchannel operations within the body will block (if necessary) by\n'parking' the calling thread rather than tying up an OS thread (or\nthe only JS thread when in ClojureScript). Upon completion of the\noperation, the body will be resumed.\n\ngo blocks should not (either directly or indirectly) perform operations\nthat may block indefinitely. Doing so risks depleting the fixed pool of\ngo block threads, causing all go block processing to stop. This includes\ncore.async blocking ops (those ending in !!) and other blocking IO.\n\nReturns a channel which will receive the result of the body when\ncompleted",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/go"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "go-loop",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L500",
   :line 500,
   :var-type "macro",
   :arglists ([bindings & body]),
   :doc "Like (go (loop ...))",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/go-loop"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "into",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L1010",
   :line 1010,
   :var-type "function",
   :arglists ([coll ch]),
   :doc
   "Returns a channel containing the single (collection) result of the\nitems taken from the channel conjoined to the supplied\ncollection. ch must close before into produces a result.",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/into"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "map",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L956",
   :line 956,
   :var-type "function",
   :arglists ([f chs] [f chs buf-or-n]),
   :doc
   "Takes a function and a collection of source channels, and returns a\nchannel which contains the values produced by applying f to the set\nof first items taken from each source channel, followed by applying\nf to the set of second items from each channel, until any one of the\nchannels is closed, at which point the output channel will be\nclosed. The returned channel will be unbuffered by default, or a\nbuf-or-n can be supplied",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/map"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "merge",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L992",
   :line 992,
   :var-type "function",
   :arglists ([chs] [chs buf-or-n]),
   :doc
   "Takes a collection of source channels and returns a channel which\ncontains all values taken from them. The returned channel will be\nunbuffered by default, or a buf-or-n can be supplied. The channel\nwill close after all the source channels have closed.",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/merge"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "mix",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L774",
   :line 774,
   :var-type "function",
   :arglists ([out]),
   :doc
   "Creates and returns a mix of one or more input channels which will\nbe put on the supplied out channel. Input sources can be added to\nthe mix with 'admix', and removed with 'unmix'. A mix supports\nsoloing, muting and pausing multiple inputs atomically using\n'toggle', and can solo using either muting or pausing as determined\nby 'solo-mode'.\n\nEach channel can have zero or more boolean modes set via 'toggle':\n\n:solo - when true, only this (ond other soloed) channel(s) will appear\n        in the mix output channel. :mute and :pause states of soloed\n        channels are ignored. If solo-mode is :mute, non-soloed\n        channels are muted, if :pause, non-soloed channels are\n        paused.\n\n:mute - muted channels will have their contents consumed but not included in the mix\n:pause - paused channels will not have their contents consumed (and thus also not included in the mix)",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/mix"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "mult",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L708",
   :line 708,
   :var-type "function",
   :arglists ([ch]),
   :doc
   "Creates and returns a mult(iple) of the supplied channel. Channels\ncontaining copies of the channel can be created with 'tap', and\ndetached with 'untap'.\n\nEach item is distributed to all taps in parallel and synchronously,\ni.e. each tap must accept before the next item is distributed. Use\nbuffering/windowing to prevent slow taps from holding up the mult.\n\nItems received when there are no taps get dropped.\n\nIf a tap puts to a closed channel, it will be removed from the mult.",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/mult"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "offer!",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L427",
   :line 427,
   :var-type "function",
   :arglists ([port val]),
   :doc
   "Puts a val into port if it's possible to do so immediately.\nnil values are not allowed. Never blocks. Returns true if offer succeeds.",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/offer!"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "onto-chan",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L672",
   :line 672,
   :var-type "function",
   :arglists ([ch coll] [ch coll close?]),
   :doc
   "Puts the contents of coll into the supplied channel.\n\nBy default the channel will be closed after the items are copied,\nbut can be determined by the close? parameter.\n\nReturns a channel which will close after the items are copied.",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/onto-chan"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "pipe",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L505",
   :line 505,
   :var-type "function",
   :arglists ([from to] [from to close?]),
   :doc
   "Takes elements from the from channel and supplies them to the to\nchannel. By default, the to channel will be closed when the from\nchannel closes, but can be determined by the close?  parameter. Will\nstop consuming the from channel if the to channel closes",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/pipe"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "pipeline",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L575",
   :line 575,
   :var-type "function",
   :arglists
   ([n to xf from]
    [n to xf from close?]
    [n to xf from close? ex-handler]),
   :doc
   "Takes elements from the from channel and supplies them to the to\nchannel, subject to the transducer xf, with parallelism n. Because\nit is parallel, the transducer will be applied independently to each\nelement, not across elements, and may produce zero or more outputs\nper input.  Outputs will be returned in order relative to the\ninputs. By default, the to channel will be closed when the from\nchannel closes, but can be determined by the close?  parameter. Will\nstop consuming the from channel if the to channel closes. Note this\nshould be used for computational parallelism. If you have multiple\nblocking operations to put in flight, use pipeline-blocking instead,\nIf you have multiple asynchronous operations to put in flight, use\npipeline-async instead.",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/pipeline"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "pipeline-async",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L598",
   :line 598,
   :var-type "function",
   :arglists ([n to af from] [n to af from close?]),
   :doc
   "Takes elements from the from channel and supplies them to the to\nchannel, subject to the async function af, with parallelism n. af\nmust be a function of two arguments, the first an input value and\nthe second a channel on which to place the result(s). af must close!\nthe channel before returning.  The presumption is that af will\nreturn immediately, having launched some asynchronous operation\n(i.e. in another thread) whose completion/callback will manipulate\nthe result channel. Outputs will be returned in order relative to\nthe inputs. By default, the to channel will be closed when the from\nchannel closes, but can be determined by the close?  parameter. Will\nstop consuming the from channel if the to channel closes. See also\npipeline, pipeline-blocking.",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/pipeline-async"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "pipeline-blocking",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L592",
   :line 592,
   :var-type "function",
   :arglists
   ([n to xf from]
    [n to xf from close?]
    [n to xf from close? ex-handler]),
   :doc "Like pipeline, for blocking operations.",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/pipeline-blocking"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "poll!",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L434",
   :line 434,
   :var-type "function",
   :arglists ([port]),
   :doc
   "Takes a val from port if it's possible to do so immediately.\nNever blocks. Returns value if successful, nil otherwise.",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/poll!"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "promise-chan",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L97",
   :line 97,
   :var-type "function",
   :arglists ([] [xform] [xform ex-handler]),
   :doc
   "Creates a promise channel with an optional transducer, and an optional\nexception-handler. A promise channel can take exactly one value that consumers\nwill receive. Once full, puts complete but val is dropped (no transfer).\nConsumers will block until either a value is placed in the channel or the\nchannel is closed, then return the value (or nil) forever. See chan for the\nsemantics of xform and ex-handler.",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/promise-chan"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "pub",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L881",
   :line 881,
   :var-type "function",
   :arglists ([ch topic-fn] [ch topic-fn buf-fn]),
   :doc
   "Creates and returns a pub(lication) of the supplied channel,\npartitioned into topics by the topic-fn. topic-fn will be applied to\neach value on the channel and the result will determine the 'topic'\non which that value will be put. Channels can be subscribed to\nreceive copies of topics using 'sub', and unsubscribed using\n'unsub'. Each topic will be handled by an internal mult on a\ndedicated channel. By default these internal channels are\nunbuffered, but a buf-fn can be supplied which, given a topic,\ncreates a buffer with desired properties.\n\nEach item is distributed to all subs in parallel and synchronously,\ni.e. each sub must accept before the next item is distributed. Use\nbuffering/windowing to prevent slow subs from holding up the pub.\n\nItems received when there are no matching subs get dropped.\n\nNote that if buf-fns are used then each topic is handled\nasynchronously, i.e. if a channel is subscribed to more than one\ntopic it should not expect them to be interleaved identically with\nthe source.",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/pub"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "put!",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L187",
   :line 187,
   :var-type "function",
   :arglists ([port val] [port val fn1] [port val fn1 on-caller?]),
   :doc
   "Asynchronously puts a val into port, calling fn1 (if supplied) when\ncomplete, passing false iff port is already closed. nil values are\nnot allowed. If on-caller? (default true) is true, and the put is\nimmediately accepted, will call fn1 on calling thread.\n\nfn1 may be run in a fixed-size dispatch thread pool and should not\nperform blocking IO, including core.async blocking ops (those that\nend in !!).\n\nReturns true unless port is already closed.",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/put!"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "reduce",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L635",
   :line 635,
   :var-type "function",
   :arglists ([f init ch]),
   :doc
   "f should be a function of 2 arguments. Returns a channel containing\nthe single result of applying f to init and the first item from the\nchannel, then applying f to that result and the 2nd item, etc. If\nthe channel closes without yielding items, returns init and f is not\ncalled. ch must close before reduce produces a result.",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/reduce"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "sliding-buffer",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L68",
   :line 68,
   :var-type "function",
   :arglists ([n]),
   :doc
   "Returns a buffer of size n. When full, puts will complete, and be\nbuffered, but oldest elements in buffer will be dropped (not\ntransferred).",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/sliding-buffer"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "solo-mode",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L871",
   :line 871,
   :var-type "function",
   :arglists ([mix mode]),
   :doc
   "Sets the solo mode of the mix. mode must be one of :mute or :pause",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/solo-mode"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "split",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L614",
   :line 614,
   :var-type "function",
   :arglists ([p ch] [p ch t-buf-or-n f-buf-or-n]),
   :doc
   "Takes a predicate and a source channel and returns a vector of two\nchannels, the first of which will contain the values for which the\npredicate returned true, the second those for which it returned\nfalse.\n\nThe out channels will be unbuffered by default, or two buf-or-ns can\nbe supplied. The channels will close after the source channel has\nclosed.",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/split"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "sub",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L936",
   :line 936,
   :var-type "function",
   :arglists ([p topic ch] [p topic ch close?]),
   :doc
   "Subscribes a channel to a topic of a pub.\n\nBy default the channel will be closed when the source closes,\nbut can be determined by the close? parameter.",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/sub"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "take",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L1018",
   :line 1018,
   :var-type "function",
   :arglists ([n ch] [n ch buf-or-n]),
   :doc
   "Returns a channel that will return, at most, n items from ch. After n items\n have been returned, or ch has been closed, the return channel will close.\n\nThe output channel is unbuffered by default, unless buf-or-n is given.",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/take"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "take!",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L144",
   :line 144,
   :var-type "function",
   :arglists ([port fn1] [port fn1 on-caller?]),
   :doc
   "Asynchronously takes a val from port, passing to fn1. Will pass nil\nif closed. If on-caller? (default true) is true, and value is\nimmediately available, will call fn1 on calling thread.\n\nfn1 may be run in a fixed-size dispatch thread pool and should not\nperform blocking IO, including core.async blocking ops (those that\nend in !!).\n\nReturns nil.",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/take!"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "tap",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L750",
   :line 750,
   :var-type "function",
   :arglists ([mult ch] [mult ch close?]),
   :doc
   "Copies the mult source onto the supplied channel.\n\nBy default the channel will be closed when the source closes,\nbut can be determined by the close? parameter.",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/tap"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "thread",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L491",
   :line 491,
   :var-type "macro",
   :arglists ([& body]),
   :doc
   "Executes the body in another thread, returning immediately to the\ncalling thread. Returns a channel which will receive the result of\nthe body when completed, then close.",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/thread"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "thread-call",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L473",
   :line 473,
   :var-type "function",
   :arglists ([f]),
   :doc
   "Executes f in another thread, returning immediately to the calling\nthread. Returns a channel which will receive the result of calling\nf when completed, then close.",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/thread-call"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "timeout",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L109",
   :line 109,
   :var-type "function",
   :arglists ([msecs]),
   :doc "Returns a channel that will close after msecs",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/timeout"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "to-chan",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L687",
   :line 687,
   :var-type "function",
   :arglists ([coll]),
   :doc
   "Creates and returns a channel which contains the contents of coll,\nclosing when exhausted.",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/to-chan"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "toggle",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L859",
   :line 859,
   :var-type "function",
   :arglists ([mix state-map]),
   :doc
   "Atomically sets the state(s) of one or more channels in a mix. The\nstate map is a map of channels -> channel-state-map. A\nchannel-state-map is a map of attrs -> boolean, where attr is one or\nmore of :mute, :pause or :solo. Any states supplied are merged with\nthe current state.\n\nNote that channels can be added to a mix via toggle, which can be\nused to add channels in a particular (e.g. paused) state.",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/toggle"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "transduce",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L651",
   :line 651,
   :var-type "function",
   :arglists ([xform f init ch]),
   :doc
   "async/reduces a channel with a transformation (xform f).\nReturns a channel containing the result.  ch must close before\ntransduce produces a result.",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/transduce"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "unblocking-buffer?",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L75",
   :line 75,
   :var-type "function",
   :arglists ([buff]),
   :doc
   "Returns true if a channel created with buff will never block. That is to say,\nputs into this buffer will never cause the buffer to be full. ",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/unblocking-buffer?"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "unmix",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L849",
   :line 849,
   :var-type "function",
   :arglists ([mix ch]),
   :doc "Removes ch as an input to the mix",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/unmix"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "unmix-all",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L854",
   :line 854,
   :var-type "function",
   :arglists ([mix]),
   :doc "removes all inputs from the mix",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/unmix-all"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "unsub",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L944",
   :line 944,
   :var-type "function",
   :arglists ([p topic ch]),
   :doc "Unsubscribes a channel from a topic of a pub",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/unsub"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "unsub-all",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L949",
   :line 949,
   :var-type "function",
   :arglists ([p] [p topic]),
   :doc "Unsubscribes all channels from a pub, or a topic of a pub",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/unsub-all"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "untap",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L758",
   :line 758,
   :var-type "function",
   :arglists ([mult ch]),
   :doc "Disconnects a target channel from a mult",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/untap"}
  {:raw-source-url
   "https://github.com/clojure/core.async/raw/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj",
   :name "untap-all",
   :file "src/main/clojure/clojure/core/async.clj",
   :source-url
   "https://github.com/clojure/core.async/blob/62c8e01bad07fe463e65092bcbd605c2360f5c87/src/main/clojure/clojure/core/async.clj#L763",
   :line 763,
   :var-type "function",
   :arglists ([mult]),
   :doc "Disconnects all target channels from a mult",
   :namespace "clojure.core.async",
   :wiki-url
   "https://clojure.github.io/core.async//index.html#clojure.core.async/untap-all"})}
