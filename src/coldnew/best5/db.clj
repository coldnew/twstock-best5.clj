(ns coldnew.best5.db
  (:require ;; [coldnew.best5.stock :as stock]
            [yesql.core :refer [defqueries]]))

(def conn {:classname   "org.sqlite.JDBC"
           :subprotocol "sqlite"
           :subname     "database.db"
           :user        "best5"
           :password    ""})

(defqueries "sql/queries.sql" {:connection conn})

(defn setup-db!
  []
  (best5-setup-db!))
