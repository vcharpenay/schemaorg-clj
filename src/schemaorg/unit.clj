(ns schemaorg.unit
	(:require [clojure.string :as str]
						[sparql.client :as sparql]))

(defn entity-query [term]
	(str/replace "
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX schema: <http://schema.org/>

SELECT *
WHERE {
	GRAPH ?graph {
  	<%term%> rdf:type ?type ;
    	rdfs:label ?termLabel ;
    	rdfs:comment ?termDesc .
	}
}
	" "%term%" term))

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
		OPTIONAL {
			?member a <%term%> .
		}
	}
}
  " "%term%" term))

(defn group-by-var
	"Helper function that groups bindings by `var`, if bound."
	[var bindings]
	(if (every? (fn [b] (empty? (b var))) bindings)
		nil
		(vals (group-by var bindings))))

(defn local-name
	"Local name in term URI (used as default label)."
	[term]
	(last (str/split term #"/")))

(defn get-entity [term endpoint]
	(let [bindings (sparql/select (entity-query term) endpoint)
	      b (first bindings)]
		(if (some? b)
			{"rdfs_type" (:type b)
			 "term" term
			 "label" (:termLabel b)
			 "desc" (:termDesc b)}
			nil)))

(defn get-entity-ref [term endpoint]
	(let [entity (get-entity term endpoint)]
		(if (nil? entity)
			{"term" term
			 "label" (local-name term)}
			entity)))

(defn get-prop-hierarchy [term endpoint]
	; TODO Thing > Property [> parent] > term
	; TODO test term is IRI instead
	(if (empty? term)
		(list)
		(let [bindings (sparql/select (prop-query term) endpoint)
			    b (first bindings)]
			(if (nil? b)
				[{"term" term
				  "label" (local-name term)}]
				(list* {"term" term
								"label" (:termLabel b)
								"desc" (:termDesc b)}
							(get-prop-hierarchy (:parent b) endpoint))))))

(defn get-prop [term endpoint]
	; FIXME sub-property not found if defined in other namespace
	; TODO sort properties/classes alphabetically
	(let [bindings (sparql/select (prop-query term) endpoint)]
		(if (empty? bindings)
			{"term" term
			 "label" (local-name term)}
			(let [parent (-> bindings first :parent)
						hierarchy (if (empty? parent) () (get-prop-hierarchy parent endpoint))]
				{"rdfs_type" "rdf:Property"
				"term" term
				"label" (-> bindings first :termLabel)
				"desc" (-> bindings first :termDesc)
				"domain" (map (fn [cl] (get-entity-ref (-> cl first :dom) endpoint))
											(group-by-var :dom bindings))
				"range" (map (fn [cl] (get-entity-ref (-> cl first :range) endpoint))
										 (group-by-var :range bindings))
				"parent" hierarchy
				"reverse_parent" (reverse hierarchy) ; for breadcrumbs
				"parent_of" (map (fn [p] {"term" (-> p first :child)
																	"label" (-> p first :childLabel)
																	"desc" (-> p first :childDesc)})
												 (group-by-var :child bindings))}))))

(defn get-class-hierarchy [term endpoint]
	; TODO test term is IRI instead
	(if (empty? term)
		(list)
		(let [bindings (sparql/select (class-query term) endpoint)
		      b (first bindings)]
			(if (nil? b)
				[{"term" term
				  "label" (local-name term)}]
				(list* {"term" term
								"label" (:termLabel b)
								"desc" (:termDesc b)
								"domain_of" (map (fn [p] (get-prop (-> p first :domProp) endpoint))
																(group-by-var :domProp bindings))}
							(get-class-hierarchy (:parent b) endpoint))))))

(defn get-class [term endpoint]
	; TODO multiple inheritance?
	; TODO enumeration
	; FIXME sub-class not found if defined in other namespace
	; TODO sort properties/classes alphabetically
	(let [bindings (sparql/select (class-query term) endpoint)]
		(if (empty? bindings)
			{"term" term
			 "label" (local-name term)}
			(let [parent (-> bindings first :parent)
						hierarchy (if (empty? parent) () (get-class-hierarchy parent endpoint))]
				{"rdfs_type" "rdfs:Class"
		 		 "term" term
				 "label" (-> bindings first :termLabel)
				 "desc" (-> bindings first :termDesc)
				 "parent" hierarchy
				 "reverse_parent" (reverse hierarchy) ; for breadcrumbs
				 "domain_of" (map (fn [p] (get-prop (-> p first :domProp) endpoint))
													(group-by-var :domProp bindings))
				 "range_of" (map (fn [p] (get-prop (-> p first :rangeProp) endpoint))
													(group-by-var :rangeProp bindings))
				 "parent_of" (map (fn [cl] (get-entity-ref (-> cl first :child) endpoint))
													(group-by-var :child bindings))
				 "members" (map (fn [i] (get-entity-ref (-> i first :member) endpoint))
											 (group-by-var :member bindings))}))))

(defn get-unit [term endpoint]
	(let [entity (get-entity term endpoint)
	      type (get entity "rdfs_type")]
		(case type
			    "http://www.w3.org/2000/01/rdf-schema#Class" (get-class term endpoint)
			    "http://www.w3.org/1999/02/22-rdf-syntax-ns#Property" (get-prop term endpoint)
			    (merge
						entity
						{"reverse_parent" (reverse (get-class-hierarchy type endpoint))}))))