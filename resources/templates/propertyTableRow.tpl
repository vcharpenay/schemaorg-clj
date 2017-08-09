<tr typeof="rdfs:Property" resource="{{ prop.term }}">
  <th class="prop-nam" scope="row"><code property="rdfs:label"><a href="{{ prop.term }}">{{ prop.label }}</a></code></th>
  <td class="prop-ect">
  {% for cl in prop.domain %}
  <a href="{{ cl.term }}">{{ cl.label }}</a> {% if not loop.last %}&nbsp;or<br/>{% endif %}
  {% endfor %}
  </td>
  <td class="prop-desc" property="rdfs:comment">{{ prop.desc }}</td>
</tr>