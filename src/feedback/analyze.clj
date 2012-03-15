(ns feedback.analyze
  (:require [clojure.walk :as w]))

(defn with-meta*
  "Like with-meta, but doesn't fail on non IMeta objects"
  [obj m]
  (if (instance? clojure.lang.IMeta obj)
    (vary-meta obj merge m)
    obj))

(defn walk
  "Like clojure.walk/walk, but preserves metadata"
  [inner outer form]
  (let [res (w/walk inner outer form)]
    (with-meta* res (meta form))))

(defn get-path [form]
  (let [p (::path (meta form) :not-found)]
    (when-not (= p :not-found)
      (vec (reverse p)))))

(def ^:dynamic *path* nil)

(defmacro alter!
  "updates a dynamic variable"
  [var f & args]
  `(set! ~var (~f ~var ~@args)))

(defn push-path []
  (alter! *path* conj 0))

(defn pop-path []
  (alter! *path* next))

(defn inc-path []
  (alter! *path* (fn [[x & xs]]
                   (when x
                     (cons (inc x)
                           xs)))))

(defn rec-add-path [form]
  (let [form (with-meta* form {::path *path*})]
    (push-path)
    (walk rec-add-path
          (fn [frm]
            (pop-path)
            (inc-path)
            frm)
          form)))

(defn with-path [form]
  (binding [*path* nil]
    (rec-add-path form)))

(declare transform)

(defn clj-macroexpand [form]
  (let [res (macroexpand-1 form)]
    (when-not (= res form)
      (fn []
        (with-meta res (meta form))))))

(def ^:dynamic *expanders* ())

(defmacro with-expanders [exps & body]
  `(binding [*expanders* (concat ~exps *expanders*)]
     ~@body))

(defmacro without-expanders [exps & body]
  `(binding [*expanders* (remove (set exps)
                                 *expanders*)]
     ~@body))

(defn quoted? [form]
  (and (seq? form)
       (= 'quote (first form))))

(defn expand? [form]
  (and (not (::dont-expand (meta form)))
       (not (quoted? form))))

(defn find-expander [form]
  (when (get-path form)
    (some #(% form) *expanders*)))

(defn transform [form]
  (if (expand? form)
    (if-let [exp (or (find-expander form)
                     (clj-macroexpand form))]
      (recur (exp))
      (walk transform identity form))
    form))

(def analyze (comp transform with-path))

(def analyze-and-eval (comp eval analyze))
