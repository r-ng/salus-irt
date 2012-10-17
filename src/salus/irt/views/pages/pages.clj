(ns salus.irt.views.pages.pages
    (:use noir.core
          salus.irt.views.generation.icsr-form
          salus.irt.views.definitions.icsr-form
          salus.irt.util))


(defpage "/" {} (full-page :icsr-input-page-title

    [:h1 "ICSR Reporting"]

;     (form-to [:post "/new-icsr"]

;         (submit-button "Validate"))

    (icsr-definition->hiccup full-icsr)
))

(defpage [:post "/new-icsr"] m (full-page :blank-title
    (str m)
))

