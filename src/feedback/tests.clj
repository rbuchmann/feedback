(ns feedback.tests)

(defn test-fn [x]
  (let [a (inc x)
        b (inc a)]
    (if (<= b 10)
      (recur b)
      b)))
