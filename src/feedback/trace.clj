(ns feedback.trace
  (:require [clojure.string :as str])
  (:use [feedback.analyze :only [get-path analyze-and-eval]]))

(def feedbacks (atom []))

(def ^:dynamic *trace* nil)

(defmacro with-trace [& forms]
  `(binding [*trace* (atom [])]
     (let [res# (do ~@forms)]
       (swap! feedbacks conj @*trace*)
       {:result res#
        :trace  @*trace*})))

(defn run-traced [form]
  (with-trace (analyze-and-eval form)))

(defn trace! [iteration]
  (swap! *trace* conj iteration))

(defn clear-and-return []
  (let [res @feedbacks]
    (swap! feedbacks
           (constantly []))
    res))

(defn paths-in-eval-order?
  "Is the first node called before the second under eager evaluation,
   meaning inside-first?"
  [[x & xs] [y & ys]]
  (when x
    (let [res (compare x y)]
      (or (not y)
          (neg? res)
          (and (zero? res)
               (or xs ys)
               (or (not ys)
                   (recur xs ys)))))))

(defn- forms-in-eval-order?
  "Checks if a form has been seen before in the current trace"
  [{last :path} {current :path}]
  (paths-in-eval-order? last current))

(defn- iteration
  "Compute an iteration step from a form description
   and the previous iteration"
  [form previous]
  (assoc form
    :iteration
    (if previous
      (if (forms-in-eval-order? previous form)
        (:iteration previous)
        (inc (:iteration previous)))
      0)))

(defn protocol
  "Write the values of a binding form into the currently
   bound trace"
  [form]
  (let [last-iteration (peek @*trace*)]
    (trace!
     (iteration form last-iteration)))
  nil)

(defn trace [type form & {:as argmap}]
  (with-meta `(when *trace*
                (protocol ~(merge {:type type
                                   :path (get-path form)
                                   :source `'~form
                                   :result `(pr-str ~form)}
                                  argmap)))
    {:feedback.analyze/dont-expand true}))
