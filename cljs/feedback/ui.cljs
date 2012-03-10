(ns feedback.ui
  (:require [goog.dom             :as gdom]
            [crate.core           :as crate]
            [fetch.remotes        :as remotes]
            [goog.ui.RoundedPanel :as panel])
  (:require-macros [fetch.macros  :as fm]))

(def rp (goog.ui.RoundedPanel/create 5 1 1))

(defn s
  "Convert objects to a readable string, separarated by a space."
  [& objs]
  (pr-str-with-opts objs (assoc (pr-opts) :readably false)))

(defn log [& objs]
  (.log js/console (apply s objs)))

(defn decorate []
  (let [node (gdom/getNode "feedbacks")]
    (.decorate rp dom)))

(defn build-dom [feedbacks]
  (log "build")
  [:div#feedbacks
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
      (decorate)
      (update-state))))

(defn ^:export init []
  (loop []
    (update-state)))
