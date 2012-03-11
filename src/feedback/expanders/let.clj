(ns feedback.expanders.let
  (:use [feedback.analyze :only [transform]]
        [feedback.expander :only [expander call?]]
        [feedback.trace :only [log-call]]))

(defn dbg-let [let-sym bindings & body]
  (let [transform-binding (fn [[var-sym value]]
                            [var-sym (transform value)
                             '_      (log-call :let var-sym
                                               :value var-sym)])
        new-bindings (->> bindings
                          (partition 2)
                          (map transform-binding)
                          (apply concat)
                          vec)
        new-body (vec (map transform body))]
    `(~let-sym ~new-bindings
               ~@new-body)))

(def let-expanders
  [(expander (call? 'let*) dbg-let)])
