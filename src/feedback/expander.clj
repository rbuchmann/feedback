(ns feedback.expander
  (:require [clojure.string :as str]))

(defn expander [guard expand]
  (fn [form]
    (when (guard form)
      (fn [] (expand form)))))

(defn call? [sym]
  (fn [form]
    (and (list? form)
         (= sym (first form)))))

(defn ungenify [sym]
  (-> (name sym)
      (str/replace #"__[0-9]*" "#")
      symbol))
