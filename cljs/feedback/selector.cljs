(ns feedback.selector
  (:require [goog.dom             :as gdom]
            [goog.events          :as event]
            [crate.core           :as crate]
            [fetch.remotes        :as remotes]
            [goog.ui.ComboBox     :as combo]
            [goog.ui.ComboBoxItem :as combo-item])
  (:require-macros [fetch.macros  :as fm]))

(defn fn-selector []
  (->> [:div#fns {:style "position:relative;width:460px"}
        [:div {:style "float:left;"}
          [:div#nsbox]]
        [:div {:style "float:right;"}
          [:div#fnbox]]]
      (crate/html)
      (gdom/appendChild (gdom/$ "main"))))

(defn replace-items [cb items]
  (.removeAllItems cb)
  (doseq [item items]
    (.addItem cb
              (goog.ui.ComboBoxItem. item))))

(defn add [on-fn-selected]
  (fn-selector)
  (let [ns-combo (goog.ui.ComboBox.)
        fn-combo (goog.ui.ComboBox.)
        ns-div   (gdom/$ "nsbox")
        fn-div   (gdom/$ "fnbox")]
    (doto ns-combo
      (.setUseDropdownArrow true)
      (.setDefaultText "name spaces...")
      (.addItem (goog.ui.ComboBoxItem. "name spaces...")))
    (doto fn-combo
      (.setUseDropdownArrow true)
      (.setDefaultText "functions..."))
    (.render ns-combo ns-div)
    (.render fn-combo fn-div)
    (event/listen ns-div "click"
      (fn [_]
        (fm/letrem [namespaces (namespaces)]
          (replace-items ns-combo namespaces))))
    (event/listen ns-combo "change"
      (fn [evt]
        (fm/letrem [fns (publics (.getValue (.-target evt)))]
                   (replace-items fn-combo fns))))
    (event/listen fn-combo "change"
      (fn [evt]
        (fm/letrem [code (styled-code (.getValue ns-combo)
                                      (.getValue fn-combo))]
          (on-fn-selected code))))))
