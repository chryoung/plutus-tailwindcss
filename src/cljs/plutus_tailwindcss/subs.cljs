(ns plutus-tailwindcss.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 ::active-panel
 (fn [db _]
   (:active-panel db)))

(re-frame/reg-sub
  ::currencies
  (fn [db _]
    (:currency db)))

(re-frame/reg-sub
  ::selected-currency
  (fn [db _]
    (:selected-currency db)))