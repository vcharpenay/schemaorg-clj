# Schema.org

Re-writing of [schemaorg](https://github.com/schemaorg/schemaorg/) in Clojure.

Changes:
 - no local triple store, schema units are retrieved via SPARQL HTTP
 - extended generic term page template
 - no extension, all namespaces considered equal

TODO:
 - other templates
 - redirection to other known namespace (and mark domain/range as unknown if somewhere else)
 - examples
 - RDFa annotations (or JSON-LD?)
 - content negotiation
 - unit caching
 - logging
 - reorganizing templates (e.g. put HTML head in a separate file)
 - unit tests
 - expand 'Getting started'

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein ring server

## License

Copyright © 2017 Victor Charpenay
