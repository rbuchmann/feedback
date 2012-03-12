(ns feedback.expanders.defn
  (:use [feedback.analyze  :only (transform)]
        [feedback.expander :only (macro-expander)]))

(def defn-expander
  (macro-expander defn [& rest]
                  `(fn ~@rest)))
