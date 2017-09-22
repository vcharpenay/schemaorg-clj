<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<!-- Generated from genericTermPageHeader.tpl -->
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	{% if noindexpage %}<meta name="robots" content="noindex">{% endif %}
    <title>{{ label }} - {{ sitename }}</title>
    <meta name="description" content="{{ desc }}" />
    <link rel="stylesheet" type="text/css" href="docs/schemaorg.css" />
    <link href="docs/prettify.css" type="text/css" rel="stylesheet" />
    <script type="text/javascript" src="docs/prettify.js"></script>
    <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.5.1/jquery.min.js"></script>

<script type="text/javascript">
      $(document).ready(function(){
        prettyPrint();
        setTimeout(function(){

  $(".atn:contains(itemscope), .atn:contains(itemtype), .atn:contains(itemprop), .atn:contains(itemid), .atn:contains(time), .atn:contains(datetime), .atn:contains(datetime), .tag:contains(time) ").addClass('new');
  $('.new + .pun + .atv').addClass('curl');

        }, 500);
        setTimeout(function(){

  $(".atn:contains(property), .atn:contains(typeof) ").addClass('new');
  $('.new + .pun + .atv').addClass('curl');

        }, 500);
        setTimeout(function() {
          $('.ds-selector-tabs .selectors a').click(function() {
            var $this = $(this);
            var $p = $this.parents('.ds-selector-tabs');
            $('.selected', $p).removeClass('selected');
            $this.addClass('selected');
            $('pre.' + $this.data('selects'), $p).addClass('selected');
          });
        }, 0);
      });
</script>

<style>

  .pln    { color: #444;    } /* plain text                 */
  .tag    { color: #515484; } /* div, span, a, etc          */
  .atn,
  .atv    { color: #314B17; } /* href, datetime             */
  .new    { color: #660003; } /* itemscope, itemtype, etc,. */
  .curl   { color: #080;    } /* new url                    */

  table.definition-table {
    border-spacing: 3px;
    border-collapse: separate;
  }
  
  #morecheck {
	  outline: none;
  }
#morecheck:checked + div { display: none; }
  

</style>

</head>
<body class="{{ sitemode }}">

{% include 'templates/basicPageHeader.tpl' with context %}

  {% if rdfs_type %}

  <div id="mainContent" typeof="{{ rdfs_type }}" resource="{{ term }}">
  {{ ext_mappings | safe }}

  
  <h1 property="rdfs:label" class="page-title">{{ label }}</h1>
  <span class="canonicalUrl">Canonical URL: <a href="{{ term }}">{{ term }}</a></span>
  
  <h4><span class='breadcrumbs'>
    {% for cl in reverse_parent %}
    <a href="{{ cl.term }}">{{ cl.label }}</a> &gt;
    {% endfor %}
    <a href="{{ term }}">{{ label }}</a>
  </span></h4>

  <div property="rdfs:comment">{{ desc }}</div><br/>

  {% else %}{# 404 Not Found #}

  <div id="mainContent">

  {% endif %}

  {% if rdfs_type == "rdfs:Class" %}

  {% for prop in domain_of %}
  {% if loop.first %}
  <table class="definition-table">
    <thead><tr><th>Property</th><th>Expected Type</th><th>Description</th></tr></thead>
    <tr class="supertype"><th class="supertype-name" colspan="3">Properties from <a href="{{ term }}">{{ label }}</a></th></tr>
    <tbody class="supertype">
  {% endif %}
    <tr typeof="rdfs:Property" resource="{{ prop.term }}">
      <th class="prop-nam" scope="row"><code property="rdfs:label"><a href="{{ prop.term }}">{{ prop.label }}</a></code></th>
      <td class="prop-ect">
      {% for cl in prop.range %}
      <a href="{{ cl.term }}">{{ cl.label }}</a> {% if not loop.last %}&nbsp;or<br/>{% endif %}
      {% endfor %}
      </td>
      <td class="prop-desc" property="rdfs:comment">{{ prop.desc }}</td>
    </tr>
  {% if loop.last %}
    </tbody>
    {% for p in parent %}
    {% for pprop in p.domain_of %}
    {% if loop.first %}
    <tr class="supertype"><th class="supertype-name" colspan="3">Properties from <a href="{{ p.term }}">{{ p.label }}</a></th></tr>
    <tbody class="supertype">
    {% endif %}
      <tr typeof="rdfs:Property" resource="{{ prop.term }}">
        <th class="prop-nam" scope="row"><code property="rdfs:label"><a href="{{ pprop.term }}">{{ pprop.label }}</a></code></th>
        <td class="prop-ect">
        {% for pcl in pprop.range %}
        <a href="{{ pcl.term }}">{{ pcl.label }}</a> {% if not loop.last %}&nbsp;or<br/>{% endif %}
        {% endfor %}
        </td>
        <td class="prop-desc" property="rdfs:comment">{{ pprop.desc }}</td>
      </tr>
    {% if loop.last %}
    </tbody>
    {% endif %}
    {% endfor %}
    {% endfor %}
  </table>
  <br/>
  {% endif %}
  {% endfor %}

  {% for prop in range_of %}
  {% if loop.first %}
  <br/>
  <div id="incoming">Instances of <a href="{{ term }}">{{ label }}</a> may appear as values for the following properties</div>
  <br/>
  <table class="definition-table">
    <thead><tr><th>Property</th><th>On Types</th><th>Description</th></tr></thead>
  {% endif %}
    <tr typeof="rdfs:Property" resource="{{ prop.term }}">
      <th class="prop-nam" scope="row"><code property="rdfs:label"><a href="{{ prop.term }}">{{ prop.label }}</a></code></th>
      <td class="prop-ect">
      {% for cl in prop.domain %}
      <a href="{{ cl.term }}">{{ cl.label }}</a> {% if not loop.last %}&nbsp;or<br/>{% endif %}
      {% endfor %}
      </td>
      <td class="prop-desc" property="rdfs:comment">{{ prop.desc }}</td>
    </tr>
  {% if loop.last %}
  </table>
  {% endif %}
  {% endfor %}

  {% for cl in parent_of %}
  {% if loop.first %}
  <h4>More specific Types</h4>
  <ul>
  {% endif %}
    <li><a href="{{ cl.term }}">{{ cl.label }}</a></li>
  {% if loop.last %}
  </ul>
  {% endif %}
  {% endfor %}

  {% elif rdfs_type == "rdf:Property" %}

  {% for cl in domain %}
  {% if loop.first %}
  <table class="definition-table">
    <thead><tr><th>Values expected to be one of these types</th></tr></thead>
    <tr><td>
  {% endif %}
      <code><a href="{{ cl.term }}" title="The '{{ label }}' property has values that include instances of the '{{ cl.label }}' type.">{{ cl.label }}</a></code>
      {% if not loop.last %}<br/>{% endif %}
  {% if loop.last %}
    </td></tr>
  </table>
  {% endif %}
  {% endfor %}

  {% for cl in range %}
  {% if loop.first %}
  <table class="definition-table">
    <thead><tr><th>Used on these types</th></tr></thead>
    <tr><td>
  {% endif %}
      <code><a href="{{ cl.term }}" title="The '{{ label }}' property is used on the '{{ cl.label }}' type.">{{ cl.label }}</a></code>
      {% if not loop.last %}<br/>{% endif %}
  {% if loop.last %}
    </td></tr>
  </table>
  {% endif %}
  {% endfor %}

  {% for p in parent %}
  {% if loop.first %}
  <table class="definition-table">
    <thead><tr><th>Super-properties</th></tr></thead>
    <tr><td>
  {% endif %}
      <code><a href="{{ p.term }}" title="{{ p.label }}: {{ p.desc }}.">{{ p.label }}</a></code>
      {% if not loop.last %}<br/>{% endif %}
  {% if loop.last %}
    </td></tr>
  </table>
  {% endif %}
  {% endfor %}

  {% for p in parent_of %}
  {% if loop.first %}
  <table class="definition-table">
    <thead><tr><th>Sub-properties</th></tr></thead>
    <tr><td>
  {% endif %}
      <code><a href="{{ p.term }}" title="{{ p.label }}: {{ p.desc }}.">{{ p.label }}</a></code>
      {% if not loop.last %}<br/>{% endif %}
  {% if loop.last %}
    </td></tr>
  </table>
  {% endif %}
  {% endfor %}

  {% else %}{# 404 Not Found #}

  <h3>404 Not Found.</h3><p><br/>Page not found. Please <a href="/">try the homepage.</a><br/><br/></p>

  {% endif %}

  <p class="version"><b>Schema Version 3.2</b></p>

  </div>

</body>
</html>

<!-- end of genericTermPageHeader.tpl -->