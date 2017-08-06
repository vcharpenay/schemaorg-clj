(ns schemaorg.handler
  (:import com.hubspot.jinjava.Jinjava)
  (:import java.util.HashMap)
  (:import java.util.Properties)
  (:require [clojure.java.io :as io]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [schemaorg.unit :as unit]))

(def properties
  "Loads the content of schemaorg.properties (called once at startup)."
  (with-open [reader (io/reader (io/resource "schemaorg.properties"))]
    (let [props (new Properties)]
      (.load props reader)
      (into {} props))))

(def jj
  "Static Jinjava engine"
  (new Jinjava))

(defn ctx [term]
  (merge
    properties
    (unit/get-unit (str "http://" (properties "sitename") "/" term)
                   (properties "sparql_endpoint"))))

(defn render-home []
  (.render jj (slurp (io/resource "templates/homepage.tpl")) properties))

(defn render-generic [term]
  (.render jj (slurp (io/resource "templates/genericTermPageHeader.tpl")) (ctx term)))

(defroutes app-routes
  (GET "/" [] (render-home))
  (GET "/:term" [term] (render-generic term))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
