(ns salus.irt.views.definitions.icsr-form
    (use salus.irt.views.generation.icsr-form))


;; Some helper functions to create an ICSR form definition:

(defn section [n & inner] (div ["section" :h3] n inner))

(defn tab [n & inner] (div ["tab" :h2] n inner))

(defn bool-field [f]
    (selector f ["Yes" 1] ["No" 2]))

(defn iso3166 [f]
    (selector f "US" "FR" "UK" "..."))  ;; TO BE COMPLETED

(defn date [f]
    {:date "yymmdd", :naming f})


;; For now, this form definition is based solely on E2B.


(def safety-report (section "Safety report"
    (iso3166 "Primary source country")
    (iso3166 ["Occurence country" "occurcountry"])
    (date "Transmission date")
    (bool-field "Serious")
    (id-prefix "seriousness" (map bool-field [
        "Death"
        "Life threatening"
        "Hospitalization"
        "Disabling"
        ["Congenital anomaly" "congenitalanomali"]
        ["Other condition" "other"]
        ]))
    )
)

(def basic-hospital-member-info [
    (text "title")
    (id-suffix "name"
        "given"
        "middle"
        "family")
    "organization"
    "department"
    "street"
    "city"
    "state"
    "postcode"])

(def tel-info [
    ""   ; means it needs a id-prefix/id-suffix
    "extension"
    "countrycode"])

(def hospital-member-info [
    "type"
    basic-hospital-member-info
    "countrycode"
    (id-prefix "tel" tel-info)
    (id-prefix "fax" tel-info)
    "emailadress"])


(def primary-source (section "primarysource"
    (id-prefix "reporter" basic-hospital-member-info)
    "country"
    (selector "qualification" ["Physician" 1] ["Pharmacist" 2] ["Other health professional" 3]
                              ["Lawyer" 4]    ["Consumer or other non health professional" 5])
    "literaturereference"
    "studyname"
    "sponsorstudynumb"
    "observestudytype"))

(def sender (section "sender"
    (id-prefix "sender" hospital-member-info)))
(def receiver (section "receiver"
    (id-prefix "receiver" hospital-member-info)))

(def basic-patient-info [
    (id-prefix "patient"
        "initial"
        (id-suffix "recordnumb"
            "gpmedical"
            "specialist"
            "hospital"
            "investigation")
        "birthdateformat"
        "birthdate"
        "onsetage"
        "onsetageunit"
        "agegroup"
        "weight"
        "height"
        "sex")
    "gestationperiod"
    "gestationperiodunit"
    "lastmenstrualdateformat"
    "patientlastmenstrualdate"])

(def patient (section "patient"
    basic-patient-info))

(def drug (section "drug"
    "medicinalproduct"
    "obtaindrugcountry"
    (id-prefix "drug"
        "characterization"
        "batchnumb"
        (id-prefix "authorization"
            "numb"
            "country"
            "holder"))))

(def full-icsr
    [safety-report
     primary-source
     sender
     receiver
     patient
     drug])

