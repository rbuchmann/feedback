(ns feedback.trace
  (:require [clojure.string     :as str]
            [noir.fetch.remotes :as r]))

(def feedbacks (atom []))

(def ^:dynamic *trace* nil)

(defmacro with-trace [& forms]
  `(binding [*trace* (atom [])]
     (let [res# (do ~@forms)]
       (swap! feedbacks conj @*trace*)
       res#)))

(defn trace! [iteration]
  (swap! *trace* conj iteration))

(defn clear-and-return []
  (let [res @feedbacks]
    (swap! feedbacks
           (constantly []))
    res))

(r/defremote watch-feedbacks []
  @(future 
     (while (empty? @feedbacks)
       (Thread/sleep 500))
     (clear-and-return)))

(defn form [type id args]
  (assoc args
    :type    type
    :form-id id))

(def make-binding (juxt :form-id :internal-id))

(defn- already-seen?
  "Checks if a form has been seen before in the current trace"
  [last current]
  (> (compare (make-binding last)
              (make-binding current))
     0))

(defn- iteration
  "Compute an iteration step from a form description
   and the previous iteration"
  [form previous]
  (assoc form
    :iteration
    (if previous
      (if-not (already-seen? previous form)
        (:iteration previous)
        (inc (:iteration previous)))
      0)))

(defn protocol
  "Write the values of a binding form into the currently
   bound trace"
  [type form-id & {:keys [value] :as args}]
  (let [trace-form     (form type form-id args)
        last-iteration (peek @*trace*)]
    (trace!
      (iteration trace-form last-iteration)))
  value)
