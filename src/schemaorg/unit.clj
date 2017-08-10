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
    		<%term%> rdfs:subPropertyOf ?parent ;
    			rdfs:label ?parentLabel ;
    			rdfs:comment ?parentDesc .
		}
    	OPTIONAL {
    		?child rdfs:subPropertyOf <%term%> ;
    			rdfs:label ?childLabel ;
    			rdfs:comment ?childDesc .
		}
    	OPTIONAL {
    		<%term%> schema:domainIncludes ?dom  .
    		?dom rdfs:label ?domLabel ;
    		     rdfs:comment ?domDesc .
		}
    	OPTIONAL {
    		<%term%> schema:rangeIncludes ?range  .
    		?range rdfs:label ?rangeLabel ;
    		       rdfs:comment ?rangeDesc .
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

(defn get-prop [term endpoint]
	; FIXME parent, child, domain, range might be in another namespace
	; TODO Thing > Property [> parent] > term
	(let [bindings (sparql/query (prop-query term) endpoint)]
		(if (empty? bindings)
			{"term" nil}
			{"rdfs_type" "rdf:Property"
			"term" term
			"label" (-> bindings first :termLabel)
			"desc" (-> bindings first :termDesc)
			"parent" {"term" (-> bindings first :parent)
								"label" (-> bindings first :parentLabel)
								"desc" (-> bindings first :parentDesc)}
			"domain" (map (fn [cl] {"term" (-> cl first :dom)
															"label" (-> cl first :domLabel)
															"desc" (-> cl first :domDesc)})
										(vals (group-by :dom bindings)))
			"range" (map (fn [cl] {"term" (-> cl first :range)
															"label" (-> cl first :rangeLabel)
															"desc" (-> cl first :rangeDesc)})
										(vals (group-by :range bindings)))
			"parent_of" (map (fn [p] {"term" (-> p first :child)
																"label" (-> p first :childLabel)
																"desc" (-> p first :childDesc)})
											 (vals (group-by :child bindings)))})))

(defn get-hierarchy [term endpoint]
	(let [bindings (sparql/query (class-query term) endpoint)
			  b (first bindings)]
		(if (nil? b)
			(list)
			(list* {"term" term
							"label" (:termLabel b)
							"desc" (:termDesc b)
							"domain_of" (map (fn [p] (get-prop (-> p first :domProp) endpoint))
		 											(vals (group-by :domProp bindings)))}
						 (get-hierarchy (:parent b) endpoint)))))

(defn get-class [term endpoint]
	; TODO multiple inheritance?
	(let [bindings (sparql/query (class-query term) endpoint)
				hierarchy (get-hierarchy (-> bindings first :parent) endpoint)]
		(if (empty? bindings)
			{"term" nil}
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
											 (vals (group-by :child bindings)))})))

(defn get-unit [term endpoint]
	; TODO datatype
	(let [idx (+ 1 (str/last-index-of term "/"))]
		(if (java.lang.Character/isUpperCase (nth term idx))
			(get-class term endpoint)
			(get-prop term endpoint))))