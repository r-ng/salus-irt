(ns salus.irt.views.pages.pages
    (:use noir.core
          salus.irt.views.generation.icsr-form
          salus.irt.views.definitions.icsr-form
          salus.irt.util))


(def hiccup-repr-of-icsr-form
    (future (icsr-node->hiccup full-icsr "" "")))

(defpage "/" {} (full-page :icsr-input-page-title

    [:h1 "ICSR Reporting"]

;     (form-to [:post "/new-icsr"]

;         (submit-button "Validate"))

    @hiccup-repr-of-icsr-form
))

(defpage [:post "/new-icsr"] m (full-page :blank-title
    (str m)
))

