(require '[clojure.core.async :as async :refer [<! >! <!! >!! timeout chan alt! alts!! go]])

(defn fan-in [ins]
  (let [c (chan)]
   (future (while true
             (let [[x] (alts!! ins)]
               (>!! c x))))
   c))

(defn fan-out [in cs-or-n]
  (let [cs (if (number? cs-or-n)
             (repeatedly cs-or-n chan)
             cs-or-n)]
   (future (while true
             (let [x (<!! in)
                   outs (map #(vector % x) cs)]
               (alts!! outs))))
   cs))

(let [cout (chan)
      cin (fan-in (fan-out cout (repeatedly 3 chan)))]
  (dotimes [n 10]
    (>!! cout n)
    (prn (<!! cin))))
