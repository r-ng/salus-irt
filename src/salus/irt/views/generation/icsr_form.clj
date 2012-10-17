(ns salus.irt.views.generation.icsr-form
    (:require [hiccup.form :as h]))


(defn text [f] {:naming f})

(defn selector [f & options]
    {:selector options, :naming f})

(defn div [class naming inner]
    (let [attrs (conj {:naming naming}
                      (if (vector? class) {:class (first class), :header-tag (second class)}
                                          {:class class}))]
        [::div attrs inner]))

(defn id-prefix [p & inner] [::id-modifier #(str p %) inner])

(defn id-suffix [s & inner] [::id-modifier #(str % s) inner])

;(defn same-line [& inner]    [::g/layout :same-line inner])
;
;(defn one-per-line [& inner] [::g/layout :one-per-line inner])


(defn get-label-and-id [names]
    (let [make-id (fn [label] [label
                               (-> label (.replace " " "") .toLowerCase)])]
        (cond (sequential? names) (let [[label id] names]
                                     (if id names
                                            (make-id label)))
              (string? names) (make-id names))))

(defn node-type [n]
    (cond (sequential? n) (if (keyword? (first n))
                              (first n)
                              ::enumeration)
          (string? n) ::simple-text-field
          (map? n)    ::field))

(defn query-map [m & c] (loop [clauses c]
    (if clauses
        (let [[keys func & clauses-rest] clauses]
            (if (and keys func)
                (let [values (map m keys)]
                    (if (reduce #(and %1 %2) true values)
                        (apply func values)
                        (recur clauses-rest)))
                (throw (IllegalArgumentException.
                        "query-map requires a even number of forms")))))))


(defmulti icsr-node->hiccup (fn [node & args] (node-type node)))
; Returns a _list_ of hiccup nodes

(defmethod icsr-node->hiccup ::enumeration [content id-modifier]
    (map #(icsr-node->hiccup % id-modifier) content))

(defmethod icsr-node->hiccup ::simple-text-field [label id-modifier]
    (let [[label id] (get-label-and-id label)
          id         (id-modifier id)
          attrs      {:id id, :name id}]
        (list (h/label id label) (h/text-field attrs ""))))

(defmethod icsr-node->hiccup ::field [field id-modifier]
    (let [[label id] (get-label-and-id (:naming field))
          id     (id-modifier id)
          attrs  {:id id, :name id}
          field  (query-map field
                     [:selector]   #(h/drop-down attrs "" %)
                     [:date]       (fn [format] (h/text-field (conj attrs [:class "datepicker"]) ""))
                                      ;; TODO: Handle format (if different than "") through javascript
                     []            #(h/text-field attrs ""))]
        (let [opts (:opts field)]
            (if (and opts (:no-label opts))
                field
                (list (h/label id label) field)))))

(defmethod icsr-node->hiccup ::div [[_ params inner] id-modifier]
    (let [[label id] (get-label-and-id (:naming params))
          attrs      (conj (select-keys params #{:class}) [:id id])
          header     (if-let [tag (:header-tag params)]
                        [tag label] '())]
        [:div attrs header (map #(icsr-node->hiccup % id-modifier) inner)]))

(defmethod icsr-node->hiccup ::id-modifier [[_ func inner] id-modifier]
    (map #(icsr-node->hiccup % (comp id-modifier func)) inner))


(defn icsr-definition->hiccup [node]
    (icsr-node->hiccup node identity))

