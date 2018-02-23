# Schema.org

Re-writing of [schemaorg](https://github.com/schemaorg/schemaorg/) in Clojure.

Changes:
 - no local triple store, schema units are retrieved via SPARQL HTTP
 - extended generic term page template
 - no extension, all namespaces considered equal

TODO:
 - other templates
   - fullReleasePage.tpl
 - redirection to other known namespace (and mark domain/range as unknown if somewhere else)
 - examples
 - RDFa annotations (or JSON-LD?)
 - content negotiation
 - unit caching
 - logging
 - reorganizing templates (e.g. put HTML head in a separate file)
 - unit tests
 - document properties (e.g. sitename, sitebase)

## Prerequisites

 - [Leiningen](https://github.com/technomancy/leiningen) 2.0.0 or above
 - A SPARQL 1.1 query endpoint accessible via HTTP (e.g. [Apache Fuseki](http://jena.apache.org/documentation/fuseki2/index.html))

## SPARQL dataset structure

Schema definitions belonging to a given namespace are expected to be put in a
named graph whose URI is the same as the namespace URI (without trailing slash).

For instance, one can have

    GRAPH <http://schema.org> {
        <http://schema.org/Thing> a rdfs:Class ;
            rdfs:label "Thing" ;
            rdfs:comment "The most generic type of item." .
    }
    
    GRAPH <http://core.example.org> {
        <http://core.example.org/Something> a rdfs:Class ;
            rdfs:label "Something" ;
            rdfs:comment "Well... Anything." ;
            rdfs:subClassOf <http://schema.org/Thing> .
    }

## Running

First, open `resources/schemaorg.properties` and change `sparql_endpoint` to
point to your own SPARQL endpoint:

    sitename=example.org
    sitemode=mainsite
    sparql_endpoint=http://example.org/someSPARQLEndpoint

Then, to start a web server for the application, run:

    lein ring server

## License

Copyright © 2017 Victor Charpenay
