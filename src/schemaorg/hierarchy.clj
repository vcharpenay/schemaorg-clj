(ns schemaorg.hierarchy
	(:require [clojure.string :as str]
            [clojure.set :as set]
            [schemaorg.unit :as unit]
						[sparql.client :as sparql]))

(def query
	"
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX  owl:  <http://www.w3.org/2002/07/owl#>
PREFIX schema: <http://schema.org/>

SELECT *
WHERE {
  GRAPH ?graph {
    ?child rdfs:subClassOf ?parent .
  } OPTIONAL {
    GRAPH ?parentGraph {
      ?parent rdf:type rdfs:Class ;
        rdfs:label ?parentLabel
    }
  } OPTIONAL {
    GRAPH ?childGraph {
      ?child rdf:type rdfs:Class ;
        rdfs:label ?childLabel
    }
  }
}
	")

(defn get-roots [bindings]
  (let [parents (set (map :parent bindings))
        children (set (map :child bindings))]
    (set/difference parents children)))

(defn get-term [term bindings]
  (let [b (filter #(= (:parent %) term) bindings)
        rest (remove #(= (:parent %) term) bindings)]
    {"term" term
     "label" (unit/local-name term)
     "children" (map #(get-term % rest) (set (map :child b)))}))

(defn get-hierarchy [endpoint]
  (let [bindings (sparql/select query endpoint)]
    {"thing_tree" (map #(get-term % bindings) (get-roots bindings))}))