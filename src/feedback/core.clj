(ns feedback.core
  (:require [clojure.java.io :as io])
  (:import (java.io LineNumberReader InputStreamReader PushbackReader)))

(defn source-fn
  "Stolen from clojure.repl.source"
  [v]
  (when-let [filepath (:file (meta v))]
    (when-let [strm (or (io/input-stream (io/resource filepath))
                        (io/input-stream filepath))]
      (with-open [rdr (LineNumberReader. (InputStreamReader. strm))]
        (dotimes [_ (dec (:line (meta v)))]
          (.readLine rdr))
        (let [text (StringBuilder.)
              pbr (proxy [PushbackReader] [rdr]
                    (read [] (let [i (proxy-super read)]
                               (.append text (char i))
                               i)))]
          (read (PushbackReader. pbr)))))))

(declare analyze-code)

(defn feedback-fn [v]
  (when v
    (when-let [code (source-fn v)]
      (analyze-code code))))

(defmacro feedback [f]
  `(feedback-fn ~(ns-resolve *ns* f)))
