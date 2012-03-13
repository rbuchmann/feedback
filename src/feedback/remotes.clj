(ns feedback.remotes
  (:require [clojure.java.io        :as io]
            [feedback.trace         :as trace]
            [noir.fetch.remotes     :as r])
  (:use [clojure.java.shell   :only (sh)]
        [clojure.contrib.core :only (-?>)])
  (:import (java.io LineNumberReader InputStreamReader PushbackReader)))

(r/defremote watch-feedbacks []
  @(future
     (while (empty? @trace/feedbacks)
       (Thread/sleep 500))
     (trace/clear-and-return)))

(def sort-map (comp sort map))

(r/defremote namespaces []
  (sort-map (comp name ns-name) (all-ns)))

(r/defremote publics [ns]
  (->> ns
       (symbol)
       (ns-publics)
       (filter (comp fn? deref second))
       (sort-map (comp name first))))

(def highlight
  (comp :out
        (partial sh "/usr/bin/pygmentize" "-f" "html" "-l" "clojure" :in)))

(defn source-fn
  "Stolen from clojure.repl.source"
  [v]
  (when-let [filepath (:file (meta v))]
    (when-let [strm (or (-?> (io/resource filepath)
                             (io/input-stream))
                        (io/input-stream filepath))]
      (with-open [rdr (LineNumberReader. (InputStreamReader. strm))]
        (dotimes [_ (dec (:line (meta v)))]
          (.readLine rdr))
        (let [text (StringBuilder.)
              pbr (proxy [PushbackReader] [rdr]
                    (read [] (let [i (proxy-super read)]
                               (.append text (char i))
                               i)))]
          (read (PushbackReader. pbr))
          (str text))))))

(r/defremote styled-code [ns name]
  (-> (ns-resolve (symbol ns) (symbol name))
      (source-fn)
      (str)
      (highlight)))
