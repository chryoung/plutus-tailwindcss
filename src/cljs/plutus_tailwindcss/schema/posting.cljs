(ns plutus-tailwindcss.schema.posting
  (:require
   [cljs.spec.alpha :as s]))

(s/def ::account string?)
(s/def ::amount number?)
(s/def ::currency string?)
(s/def ::posting (s/keys :req-un [::account]
                         :opt-un [::amount
                                  ::currency]))

(defn create-posting [currency]
  {:account ""
   :amount 0
   :currency currency})
