(ns feedback.test.expanders.let
  (:use [feedback.trace :only [protocol]]
        [feedback.analyze :reload true]
        [feedback.expanders.let :reload true]
        [clojure.test]
        [midje.sweet]))

(deftest let-test
  (with-expanders let-expanders
    (facts

     (analyze-and-eval '(let [x 1] x))
     => 1
     (provided (protocol :let 0, :internal-id 0, :var 'x, :value 1) => 1)

     ((analyze-and-eval '(fn [x]
                           (let [a x]
                             x)))
      5)
     => 5
     (provided
      (protocol :let 0, :internal-id 0, :var 'a, :value 5) => 5)

     ((analyze-and-eval '(fn [x]
                           (let [a x]
                             (let [b (+ a 1)]
                               b))))
      3)
     => 4
     (provided
      (protocol :let 0, :internal-id 0, :var 'a, :value 3) => 3
      (protocol :let 1, :internal-id 0, :var 'b, :value 4) => 4)

     ((analyze-and-eval '(fn [x]
                          (let [a (inc x)]
                            (if (<= a 5)
                              (recur a)
                              a))))
      1)
     => 6
     (provided
      (protocol :let 0, :internal-id 0, :var 'a, :value 2) => 2
      (protocol :let 0, :internal-id 0, :var 'a, :value 3) => 3
      (protocol :let 0, :internal-id 0, :var 'a, :value 4) => 4
      (protocol :let 0, :internal-id 0, :var 'a, :value 5) => 5
      (protocol :let 0, :internal-id 0, :var 'a, :value 6) => 6))))
