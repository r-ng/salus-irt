(ns salus.irt.views.definitions.icsr-form)


;; For now, this form definition file is based solely on E2B.


(defn bool-field [f]
    (list f :select-in [["Yes" 1] ["No" 2]]))

(defn iso3166-field [f]
    (list f :select-in ["US" "FR" "UK" "..."]))  ;; TO BE COMPLETED


(def safety-report [:section "Safety report"
    (iso3166-field "Primary source country")
    (iso3166-field "Occurence country")
    '("Transmission date" :date "yymmdd")
    ]
)

(def basic-hospital-member-info [
    "title"
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
    "type"
    basic-hospital-member-info
    "countrycode"
    [:prefix "tel" tel-info]
    [:prefix "fax" tel-info]
    "emailadress"])


(def primary-source [:section "primarysource"
    [:prefix "reporter" basic-hospital-member-info]
    "country"
    '("qualification" :select-in [["Physician" 1] ["Pharmacist" 2] ["Other health professional" 3]
                                  ["Lawyer" 4]    ["Consumer or other non health professional" 5]])
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
                                                ; NOT HANDLED SO FAR and then NOT DEFINITIVE SYNTAX
        "gestationperiod"
        "gestationperiodunit"
        "lastmenstrualdateformat"
        "patientlastmenstrualdate"]])

(def patient [:section "patient"
    basic-patient-info])

(def drug [:section "drug"
    "medicinalproduct"
    "obtaindrugcountry"
    [:prefix "drug"
        "characterization"
        "batchnumb"
        [:prefix "authorization"
            "numb"
            "country"
            "holder"]]])

(def full-icsr
    [safety-report
     primary-source
     sender
     receiver
     patient
     drug])

