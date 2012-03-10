(ns feedback.expanders.defn
  (:use [feedback.analyze  :only (transform)]
        [feedback.expander :only (defexpander)]))

(defexpander defn->fn ['defn & rest]
  `(fn ~@(transform rest)))
