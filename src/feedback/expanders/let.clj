(ns feedback.expanders.let
  (:use [feedback.analyze :only [transform]]
        [feedback.expander :only [expander call?]]
        [feedback.trace :only [trace]]))

(defn dbg-let [[let-sym bindings & body]]
  (let [transform-binding (fn [[var-sym value]]
                            [var-sym value
                             '_      (trace :let var-sym)])
        new-bindings (->> bindings
                          (partition 2)
                          (map transform-binding)
                          (apply concat)
                          vec)]
    `(~let-sym ~new-bindings
               ~@body)))

(def let-expander
  (expander (call? 'let*) dbg-let))
