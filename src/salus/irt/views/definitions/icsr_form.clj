(ns salus.irt.views.definitions.icsr-form
    (use salus.irt.views.generation.icsr-form))


;; Some helper functions to create an ICSR form definition:

(defn yes-no [& f]
    (selector f ["Yes" 1] ["No" 2]))

(defn iso3166 [& f]
    (selector f "US" "FR" "UK" "..."))  ;; TO BE COMPLETED

(defn date [& f]
    {:date "yymmdd", :naming f})


;; For now, this form definition is based solely on E2B.


(def safety-report (section "Safety report"
    (iso3166 "Primary source country")
    (iso3166 "Occurence country" "occurcountry")
    (date "Transmission date")
    (yes-no "Serious")
    (section "Seriousness" (id-prefix "seriousness"
        (yes-no "Death")
        (yes-no "Life threatening")
        (yes-no "Hospitalization")
        (yes-no "Disabling")
        (yes-no "Congenital anomaly" "congenitalanomali")
        (yes-no "Other condition" "other")))
    (date "Receive date")
    (date "Most recent information receipt" "receiptdate")
    (yes-no "Additional document")
    (text-area "Document list" [])
    (yes-no "Fullfils local criteria for expedited report" "fulfillexpeditecriteria")
    (text "Regulatory authority's case report number" "authoritynumb")
    (text "Other sender's case report number" "companynumb")
    (yes-no "Other case identifiers in previous transmissions" "duplicate")
    (repeated-section 0 "Report duplicate"
        (text "Source(s) of the case identifier" "duplicatesource")
        (text "Case indentifiers" "duplicatenumb"))                
    (repeated-section 0 "Linked report"
        (text "Number" "linkreportnumb"))
    (yes-no "Nullification" "casenullification")
    "Nullification reason"
    (yes-no "Medically confirmed" "medicallyconfirm")
))

(def basic-hospital-member-info
   [(same-line "Title"
               "Given name"
               "Middle name"
               "Family name")
    "Organization"
    "Department"
    "Street"
    "City"
    "State"
    "Postcode"])

(def tel-info
   (same-line (text "Number" "")
              "Extension"
              "Countrycode"))

(def hospital-member-info
   ["Type"
    basic-hospital-member-info
    "Countrycode"
    (section "Telephone" (id-prefix "tel" tel-info))
    (section "Fax" (id-prefix "fax" tel-info))
    "emailadress"])


(def primary-source (repeated-section 1 "Primary source"
    (id-prefix "reporter" basic-hospital-member-info)
    "Country"
    (selector "Qualification"
        ["Physician" 1] ["Pharmacist" 2] ["Other health professional" 3]
        ["Lawyer" 4] ["Consumer or other non health professional" 5])
    "Literature reference"
    "Study name"
    (text "Sponsor's study number" "sponsorstudynumb")
    (text "Observer's study type" "observestudytype")))

(def sender (section "Sender"
    (id-prefix "sender" hospital-member-info)))
(def receiver (section "Receiver"
    (id-prefix "receiver" hospital-member-info)))

(def basic-patient-info [
    (id-prefix "patient"
        "Initial"
        (id-suffix "recordnumb"
            "GP medical"
            "Specialist"
            "Hospital"
            "Investigation")
        "Birth date format"
        "Birth date"
        "Onset age"
        "Onset age unit"
        "Age group"
        "Weight"
        "Height"
        (selector "Sex" ["Male" 1] ["Female" 2]))
    "Gestation period"
    "Gestation period unit"
    "Last menstrual date format"
    (id-prefix "patient" "Last menstrual date")])

(def patient (section "Patient"
    basic-patient-info))

(def drug (section "Drug"
    "Medicinal product"
    "Obtain drug country"
    (id-prefix "drug"
        "Characterization"
        (text "Batch number" "batchnumb")
        (id-prefix "authorization"
            (text "Number" "numb")
            "Country"
            "Holder"))))

(def full-icsr (section "ICSR tab"
    [safety-report
     primary-source
     sender
     receiver
     patient
     drug]))

