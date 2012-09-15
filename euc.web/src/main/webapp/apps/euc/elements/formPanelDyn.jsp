<%@ page import="org.sri.nodeservice.web.nodedef.util.*" %>
<%@ taglib prefix="sc" tagdir="/WEB-INF/tags" %>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<title>Node Form Panel (Dynamic)</title>
	
	<link rel="stylesheet" type="text/css" href="../../../js/extjs/resources/css/ext-all.css" />

 	<!-- Ext JS Imports -->
    <script type="text/javascript" src="../../../js/extjs/ext-all-debug.js"></script>
        
    <!-- EUC NS Imports -->
    <script type="text/javascript" src="../../../js/euc/config/config.js"></script>
    <script type="text/javascript" src="../../../js/euc/elements/activityFormPanelDyn.js"></script>


	<style type="text/css">
	    .complete .x-tree-node-anchor span {
	        text-decoration: line-through;
	        color: #777;
	    }
	</style>

	<!-- Put it together -->
	<script type="text/javascript">
	
		<%
			String nodeId = request.getParameter("nodeId");
			String nodeType = request.getParameter("nodeType");
			if (nodeId == null || nodeType == null) {
		        out.println("No ID or Type supplied");
		    } else {
		        //
		    }
		%>
	
		<sc:formConfig nodeType="<%=nodeType%>" id="<%=nodeId%>"/>
	
		Ext.onReady(function(){
			
			var form = new ActivityFormPanelDyn({
				preventHeader: true,
				autoScroll: true,
				flex:1
			});
		    
			Ext.create('Ext.Viewport', {
			    preventHeader: true,
			    border: false,
			    autoScroll: false,
			    layout:'fit',
		    	layoutConfig: {
		    	    align : 'stretch',
		    	    pack  : 'start'
		    	},
			    renderTo: 'nodeLayout',
			    items: [form]
			});
		    
		});
	</script>

</head>
	<body>
		<br><br>
		<br><br>
		
		<table align="center">
			<tr><td>
				<div id="nodeLayout"></div>
			</td></tr>
		</table>
	</body>
	
</html>