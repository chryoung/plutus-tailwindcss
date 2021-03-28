(ns plutus-tailwindcss.helper
  (:require [clojure.string :as clstr]))

(defn event-value
  "Gets the event.target.value"
  [event]
  (-> event .-target .-value))

(def not-blank? (comp not clstr/blank?))

(defn double-quote [s] (str "\"" s "\""))

(defn insert-at [coll n element]
  ((comp vec concat) (subvec coll 0 n) [element] (subvec coll n (count coll))))

(defn remove-at [coll n]
  ((comp vec concat) (subvec coll 0 n) (subvec coll (inc n) (count coll))))

(def doall-map (comp doall map))
(def doall-map-indexed (comp doall map-indexed))
