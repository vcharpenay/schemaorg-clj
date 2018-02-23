<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Home - {{ sitename }}</title>
    <meta name="description" content="Schema.org is a set of extensible schemas that enables webmasters to embed
    structured data on their web pages for use by search engines and other applications." />
    <link rel="stylesheet" type="text/css" href="docs/schemaorg.css">

</head>
<body>
{% include 'templates/basicPageHeader.tpl' with context %}

  <div id="mainContent">


{% import 'templates/ext.tpl' as ext with context %}

{% if mybasehost in [ "sdo-deimos.appspot.com", "sdo-phobos.appspot.com", "sdo-ganymede.appspot.com", "sdo-gozer.appspot.com", "sdo-callisto.appspot.com", "webschemas.org", "sdo-scripts.appspot.com", "localhost" ] %}

<!--<p id="lli" class="layerinfo">
Note: This is {{ mybasehost }}. you are viewing an unstable work-in-progress preview of <a href="http://schema.org/">schema.org</a>.
See the draft <b><a href="{{staticPath}}/docs/releases.html">releases</a></b> page to learn more about this version ({{ SCHEMA_VERSION }}).
</p>-->

{% endif %}


{% if ENABLE_HOSTED_EXTENSIONS and extComment != "" %}
  {{ ext.overview() }}

<p>
  {{extComment |safe}}
</p>

{% elif ENABLE_HOSTED_EXTENSIONS and host_ext == "test001" %}
  {{ ext.overview(name="Test Extension", abbrev="test1") }}

  <p>This is purely here for testing, please ignore.</p>

  <p><br/></p>

{% else %}


  <h1>Welcome to iotschema.org</h1>


<p>
    Schema.org is a collaborative, community activity with a mission to create,
    maintain, and promote schemas for structured data on the
    Internet, on web pages, in email messages, and beyond.
</p>
<p>
    iotschema.org is an extension of schema.org for Internet of Things. The goal 
  	is to enable web applications to interact with the physical world based on 
  	machine interpretable information. iotschema.org enables semantic interoperability 
  	for connected things across diverse IoT ecosystems. 
</p>
<p>
    iotschema.org vocabularies are developed by an open community process, 
  	using the <a
    href="https://groups.google.com/forum/#!forum/sdo-iot-sync">sdo-iot-sync</a>
    mailing list and through <a href="https://github.com/iot-schema-collab">GitHub</a>.
</p>
<p>
   We invite you to get started by browsing thing <a href="http://iotschema.org/Capability">Capabilities</a>!
</p>

<br/>
</div>


{% endif %}

{{ ext_contents | safe }}

<div id="footer"><p>
  <a href="docs/terms.html">Terms and conditions</a></p>
</div>

<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
  ga('create', 'UA-52672119-1', 'auto');
  ga('send', 'pageview');
</script>



<p><br/></p>

{{ ext.debugInfo() }}

</body>
</html>