(ns feedback.expanders.defn
  (:use [feedback.analyze  :only (transform)]
        [feedback.expander :only (macro-expander)]))

(def defn-expander
  (macro-expander defn [x y & rest]
    (if (string? y)
      `(fn ~@(cons x rest))
      `(fn ~@(list* x y rest)))))
