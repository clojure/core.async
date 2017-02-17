(ns clojure.core.async.ioc-macros-test
  (:refer-clojure :exclude [map into reduce transduce merge take partition
                            partition-by])
  (:require [clojure.core.async.impl.ioc-macros :as ioc]
            [clojure.core.async :refer :all :as async]
            [clojure.test :refer :all])
  (:import [java.io FileInputStream ByteArrayOutputStream File]))

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
  (let [terminators {`pause `pause-run}]
    `(let [captured-bindings# (clojure.lang.Var/getThreadBindingFrame)
           state# (~(ioc/state-machine `(do ~@body) 0 (keys &env) terminators))]
       (ioc/aset-all! state#
                  ~ioc/BINDINGS-IDX
                  captured-bindings#)
       (ioc/run-state-machine state#)
       (ioc/aget-object state# ioc/VALUE-IDX))))


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
               (catch Throwable ex false)
               (finally (pause (reset! a true)))))]
      (is (and @a v)))

   (let [a (atom false)
          v (runner
             (try
               (assert false)
               (catch Throwable ex true)
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
    "Defines a channel that instantly writes the given value"
    [x]
    (let [c (chan 1)]
      (>!! c x)
      (close! c)
      c))

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

    (testing "alt obeys its random-array initialization"
      (is (= #{:two}
             (with-redefs [clojure.core.async/random-array
                           (constantly (int-array [1 2 0]))]
               (<!! (go (loop [acc #{}
                               cnt 0]
                          (if (< cnt 10)
                            (let [label (alt!
                                         (identity-chan :one) ([v] v)
                                         (identity-chan :two) ([v] v)
                                         (identity-chan :three) ([v] v))]
                              (recur (conj acc label) (inc cnt)))
                            acc)))))))))

  (deftest close-on-exception-tests
    (testing "threads"
      (is (nil? (<!! (thread (assert false "This exception is expected")))))
      (is (nil? (<!! (thread (alts!! [(identity-chan 42)])
                             (assert false "This exception is expected"))))))
    (testing "go blocks"
      (is (nil? (<!! (go (assert false "This exception is expected")))))
      (is (nil? (<!! (go (alts! [(identity-chan 42)])
                         (assert false "This exception is expected")))))))

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
