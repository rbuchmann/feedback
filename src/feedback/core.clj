(ns feedback.core
  (:require [clojure.java.io         :as io]
            [feedback.expanders.let  :as lt]
            [feedback.expanders.defn :as dfn])
  (:use     [clojure.contrib.core :only (-?>)]
            [feedback.analyze     :only (analyze-and-eval with-expanders)]
            [feedback.trace       :only (with-trace)])
  (:import (java.io LineNumberReader InputStreamReader PushbackReader)))

(def stored-fns (atom []))

(defn make-feedback [sym args]
  {:name sym :args args})

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
          (read (PushbackReader. pbr)))))))

(defn feedback-fn [v args]
  (-?> v
       (source-fn)
       (analyze-and-eval)
       (apply args)))

(defmacro feedback [f & args]
  `(swap! stored-fns
          conj
          (make-feedback '~f ~(vec args))))

(defn run []
  (with-expanders [dfn/defn-expander lt/let-expander]
    (doseq [f @stored-fns]
      (with-trace
        (feedback-fn (resolve (:name f)) (:args f)))))
  stored-fns)

(defn test-fn [x]
  (let [a (inc x)
        b (inc a)]
    (if (<= b 10)
      (recur b)
      b)))
