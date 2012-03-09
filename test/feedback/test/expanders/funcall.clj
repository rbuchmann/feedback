(ns feedback.test.expanders.funcall
  (:use [feedback.trace :only [protocol] :reload true]
        [feedback.analyze :reload true]
        [feedback.expanders.funcall :reload true]
        [clojure.test]
        [midje.sweet]))

(deftest arith-test
  (with-expanders arith-expanders
    (facts

     (analyze-and-eval '(+ 1 2))
     => 3
     (provided
      (protocol :funcall 0, :call '(+ 1 2), :value 3) => 3)

     (analyze-and-eval '(+ 2 (* 3 4)))
     => 14
     (provided
      (protocol :funcall 0, :call '(+ 2 (* 3 4)), :value 14) => 14
      (protocol :funcall 1, :call '(* 3 4)      , :value 12) => 12))))
