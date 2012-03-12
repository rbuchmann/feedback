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

(deftest protocol-test
  (against-background [(before :checks (clear-and-return))]
    (facts

     (with-trace (protocol {:path [1 2]
                            ::some ::data}))
     => {:trace [{:path [1 2]
                  :iteration 0
                  ::some ::data}]
         :result nil}

     (do (with-trace
           (protocol {:path [1 2]})
           (protocol {:path [1]})
           (protocol {:path [1 1]}))
         (clear-and-return))
     => [[{:iteration 0
           :path      [1 2]}
          {:iteration 0
           :path      [1]}
          {:iteration 1
           :path      [1 1]}]])))
