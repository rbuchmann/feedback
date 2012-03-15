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
  (let [rp (goog.ui.RoundedPanel/create 5 1 "#d0d0d0" "#202020" 15)]
    (.decorate rp node)))

(defn build-dom [feedbacks]
  [:div#feedbacks
   [:div
    (let [trace (first feedbacks)]
      (for [iteration (partition-by :iteration trace)]
        [:div {:class "highlight" :style "display:inline-block;font-family:monospace;"}
         (for [line (partition-by :line iteration)]
           [:div
            [:ul {:style "list-style-type:none;padding:5px;"}
             (for [{:keys [source result]} line]
               [:li
                [:span {:class "nv"} (s source)]
                ":"
                [:span {:class "mi"} (s result)]])]])]))]])

(defn- position-trace [source trace]
  (let [source-width (-> source .-style .-width)]
    (set! (.-left trace) source-width)))

(defn update-state []
  (fm/letrem [feedbacks (watch-feedbacks)]
    (let [source (gdom/$ "source")
          trace  (gdom/$ "trace")
          child  (-> feedbacks
                     build-dom
                     crate/html)]
      ;(position-trace source trace)
      (gdom/removeChildren trace)
      (gdom/appendChild trace child)
      (update-state))))

(defn build-function-view [code]
  (let [panel       (gdom/$ "code")
        replacement (crate/html [:div#code {:class "code-panel"}])
        source      (crate/html [:div#source])
        trace       (crate/html [:div#trace])
        code-node   (gdom/htmlToDocumentFragment code)]
    (gdom/appendChild replacement source)
    (gdom/appendChild replacement trace)
    (gdom/appendChild source code-node)
    (decorate replacement)
    (if panel
      (gdom/replaceNode replacement panel)
      (gdom/appendChild (gdom/$ "main") replacement))
    (update-state)))

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
  (add-header)
  (selector/add build-function-view))
