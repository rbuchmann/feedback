(ns feedback.server
  (:require [noir.server :as server]
            [noir.fetch.remotes :as r]
            [feedback.core]
            [feedback.views.ui])
  (:gen-class))

(server/load-views-ns 'feedback.views)

(server/add-middleware r/wrap-remotes)

(defn -main []
  (server/start 8080 {:mode :dev
                      :ns 'feedback}))
