(ns feedback.test.core
  (:require [feedback.analyze :as a])
  (:use [feedback.core :reload-all true]
        [clojure.test]
        [midje.sweet]))

(defn test-fn [x])

(deftest feedback-test
  (facts
    (feedback test-fn) => ...analyzed...
    (provided
      (a/analyze '(defn test-fn [x])) => ...analyzed...)))
