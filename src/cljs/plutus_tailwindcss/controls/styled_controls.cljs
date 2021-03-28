(ns plutus-tailwindcss.controls.styled-controls)

(defn styled-input [prop]
  (fn [] [:input.px-4.py-1.bg-white.focus:outline-none.w-full prop]))

(defn styled-button [prop & rest]
  (fn [] (into [:button.text-blue-500.active:text-blue-300.focus:outline-none.p-2 prop] rest)))
