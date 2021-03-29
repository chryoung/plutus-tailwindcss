(ns plutus-tailwindcss.panels.home
  (:require
   [re-frame.core :as re-frame]
   [plutus-tailwindcss.subs :as subs]
   [plutus-tailwindcss.events :as events]
   [plutus-tailwindcss.routes :as routes]
   ))

(defn home-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1 (str "Hello from " @name ". This is the Home Page.")]

     [:div
      [:a {:on-click #(re-frame/dispatch [::events/navigate :transaction])}
       "go to Transaction Page"]]]))

(defmethod routes/panels :home-panel [] [home-panel])
