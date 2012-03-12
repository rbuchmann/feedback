(ns feedback.expanders.funcall
  (:use [feedback.analyze :only [get-path transform]]
        [feedback.expander :only [expander call?]]
        [feedback.trace :only [trace]]))

(defn dbg-call [[op & args :as form]]
  (let [argsyms (vec (for [_ args]
                       (gensym "arg")))
        res-sym `res#]
    `(let [~argsyms [~@args]
           ~res-sym (~op ~@argsyms)]
       ~(trace :call form
               :args argsyms
               :result res-sym)
       ~res-sym)))

(def arith-expander
  (expander (->> `[+ - * /]
                 (map call?)
                 (apply some-fn))
            dbg-call))
