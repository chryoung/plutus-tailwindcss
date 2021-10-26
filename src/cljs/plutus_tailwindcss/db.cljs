(ns plutus-tailwindcss.db)

(def default-db
  {:name       "re-frame"
   :currencies ["CNY" "USD" "HKD" "CAD" "JPY"]
   :accounts   {"Assets"      nil
                "Liabilities" nil
                "Expenses"    nil
                "Income"      nil
                "Equity"      {"Opening-Balances" nil}}
   :transaction {:date ""
                 :status "!"
                 :payee ""
                 :description ""
                 :tags []
                 :postings [{:account ""
                             :amount  0}
                            {:account ""
                             :amount  0}]}})
