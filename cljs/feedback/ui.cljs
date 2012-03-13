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

(defn decorate [node]
  (log "decorate")
  (let [rp (goog.ui.RoundedPanel/create 5 1 "#d0d0d0" "#202020" 15)]
    (.decorate rp node)))

(defn build-dom [code]
  [:div#feedbacks
   [:div {:class "goog-roundedpanel-content"}
    (let [trace (first feedbacks)]
      (for [iteration (partition-by :iteration trace)]
        [:div {:style "display:inline-block"}
         [:ul {:style "list-style-type:none"}
          (for [{:keys [source result]} iteration]
            [:li (str (s source) ": " (s result))])]]))]])

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

(defn build-function-view [code]
  (let [panel       (gdom/$ "code")
        replacement (crate/html [:div#code {:class "code-panel"}])
        code-node   (gdom/htmlToDocumentFragment code)]
    (gdom/appendChild replacement code-node)
    (decorate replacement)
    (if panel
      (gdom/replaceNode replacement panel)
      (gdom/appendChild (gdom/$ "main") replacement))))

(defn add-header []
  (let [header (gdom/$ "header")]
    (gdom/appendChild
     header
     (crate/html
      [:div {:style "width:100%"}
       [:embed {:src    "images/parakeet.svg"
                :type   "image/svg+xml"
                :width  110
                :height 60
                :style  "float:right;margin:15px"}]
       [:div {:class "header-bottom"}]]))))

(defn ^:export init []
  (loop []
    (add-header)
    (selector/add build-function-view)
;    (update-state)
    ))
