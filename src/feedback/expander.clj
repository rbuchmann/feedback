(ns feedback.expander
  (:require [clojure.string :as str]))

(defn expander [guard expand]
  (fn [form]
    (when (guard form)
      (fn [] (expand form)))))

(defn qualify [sym]
  (let [v (when (symbol? sym)
            (resolve sym))]
    (if v
      (-> (str v)
          (.substring 2)
          symbol)
      sym)))

(defn call? [sym]
  (fn [form]
    (and (seq? form)
         (= (qualify sym)
            (qualify (first form))))))

(defmacro macro-expander [sym args & body]
  `(expander (call? '~sym)
             (fn [[_# ~@args]]
               ~@body)))

(defn ungenify [sym]
  (-> (name sym)
      (str/replace #"__[0-9]*" "#")
      symbol))
