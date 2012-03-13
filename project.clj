(defproject feedback "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :plugins [[lein-cljsbuild "0.1.2"]]
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [org.clojure/core.match "0.2.0-alpha9"]
                 [noir  "1.2.2"]
                 [fetch "0.1.0-alpha1"]
                 [crate "0.1.0-alpha1"]
                 [clj-tagsoup "0.2.6"]]
  :dev-dependencies [[midje "1.3.2-SNAPSHOT"]]
  :cljsbuild
  {:crossovers [fetch.macros]
   :crossover-path "crossover"
   :builds
   [{:source-path "cljs"
     :compiler {:output-to "resources/public/js/feedback.js"
                ;; :optimizations :advanced
                :optimizations :whitespace
                :pretty-print true}}]})
