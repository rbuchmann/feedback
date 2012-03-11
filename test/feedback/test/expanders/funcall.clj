(ns feedback.test.expanders.funcall
  (:use [feedback.trace :only [run-traced] :reload true]
        [feedback.analyze :reload true]
        [feedback.expanders.funcall :reload true]
        [clojure.test]
        [midje.sweet]))

(deftest arith-test
  (with-expanders arith-expanders
    (facts

     (run-traced '(+))
     =>
     {:result 0
      :trace [{:iteration 0
               :type :call
               :path []
               :source '(+)
               :args []
               :result 0}]}

     (run-traced '(+ 1))
     =>
     {:result 1
      :trace [{:iteration 0
               :type :call
               :path []
               :source '(+ 1)
               :args [1]
               :result 1}]}

     (run-traced '(+ 1 2))
     =>
     {:result 3
      :trace [{:iteration 0
               :type :call
               :path []
               :source '(+ 1 2)
               :args [1 2]
               :result 3}]}

     (run-traced '(+ 1 (* 2 3)))
     =>
     {:result 7
      :trace [{:iteration 0
               :type :call
               :path [2]
               :source '(* 2 3)
               :args [2 3]
               :result 6}
              {:iteration 0
               :type :call
               :path []
               :source '(+ 1 (* 2 3))
               :args [1 6]
               :result 7}]})))
