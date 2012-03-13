(ns feedback.server
  (:require [noir.server :as server]
            [noir.fetch.remotes :as r]
            [feedback.core]
            [feedback.remotes]
            [feedback.views.ui])
  (:gen-class))

(server/load-views-ns 'feedback.views)

(server/add-middleware r/wrap-remotes)

(defn start []
  (server/start 8080 {:mode :dev
                      :ns 'feedback}))
