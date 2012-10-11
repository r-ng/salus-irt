(ns salus.irt.views.definitions.icsr-form)


(def basic-hospital-member-info [
    '("title" :select-in [["Dr" 0] ["Pr" 1]], :label :none)
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
    [primary-source
     sender
     receiver
     patient
     drug])

