(ns salus.irt.models.e2b
    (:import (clojure.lang PersistentList
                           PersistentVector
                           PersistentArrayMap)))

; (defn- prefix [p fields]
;     (map (fn [field] (if (seq? field) (let [[fname & opts] field]
;                                          (cons (str p fname) opts))
;                                       (str p field)))
;          fields))

(def hierarchy (-> (make-hierarchy)
    (derive ::field-with-opts ::field)
    (derive ::simple-field ::field)
    (derive ::block-with-opts ::block)
    (derive ::simple-block ::block)))

(defn node-type [n]
    (cond (vector? n) (if (keyword? (first n))
                          ::block-with-opts
                          ::simple-block)
          (list? n)   ::field-with-opts
          (string? n) ::simple-field))

(def basic-hospital-member-info [
    '("title" :select-in {0 "Dr", 1 "Pr", 2 "Mrs", 3 "Mr"})
    [:suffix "name"
        "given"
        "middle"
        "family"]
    "organization"
    "department"
    "street"
    "city"
    "state"
    "postcode"])

(def tel-info [
    ""   ; means it needs a prefix/suffix
    "extension"
    "countrycode"])

(def hospital-member-info [
    basic-hospital-member-info
    "countrycode"
    [:prefix "tel" tel-info]
    [:prefix "fax" tel-info]
    "emailadress"])


(def primary-source [:section "primarysource"
    [:prefix "reporter" basic-hospital-member-info]
    "country"
    "qualification"
    "literaturereference"
    "studyname"
    "sponsorstudynumb"
    "observestudytype"])

(def sender [:section "sender"
    [:prefix "sender" hospital-member-info]])
(def receiver [:section "receiver"
    [:prefix "receiver" hospital-member-info]])

(def basic-patient-info [
    [:prefix "patient"
        "initial"
        [:suffix "recordnumb"
            "gpmedical"
            "specialist"
            "hospital"
            "investigation"]
        "birthdateformat"
        "birthdate"
        "onsetage"
        "onsetageunit"
        "agegroup"
        "weight"
        "height"
        "sex"]
    [:only-if [== [:field "patientsex"] "2"]    ; Contextual verification: these fields are useful only if the patient is a women
        "gestationperiod"
        "gestationperiodunit"
        "lastmenstrualdateformat"
        "patientlastmenstrualdate"]])

(def patient [:section "patient"
    basic-patient-info])

(def full-icsr
    [:section "ichicsr"
        [:section "safetyreport"
            primary-source
            sender
            receiver
            patient]])

