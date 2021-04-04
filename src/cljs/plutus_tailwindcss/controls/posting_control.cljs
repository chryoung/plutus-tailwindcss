(ns plutus-tailwindcss.controls.posting-control
  (:require
   [plutus-tailwindcss.helper :as h]
   [plutus-tailwindcss.controls.styled-controls :as styled]))

(defn posting-control [postings]
  (let [entry (fn [index posting]
                [:div
                 {:key (str 'pc-' index)}
                 [styled/input {:type "number"
                                :placeholder "Amount"
                                :value (:amount posting)
                                :on-change #(swap! posting assoc :amount (h/event-value %))}]
                 ])]
    (h/doall-map-indexed entry @postings)))
