(ns cljs.core.async.tests
  (:require [cljs.core.async :refer [buffer dropping-buffer sliding-buffer put! take! chan close!] :as async]
            [cljs.core.async.impl.dispatch :as dispatch]
            [cljs.core.async.impl.buffers :as buff]
            [cljs.core.async.impl.timers :as timers :refer [timeout]]
            [cljs.core.async.impl.protocols :refer [full? add! remove!]])
  (:require-macros [cljs.core.async.test-helpers :as h :refer [is= is deftest testing runner throws?]]
                   [cljs.core.async.macros :as m :refer [go alt!]]))

(let [c (chan 1)]
  (put! c 42 #(is true) true)
  (take! c #(is (= 42 %)) true))

(let [c (chan)]
  (put! c 42 #(is true) true)
  (take! c #(is (= 42 %)) true))

(defn identity-chan
  [x]
  (let [c (chan 1)]
    (go (>! c x)
        (close! c))
    c))

(defn debug [x]
  (.log js/console x)
  x)

(go (is= (<! (identity-chan 42)) 42))

(let [c (identity-chan 42)]
  (go (is= [42 c]
           (alts! [c]))))

(deftest alt-tests
  (testing "alts! works at all"
    (let [c (identity-chan 42)]
      (go (is= [42 c]
               (alts! [c])))))

  (testing "alt! works"
    (go
     (is= [42 :foo]
          (alt! (identity-chan 42) ([v] [v :foo])))))
  (testing "alts! can use default"
    (go
     (is= [42 :default]
          (alts!
           [(chan 1)] :default 42)))))

(deftest timeout-tests
  (testing "timeout will return same channel if within delay"
    (is= (timeout 10) (timeout 5))
    (is= 1 (count (seq timers/timeouts-map))))

  #_(testing "timeout map is empty after timeout expires"
    (go
     (<! (timeout 300))
     (is= 0 (count (seq timers/timeouts-map)))))
  #_(testing "timeout map is empty after timeout expires with namespaced take"
    (go
     (async/<! (timeout 300))
     (is= 0 (count (seq timers/timeouts-map))))))


#_(deftest queue-limits
  (testing "async put!s are limited"
    (let [c (chan)]
      (dotimes [x 1024]
        (put! c x))
      (is (throws? (put! c 42)))
      (take! c (fn [x] (is= x 0)))))

  (testing "async take!s are limited"
    (let [c (chan)]
      (dotimes [x 1024]
        (take! c (fn [x])))
      (is (throws? (take! c (fn [x]))))
      (put! c 42))))

(deftest close-on-exception-tests
  (testing "go blocks"
    (go
     (alt! (go (assert false "This exception is expected")) ([v] (is (nil? v)))
           (timeout 500) ([v] (assert false "Channel did not close")))
     (alt! (go (alts! [(identity-chan 42)])
               (assert false "This exception is expected"))  ([v] (is (nil? v)))
               (timeout 500) ([v] (assert false "Channel did not close"))))))


(deftest cleanup
  (testing "alt handlers are removed from put!"
    (go
     (let [c (chan)]
       (dotimes [x 1024]
         (alts! [[c x]] :default 42))
       (put! c 42))))
    (testing "alt handlers are removed from take!"
    (go
     (let [c (chan)]
       (dotimes [x 1024]
         (alts! [c] :default 42))
       (take! c (fn [x] nil))))))



;;;; ops tests


(deftest ops-tests
  (testing "map<"
    (go
     (is= [2 3 4 5]
          (<! (async/into [] (async/map< inc (async/to-chan [1 2 3 4])))))))
  (testing "map>"
    (go
     (is= [2 3 4 5]
          (let [out (chan)
                in (async/map> inc out)]
            (async/onto-chan in [1 2 3 4])
            (<! (async/into [] out))))))
  (testing "filter<"
    (go
     (is= [2 4 6]
          (<! (async/into [] (async/filter< even? (async/to-chan [1 2 3 4 5 6])))))))
  (testing "remove<"
    (go
     (is= [1 3 5]
          (<! (async/into [] (async/remove< even? (async/to-chan [1 2 3 4 5 6])))))))
  (testing "filter>"
    (go
     (is= [2 4 6]
          (let [out (chan)
                in (async/filter> even? out)]
            (async/onto-chan in [1 2 3 4 5 6])
            (<! (async/into [] out))))))
  (testing "remove>"
    (go
     (is= [1 3 5]
          (let [out (chan)
                in (async/remove> even? out)]
            (async/onto-chan in [1 2 3 4 5 6])
            (<! (async/into [] out))))))
  (comment (testing mapcat<
             (is (= [0 0 1 0 1 2]
                    (<!! (a/into [] (mapcat< range
                                             (a/to-chan [1 2 3])))))))
           (testing mapcat>
             (is (= [0 0 1 0 1 2]
                    (let [out (chan)
                          in (mapcat> range out)]
                      (onto-chan in [1 2 3])
                      (<!! (a/into [] out))))))
           (testing pipe
             (is (= [1 2 3 4 5]
                    (let [out (chan)]
                      (pipe (a/to-chan [1 2 3 4 5])
                            out)
                      (<!! (a/into [] out))))))
           (testing split
             ;; Must provide buffers for channels else the tests won't complete
             (let [[even odd] (a/split even? (a/to-chan [1 2 3 4 5 6]) 5 5)]
               (is (= [2 4 6]
                      (<!! (a/into [] even))))
               (is (= [1 3 5]
                      (<!! (a/into [] odd))))))
           (testing map
             (is (= [0 4 8 12]
                    (<!! (a/into [] (a/map + [(a/to-chan (range 4))
                                              (a/to-chan (range 4))
                                              (a/to-chan (range 4))
                                              (a/to-chan (range 4))]))))))
           (testing merge
             ;; merge uses alt, so results can be in any order, we're using
             ;; frequencies as a way to make sure we get the right result.
             (is (= {0 4
                     1 4
                     2 4
                     3 4}
                    (frequencies (<!! (a/into [] (a/merge [(a/to-chan (range 4))
                                                           (a/to-chan (range 4))
                                                           (a/to-chan (range 4))
                                                           (a/to-chan (range 4))])))))))
))
