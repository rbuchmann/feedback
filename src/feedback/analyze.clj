(ns feedback.analyze
  (:use [clojure.walk :only [walk]]
        [feedback.trace :only [with-trace]]))

(def ^:dynamic *expanders* [])
(def ^:dynamic *form-id* nil)

(defn add-path [path form]
  (if (instance? clojure.lang.IMeta form)
    (with-meta form {::path path})
    form))

(defn get-path [form]
  (::path (meta form)))

(defn map-entry? [x]
  (instance? clojure.lang.IMapEntry x))

(defn rec-add-path [path form]
  (let [res (if (coll? form)
              (let [res-seq (map-indexed (fn [i f]
                                           (rec-add-path (conj path i)
                                                         f))
                                         form)]
                (cond (list? form)      (apply list res-seq)
                      (map-entry? form) (vec res-seq)
                      (seq? form)       (doall res-seq)
                      :else             (into (empty form) res-seq)))
              form)]
    (add-path path res)))

(defn with-path [form]
  (rec-add-path [] form))

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

(def analyze-and-eval (comp eval analyze))
