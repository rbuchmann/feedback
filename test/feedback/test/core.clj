(ns feedback.test.core
  (:require [feedback.analyze :as a])
  (:use [feedback.core :reload-all true]
        [clojure.test]
        [midje.sweet]))

#_(deftest feedback-test
  (facts
    (feedback test-fn) => ...analyzed...
    (provided
      (a/analyze '(defn test-fn [x])) => ...analyzed...)))
