(ns feedback.remotes
  (:require [feedback.trace :as trace]
            [noir.fetch.remotes :as r]))

(r/defremote watch-feedbacks []
  @(future
     (while (empty? @trace/feedbacks)
       (Thread/sleep 500))
     (trace/clear-and-return)))

(def sort-map (comp sort map))

(r/defremote namespaces []
  (sort-map (comp name ns-name) (all-ns)))

(r/defremote publics [ns]
  (->> ns
       (symbol)
       (ns-publics)
       (filter (comp fn? deref second))
       (sort-map (comp name first))))
