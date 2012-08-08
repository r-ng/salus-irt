(ns salus.irt.views.e2b-input-page
    (:use noir.core
          hiccup.core
          salus.irt.util))

(defpage "/" {} (full-page :e2b-input-page-title
    [:p "This will be the I2B filling form"]))

