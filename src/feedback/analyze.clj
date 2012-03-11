(ns feedback.analyze
  (:require [clojure.walk :as cljwalk]))

(defn with-meta*
  "Like with-meta, but doesn't fail on non IMeta objects"
  [obj m]
  (if (instance? clojure.lang.IMeta obj)
    (with-meta obj m)
    obj))

(defn walk
  "Like clojure.walk/walk, but preserves metadata"
  [inner outer form]
  (let [res (cljwalk/walk inner outer form)]
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
        (transform res)))))

(def ^:dynamic *expanders* (list clj-macroexpand))

(defmacro with-expanders [exps & body]
  `(binding [*expanders* (concat ~exps *expanders*)]
     ~@body))

(defmacro without-expanders [exps & body]
  `(binding [*expanders* (remove (set exps)
                                 *expanders*)]
     ~@body))

(defn expand? [form]
  (not (and (seq? form)
            (= 'quote (first form)))))

(defn find-expander [form]
  (when (expand? form)
    (some #(% form) *expanders*)))

(defn transform [form]
  (if-let [exp (find-expander form)]
    (exp)
    (walk transform identity form)))

(def analyze (comp transform with-path))

(def analyze-and-eval (comp eval analyze))
