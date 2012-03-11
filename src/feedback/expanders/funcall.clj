(ns feedback.expanders.funcall
  (:use [feedback.analyze :only [get-path transform]]
        [feedback.expander :only [expander call?]]
        [feedback.trace :only [trace]]))

(defn dbg-call [[op & args :as form]]
  (let [argsyms (vec (for [_ args]
                       (gensym "arg")))]
    `(let [~argsyms [~@(map transform args)]]
       ~(trace :call `(~op ~@argsyms)
               :source `'~form
               :path (get-path form)
               :args argsyms))))

(def arith-expanders
  [(expander (->> `[+ - * /]
                  (map call?)
                  (apply some-fn))
             dbg-call)])
