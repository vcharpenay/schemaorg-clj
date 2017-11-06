(ns sparql.client
  (:require
    [clj-http.client :as http]
    [clojure.data.csv :as csv]))

(defn to-map
  "Turns each CSV entry (vector) into a map."
  [csv]
  (let [head (map keyword (first csv))
	      bindings (rest csv)]
    (map (fn [b] (zipmap head b)) bindings)))

(defn select
  "Minimal implementation of the SPARQL protocol (SELECT queries). See http://www.w3.org/TR/sparql11-protocol/."
  ; TODO sync function / no error handling
  [query remote]
  (to-map
    (csv/read-csv
      (:body
        (http/post remote
          {:content-type "application/sparql-query"
           :accept "text/csv"
           :body query})))))

(defn construct
  ; TODO forward stream instead of string? 
  "Minimal implementation of the SPARQL protocol (CONSTRUCT queries). See http://www.w3.org/TR/sparql11-protocol/."
  [query mime remote]
  (:body
    (http/post remote
      {:content-type "application/sparql-query"
       :accept mime
       :body query})))