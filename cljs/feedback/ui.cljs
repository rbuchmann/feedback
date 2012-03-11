(ns feedback.ui
  (:require [goog.dom             :as gdom]
            [goog.events          :as event]
            [crate.core           :as crate]
            [fetch.remotes        :as remotes]
            [feedback.selector          :as selector]
            [goog.ui.ComboBox     :as combo]
            [goog.ui.ComboBoxItem :as combo-item]
            [goog.ui.RoundedPanel :as panel])
  (:require-macros [fetch.macros  :as fm]))

(defn s
  "Convert objects to a readable string, separarated by a space."
  [& objs]
  (pr-str-with-opts objs (assoc (pr-opts) :readably false)))

(defn log [& objs]
  (.log js/console (apply s objs)))

(defn decorate []
  (log "decorate")
  (let [rp   (goog.ui.RoundedPanel/create 5 1 "#fedcba" "#abcdef" 15)
        node (gdom/$ "feedbacks")]
    (.decorate rp node)))

(defn build-dom [feedbacks]
  [:div#feedbacks
   [:div {:class "goog-roundedpanel-content"}
    (let [trace (first feedbacks)]
      (for [iteration (partition-by :iteration trace)]
        [:div {:style "display:inline-block"}
         [:ul {:style "list-style-type:none"}
          (for [{:keys [var value]} iteration]
            [:li (str (s var) ": " (s value))])]]))]])

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

(defn add-header []
  (gdom/appendChild
    (gdom/$ "header")
    (crate/html
     [:embed {:src    "images/parakeet.svg"
              :type   "image/svg+xml"
              :width  110
              :height 60
              :style  "position:absolute;right:0px;margin:15px"}])))

(defn ^:export init []
  (loop []
    (add-header)
    (selector/add)
;    (update-state)
    ))
