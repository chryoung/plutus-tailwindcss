(ns plutus-tailwindcss.panels.transaction
  (:require
   [reagent.core :as r]
   [re-frame.core :as re-frame]
   [plutus-tailwindcss.subs :as subs]
   [plutus-tailwindcss.routes :as routes]
   [plutus-tailwindcss.helper :as h]
   [plutus-tailwindcss.schema.posting :refer [create-posting]]
   [plutus-tailwindcss.display.transaction :refer [transaction-preview]]
   [plutus-tailwindcss.controls.styled-controls :as styled]
   [plutus-tailwindcss.controls.tags-control :refer [tags-control]]
   [plutus-tailwindcss.controls.date-control :refer [date-control]]))

(defn transaction-panel []
  (let [transaction-date   (r/atom (js/Date.))
        transaction-status (r/atom "*")
        payee              (r/atom "")
        description        (r/atom "")
        tags               (r/atom [""])
        currencies         (re-frame/subscribe [::subs/currencies])
        default-currency   (first @currencies)
        postings           (r/atom [(create-posting default-currency) (create-posting default-currency)])
        selected-currency  (r/atom default-currency)]
    (fn []
      [:div.flex.flex-col.md:flex-row
       [:div
        {:className "md:w-4/6 md:mr-4"}
        [:h2.text-xl.mx-4 "Preview"]
        [transaction-preview {:date        transaction-date
                              :status      transaction-status
                              :payee       payee
                              :description description
                              :tags        tags
                              :postings    postings}]]
       [:div
        {:className "editor md:w-2/6"}
        [:div.transation-header.bg-white.divide-y
         [date-control {:date  transaction-date
                        :label "Transaction date"}]
         [:div.flex.justify-center.mx-4.my-1
          [:span.mr-auto.text-gray-400 "Status"]
          [styled/button {:on-click #(reset! transaction-status "*")} "*"]
          [styled/button {:on-click #(reset! transaction-status "!")} "!"]]
         [:div.divide-y
          [styled/input {:id          "payee-input"
                         :type        "text"
                         :placeholder "Payee"
                         :value       @payee
                         :on-change   #(reset! payee (h/event-value %))}]
          [styled/input {:id          "description-input"
                         :type        "text"
                         :placeholder "Description"
                         :value       @description
                         :on-change   #(reset! description (h/event-value %))}]
          [tags-control tags]]]
        [:div.mt-6
         [:select.focus:outline-none {:value     @selected-currency
                                      :on-change #(reset! selected-currency (h/event-value %))}
          (map (fn [c] [:option {:value c :key (str "currency-" c)} (str c)]) @currencies)]]]])))

(defmethod routes/panels :transaction-panel [] [transaction-panel])
