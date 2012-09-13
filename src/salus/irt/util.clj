(ns salus.irt.util
    (:use noir.core hiccup.page
          [salus.irt.config :only [irt-config]]))

(defn full-page [config-title-kw & b]  ; b is a sequence: hiccup accepts sequences
    (html5
        [:head [:title (irt-config config-title-kw)]]
        [:body b]))

