(ns feedback.analyze
  (:use [feedback.trace :only [with-trace]]))

(def ^:dynamic *expanders* [])
(def ^:dynamic *form-id* nil)

(defmacro with-expanders [exps & body]
  `(binding [*expanders* ~exps]
     ~@body))

(defmacro with-form-id [id & body]
  `(binding [*form-id* ~id]
     ~@body))

(defn find-expander [form]
  (some #(% form) *expanders*))

(defn expand [expander]
  #_(prn "expand" *form-id*)
  (set! *form-id* (inc *form-id*))
  (expander))

(defn dont-expand? [form]
  (or (not (seq? form))
      (= 'quote (first form))))

(defn call-macroexpand [form]
  (let [res (macroexpand-1 form)]
    (when-not (= res form)
      res)))

(defn transform [form]
  #_(prn "trans" form *form-id* *expanders*)
  (if (dont-expand? form)
    form
    (let [expander        (find-expander form)
          macro-expansion (call-macroexpand form)]
      (cond expander        (expand expander)
            macro-expansion (transform macro-expansion)
            :else           (doall (map transform form))))))

(defn analyze [form]
  (with-form-id -1
    (transform form)))

(defn analyze-and-eval [form]
  (with-trace
    (eval (analyze form))))
