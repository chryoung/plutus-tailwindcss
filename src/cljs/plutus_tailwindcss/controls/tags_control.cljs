(ns plutus-tailwindcss.controls.tags-control
  (:require
   [plutus-tailwindcss.helper :as h]
   [plutus-tailwindcss.controls.styled-controls :as styled]))

(defn tags-control [tags]
  (fn []
    (let [entry (fn [tag-index tag-value]
                  [:div.flex.flex-row
                   {:key (str "tag-input-" tag-index)}
                   [styled/input
                    {:type        "text"
                     :placeholder (str "Tag " (inc tag-index))
                     :value       tag-value
                     :on-change   (fn [e] (swap! tags assoc tag-index (h/event-value e)))}]
                   (when (> (count @tags) 1) [styled/button {:on-click #(swap! tags h/remove-at tag-index)} "-"])
                   [:div.mr-3
                    [styled/button {:on-click #(swap! tags h/insert-after tag-index "")} "+"]]])]
      [:div.tags-controller
       (h/doall-map-indexed entry @tags)])))
