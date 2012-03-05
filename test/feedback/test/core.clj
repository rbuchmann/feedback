(ns feedback.test.core
  (:use [feedback.core]
        [clojure.test]
        [midje.sweet]))

(defn test-fn [x])

(deftest feedback-test
  (facts
    (feedback test-fn) => ...analyzed...
    (provided
      (analyze-code '(defn test-fn [x])) => ...analyzed...)))
