(ns feedback.test.formatter
  (:require [goog.dom :as gdom]
            [crate.core :as crate])
  (:use [feedback.formatter :only [render s log]]))

(def test-forms
  '[nil
    1
    :x
    x
    ()
    (foo)
    (foo bar)
    (foo bar baz bo bup)
    []
    [1 2 3 4 5 6]
    {}
    {:a 1, :b 2}
    #{}
    #{1 2 3 4}
    (defn foo [x y]
      (- x (dec y)))
    ])

(defn test-formatter []
  (let [wrapper (gdom/$ "wrapper")
        dom     [:div [:h1 "Format tests"]
                 (for [f test-forms]
                   (do (log f (render f))
                       [:div
                        [:h2 (s f)]
                        (render f)]))]]
    (log (crate/html dom))
    (gdom/removeChildren wrapper)
    (gdom/appendChild wrapper (crate/html dom))))
