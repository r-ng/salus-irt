(ns salus.irt.views.generation.icsr-form
    (:require [hiccup.form :as h]))


(defn node-type [n]
    (cond (vector? n) (if (keyword? (first n))
                          ::block-with-opts
                          ::simple-block)
          (list? n)   ::field-with-opts
          (string? n) ::simple-field))

(defn query-map [m & clauses]
    (if clauses
        (let [[keys func & clauses-rest] clauses]
            (if (and keys func)
                (let [values (map m keys)]
                    (if (reduce #(and %1 %2) true keys)
                        (apply func values)
                        (query-map m clauses-rest)))
                (throw (IllegalArgumentException. "query-map requires a even number of forms"))))))


(defmulti icsr-node->hiccup (fn [node & args] (node-type node)))
; Returns a _list_ of hiccup nodes

(defmethod icsr-node->hiccup ::simple-block [content prefix suffix]
    (map #(icsr-node->hiccup % prefix suffix) content))

(defmethod icsr-node->hiccup ::simple-field [field-name prefix suffix]
    (let [full-name (str prefix field-name suffix)]
        (list (h/label full-name full-name) (h/text-field full-name))))

(defmethod icsr-node->hiccup ::field-with-opts [[field-name & {:as opts}] prefix suffix]
    (let [full-name (str prefix field-name suffix)
          field     (query-map opts
                        [:select-in]   #(h/drop-down full-name %)
                        []             #(h/text-field full-name))]
        (if-let [l (:label opts)]
            (if (= l :none) field
                            (list (h/label l l) field))
            (list (h/label full-name full-name) field))))

(defmethod icsr-node->hiccup ::block-with-opts [[opt param & content] prefix suffix]
    (let [assert-param  (fn [f n] (assert (f param)
                                          (str "In ICSR definition: " opt " not followed by a " n ".")))
          assert-string #(assert-param string? "string")
          rec           (fn [p s] (map #(icsr-node->hiccup % p s) content))
          div-elem      (fn [css-class header-tag]
                            (do (assert-string)
                                [:div {:class css-class}
                                    [header-tag param]
                                    (rec prefix suffix)]))]
        (case opt :prefix (do (assert-string)
                              (rec (str prefix param) suffix))
                  :suffix (do (assert-string)
                              (rec prefix (str param suffix)))
                  :section (div-elem "section" :h3)
                  :tab     (div-elem "tab"     :h2)
                  :only-if (rec prefix suffix))))

