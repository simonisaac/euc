<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ attribute name="nodeType" required="true" %>
<%@ attribute name="id" required="true" %>

<jsp:useBean id="nodeDefHelper" class="org.sri.nodeservice.web.nodedef.util.NodeDefMgrHelper" scope="page" >
  <jsp:setProperty name="nodeDefHelper" property="nodeType" value="${nodeType}" />
</jsp:useBean>

<c:set var="first" value='true'/>
var fieldConfig = {
	items: [
		<c:forEach var="attr" items="${nodeDefHelper.nodeDefinition.attributes}">
		
			<c:if test="${first == 'false'}">,</c:if>
			<c:set var="first" value='false'/>
			{
		    	xtype:'${attr.xtype}',
		    	id:'${attr.id}',
		    	fieldLabel:'${attr.label}',
		    	value:'${attr.defaultValue}',
		    	anchor:'98%'
			}
		</c:forEach>
	]
}

var nodeTypeDyn = '${nodeType}'
var idDyn = '${id}'

