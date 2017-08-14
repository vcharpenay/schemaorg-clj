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
  "Static Jinjava template engine"
  (new Jinjava))

(def homepage-tpl
  "Homepage template."
  (slurp (io/resource "templates/homepage.tpl")))

(def generic-tpl
  "Generic template for vocabulary term documentation."
  (slurp (io/resource "templates/genericTermPageHeader.tpl")))

(defn ctx [term]
  (merge
    properties
    (unit/get-unit (str "http://" (properties "sitename") "/" term)
                   (properties "sparql_endpoint"))))

(defn send-home []
  {:status 200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body (.render jj homepage-tpl properties)})

(defn send-generic [term]
  (let [c (ctx term)]
    {:status (if (nil? (get "rdfs_type" c)) 404 200)
     :headers {"Content-Type" "text/html; charset=utf-8"}
     :body (.render jj generic-tpl c)}))

(defroutes app-routes
  ; TODO content negotiation
  (GET "/" [] (send-home))
  (GET "/:term" [term] (send-generic term))
  (route/not-found (send-generic "?")))

(def app
  (wrap-defaults app-routes site-defaults))
