(defproject schemaorg "0.1.0-SNAPSHOT"
  :description "Re-writing of schemaorg in Clojure"
  :url "https://github.com/vcharpenay/schemaorg-clj"
  :min-lein-version "2.0.0"
  :license {:name "Apache License 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"
            :comments "Same as schemaorg"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.csv "0.1.4"]
                 [compojure "1.5.1"]
                 [ring/ring-defaults "0.2.1"]
                 [clj-http "3.6.1"]
                 [com.hubspot.jinjava/jinjava "2.2.6"]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler schemaorg.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})
