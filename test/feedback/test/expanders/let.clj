(ns feedback.test.expanders.let
  (:use [feedback.analyze :reload true]
        [feedback.expanders.let :reload true]
        [feedback.trace :reload true]
        [clojure.test]
        [midje.sweet]))

(deftest let-test
  (with-expanders let-expanders
    (facts

     (run-traced '(let [x 1
                        y (inc x)]
                    y))
     =>
     {:result 2
      :trace [{:iteration 0
               :type :let
               :path [1 0]
               :source 'x
               :value 1}
              {:iteration 0
               :type :let
               :path [1 2]
               :source 'y
               :value 2}]}

     (run-traced '((fn [x]
                     (let [a x]
                       (inc x)))
                   5))
     =>
     {:result 6
      :trace [{:iteration 0
               :type :let
               :path [0 2 1 0]
               :source 'a
               :value 5}]}

     (run-traced '((fn [x]
                     (let [a x]
                       (let [b (+ a 1)]
                         b)))
                   3))
     =>
     {:result 4
      :trace [{:iteration 0
               :type :let
               :path [0 2 1 0]
               :source 'a
               :value 3}
              {:iteration 0
               :type :let
               :path [0 2 2 1 0]
               :source 'b
               :value 4}]}

     (run-traced '((fn [x]
                     (let [a (inc x)]
                       (if (<= a 5)
                         (recur a)
                         a)))
                   3))
     =>
     {:result 6
      :trace [{:iteration 0
               :type :let
               :path [0 2 1 0]
               :source 'a
               :value 4}
              {:iteration 1
               :type :let
               :path [0 2 1 0]
               :source 'a
               :value 5}
              {:iteration 2
               :type :let
               :path [0 2 1 0]
               :source 'a
               :value 6}]})))
