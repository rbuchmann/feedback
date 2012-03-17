(ns feedback.expanders.defn
  (:use [feedback.analyze  :only (transform)]
        [feedback.expander :only (macro-expander)]))

(def defn-expander
  (macro-expander defn [x & rest]
     `(fn ~@(cons x (drop-while #(or (string? %) (map? %))
                               rest)))))
