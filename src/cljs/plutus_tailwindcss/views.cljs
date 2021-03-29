(ns plutus-tailwindcss.views
  (:require
   [reagent.core :as r]
   [re-frame.core :as re-frame]
   ["date-fns/format" :as formatDate]
   [clojure.string :as clstr]
   [plutus-tailwindcss.events :as events]
   [plutus-tailwindcss.routes :as routes]
   [plutus-tailwindcss.subs :as subs]
   [plutus-tailwindcss.helper :as h]
   [plutus-tailwindcss.formatter :as fmt]
   [plutus-tailwindcss.controls.styled-controls :as styled]
   [plutus-tailwindcss.controls.date-control :refer [date-control]]))

;; *******************************************************************
;;
;; schema
;;
;; *******************************************************************


(defn create-posting [currency]
  {:account ""
   :amount 0
   :currency currency})

;; *******************************************************************
;;
;; components
;;
;; *******************************************************************

(defn postings [number]
  (fn []
    [:<>
     [styled/input {:id          (str "account" number)
                    :placeholder "Account"
                    :type        "text"}]
     [styled/input {:id          (str "amount" number)
                    :placeholder "Amount"
                    :type        "text"}]
     [styled/button {} "+Currency"]]))

(defn transaction-preview [{:keys [date state payee description tags postings]}]
  (fn []
    (let [non-blank-tags (filter h/not-blank? @tags)]
      [:pre.bg-white.p-2.my-1.mx-4.rounded-md.md:h-auto
       (formatDate @date "yyyy-MM-dd")
       (str " " @state " ")
       (h/double-quote @payee)
       (when (h/not-blank? @description) (str " " (h/double-quote @description)))
       (when (seq non-blank-tags) (str " " (clstr/join " " (map fmt/format-tag non-blank-tags))))
       (clstr/join " " @tags)])))

(defn tags-control [tags]
  (fn []
    (let [entry (fn [tag-index tag-value]
                  [:div.flex.flex-row
                   {:key (str "tag-input-" tag-index)}
                   [styled/input {:type        "text"
                                  :placeholder (str "Tag " (inc tag-index))
                                  :value       tag-value
                                  :on-change   (fn [e] (swap! tags assoc tag-index (h/event-value e)))}]
                   (when (> (count @tags) 1) [styled/button {:on-click #(swap! tags h/remove-at tag-index)} "-"])
                   [styled/button {:on-click #(swap! tags h/insert-at tag-index "")} "+"]])]
      [:div.tags-controller
       (h/doall-map-indexed entry @tags)])))

;; *******************************************************************
;;
;; panels
;;
;; *******************************************************************

;; home


(defn home-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1 (str "Hello from " @name ". This is the Home Page.")]

     [:div
      [:a {:on-click #(re-frame/dispatch [::events/navigate :transaction])}
       "go to Transaction Page"]]]))

(defmethod routes/panels :home-panel [] [home-panel])

;; transaction


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
                         :on-blur     #(reset! payee (h/event-value %))}]
          [styled/input {:id          "description-input"
                         :type        "text"
                         :placeholder "Description"
                         :on-blur     #(reset! description (h/event-value %))}]
          [tags-control tags]]]
        [:div.mt-6
         [:select.focus:outline-none {:value     @selected-currency
                                      :on-change #(reset! selected-currency (h/event-value %))}
          (map (fn [c] [:option {:value c :key (str "currency-" c)} (str c)]) @currencies)]]]])))

(defmethod routes/panels :transaction-panel [] [transaction-panel])

;; main

(defn main-panel []
  [:div.container.mx-auto.bg-gray-200.py-4.md:p-4.h-screen
   (let [active-panel (re-frame/subscribe [::subs/active-panel])]
     (routes/panels @active-panel))])
