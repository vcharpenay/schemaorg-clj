(ns schemaorg.namespace
	(:require [clojure.string :as str]
						[sparql.client :as sparql]))

(defn vocab-query [namespace]
	(str/replace "
CONSTRUCT {
  ?s ?p ?o
} WHERE {
	GRAPH <%namespace%> {
    ?s ?p ?o
	}
}
	" "%namespace%" namespace))

(defn get-vocab [namespace mime endpoint]
	(sparql/construct (vocab-query namespace) mime endpoint))