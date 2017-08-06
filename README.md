# Schema.org

Re-writing of [schemaorg](https://github.com/schemaorg/schemaorg/) in Clojure.

Changes:
 - no local triple store, schema units are retrieved via SPARQL HTTP
 - extended generic term page template
 - no extension, all namespaces considered equal

TODO:
 - other templates
 - 404
 - RDFa annotations (or JSON-LD?)
 - unit caching
 - logging

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein ring server

## License

Copyright © 2017 Victor Charpenay
