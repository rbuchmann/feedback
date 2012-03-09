(ns feedback.test.trace
  (:use [feedback.trace :reload-all true]
        [clojure.test]
        [midje.sweet]))

(deftest protocol-test
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
