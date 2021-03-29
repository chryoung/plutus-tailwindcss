(ns plutus-tailwindcss.controls.date-control
  (:require
   [plutus-tailwindcss.helper :refer [event-value]]
   [plutus-tailwindcss.controls.styled-controls :refer [button]]
   ["date-fns/getDaysInMonth" :as getDaysInMonth]
   ["date-fns/addDays" :as addDays]))

(def day-jumper [1 7 30])

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

(defn day-jump-back [date]
  (fn []
    [:<>
     (map (fn [d]
            [button
             {:key (str "day-jump-back-" d)
              :on-click #(reset! date (addDays @date (- d)))}
             (str (- d))]) (reverse day-jumper))]))

(defn day-jump-forward [date]
  (fn []
    [:<>
     (map (fn [d]
            [button
             {:key (str "day-jump-forward-" d)
              :on-click #(reset! date (addDays @date d))}
             (str "+" d)]) day-jumper)]))

(defn date-control [{:keys [date label]}]
  [:div
   [:div.flex.flex-row.md:flex-col.px-4.py-1
    [:label.text-gray-400 label]
    [:div.ml-auto.md:ml-0
     [date-picker date]]]
   [:div.flex.flex-row.justify-center
    [:div
     [day-jump-back date]]
    [:div
     [button
      {:on-click #(reset! date (js/Date.))}
      "Today"]]
    [:div [day-jump-forward date]]]])
