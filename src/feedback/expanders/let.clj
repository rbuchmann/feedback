(ns feedback.expanders.let
  (:use [feedback.analyze :only [transform *form-id*]]
        [feedback.expander :only [defexpander ungenify]]
        [feedback.trace :only [protocol]]))

(defexpander dbg-let ['let* bindings & body]
  (let [transform-binding (fn [bind-id [var-sym value]]
                            [var-sym
                             `(protocol :let         ~*form-id*
                                        :internal-id ~bind-id
                                        :var         '~(ungenify var-sym)
                                        :value       ~(transform value))])
        new-bindings (->> bindings
                          (partition 2)
                          (map-indexed transform-binding)
                          (apply concat)
                          vec)
        new-body (vec (map transform body))
        ret `(let ~new-bindings
               ~@new-body)]

    ret))

(def let-expanders
  [dbg-let])
