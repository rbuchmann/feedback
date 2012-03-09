(ns feedback.analyze
  (:use [feedback.trace :only [with-trace]]))

(def ^:dynamic *expanders* {})
(def ^:dynamic *form-id* nil)

(defmacro with-expanders [exps & body]
  `(binding [*expanders* ~exps]
     ~@body))

(defmacro with-form-id [id & body]
  `(binding [*form-id* ~id]
     ~@body))

(defn expand [form]
  #_(prn "expand" form *form-id* (keys *expanders*))
  (if-let [expander (*expanders* (first form))]
    (do (set! *form-id* (inc *form-id*))
        #_(prn "set form-id to" *form-id*)
        (apply expander (rest form)))
    ::no-expander))

(defn dont-expand? [form]
  (or (not (seq? form))
      (= 'quote (first form))))

(defn transform [form]
  #_(prn "trans" form)
  (if (dont-expand? form)
    form
    (let [expansion       (expand form)
          expanded?       (not= expansion ::no-expander)
          macro-expansion (macroexpand-1 form)
          macro-expanded? (not= form macro-expansion)]
      (cond expanded?       expansion
            macro-expanded? (transform macro-expansion)
            :else           (doall (map transform form))))))

(defn analyze [form]
  (with-form-id -1
    (transform form)))

(defn analyze-and-eval [form]
  (with-trace
    (eval (analyze form))))
