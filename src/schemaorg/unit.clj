(ns schemaorg.unit
	(:require [clojure.string :as str]
						[sparql.client :as sparql]))

(defn class-query [term]
  (format "
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX  schema: <http://schema.org/>

SELECT * WHERE {
	GRAPH ?graph {
    	<%s> rdf:type rdfs:Class ;
    		rdfs:label ?termLabel ;
    		rdfs:comment ?termDesc .
    	OPTIONAL {
    		<%s> rdfs:subClassOf ?parent ;
    			rdfs:label ?parentLabel ;
    			rdfs:comment ?parentDesc .
		}
    	OPTIONAL {
    		?domProp schema:domainIncludes <%s> ;
    			rdfs:label ?domPropLabel ;
    			rdfs:comment ?domPropDesc .
				OPTIONAL {
					?domProp schema:rangeIncludes ?domPropRange .
					?domPropRange rdfs:label ?domPropRangeLabel ;
						rdfs:comment ?domPropRangeDesc .
				}
		}
    	OPTIONAL {
    		?rangeProp schema:rangeIncludes <%s> ;
    			rdfs:label ?rangePropLabel ;
    			rdfs:comment ?rangePropDesc .
				OPTIONAL {
					?rangeProp schema:domainIncludes ?rangePropDom .
					?rangePropDom rdfs:label ?rangePropDomLabel ;
						rdfs:comment ?rangePropDomDesc .
				}
		}
	}
}
  " term term term term))

(defn prop-query [term]
	(format "
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX  owl:  <http://www.w3.org/2002/07/owl#>
PREFIX schema: <http://schema.org/>

SELECT *
WHERE {
	GRAPH ?graph {
    	<%s> rdf:type rdf:Property ;
    		rdfs:label ?termLabel ;
    		rdfs:comment ?termDesc .
    	OPTIONAL {
    		<%s> rdfs:subPropertyOf ?parent ;
    			rdfs:label ?parentLabel ;
    			rdfs:comment ?parentDesc .
		}
    	OPTIONAL {
    		?child rdfs:subPropertyOf <%s> ;
    			rdfs:label ?childLabel ;
    			rdfs:comment ?childDesc .
		}
    	OPTIONAL {
    		<%s> schema:domainIncludes ?dom  .
    		?dom rdfs:label ?domLabel ;
    		     rdfs:comment ?domDesc .
		}
    	OPTIONAL {
    		<%s> schema:rangeIncludes ?range  .
    		?range rdfs:label ?rangeLabel ;
    		       rdfs:comment ?rangeDesc .
		}
	}
}
	" term term term term term))

(defn class-def [term bindings]
	; TODO all parent classes
	; TODO domain/range of parent classes
	{"rdfs_type" "rdfs:Class"
	 "term" term
	 "label" (-> bindings first :termLabel)
	 "desc" (-> bindings first :termDesc)
	 "parent" {"term" (-> bindings first :parent)
	           "label" (-> bindings first :parentLabel)
			       "desc" (-> bindings first :parentDesc)}
	 "domain_of" (map (fn [p] {"term" (-> p first :domProp)
	 													 "label" (-> p first :domPropLabel)
														 "desc" (-> p first :domPropDesc)
														 "range" (map (fn [cl] {"term" (:domPropRange cl)
																									  "label" (:domPropRangeLabel cl)
	 																								  "desc" (:domPropRangeDesc cl)}) p)})
									 (vals (group-by :domProp bindings)))
	 "range_of" (map (fn [p] {"term" (-> p first :rangeProp)
	 												  "label" (-> p first :rangePropLabel)
													  "desc" (-> p first :rangePropDesc)
													  "domain" (map (fn [cl] {"term" (:rangePropDom cl)
																								 		"label" (:rangePropDomLabel cl)
	 																						  		"desc" (:rangePropDomDesc cl)}) p)})
									 (vals (group-by :rangeProp bindings)))})

(defn prop-def [term bindings]
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
	 "parent_of" "TODO"})

(defn get-class [term endpoint]
	(class-def term (sparql/query (class-query term) endpoint)))

(defn get-prop [term endpoint]
	(prop-def term (sparql/query (prop-query term) endpoint)))

(defn get-unit [term endpoint]
	(let [idx (+ 1 (str/last-index-of term "/"))]
		(if (java.lang.Character/isUpperCase (nth term idx))
			(get-class term endpoint)
			(get-prop term endpoint))))