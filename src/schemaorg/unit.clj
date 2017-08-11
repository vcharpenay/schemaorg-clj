(ns schemaorg.unit
	(:require [clojure.string :as str]
						[sparql.client :as sparql]))

(defn prop-query [term]
	(str/replace "
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX  owl:  <http://www.w3.org/2002/07/owl#>
PREFIX schema: <http://schema.org/>

SELECT *
WHERE {
	GRAPH ?graph {
    	<%term%> rdf:type rdf:Property ;
    		rdfs:label ?termLabel ;
    		rdfs:comment ?termDesc .
    	OPTIONAL {
    		<%term%> rdfs:subPropertyOf ?parent .
		}
    	OPTIONAL {
    		?child rdfs:subPropertyOf <%term%> ;
    			   rdfs:label ?childLabel ;
    			   rdfs:comment ?childDesc .
		}
    	OPTIONAL {
    		<%term%> schema:domainIncludes ?dom  .
		}
    	OPTIONAL {
    		<%term%> schema:rangeIncludes ?range  .
		}
	}
}
	" "%term%" term))

(defn class-query [term]
  (str/replace "
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX  schema: <http://schema.org/>

SELECT * WHERE {
	GRAPH ?graph {
    	<%term%> rdf:type rdfs:Class ;
    		rdfs:label ?termLabel ;
    		rdfs:comment ?termDesc .
    	OPTIONAL {
    		<%term%> rdfs:subClassOf ?parent .
		}
    	OPTIONAL {
    		?child rdfs:subClassOf <%term%> ;
    			   rdfs:label ?childLabel ;
    			   rdfs:comment ?childDesc .
		}
    	OPTIONAL {
    		?domProp schema:domainIncludes <%term%> .
		}
    	OPTIONAL {
    		?rangeProp schema:rangeIncludes <%term%> .
		}
	}
}
  " "%term%" term))

(defn get-class-ref [term endpoint]
	(let [bindings (sparql/query (class-query term) endpoint)
				b (first bindings)]
		(if (nil? b)
			{"term" nil}
			{"rdfs_type" "rdfs:Class"
			 "term" term
			 "label" (-> bindings first :termLabel)
			 "desc" (-> bindings first :termDesc)})))

(defn get-prop-hierarchy [term endpoint]
	(let [bindings (sparql/query (prop-query term) endpoint)
			  b (first bindings)]
		(if (nil? b)
			; see meta-schema
			(list {"term" "http://schema.org/Property"
						 "label" "Property"
						 "desc" "Equivalent to rdf:Property."})
			(list* {"term" term
							"label" (:termLabel b)
							"desc" (:termDesc b)}
						 (get-prop-hierarchy (:parent b) endpoint)))))

(defn get-prop [term endpoint]
	; FIXME sub-property not found if defined in other namespace
	(let [bindings (sparql/query (prop-query term) endpoint)]
		(if (empty? bindings)
			{"term" nil}
			(let [hierarchy (get-prop-hierarchy (-> bindings first :parent) endpoint)]
				{"rdfs_type" "rdf:Property"
				"term" term
				"label" (-> bindings first :termLabel)
				"desc" (-> bindings first :termDesc)
				"domain" (map (fn [cl] (get-class-ref (-> cl first :dom) endpoint))
												(vals (group-by :dom bindings)))
				"range" (map (fn [cl] (get-class-ref (-> cl first :range) endpoint))
												(vals (group-by :range bindings)))
				"parent" hierarchy
				"reverse_parent" (reverse hierarchy) ; for breadcrumbs
				"parent_of" (map (fn [p] {"term" (-> p first :child)
																	"label" (-> p first :childLabel)
																	"desc" (-> p first :childDesc)})
												 (vals (group-by :child bindings)))}))))

(defn get-class-hierarchy [term endpoint]
	(let [bindings (sparql/query (class-query term) endpoint)
			  b (first bindings)]
		(if (nil? b)
			(list)
			(list* {"term" term
							"label" (:termLabel b)
							"desc" (:termDesc b)
							"domain_of" (map (fn [p] (get-prop (-> p first :domProp) endpoint))
		 											(vals (group-by :domProp bindings)))}
						 (get-class-hierarchy (:parent b) endpoint)))))

(defn get-class [term endpoint]
	; TODO multiple inheritance?
	; TODO enumeration
	; FIXME sub-class not found if defined in other namespace
	(let [bindings (sparql/query (class-query term) endpoint)]
		(if (empty? bindings)
			{"term" nil}
			(let [hierarchy (get-class-hierarchy (-> bindings first :parent) endpoint)]
				{"rdfs_type" "rdfs:Class"
		 		 "term" term
				 "label" (-> bindings first :termLabel)
				 "desc" (-> bindings first :termDesc)
				 "parent" hierarchy
				 "reverse_parent" (reverse hierarchy) ; for breadcrumbs
				 "domain_of" (map (fn [p] (get-prop (-> p first :domProp) endpoint))
													(vals (group-by :domProp bindings)))
				 "range_of" (map (fn [p] (get-prop (-> p first :rangeProp) endpoint))
												(vals (group-by :rangeProp bindings)))
				 "parent_of" (map (fn [cl] {"term" (-> cl first :child)
																		"label" (-> cl first :childLabel)
																		"desc" (-> cl first :childDesc)})
												(vals (group-by :child bindings)))}))))

(defn get-unit [term endpoint]
	; TODO datatype
	(let [idx (+ 1 (str/last-index-of term "/"))]
		(if (java.lang.Character/isUpperCase (nth term idx))
			(get-class term endpoint)
			(get-prop term endpoint))))