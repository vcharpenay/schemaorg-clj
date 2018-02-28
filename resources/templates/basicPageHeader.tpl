<!-- Header start from basicPageHeader.tpl -->
<div id="container">
	<div id="intro">
		<div id="pageHeader">
			<div class="wrapper">
				<div id="sitename">
				<h1>
					<a href="{{ sitebase }}">{{ sitename }}</a>
				</h1>
				</div>
				<div id="cse-search-form" style="width: 400px;">
<script>
  (function() {
    var cx = '003149661203300036131:li74k4fnhpu';
    var gcse = document.createElement('script');
    gcse.type = 'text/javascript';
    gcse.async = true;
    gcse.src = 'https://cse.google.com/cse.js?cx=' + cx;
    var s = document.getElementsByTagName('script')[0];
    s.parentNode.insertBefore(gcse, s);
  })();
</script>
<gcse:search></gcse:search>
</div>
			</div>
		</div>
	</div>
</div>
<div id="selectionbar">
	<div class="wrapper">
		<ul>
	        {% if menu_sel == "Documentation" %}
	        <li class="activelink">
	        {% else %}
	        <li>
	        {% endif %}
				<a href="{{ sitebase }}docs/documents.html">Documentation</a>
			</li>
	        {% if menu_sel == "Schemas" %}
	        <li class="activelink">
	        {% else %}
	        <li>
	        {% endif %}
				<a href="{{ sitebase }}docs/schemas.html">Schemas</a>
			</li>
			<li>
				<a href="{{ sitebase }}docs/about.html">About</a>
			</li>
		</ul>
	</div>
</div>
<div style="padding: 14px; float: right;" id="languagebox"></div>

{% if mybasehost in [ "webschemas.org", "localhost"] %}
<div class="devnote"><b>Note</b>: you are viewing the
	<a href="http://webschemas.org/">webschemas.org</a> development
	version of <a href="http://schema.org/">schema.org</a>.
	See <a href="docs/howwework.html">How we work</a> for more details.
</div>
{% endif %}

<!-- Header end from basicPageHeader.tpl -->
