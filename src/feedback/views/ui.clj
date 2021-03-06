(ns feedback.views.ui
  (:use (noir core)
        (hiccup core page-helpers)))

(defpage "/feedback" {}
  (html5
   [:head
    [:meta {:charset "UTF-8"}]
    [:title "Feedback"]
    (include-css "/css/main.css")
    (include-css "/css/panel.css")
    (javascript-tag "var CLOSURE_NO_DEPS = true;")
    (include-js "/js/feedback.js")]
   [:body
    [:div#wrapper
      [:div#header]
      [:div#main]]
    (javascript-tag "feedback.ui.init()")]))

