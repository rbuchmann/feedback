(ns feedback.test.trace
  (:use [feedback.trace :reload-all true]
        [clojure.test]
        [midje.sweet]))

(deftest path-order-test
  (facts
   (paths-in-eval-order? [0] [])
   => truthy
   (paths-in-eval-order? [] [])
   => falsey
   (paths-in-eval-order? [] [0])
   => falsey
   (paths-in-eval-order? [0] [0])
   => falsey
   (paths-in-eval-order? [0] [1])
   => truthy
   (paths-in-eval-order? [1] [0])
   => falsey
   (paths-in-eval-order? [0 1] [0])
   => truthy
   (paths-in-eval-order? [0] [0 1])
   => falsey
   (paths-in-eval-order? [0] [1 0])
   => truthy
   (paths-in-eval-order? [0 2 1 0] [0 2 2 1 0])
   => truthy))

#_(deftest protocol-test
  (against-background [(before :facts (clear-and-return))]
    (fact
     (with-trace
       (protocol ...type... 0 :internal-id 0 :value ...val...) => ...val...)
     (provided
      (trace! {:type        ...type...
               :form-id     0
               :internal-id 0
               :iteration   0
               :value       ...val...}) => nil))
    (fact
     (with-trace
       (protocol ...type... 0 :internal-id 0 :value ...val...)  => ...val...
       (protocol ...type... 1 :internal-id 0 :value ...val...)  => ...val...
       (protocol ...type... 0 :internal-id 0 :value ...val...)) => ...val...
       (clear-and-return) => [[{:iteration   0
                                :form-id     0
                                :internal-id 0
                                :type        ...type...
                                :value       ...val...}
                               {:iteration   0
                                :form-id     1
                                :internal-id 0
                                :type        ...type...
                                :value       ...val...}
                               {:iteration   1
                                :form-id     0
                                :internal-id 0
                                :type        ...type...
                                :value       ...val...}]])))
