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
  (let [transaction-date  (r/atom (js/Date.))
        transaction-state (r/atom "*")
        payee             (r/atom "")
        description       (r/atom "")
        tags              (r/atom [""])
        currencies        (re-frame/subscribe [::subs/currencies])
        default-currency  (first @currencies)
        postings          (r/atom [(create-posting default-currency) (create-posting default-currency)])
        selected-currency (r/atom default-currency)]
    (fn []
      [:div.flex.flex-col.md:flex-row
       [:div
        {:className "md:w-4/6 md:mr-4"}
        [:h2.text-xl.mx-4 "Preview"]
        [transaction-preview {:date        transaction-date
                              :state       transaction-state
                              :payee       payee
                              :description description
                              :tags        tags
                              :postings    postings}]]
       [:div.editor
        {:className "md:w-2/6"}
        [:div.transation-header.bg-white
         [date-control {:date  transaction-date
                        :label "Transaction date"}]
         [:div
          [styled/button {:on-click #(reset! transaction-state "*")} "*"]
          [styled/button {:on-click #(reset! transaction-state "!")} "!"]]
         [:div.divide-y
          [styled/input {:id          "payee-input"
                         :placeholder "Payee"
                         :type        "text"
                         :value       @payee
                         :on-change   #(reset! payee (h/event-value %))}]
          [styled/input {:id          "description-input"
                         :type        "text"
                         :value       @description
                         :placeholder "Description"
                         :on-change   #(reset! description (h/event-value %))}]
          [tags-control tags]]]
        [:div.mt-6
         [:select.focus:outline-none {:value     @selected-currency
                                      :on-change #(reset! selected-currency (h/event-value %))}
          (map (fn [c] [:option {:value c :key (str "currency-" c)} (str c)]) @currencies)]]]])))

(defmethod routes/panels :transaction-panel [] [transaction-panel])
