(ns plutus-tailwindcss.views
  (:require
   [re-frame.core :as re-frame]
   [plutus-tailwindcss.panels.home]
   [plutus-tailwindcss.panels.transaction]
   [plutus-tailwindcss.routes :as routes]
   [plutus-tailwindcss.subs :as subs]
   ))

;; main

(defn main-panel []
  [:div.container.mx-auto.bg-gray-200.py-4.md:p-4.h-screen
   (let [active-panel (re-frame/subscribe [::subs/active-panel])]
     (routes/panels @active-panel))])
