(ns plutus-tailwindcss.subs
  (:require
   [re-frame.core :refer [reg-sub]]))

(reg-sub
 ::name
 (fn [db]
   (:name db)))

(reg-sub
 ::active-panel
 (fn [db _]
   (:active-panel db)))

(reg-sub
  ::currencies
  (fn [db _]
    (:currencies db)))

(reg-sub
 ::accounts
 (fn [db _]
   (:accounts db))
 )
