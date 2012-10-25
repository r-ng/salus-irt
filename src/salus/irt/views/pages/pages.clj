(ns salus.irt.views.pages.pages
    (:use noir.core
          hiccup.page
          salus.irt.views.generation.icsr-form
          salus.irt.views.definitions.icsr-form
          [salus.irt.config :only [irt-config]]))


(defpage "/" {}
  (let [[hiccup-code jq-code] (icsr-definition->hiccup full-icsr)]
    (html5
      [:head
        [:title (irt-config :icsr-input-page-title)]
        (include-js "http://code.jquery.com/jquery.min.js"
                    "http://code.jquery.com/ui/1.9.0/jquery-ui.js")
        (include-css "http://code.jquery.com/ui/1.9.0/themes/base/jquery-ui.css")
       
        [:script {:type "text/javascript"}
           (str "$(function(){" jq-code "})")]]
      [:body
        [:h1 "ICSR Reporting"]
        hiccup-code])
;     (form-to [:post "/new-icsr"]

;         (submit-button "Validate"))
))

(defpage [:post "/new-icsr"] m
    (str m))

