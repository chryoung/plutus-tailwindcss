(ns plutus-tailwindcss.controls.styled-controls)

(defn input [prop]
  [:input.px-4.py-1.bg-white.focus:outline-none.w-full prop])

(defn button [prop & rest]
  (into [:button.text-blue-500.active:text-blue-300.focus:outline-none.px-2 prop] rest))
