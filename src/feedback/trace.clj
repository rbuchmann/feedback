(ns feedback.trace
  (:require [clojure.string     :as str]
            [noir.fetch.remotes :as r])
  (:use [feedback.analyze :only [get-path]]))

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

(defn paths-in-call-order? [[x & xs]
                            [y & ys]]
  (or (< x y)
      (when (= x y)
        (or (not xs)
            (when ys
              (recur xs ys))))))

(defn- already-seen?
  "Checks if a form has been seen before in the current trace"
  [{:path last} {:path current}]
  ((paths-in-call-order? last current)))

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
  [form]
  (let [last-iteration (peek @*trace*)]
    (trace!
     (iteration form last-iteration)))
  nil)

(defn log-call [type form & {:as argmap}]
  `(protocol ~(merge {:type type
                      :path (get-path form)
                      :source `'~form}
                     argmap)))
