(ns plutus-tailwindcss.controls.date-control
  (:require
   [plutus-tailwindcss.helper :refer [event-value]]
   [plutus-tailwindcss.controls.styled-controls :as styled]
   ["date-fns" :refer (getDaysInMonth addDays)]))

(def day-jumper [1 7 30])

(defn date-picker
  "Date picker components.
   `date` is a reagent atom storing `js/Date.`"
  [date]
  (let [year    (.getFullYear @date)
        month   (.getMonth @date)
        day     (.getDate @date)
        min-day (fn [nyear nmonth nday]
                  (let [end-day (getDaysInMonth (js/Date. nyear nmonth))]
                    (min nday end-day)))]
    [:div
     [styled/select {:id        "date-picker-year-selector"
                     :value     year
                     :on-change (fn [e]
                                  (let [nyear (event-value e)
                                        nday  (min-day nyear month day)]
                                    (reset! date (js/Date. nyear month nday))))}
      (map #(vector :option {:value % :key (str "dp-year-" %)} %) (range 1990 2051))]
     [styled/select {:id        "date-picker-month-selector"
                     :value     (inc month)
                     :on-change (fn [e]
                                  (let [nmonth (dec (event-value e))
                                        nday   (min-day year nmonth day)]
                                    (reset! date (js/Date. year nmonth nday))))}
      (map #(vector :option {:value % :key (str "dp-month-" %)} %) (range 1 13))]
     [styled/select {:id        "date-picker-day-selector"
                     :value     day
                     :on-change #(reset! date (js/Date. year month (event-value %)))}
      (map #(vector :option {:value % :key (str "dp-day-" %)} %) (range 1 (+ 1 (getDaysInMonth @date))))]]))

(defn day-jump-back [date]
  [:<>
   (map (fn [d]
          [styled/button
           {:key (str "day-jump-back-" d)
            :on-click #(reset! date (addDays @date (- d)))}
           (str (- d))]) (reverse day-jumper))])

(defn day-jump-forward [date]
  [:<>
   (map (fn [d]
          [styled/button
           {:key (str "day-jump-forward-" d)
            :on-click #(reset! date (addDays @date d))}
           (str "+" d)]) day-jumper)])

(defn date-control [{:keys [date label]}]
  [:div
   [:div.flex.flex-row.md:flex-col.px-4.py-1
    [:span.mr-auto.text-gray-400 label]
    [:div.md:ml-0
     [date-picker date]]]
   [:div.flex.flex-row.justify-center
    [:div
     [day-jump-back date]]
    [:div
     [styled/button
      {:on-click #(reset! date (js/Date.))}
      "Today"]]
    [:div [day-jump-forward date]]]])
