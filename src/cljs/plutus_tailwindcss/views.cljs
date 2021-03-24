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
                 :value     year
                 :on-change (fn [e]
                              (let [nyear (event-value e)
                                    nday  (min-day nyear month day)]
                                (reset! date (js/Date. nyear month nday))))}
        (map #(vector :option {:value % :key (str "dp-year-" %)} %) (range 1990 2051))]
       [:select {:id        "date-picker-month-selector"
                 :value     (inc month)
                 :on-change (fn [e]
                              (let [nmonth (dec (event-value e))
                                    nday   (min-day year nmonth day)]
                                (reset! date (js/Date. year nmonth nday))))}
        (map #(vector :option {:value % :key (str "dp-month-" %)} %) (range 1 13))]
       [:select {:id        "date-picker-day-selector"
                 :value     day
                 :on-change #(reset! date (js/Date. year month (event-value %)))}
        (map #(vector :option {:value % :key (str "dp-day-" %)} %) (range 1 (+ 1 (getDaysInMonth @date))))]])))


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
  (let [transaction-date (r/atom (js/Date.))
        payee (r/atom "")
        description (r/atom "")
        transactions (r/atom [])]
    (fn []
      [:div.flex.flex-col
       [:h2.text-xl.mx-4 "Preview"]
       [:pre.bg-white.p-2.my-1.mx-4.rounded-md
        (gstring/format "%s \"%s\" \"%s\"" (formatDate @transaction-date "yyyy-MM-dd") @payee @description)]
       [:div
        [:label "Transaction date"]
        [date-picker transaction-date]
        [:button.text-blue-500.active:text-blue-300.focus:outline-none
         {:on-click #(reset! transaction-date (js/Date.))}
         "Today"]
        [:button.text-blue-500.active:text-blue-300.focus:outline-none
         {:on-click #(reset! transaction-date (addDays (js/Date.) -1))}
         "Yesterday"]]
       [:div.flex.flex-col.divide-y
        [:input.px-4.py-1.bg-white.focus:outline-none {:id          "payee-input"
                                                       :placeholder "Payee"
                                                       :type        "text"
                                                       :on-blur     #(reset! payee (event-value %))}]
        [:input.px-4.py-1.bg-white.focus:outline-none {:id          "description-input"
                                                       :type        "text"
                                                       :placeholder "Description"
                                                       :on-blur     #(reset! description (event-value %))}]]])))

(defmethod routes/panels :transaction-panel [] [transaction-panel])

;; main

(defn main-panel []
  [:div.container.mx-auto.max-w-sm.bg-gray-200.py-4.h-screen
   (let [active-panel (re-frame/subscribe [::subs/active-panel])]
     (routes/panels @active-panel))])
