<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ attribute name="nodeType" required="true" %>
<%@ attribute name="id" required="true" %>

<jsp:useBean id="nodeDefHelper" class="org.sri.nodeservice.web.nodedef.util.NodeDefMgrHelper" scope="page" >
  <jsp:setProperty name="nodeDefHelper" property="nodeType" value="${nodeType}" />
</jsp:useBean>

<c:set var="first" value='true'/>
var readerFields = [
	{name: 'id', mapping: 'id'},
	{name: 'errors', mapping: 'errors'}
	<c:set var="first" value='false'/>
	<c:forEach var="attr" items="${nodeDefHelper.nodeDefinition.attributes}">
	
		<c:if test="${first == 'false'}">,</c:if>		
		{name: '${attr.id}', mapping: '${attr.id}'}
	</c:forEach>
];

<c:set var="first" value='true'/>
var gridColumns = [
	<c:forEach var="attr" items="${nodeDefHelper.nodeDefinition.attributes}">
	
		<c:if test="${first == 'false'}">,</c:if>
		<c:set var="first" value='false'/>
		{
			id: '${attr.id}',
			header: '${attr.label}',
			dataIndex: '${attr.id}',
			flex: 1
        }
	</c:forEach>
];


var nodeTypeDyn = '${nodeType}';
var idDyn = '${id}';
var idDynVersion;

