(ns feedback.ui
  (:require [goog.dom :as gdom]
            [crate.core :as crate]
            [fetch.remotes :as remotes])
  (:require-macros [fetch.macros :as fm]))

(defn s
  "Convert objects to a readable string, separarated by a space."
  [& objs]
  (pr-str-with-opts objs (assoc (pr-opts) :readably false)))

(defn log [& objs]
  (.log js/console (apply s objs)))

(defn build-dom [feedbacks]
  (log "build")
  [:div#feedbacks
   [:h1 (str "Feedbacks:")]
   (let [trace (first feedbacks)]
     (log trace)
     (for [iteration (partition-by first trace)]
       [:div {:style "display:inline-block"}
        [:ul
         (for [[iter-id let-id binding-id var value] iteration]
           [:li (str var ": " value)])]]))])

(defn update-state []
  (fm/letrem [feedbacks (watch-feedbacks)]
             (let [wrapper (gdom/$ "wrapper")
                   child (-> feedbacks
                             build-dom
                             crate/html)]
               (gdom/removeChildren wrapper)
               (gdom/appendChild wrapper child))))

(defn ^:export init []
  (loop []
    (update-state)
;    (recur)
))
