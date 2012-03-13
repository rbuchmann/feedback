(ns feedback.formatter)

(defn s
  "Convert objects to a readable string, separarated by a space."
  [& objs]
  (pr-str-with-opts objs (assoc (pr-opts) :readably false)))

(defn log [& objs]
  (.log js/console (apply s objs)))

(def renderers (atom ()))

(defn render [x]
  (some (fn [[pred render]]
          (when (pred x)
            (render x)))
        @renderers))

(defn add-renderer [pred render]
  (swap! renderers conj [pred render]))

(defn add-renderers [& preds-and-renderers]
  (doseq [[pred render] (partition 2 preds-and-renderers)]
    (add-renderer pred render)))

(defn div [class & args]
  [:div {:class (str (name class) " clj")}
   args])

(def boolean? #{true false})

(add-renderers
 (constantly true) #(div :other
                         (str %))
 seq?              #(div :list
                         (map render %))
 vector?           #(div :vector
                         (map render %))
 set?              #(div :set
                         (map render %))
 map?              #(div :map
                         (interpose [:div.map-separator]
                                    (for [[k v] %]
                                      [:div.map-entry
                                       [:div.map-key (render k)]
                                       [:div.map-value (render v)]])))
 string?          #(div :string %)
 keyword?         #(div :keyword (str %))
 number?          #(div :number (str %))
 boolean?         #(div :boolean (str %))
 nil?             #(div :nil))
