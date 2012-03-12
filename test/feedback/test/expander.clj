(ns feedback.test.expander
  (:use [feedback.expander :reload true]
        [clojure.test]
        [midje.sweet]))

(deftest qualify-test
  (facts
   (qualify nil)   => nil
   (qualify 1)     => 1
   (qualify 'x)    => 'x
   (qualify `x)    => `x
   (qualify 'let*) => 'let*
   (qualify 'let)  => `let
   (qualify `let)  => `let))

(deftest call-test
  (facts
   ((call? 'x) 42)       => falsey
   ((call? 'x) 'x)       => falsey
   ((call? 'x) '(y))     => falsey
   ((call? 'x) '(x 1))   => truthy
   ((call? 'x) '(x 1 2)) => truthy
   ;; special forms
   ((call? 'let*) '(let*)) => truthy
   ((call? 'let*) `(let*)) => truthy
   ((call? `let*) '(let*)) => truthy
   ((call? `let*) `(let*)) => truthy
   ;; vars
   ((call? 'let) '(let)) => truthy
   ((call? 'let) `(let)) => truthy
   ((call? `let) '(let)) => truthy
   ((call? `let) `(let)) => truthy))
