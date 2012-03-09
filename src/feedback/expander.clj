(ns feedback.expander
  (:require [clojure.string :as str])
  (:use [clojure.core.match :only [match]]))

(defmacro defexpander [name pattern & body]
  (let [expander `(fn []
                    ~@body)]
    `(defn ~name [form#]
       (match form# (~pattern :seq) ~expander))))

(defn ungenify [sym]
  (-> (name sym)
      (str/replace #"__[0-9]*" "#")
      symbol))
