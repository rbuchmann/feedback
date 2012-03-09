(ns feedback.expanders.funcall
  (:use [feedback.analyze :only [transform *form-id*]]
        [feedback.trace :only [protocol]]))

(defn dbg-funcall [fsym]
  (fn [& args]
    (let [form `(~fsym ~@(map transform args))]
      #_(prn "form" (str "<" form ">"))
      `(protocol :funcall ~*form-id*
                 :call '(~fsym ~@args)
                 :value ~form))))

(def arith-expanders
  {'+ (dbg-funcall '+)
   '- (dbg-funcall '-)
   '* (dbg-funcall '*)
   '/ (dbg-funcall '/)})
