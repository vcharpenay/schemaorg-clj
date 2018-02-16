<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Full Hierarchy - {{ sitename }}</title>
    <meta name="description" content="Schema.org is a set of extensible schemas that enables webmasters to embed
    structured data on their web pages for use by search engines and other applications." />
    <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.5.1/jquery.min.js"></script>
    <link rel="stylesheet" type="text/css" href="{{ sitebase }}docs/schemaorg.css" />

<script type="text/javascript">
$(document).ready(function(){
    $('input[type="radio"]').click(function(){
        if($(this).attr("value")=="local"){
            $("#full_thing_tree").hide();
            $("#ext_thing_tree").hide();
            $("#thing_tree").show(500);
        }
        if($(this).attr("value")=="full"){
            $("#thing_tree").hide();
            $("#ext_thing_tree").hide();
            $("#full_thing_tree").show(500);
        }
        if($(this).attr("value")=="ext"){
            $("#thing_tree").hide();
            $("#full_thing_tree").hide();
            $("#ext_thing_tree").show(500);
        }
     });
	$("#full_thing_tree").hide();
	$("#ext_thing_tree").hide();
});
</script>
</head>
<body style="text-align: center;">

{% include 'templates/basicPageHeader.tpl' with context %}

<div style="text-align: left; margin-left: 8%; margin-right: 8%">

<h3>Full Hierarchy</h3>

<p>
Schema.org is defined as two hierarchies: one for textual property values, and one for the things that they describe. 
</p> 

<h4>Thing</h4>

<p>This is the main schema.org hierarchy: a collection of types (or "classes"), each of which has one or more parent types. Although a type may have more than one super-type, here we show each type in one branch of the tree only. There is also a parallel hierarchy for <a href="#datatype_tree">data types</a>.</p>

<br/>
<div>Select vocabulary view:<br/>
    <div>
        <label><input type="radio" name="viewSel" value="local" checked="checked"> {{local_button}}</label>
        <label><input type="radio" name="viewSel" value="full"> {{full_button}}</label>
		{% if ext_button != "" %}
        	<label><input type="radio" name="viewSel" value="ext"> {{ext_button}}</label>
		{% endif %}
	</div>
</div>

{# FIXME recursion not supported by Jinjava, macro not applicable #}

<div id="thing_tree">
<ul>
    {% for cl1 in thing_tree %}
            <li>
                <a href="{{ cl1.term }}">{{ cl1.label }}</a>
                <ul>
                {% for cl2 in cl1.children %}
                    <li>
                        <a href="{{ cl2.term }}">{{ cl2.label }}</a>
                        <ul>
                        {% for cl3 in cl2.children %}
                            <li>
                                <a href="{{ cl3.term }}">{{ cl3.label }}</a>
                                <ul>
                                {% for cl4 in cl3.children %}
                                    <li>
                                        <a href="{{ cl4.term }}">{{ cl4.label }}</a>
                                        <ul>
                                        {% for cl5 in cl4.children %}
                                            <li>
                                                <a href="{{ cl5.term }}">{{ cl5.label }}</a>
                                                <ul>{% if cl5.children %}...{% endif %}</ul>
                                            </li>
                                        {% endfor %}
                                        </ul>
                                    </li>
                                {% endfor %}
                                </ul>
                            </li>
                        {% endfor %}
                        </ul>
                    </li>
                {% endfor %}
                </ul>
            </li>
    {% endfor %}
</ul>
</div>
<div class="display: none" id="full_thing_tree">
{{ full_thing_tree | safe }}
</div>
{% if ext_button != "" %}
	<div class="display: none" id="ext_thing_tree">
	{{ ext_thing_tree | safe }}
	</div>
{% endif %}
<div id="datatype_tree">
{{ datatype_tree | safe }}
</div>



<p>An <em>experimental</em> <a href="http://d3js.org">D3</a>-compatible <a href="docs/tree.jsonld">JSON</a> version is also available.</p>
<br/><br/>

</div>

