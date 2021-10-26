(ns plutus-tailwindcss.display.transaction
  (:require
   [plutus-tailwindcss.helper :as h]
   ["date-fns" :rename {format formatDate}]
   [clojure.string :as clstr]
   [plutus-tailwindcss.formatter :as fmt]))

(defn transaction-preview [{:keys [date state payee description tags postings]}]
  (fn []
    (let [non-blank-tags (filter h/not-blank? @tags)]
      [:pre.bg-white.p-2.my-1.mx-4.rounded-md.md:h-auto
       (formatDate @date "yyyy-MM-dd")
       (str " " @state " ")
       (h/double-quote @payee)
       (when (h/not-blank? @description) (str " " (h/double-quote @description)))
       (when (seq non-blank-tags) (str " " (clstr/join " " (map fmt/format-tag non-blank-tags))))])))
