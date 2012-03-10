(ns feedback.test.analyze
  (:use [feedback.trace :only [protocol]]
        [feedback.analyze :reload true]
        [feedback.expanders.let :reload true]
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
   (meta (with-path 'f)) => {pk []}
   (meta (with-path '(foo))) => {pk []}
   (meta (first (with-path '(foo)))) => {pk [0]}
   (meta (second (with-path '(4 g)))) => {pk [1]}
   (meta (first (second (with-path '(4 (a b)))))) => {pk [1 0]}
   (meta (second (first (with-path {:a :b})))) => {pk [1 1]}))
