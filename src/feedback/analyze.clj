(ns feedback.analyze
  (:require [clojure.walk :as cljwalk])
  (:use [feedback.expander :only [ignorer]]))

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
  (-> form
      meta
      ::path
      reverse
      vec))

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

(declare macro-expander)

(def ^:dynamic *expanders* (list macro-expander))

(defmacro with-expanders [exps & body]
  `(binding [*expanders* (concat ~exps *expanders*)]
     ~@body))

(defmacro without-expanders [exps & body]
  `(binding [*expanders* (remove (set exps)
                                 *expanders*)]
     ~@body))

(defn find-expander [form]
  (when-not (and (list? form)
                 (= 'quote (first form)))
    (some #(% form) *expanders*)))

(defn transform [form]
  (if-let [exp (find-expander form)]
    (exp)
    (walk transform identity form)))

(defn macro-expander [form]
  (let [res (macroexpand-1 form)]
    (when-not (= res form)
      (fn []
        (transform res)))))

(def analyze (comp transform with-path))

(def analyze-and-eval (comp eval analyze))
