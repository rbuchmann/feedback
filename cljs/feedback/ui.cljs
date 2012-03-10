(ns feedback.ui
  (:require [goog.dom      :as gdom]
            [crate.core    :as crate]
            [fetch.remotes :as remotes])
  (:require-macros [fetch.macros :as fm]))

(defn s
  "Convert objects to a readable string, separarated by a space."
  [& objs]
  (pr-str-with-opts objs (assoc (pr-opts) :readably false)))

(defn log [& objs]
  (.log js/console (apply s objs)))

(defn build-dom [feedbacks]
  [:div#feedbacks
   [:h1 (str "Feedbacks:")]
   (let [trace (first feedbacks)]
     (for [iteration (partition-by :iteration trace)]
       [:div {:style "display:inline-block"}
        [:ul
         (for [{:keys [var value]} iteration]
           [:li (str (s var) ": " (s value))])]]))])

(defn update-state []
  (fm/letrem [feedbacks (watch-feedbacks)]
    (let [wrapper (gdom/$ "wrapper")
          child   (-> feedbacks
                      build-dom
                      crate/html)]
      (gdom/removeChildren wrapper)
      (gdom/appendChild wrapper child)
      (update-state))))

(defn ^:export init []
  (loop []
    (update-state)))
