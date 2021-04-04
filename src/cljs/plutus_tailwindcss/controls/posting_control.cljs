(ns plutus-tailwindcss.controls.posting-control
  (:require
   [re-frame.core :as re-frame]
   [clojure.string :as cljstr]
   [plutus-tailwindcss.subs :as subs]
   [plutus-tailwindcss.helper :as h]
   [plutus-tailwindcss.controls.styled-controls :as styled]))

(defn show-account-modal [account set-account]
  (let [accounts (re-frame/subscribe [::subs/accounts])
        account-components (cljstr/split account #":")]))

(defn posting-control [postings]
  (let [currencies (re-frame/subscribe [::subs/currencies])
        swap-postings-at! (fn [i p k e] (swap! postings assoc i (assoc p k (h/event-value e))))
        entry      (fn [index posting]
                [:div
                 {:key (str 'pc-' index)}
                 [styled/nav-button {} (:account posting)]
                 [styled/input {:type        "number"
                                :placeholder "Amount"
                                :value       (:amount posting)
                                :on-change #(swap-postings-at! index posting :amount %)}]
                 [styled/select {:value     (:currency posting)
                                 :on-change #(swap-postings-at! index posting :currency %)}
                  (h/doall-map (fn [c]
                                 [:option {:key   (str "pst-c-" index "-" c)
                                           :value c}
                                  c]) @currencies)]
                 ])]
    [:div
     (h/doall-map-indexed entry @postings)]))
