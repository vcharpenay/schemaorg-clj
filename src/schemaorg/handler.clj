(ns schemaorg.handler
  (:import com.hubspot.jinjava.Jinjava)
  (:import java.util.HashMap)
  (:import java.util.Properties)
  (:require [clojure.java.io :as io]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [schemaorg.unit :as unit]
            [schemaorg.namespace :as namespace]
            [schemaorg.hierarchy :as hierarchy]))

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

(def full-tpl
  "Full class hierarchy template"
  (slurp (io/resource "templates/full.tpl")))

(def rdf-mime-types
  "RDF MIME types"
  #{"application/rdf+xml"
    "text/turtle"
    "application/trig"
    "application/n-triples"
    "application/ld+json"})

(defn unit [term]
  (merge
    properties
    (unit/get-unit (str "http://" (properties "sitename") "/" term)
                   (properties "sparql_endpoint"))))

(defn vocab [mime-type]
  (namespace/get-vocab (str "http://" (properties "sitename"))
                       mime-type
                       (properties "sparql_endpoint")))

(defn full-hierarchy []
  (merge
    properties
    (hierarchy/get-hierarchy (properties "sparql_endpoint"))))

(defn send-home [accept]
  ; TODO JSON-LD context if ld+json
  (if (contains? rdf-mime-types accept)
    {:status 200
     :headers {"Content-Type" accept}
     :body (vocab accept)}
    {:status 200
     :headers {"Content-Type" "text/html; charset=utf-8"}
     :body (.render jj homepage-tpl properties)}))

(defn send-generic [term]
  (let [u (unit term)]
    {:status (if (nil? (get "rdfs_type" u)) 404 200)
     :headers {"Content-Type" "text/html; charset=utf-8"}
     :body (.render jj generic-tpl u)}))

(defn send-hierarchy []
  {:status 200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body (.render jj full-tpl (full-hierarchy))})

(defroutes app-routes
  ; TODO per-term content negotiation
  ; TODO proper 404
  (GET "/" request (send-home ((:headers request) "accept")))
  (GET "/:term" [term] (send-generic term))
  (GET "/docs/full.html" [] (send-hierarchy))
  (route/not-found {:status 404}))

(def app
  (wrap-defaults app-routes site-defaults))
