(ns feedback.expanders.funcall
  (:use [feedback.analyze :only [transform]]
        [feedback.expander :only [expander]]
        [feedback.trace :only [log]]))

(defn trace [[op & args] :as form]
  (let [res `res#
        argsyms (vec (map (fn [_]
                            (gensym "arg"))
                          args))]
    `(let [~argsyms [~@(map transform args)]
           ~res (~op ~@argsyms)]
       ~(log-call :trace  form
                  :args   argsyms
                  :result res)
       ~res)))

(def arith-expanders
  [(expander #{`+ `- `* `/} trace)])
