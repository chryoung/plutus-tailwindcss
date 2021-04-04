(ns plutus-tailwindcss.db)

(def default-db
  {:name       "re-frame"
   :currencies ["CNY" "USD" "HKD" "CAD" "JPY"]
   :accounts   {"Assets"      {"Bank" {"ICBC" {"CNY" nil}
                                       "CMB"  {"CNY" nil}}}
                "Liabilities" {"CreditCard" {"ICBC" {"CNY" nil}}}
                "Expenses"    {"Food" {"Fruit"    nil
                                       "Drink"    nil
                                       "FastFood" nil
                                       "Takeout"  nil}}
                "Income"      {"Salary" {"Cash"  nil
                                         "Stock" nil}}
                "Equity"      {"Opening-Balances" nil}
                }})
