(ns clojure.core.async.ioc-macros-test
  (:refer-clojure :exclude [map into reduce transduce merge take partition
                            partition-by])
  (:require [clojure.core.async.impl.ioc-macros :as ioc]
            [clojure.core.async :refer :all :as async]
            [clojure.set :as set]
            [clojure.test :refer :all])
  (:import [clojure.lang ExceptionInfo]))

(defn pause [x]
  x)

(defn pause-run [state blk val]
  (ioc/aset-all! state ioc/STATE-IDX blk ioc/VALUE-IDX val)
  :recur)


(defmacro runner
  "Creates a runner block. The code inside the body of this macro will be translated
  into a state machine. At run time the body will be run as normal. This transform is
  only really useful for testing."
  [& body]
  (let [terminators {`pause `pause-run}
        crossing-env (zipmap (keys &env) (repeatedly gensym))]
    `(let [captured-bindings# (clojure.lang.Var/getThreadBindingFrame)
           ~@(mapcat (fn [[l sym]] [sym `(^:once fn* [] ~l)]) crossing-env)
           state# (~(ioc/state-machine `(do ~@body) 0 [crossing-env &env] terminators))]
       (ioc/aset-all! state# ~ioc/BINDINGS-IDX captured-bindings#)
       (ioc/run-state-machine state#)
       (ioc/aget-object state# ioc/VALUE-IDX))))

(deftest test-try-catch-finally
  (testing "Don't endlessly loop when exceptions are thrown"
    (is (thrown? Exception
                 (runner
                  (loop []
                    (try
                      (pause (throw (Exception. "Ex")))
                      (catch ExceptionInfo _
                        :retry))))))
    (is (thrown? Throwable
                 (runner
                  (loop []
                    (try
                      (pause (throw (Throwable. "Ex")))
                      (catch ExceptionInfo _
                        :retry))))))
    ;; (is (try ((fn [] (println "Hello") (pause 5))) (catch Exception e)))
    (is (= :Throwable
           (runner
            (try
              (pause 5)
              (throw (new Throwable))
              (catch Exception _e
                :Exception)
              (catch Throwable _t
                :Throwable))))))
  (testing "finally shouldn't change the return value"
    (is (= 1 (runner (try 1 (finally (pause 2)))))))
  (testing "exception handlers stack"
    (is  (= "eee"
            (runner
             (try
               (try
                 (try
                   (throw (pause (Exception. "e")))
                   (catch Exception e
                     (pause (throw (Exception. (str (.getMessage e) "e"))))))
                 (catch Exception e
                   (throw (throw (Exception. (str (.getMessage e) "e"))))))
               (catch Exception e
                 (.getMessage e)))))))
  (testing "exception handlers and the class hierarchy"
    (is
     (runner
      (try
        (pause 10)
        (throw (RuntimeException.))
        (catch RuntimeException _r
          (pause true))
        (catch Exception _e
          (pause false)))))
    (is
     (runner
      (try
        (pause 10)
        (throw (RuntimeException.))
        (catch Exception _e
          (pause true))))))
  (testing "don't explode trying to compile this"
    (is
     (runner
      (try
        true
        (catch Exception e
          (pause 1)
          e))))))


(defmacro locals-test []
  (if (if (contains? &env :locals)
        (get (:locals &env) 'x)
        (get &env 'x))
    :pass
    :fail))


(deftest runner-tests
  (testing "macros add locals to the env"
    (is (= :pass
           (runner (let [x 42]
                     (pause (locals-test)))))))
  (testing "fn as first arg in sexpr"
    (is (= 42
           (runner ((fn [] 42))))))
  (testing "do blocks"
    (is (= 42
           (runner (do (pause 42)))))
    (is (= 42
           (runner (do (pause 44)
                       (pause 42))))))
  (testing "if expressions"
    (is (= true
           (runner (if (pause true)
                     (pause true)
                     (pause false)))))
    (is (= false
           (runner (if (pause false)
                     (pause true)
                     (pause false)))))
    (is (= true
           (runner (when (pause true)
                     (pause true)))))
    (is (= nil
           (runner (when (pause false)
                     (pause true))))))

  (testing "dot forms"
    (is (= 42 (runner (. Long (parseLong "42")))))
    (is (= 42 (runner (. Long parseLong "42")))))

  (testing "quote"
    (is (= '(1 2 3)
           (runner (pause '(1 2 3))))))

  (testing "loop expressions"
    (is (= 100
           (runner (loop [x 0]
                     (if (< x 100)
                       (recur (inc (pause x)))
                       (pause x))))))
    (is (= 100
           (runner (loop [x (pause 0)]
                     (if (< x 100)
                       (recur (inc (pause x)))
                       (pause x))))))
    (is (= [:b :a]
           (runner (loop [a :a b :b n 1]
                     (if (pos? n)
                       (recur b a (dec n)) ;; swap bindings
                       [a b])))))
    (is (= 1
           (runner (loop [x 0
                          y (inc x)]
                     y)))))

  (testing "let expressions"
    (is (= 3
           (runner (let [x 1 y 2]
                     (+ x y))))))

  (testing "vector destructuring"
    (is (= 3
           (runner (let [[x y] [1 2]]
                     (+ x y))))))

  (testing "hash-map destructuring"
    (is (= 3
           (runner (let [{:keys [x y] x2 :x y2 :y :as foo} {:x 1 :y 2}]
                     (assert (and foo (pause x) y x2 y2 foo))
                     (+ x y))))))

  (testing "hash-map literals"
    (is (= {:1 1 :2 2 :3 3}
           (runner {:1 (pause 1)
                    :2 (pause 2)
                    :3 (pause 3)}))))
  (testing "hash-set literals"
    (is (= #{1 2 3}
           (runner #{(pause 1)
                     (pause 2)
                     (pause 3)}))))
  (testing "vector literals"
    (is (= [1 2 3]
           (runner [(pause 1)
                    (pause 2)
                    (pause 3)]))))

  (testing "keywords as functions"
    (is (= :bar
           (runner (:foo (pause {:foo :bar}))))))

  (testing "vectors as functions"
    (is (= 2
           (runner ([1 2] 1)))))

  (testing "dotimes"
    (is (= 42 (runner
               (dotimes [x 10]
                 (pause x))
               42))))

  (testing "fn closures"
    (is (= 42
           (runner
            (let [x 42
                  _ (pause x)
                  f (fn [] x)]
              (f))))))

  (testing "lazy-seqs in bodies"
    (is (= nil
		   (runner
            (loop []
              (when-let [x (pause 10)]
                (pause (vec (for [i (range x)]
                              i)))
                (if-not x
                  (recur))))))))

  (testing "specials cannot be shadowed"
    (is (= 3
           (let [let* :foo] (runner (let* [x 3] x))))))

  (testing "case"
    (is (= 43
           (runner
            (let [value :bar]
              (case value
                :foo (pause 42)
                :bar (pause 43)
                :baz (pause 44))))))
    (is (= :default
           (runner
            (case :baz
              :foo 44
              :default))))
    (is (= nil
           (runner
            (case true
              false false
              nil))))
    (is (= 42
           (runner
            (loop [x 0]
              (case (int x)
                0 (recur (inc x))
                1 42))))))

  (testing "try"
    (is (= 42
           (runner
            (try 42
                 (catch Throwable ex ex)))))
    (is (= 42
           (runner
            (try
              (assert false)
              (catch Throwable ex 42)))))

   (let [a (atom false)
          v (runner
             (try
               true
               (catch Throwable _ex false)
               (finally (pause (reset! a true)))))]
      (is (and @a v)))

   (let [a (atom false)
          v (runner
             (try
               (assert false)
               (catch Throwable _ex true)
               (finally (reset! a true))))]
      (is (and @a v)))

   (let [a (atom false)
          v (try (runner
                  (try
                    (assert false)
                    (finally (reset! a true))))
                 (catch Throwable ex ex))]
      (is (and @a v)))


   (let [a (atom 0)
          v (runner
             (try
               (try
                 42
                 (finally (swap! a inc)))
               (finally (swap! a inc))))]
      (is (= @a 2)))

   (let [a (atom 0)
          v (try (runner
                  (try
                    (try
                      (throw (AssertionError. 42))
                      (finally (swap! a inc)))
                    (finally (swap! a inc))))
                 (catch AssertionError ex ex))]
      (is (= @a 2)))

   (let [a (atom 0)
          v (try (runner
                  (try
                    (try
                      (throw (AssertionError. 42))
                      (catch Throwable ex (throw ex))
                      (finally (swap! a inc)))
                    (catch Throwable ex (throw ex))
                    (finally (swap! a inc))))
                 (catch AssertionError ex ex))]
      (is (= @a 2)))

   (let [a (atom 0)
          v (try (runner
                  (try
                    (try
                      (throw (AssertionError. (pause 42)))
                      (catch Throwable ex (pause (throw ex)))
                      (finally (pause (swap! a inc))))
                    (catch Throwable ex (pause (throw ex)))
                    (finally (pause (swap! a inc)))))
                 (catch AssertionError ex ex))]
     (is (= @a 2)))))


  (defn identity-chan
    "Defines a channel that contains the given value"
    [x]
    (to-chan! [x]))

  (deftest async-test
    (testing "values are returned correctly"
      (is (= 10
             (<!! (go (<! (identity-chan 10)))))))
    (testing "writes work"
      (is (= 11
             (<!! (go (let [c (chan 1)]
                        (>! c (<! (identity-chan 11)))
                        (<! c)))))))

    (testing "case with go"
      (is (= :1
             (<!! (go (case (name :1)
                        "0" :0
                        "1" :1
                        :3))))))

    (testing "nil result of go"
      (is (= nil
             (<!! (go nil)))))

    (testing "take inside binding of loop"
      (is (= 42
             (<!! (go (loop [x (<! (identity-chan 42))]
                        x))))))

    (testing "can get from a catch"
      (let [c (identity-chan 42)]
        (is (= 42
               (<!! (go (try
                          (assert false)
                          (catch Throwable ex (<! c))))))))))

  (deftest offer-poll
    (let [c (chan 2)]
      (is (= [true true 5 6 nil]
             (<!! (go [(offer! c 5) (offer! c 6) (poll! c) (poll! c) (poll! c)]))))))

  (deftest enqueued-chan-ops
    (testing "enqueued channel puts re-enter async properly"
      (is (= [:foo 42]
             (let [c (chan)
                   result-chan (go (>! c :foo) 42)]
               [(<!! c) (<!! result-chan)]))))
    (testing "enqueued channel takes re-enter async properly"
      (is (= :foo
             (let [c (chan)
                   async-chan (go (<! c))]
               (>!! c :foo)
               (<!! async-chan)))))
    (testing "puts into channels with full buffers re-enter async properly"
      (is (= #{:foo :bar :baz :boz}
             (let [c (chan 1)
                   async-chan (go
                               (>! c :foo)
                               (>! c :bar)
                               (>! c :baz)

                               (>! c :boz)
                               (<! c))]
               (set [(<!! c)
                     (<!! c)
                     (<!! c)
                     (<!! async-chan)]))))))

  (defn rand-timeout [x]
    (timeout (rand-int x)))

  (deftest alt-tests
    (testing "alts works at all"
      (let [c (identity-chan 42)]
        (is (= [42 c]
               (<!! (go (alts!
                         [c])))))))
    (testing "alt works"
      (is (= [42 :foo]
             (<!! (go (alt!
                       (identity-chan 42) ([v] [v :foo])))))))

    (testing "alts can use default"
      (is (= [42 :default]
             (<!! (go (alts!
                       [(chan 1)] :default 42))))))

    (testing "alt can use default"
      (is (= 42
             (<!! (go (alt!
                       (chan) ([v] :failed)
                       :default 42))))))

    (testing "alt random checks all chans"
      (is (set/subset?
            (<!! (go (loop [acc #{}
                            cnt 0]
                       (if (< cnt 20)
                         (let [label (alt!
                                      (identity-chan :one) ([v] v)
                                      (identity-chan :two) ([v] v)
                                      (identity-chan :three) ([v] v))]
                           (recur (conj acc label) (inc cnt)))
                         acc))))
            #{:one :two :three}))))

  (deftest close-on-exception-tests
    (let [eh (Thread/getDefaultUncaughtExceptionHandler)
          msg "This exception is expected"]
      (try
        ;; don't spam stderr
        (Thread/setDefaultUncaughtExceptionHandler
          (reify Thread$UncaughtExceptionHandler
            (uncaughtException [_ _thread _throwable])))
        (testing "threads"
          (is (nil? (<!! (thread (assert false msg)))))
          (is (nil? (<!! (thread (alts!! [(identity-chan 42)])
                           (assert false msg))))))
        (testing "go blocks"
          (is (nil? (<!! (go (assert false msg)))))
          (is (nil? (<!! (go (alts! [(identity-chan 42)])
                             (assert false msg))))))
        (finally
          ;; restore
          (Thread/setDefaultUncaughtExceptionHandler eh)))))

(deftest resolution-tests
    (let [<! (constantly 42)]
      (is (= 42 (<!! (go (<! (identity-chan 0)))))
          "symbol translations do not apply to locals outside go"))

    (is (= 42 (<!! (go (let [<! (constantly 42)]
                         (<! (identity-chan 0))))))
        "symbol translations do not apply to locals inside go")

    (let [for vector x 3]
      (is (= [[3 [0 1]] 3]
             (<!! (go (for [x (range 2)] x))))
          "locals outside go are protected from macroexpansion"))

    (is (= [[3 [0 1]] 3]
           (<!! (go (let [for vector x 3]
                      (for [x (range 2)] x)))))
        "locals inside go are protected from macroexpansion")

    (let [c (identity-chan 42)]
      (is (= [42 c] (<!! (go (async/alts! [c]))))
          "symbol translations apply to resolved symbols")))

(deftest go-nests
  (is (= [23 42] (<!! (<!! (go (let [let* 1 a 23] (go (let* [b 42] [a b])))))))))

(defprotocol P
  (x [p]))

(defrecord R [z]
  P
  (x [this]
    (go
      (loop []
        (if (zero? (rand-int 3))
          [z (.z this)]
          (recur))))))

(deftest go-propagates-primitive-hints
  (is (= "asd" (<!! (let [a (int 1)] (go (.substring "fasd" a))))))
  (is (= 1 (<!! (let [a (int 1)] (go (Integer/valueOf a))))))
  (is (= [1 1] (<!! (x (R. 1))))))

(deftest ASYNC-186
  (is (let [y nil] (go))))

(deftest ASYNC-198
  (let [resp (runner
              (try
                (let [[r] (try
                            (let [value (pause :any)]
                              (throw (ex-info "Exception" {:type :inner})))
                            (catch Throwable e
                              [:outer-ok])
                            (finally))]
                  (throw (ex-info "Throwing outer exception" {:type :outer})))
                (catch ExceptionInfo ex
                  (is (= :outer (:type (ex-data ex))))
                  :ok)
                (catch UnsupportedOperationException ex
                  :unsupported)))]
    (is (= :ok resp))))

(deftest ASYNC-212
  (is (= 42
         (<!! (go
                (let [a nil
                      foo (identity a)]
                  (if foo
                    (<! foo)
                    42)))))))

;; The park/park-run/park-runner api is similar to the pause
;; counterpart above, but it actually parks the state machine so you
;; can test parking and unparking machines in different environments.
(defn park [x]
  x)

(defn park-run [state blk val]
  (ioc/aset-all! state ioc/STATE-IDX blk ioc/VALUE-IDX val)
  nil)

(defmacro park-runner
  [& body]
  (let [terminators {`park `park-run}
        crossing-env (zipmap (keys &env) (repeatedly gensym))]
    `(let [captured-bindings# (clojure.lang.Var/getThreadBindingFrame)
           ~@(mapcat (fn [[l sym]] [sym `(^:once fn* [] ~(vary-meta l dissoc :tag))]) crossing-env)
           state# (~(ioc/state-machine
                     `(do ~@body)
                     0
                     [crossing-env &env]
                     terminators))]
       (ioc/aset-all! state#
                      ~ioc/BINDINGS-IDX
                      captured-bindings#)
       (ioc/run-state-machine state#)
       [state# (ioc/aget-object state# ioc/VALUE-IDX)])))

(deftest test-binding
  (let [results (atom {})
        exception (atom nil)]
    ;; run the machine on another thread without any existing binding frames.
    (doto (Thread.
           ^Runnable
           (fn []
             (try
               (let [[state result] (park-runner (binding [*1 2] (park 10) 100))]
                 (ioc/run-state-machine state)
                 ;; the test is macro relies on binding to convey
                 ;; results, but we want a pristine binding
                 ;; environment on this thread, so use an atom to
                 ;; report results back to the main thread.
                 (reset! results {:park-value result :final-value (ioc/aget-object state ioc/VALUE-IDX)}))
               (catch Throwable t
                 (reset! exception t)))))
      (.start)
      (.join))
    (is (= 10 (:park-value @results)))
    (is (= 100 (:final-value @results)))
    (is (if @exception (throw @exception) true))))
