(ns salus.irt.views.e2b-input-page
    (:use noir.core hiccup.form [hiccup.page :only [include-js]]
          salus.irt.util)
    (:require [salus.irt.models.e2b :as e2b]))

(defmulti e2b-node->hiccup (fn [node & args] (e2b/node-type node))); :hierarchy e2b/hierarchy)

(defmethod e2b-node->hiccup ::e2b/simple-field [field-name prefix suffix]
    [(str prefix field-name suffix)])

(defmethod e2b-node->hiccup ::e2b/field-with-opts [[field-name & opts] prefix suffix]
    [(str prefix field-name suffix)]) ;" " opts))

(defmethod e2b-node->hiccup ::e2b/block-with-opts [[opt param & content] prefix suffix]
    (let [assert-string (fn [] (assert (string? param)
                                       (str "E2B: " opt " not followed by a string.")))
          rec           (fn [children p s] (mapcat #(e2b-node->hiccup % p s) children))]
        (case opt :prefix (do (assert-string)
                              (rec content (str prefix param) suffix))
                  :suffix (do (assert-string)
                              (rec content prefix (str param suffix)))
                  :section (do (assert-string)
                               [(concat [:div [:h3 param]] (rec content prefix suffix))])
                  :only-if (rec content prefix suffix))))

(defmethod e2b-node->hiccup ::e2b/simple-block [content prefix suffix]
    (mapcat #(e2b-node->hiccup % prefix suffix) content))

(def e2b->hiccup
    (first (e2b-node->hiccup e2b/full-icsr "" "")))

(defpage "/" {} (full-page :e2b-input-page-title

    [:h1 "ICSR Reporting"]

    (form-to [:post "/new-icsr"]
        raw-e2b-form
        (submit-button "Validate")
    )
))

(defpage [:post "/new-icsr"] m (full-page :blank-title
    (str m)
))

