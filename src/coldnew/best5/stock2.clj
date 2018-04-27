(ns coldnew.best5.stock2
  (:require [org.httpkit.client :as http]
            [clojure.pprint :refer [cl-format]]
            [hickory.core :as hickory]
            [hickory.select :as s]
            [clojure.string :as str]
            [clojure.java.io :as io]))

(defn unix-timestamp
  "Create unix timestamp."
  []
  (System/currentTimeMillis))

(def options {;; :timeout 200
              :headers {"Content-Type" "charset=big5"}})

(defn getStockListsTWSE
  "Get the Taiwan's stock lists info for twse."
  []
  (let [html (let [{:keys [status headers body error] :as resp}
                   @(http/get
                     "http://isin.twse.com.tw/isin/C_public.jsp?strMode=2"
                     options)]
               (if error
                 (println "Failed, exception: " error)
                 (println "HTTP GET success: " status))
               body)
        hickory (-> html hickory/parse hickory/as-hickory)
        parse (->> (s/select (s/child (s/tag :td)) hickory)
                   (map #(-> % :content last))
                   ;; 過濾掉我們不要的資訊
                   (filter #(not (or (map? %)
                                     (contains? #{"有價證券代號及名稱 "
                                                  "上市日"
                                                  "國際證券辨識號碼(ISIN Code)"
                                                  "市場別"
                                                  "產業別"
                                                  "CFICode"
                                                  "備註"
                                                  " 股票 "
                                                  " 臺灣存託憑證(TDR) "
                                                  " 受益證券-不動產投資信託 "
                                                  } %))))
                   ;; 將 nil 變成 "", datascript 無法接受nil
                   (map #(or % ""))
                   (partition 7))]
    (->> parse
         (map #(let [a (first %) ; "代號 名稱" 合在一起，我們要將它分開
                     a0 (str/split a #"　")]
                 (flatten               ; 合併回單一 list
                  (conj
                   (rest %) ; :ISINCode :上市日 :市場別 :產業別 :CFICode :備註
                   (rest a0)            ; :名稱
                   (first a0)))))
         (mapv #(zipmap [:代號 :名稱 :ISINCode :上市日 :市場別 :產業別 :CFICode :備註] %)))))


;; 寫入到檔案避免 repl 太慢
#_(let [X (getStockListsTWSE)
        f-name "dba.txt"]
    (io/delete-file f-name true)
    (doseq [y X]
      (spit f-name (prn-str y) :append true)))
