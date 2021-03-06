(defproject coldnew/best5 "0.1.0-SNAPSHOT"
  :description "Get the Taiwan's stock best5 data"
  :url "https://github.com/coldnew/twstock-best5.clj"
  :author "Yen-Chin, Lee"
  :license {:name "MIT License"
            :url "https://github.com/coldnew/twstock-best5.clj/blob/master/LICENSE"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-http "3.7.0"]
                 [cheshire "5.8.0"]
                 [http-kit "2.3.0"]
                 [hickory "0.7.1"]
                 [clj-time "0.14.2"]
                 [datascript "0.16.3"]
                 [datascript-transit "0.2.2"]
                 [toucan "1.1.4"]
                 [yesql "0.5.3"]
                 [org.xerial/sqlite-jdbc "3.14.2.1"]
                 [com.layerware/hugsql "0.4.8"]]

  :source-paths ["src"]
  :test-paths ["test"]

  :main coldnew.best5.core
  :jvm-opts ["-Dclojure.compiler.direct-linking=true"
             "-XX:MaxDirectMemorySize=16g" "-XX:+UseLargePages"]

  :profiles {:uberjar
             {:source-paths ["src/clj"]
              :omit-source true
              :aot :all}})
