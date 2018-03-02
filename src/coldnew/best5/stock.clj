(ns coldnew.best5.stock
  (:require [clojure.string :as str]
            [clj-http.client :as http]
            [clojure.pprint :refer [cl-format]]
            [hickory.core :as hickory]
            [hickory.select :as s]))


(defn unix-timestamp
  "Create unix timestamp."
  []
  (System/currentTimeMillis))

;; 

(defn getStockLists
  "Get the Taiwan's stock lists info."
  []
  (let [html (-> (http/get "http://isin.twse.com.tw/isin/C_public.jsp?strMode=2"
                           {:as "BIG5"}) :body)
        hickory (-> html hickory/parse hickory/as-hickory)
        parse  (->> (s/select (s/child (s/tag :td)) hickory)
                    (map #(-> % :content last))
                    (drop 8)
                    (partition 7))]
    (->> parse
         (map #(let [a (first %)
                     a0 (str/split a #"　")]
                 (flatten               ; <=== FIXME: I don't like flatten
                  (conj
                   (rest %)
                   (rest a0)
                   (first a0)))))
         (map #(zipmap [:代號 :名稱 :ISINCode :上市日 :市場別 :產業別 :CFICode :備註] %)))))

;; for test
#_(getStockLists)


;; 

(defn- fetch-data
  "Common function to retrive data."
  ([url] (fetch-data url {}))
  ([url params]
   (let [ret (try (http/get url (merge {:as :json
                                        :insecure? true
                                        ;; :debug true
                                        :socket-timeout 1000
                                        :conn-timeout 1000} params))
                  (catch Exception e    ; <= socketTimeout ?
                    (println (.getMessage e))))
         body (ret :body)]
     ;; when success, it'll return "OK" on :rtmessage
     (if (= "OK" (:rtmessage body))
       (-> body :msgArray first)
       ;; throw exception when failed
       (throw (ex-info "fetch-data failed." {:url url :params params :ret ret}))))))

(defn- translate-keys
  [coll]
  (-> coll
      (clojure.set/rename-keys
       {:c     :股票代號
        :s     :當盤成交量
        :ch    :channel
        :ex    :上市或上櫃                  ; tse: 上市, otc: 上櫃
        :n     :股票暱稱                    ; ex: 台泥
        :nf    :股票全名                    ; 台灣水泥股份有限公司
        :z     :最近成交價
        :tv    :當盤成交量                  ; temporal volume
        :v     :當日累計成交量              ; volume
        :a     :最佳五檔賣出價格
        :f     :最佳五檔賣出數量
        :b     :最佳五檔買入價格
        :g     :最佳五檔買入數量
        :tlong :資料時間                    ; :t (long)
        :t     :揭示時間
        :o     :開盤價
        :d     :今日日期
        :h     :今日最高
        :l     :今日最低
        :u     :漲停點
        :w     :跌停點
        :y     :昨收})))

(defn getStockInfo
  "Get stock info according to stock-id"
  [stock-id]
  (let [cs (clj-http.cookies/cookie-store)]
    ;; first retrive cookies data
    (fetch-data "http://mis.twse.com.tw/stock/api/getStockInfo.jsp"
                {:cookie-store cs
                 :query-params {:ex_ch "tse_t00.tw%7cotc_o00.tw%7ctse_FRMSA.tw"
                                :json 1
                                :delay 0
                                :_ (unix-timestamp)}})
    ;; we use the cookies data to get the stock info
    (if-let [ret (fetch-data "http://mis.twse.com.tw/stock/api/getStockInfo.jsp"
                             {:cookie-store cs
                              :query-params {:ex_ch (str "tse_" stock-id ".tw")
                                             :json 1
                                             :delay 0
                                             :_ (unix-timestamp)}})]
      (translate-keys ret)
      (throw (ex-info "getStockInfo failed." {:stock-id stock-id})))))

;; for test
#_(getStockInfo 2330)
