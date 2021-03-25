(ns plutus-tailwindcss.views
  (:require
   [reagent.core :as r]
   [re-frame.core :as re-frame]
   [goog.string :as gstring]
   [goog.string.format]
   [plutus-tailwindcss.events :as events]
   [plutus-tailwindcss.routes :as routes]
   [plutus-tailwindcss.subs :as subs]
   ["date-fns/getDaysInMonth" :as getDaysInMonth]
   ["date-fns/format" :as formatDate]
   ["date-fns/addDays" :as addDays]))


;; *******************************************************************
;;
;; helper
;;
;; *******************************************************************


(defn event-value
  "Gets the event.target.value"
  [event]
  (-> event .-target .-value))

;; *******************************************************************
;;
;; components
;;
;; *******************************************************************

(defn date-picker
  "Date picker components.
   `date` is a reagent atom storing `js/Date.`"
  [date]
  (fn []
    (let [year    (.getFullYear @date)
          month   (.getMonth @date)
          day     (.getDate @date)
          min-day (fn [nyear nmonth nday]
                    (let [end-day (getDaysInMonth (js/Date. nyear nmonth))]
                      (min nday end-day)))]
      [:div
       [:select {:id        "date-picker-year-selector"
                 :className "focus:outline-none"
                 :value     year
                 :on-change (fn [e]
                              (let [nyear (event-value e)
                                    nday  (min-day nyear month day)]
                                (reset! date (js/Date. nyear month nday))))}
        (map #(vector :option {:value % :key (str "dp-year-" %)} %) (range 1990 2051))]
       [:select {:id        "date-picker-month-selector"
                 :className "focus:outline-none"
                 :value     (inc month)
                 :on-change (fn [e]
                              (let [nmonth (dec (event-value e))
                                    nday   (min-day year nmonth day)]
                                (reset! date (js/Date. year nmonth nday))))}
        (map #(vector :option {:value % :key (str "dp-month-" %)} %) (range 1 13))]
       [:select {:id        "date-picker-day-selector"
                 :className "focus:outline-none"
                 :value     day
                 :on-change #(reset! date (js/Date. year month (event-value %)))}
        (map #(vector :option {:value % :key (str "dp-day-" %)} %) (range 1 (+ 1 (getDaysInMonth @date))))]])))

(defn styled-input [prop]
  (fn [] [:input.px-4.py-1.bg-white.focus:outline-none.w-full prop]))

(defn styled-button [prop & rest]
  (fn [] (into [:button.text-blue-500.active:text-blue-300.focus:outline-none.p-2 prop] rest)))

(defn posting [posting number]
  (fn []
    [:<>
     [styled-input {:id          (str "account" number)
                    :placeholder "Account"
                    :type        "text"}]
     [styled-input {:id          (str "amount" number)
                    :placeholder "Amount"
                    :type        "text"}]
     [styled-button {} "+Currency"]]))

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

(def day-jumper [1 7 30])

(defn day-jump-back [date]
  (fn []
    [:<>
     (map (fn [d]
            [styled-button
             {:on-click #(reset! date (addDays @date (- d)))}
             (str (- d))]) (reverse day-jumper))]))

(defn day-jump-forward [date]
  (fn []
    [:<>
     (map (fn [d]
            [styled-button
             {:on-click #(reset! date (addDays @date d))}
             (str "+" d)]) day-jumper)]))

(defn create-posting []
  {:account ""
   :amount 0
   :currency ""})

(defn transaction-panel []
  (let [transaction-date (r/atom (js/Date.))
        payee (r/atom "")
        description (r/atom "")
        tags (r/atom [""])
        postings (r/atom [(create-posting) (create-posting)])
        currencies (re-frame/subscribe [::subs/currencies])
        selected-currency (re-frame/subscribe [::subs/selected-currency])]
    (fn []
      [:div.flex.flex-col.md:flex-row
       [:div
        {:className "md:w-9/12 md:mr-4"}
        [:h2.text-xl.mx-4 "Preview"]
        [:pre.bg-white.p-2.my-1.mx-4.rounded-md.md:h-auto
         (gstring/format "%s \"%s\" \"%s\"" (formatDate @transaction-date "yyyy-MM-dd") @payee @description)]]
       [:div
        {:className "md:w-1/4"}
        [:div.bg-white
         [:div.flex.flex-row.md:flex-col.px-4.py-1
          [:label.text-gray-400 "Transaction date"]
          [:div.ml-auto.md:ml-0
           [date-picker transaction-date]]]
         [:div.flex.flex-row.md:flex-col.lg:flex-row.justify-center
          [:div
           [day-jump-back transaction-date]]
          [:div
           [styled-button
            {:on-click #(reset! transaction-date (js/Date.))}
            "Today"]]
          [:div [day-jump-forward transaction-date]]]]
        [:div.divide-y
         [styled-input {:id          "payee-input"
                        :placeholder "Payee"
                        :type        "text"
                        :on-blur     #(reset! payee (event-value %))}]
         [styled-input {:id          "description-input"
                        :type        "text"
                        :placeholder "Description"
                        :on-blur     #(reset! description (event-value %))}]
         (map-indexed (fn [tag-index tag-value]
                        [styled-input {:id          (str "tag-input" tag-index)
                                       :key         (str "tag-input" tag-index)
                                       :type        "text"
                                       :value       tag-value
                                       :placeholder (str "Tag " tag-value)}]) @tags)]
        [:div.mt-6
         [:select.focus:outline-none {:value @selected-currency
                                      :on-change #(re-frame/dispatch [::events/set-selected-currency (event-value %)])}
          (map (fn [c] [:option {:value c :key (str "currency-" c)} (str c)]) @currencies)]]]])))

(defmethod routes/panels :transaction-panel [] [transaction-panel])

;; main

(defn main-panel []
  [:div.container.mx-auto.bg-gray-200.py-4.md:p-4.h-screen
   (let [active-panel (re-frame/subscribe [::subs/active-panel])]
     (routes/panels @active-panel))])
