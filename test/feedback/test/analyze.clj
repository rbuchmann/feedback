(ns feedback.test.analyze
  (:use [feedback.analyze :reload true]
        [clojure.test]
        [midje.sweet]))

(def pk :feedback.analyze/path)

(deftest test-with-path
  (facts
   (with-path 5) => 5
   (with-path 'f) => 'f
   (with-path '(foo)) => '(foo)
   (with-path '[a b 3]) => '[a b 3]
   (with-path '{:a a}) => '{:a a}
   (with-path #{1 2 'c}) => #{1 2 'c}
   (get-path (with-path 'f)) => []
   (get-path (with-path '(foo))) => []
   (get-path (first (with-path '(foo)))) => [0]
   (get-path (second (with-path '(4 g)))) => [1]
   (get-path (first (second (with-path '(4 (a b)))))) => [1 0]
   (get-path (second (first (with-path {:a 'b})))) => [0 1]))
