(ns plutus-tailwindcss.formatter
  (:require [clojure.string :as clstr]))

(defn format-tag [t]
  (str "#" (clstr/replace t " " "-")))
