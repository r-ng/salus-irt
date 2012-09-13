(ns salus.irt.views.e2b-input-page
    (:use noir.core hiccup.form [hiccup.page :only [include-js]]
          salus.irt.util)
    (:require [salus.irt.models.e2b :as e2b]))

(defmulti e2b-node->hiccup (fn [node & args] (e2b/node-type node))); :hierarchy e2b/hierarchy)
; Returns a _list_ of hiccup nodes

(defmethod e2b-node->hiccup ::e2b/simple-field [field-name prefix suffix]
    (let [full-name (str prefix field-name suffix)]
        (list (label full-name full-name) (text-field full-name))))

(defmethod e2b-node->hiccup ::e2b/field-with-opts [[field-name & {:as opts}] prefix suffix]
    (let [full-name (str prefix field-name suffix)
          field     (if-let [o (:select-in opts)] (drop-down full-name o)
                                                  (text-field full-name))]
        (if-let [l (:label opts)]
            (if (= l :none) field
                            (list (label l l) field))
            (list (label full-name full-name) field))))

(defmethod e2b-node->hiccup ::e2b/block-with-opts [[opt param & content] prefix suffix]
    (let [assert-param  (fn [f n] (assert (f param)
                                          (str "E2B definition: " opt " not followed by a " n ".")))
          assert-string #(assert-param string? "string")
          rec           (fn [p s] (map #(e2b-node->hiccup % p s) content))
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

(defmethod e2b-node->hiccup ::e2b/simple-block [content prefix suffix]
    (map #(e2b-node->hiccup % prefix suffix) content))

(defn e2b->hiccup []
    (e2b-node->hiccup e2b/full-icsr "" ""))  ; every node is wrapped in a vector, even if it has no siblings


(defpage "/" {} (full-page :e2b-input-page-title

    [:h1 "ICSR Reporting"]

;     (form-to [:post "/new-icsr"]
;
;         (submit-button "Validate"))

    (e2b->hiccup)
))

(defpage [:post "/new-icsr"] m (full-page :blank-title
    (str m)
))

