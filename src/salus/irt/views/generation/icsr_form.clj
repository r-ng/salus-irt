(ns salus.irt.views.generation.icsr-form
    (:require [hiccup.form :as h]))


(defn text [n] {:naming n})

(defn text-area [n size]
    {:text-area size, :naming n})

(defn selector [n & options]
    {:selector options, :naming n})

(defn section [n & inner]
    [::section {:naming n} inner])

(defn id-prefix [p & inner] [::id-modifier #(str p %) inner])

(defn id-suffix [s & inner] [::id-modifier #(str % s) inner])

;(defn same-line [& inner]    [::g/layout :same-line inner])
;
;(defn one-per-line [& inner] [::g/layout :one-per-line inner])


(defn get-label-and-id [naming]
    (let [make-id (fn [label] [label
                               (-> label (.replace " " "") .toLowerCase)])]
        (cond (sequential? naming) (let [[label id] naming]
                                     (if id naming
                                            (make-id label)))
              (string? naming) (make-id naming))))

(defn node-type [n]
    (cond (sequential? n) (if (keyword? (first n))
                              (first n)
                              ::enumeration)
          (string? n) ::simple-text-field
          (map? n)    ::field))


(defn- query-map [m & c] (loop [clauses c]
    (if clauses
        (let [[keys func & clauses-rest] clauses]
            (if (and keys func)
                (let [values (map m keys)]
                    (if (every? #(not= % nil) values)
                        (apply func values)
                        (recur clauses-rest)))
                (throw (IllegalArgumentException.
                        "query-map requires a even number of forms")))))))

(declare icsr-node->hiccup)

(defn- rec-icsr [accum-state inner]
    (doall (map #(icsr-node->hiccup % accum-state) inner)))

(defmulti icsr-node->hiccup (fn [node & args] (node-type node)))
; Returns a _list_ of hiccup nodes

(defmethod icsr-node->hiccup ::enumeration [inner accum-state]
    (rec-icsr accum-state inner))

(defmethod icsr-node->hiccup ::simple-text-field [label {:keys [id-modifier]}]
    (let [[label id] (get-label-and-id label)
          id         (id-modifier id)
          attrs      {:id id, :name id}]
        (list (h/label id label) (h/text-field attrs ""))))

(defmethod icsr-node->hiccup ::field [field {:keys [id-modifier]}]
    (let [[label id] (get-label-and-id (:naming field))
          id     (id-modifier id)
          attrs  {:id id, :name id}
          field  (query-map field
                     [:selector]   #(h/drop-down attrs "" %)
                     [:date] (fn [format]  ;; TODO: Handle format (if different than "") through javascript
                        (h/text-field (conj attrs [:class "datepicker"]) ""))
                     [:text-area] (fn [size]  ;; TODO: See if size can be handled
                        (h/text-area attrs ""))
                     []   #(h/text-field attrs ""))]
        (let [opts (:opts field)]
            (if (and opts (:no-label opts))
                field
                (list (h/label id label) field)))))

(defmethod icsr-node->hiccup ::section [[_ params inner] accum-state]
    (let [[label id] (get-label-and-id (:naming params))
          attrs      (conj (select-keys params #{:class}) [:id id])
          [block-tag header]  (case (:section-level accum-state)
                0 [:div      [:h2 label]]
                1 [:fieldset [:legend label]]
                  [:div      [:p label]])]
        [block-tag attrs header
            (rec-icsr (merge-with + accum-state {:section-level 1}) inner)]))

(defmethod icsr-node->hiccup ::id-modifier [[_ func inner] accum-state]
    (rec-icsr (merge-with comp accum-state {:id-modifier func}) inner))


(defn icsr-definition->hiccup [node]
    (icsr-node->hiccup node {:id-modifier identity
                             :section-level 0
                             :jq-init-code (atom [])}))

