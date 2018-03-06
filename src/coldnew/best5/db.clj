(ns coldnew.best5.db
  (:require [datascript.core :as d]
            [datascript.transit :as dt]
            [coldnew.best5.stock :as stock]))

;; https://github.com/tonsky/datascript/issues/152
;;
;; The only value type supported ATM is :db.type/ref. For anything else, just
;; don’t specify :db/valueType
;; (def schema {:代號     {:db/unique :db.unique/identity}
;;              :名稱     {:db/unique :db.unique/identity}
;;              :ISINCode {:db/unique :db.unique/identity}
;;              :上市日   {:db/unique :db.unique/identity}
;;              :市場別   {:db/unique :db.unique/identity}
;;              :產業別   {:db/unique :db.unique/identity}
;;              :CFICode  {:db/unique :db.unique/identity}
;;              :備註     {:db/unique :db.unique/identity}})

(def schema {})

(def db (d/empty-db schema))

(def conn (d/conn-from-db db))

(defonce initial-data
  (stock/getStockLists))

(d/transact! conn
             (into []
                   initial-data))

(d/q '[:find ?n
       :where
       [?e :代號 "2330"]
       [?e :名稱 ?n]]
     @conn)

(d/q '[:find ?n
       :where
       [?e :代號 "6488"]
       [?e :名稱 ?n]]
     @conn)

(d/q '[:find ?n
       :where
       [?e :代號 "2330"]
       [?e :市場別 ?n]]
     @conn)

(defn save!
  "Save db to PATH."
  [path]
  )

(defn load! [path]
  )
