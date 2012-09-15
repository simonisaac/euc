<%@ page import="org.sri.nodeservice.web.nodedef.util.*" %>
<%@ taglib prefix="sc" tagdir="/WEB-INF/tags" %>

<html>
<head>

	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<title>EUC Interface Panel</title>
	<link rel="stylesheet" type="text/css" href="../../../js/extjs/resources/css/ext-all.css" />
	
	<link rel="stylesheet" type="text/css" href="../../../js/rowactions/Ext.ux.grid.RowActions.css" />
	<link rel="stylesheet" type="text/css" href="../../../js/rowactions/icons.css" />

 	<!-- Ext JS Imports -->
    <script type="text/javascript" src="../../../js/extjs/ext-all-debug.js"></script>

    
    <script type="text/javascript" src="../../../js/rowactions/Ext.ux.grid.RowActions.js"></script>
        
    <!-- EUC NS Imports -->
    <script type="text/javascript" src="../../../js/euc/config/config.js"></script>
	<script type="text/javascript" src="../../../js/euc/elements/gridPanelDyn.js"></script>
	<script type="text/javascript" src="../../../js/euc/elements/iFrame.js"></script>

	<style type="text/css">
	    .complete .x-tree-node-anchor span {
	        text-decoration: line-through;
	        color: #777;
	    }
	    
	    .red-row { 
	    	background-color: #F8A1A1; 
	    }
	     
		.white-row {
			background-color: white; 
		}
		 
	</style>
	
	<!-- Put it together -->
	<script type="text/javascript">
	
		<%
			String nodeType = request.getParameter("nodeType");
			String nodeId = request.getParameter("nodeId");
			if (nodeType == null || nodeId == null) {
		        out.println("No Node Type or Node Id is supplied");
		    } else {
		        //
		    }
		%>		
		
		<sc:gridConfig nodeType="<%=nodeType%>" id="<%=nodeId%>"/>
		
		var dynGridPanel;
		
		var popUpFormWindowRef;
		
		Ext.onReady(function(){
		
			dynGridPanel = new NodeGridPanel({
				title: 'Spreadsheet Rows',
				autoScroll: true,
				scroll: true,
				flex:1
			});
			
			Ext.create('Ext.Viewport', {
			    preventHeader: true,
			    border: false,
			    layout:'fit',
			    autoScroll: false,
		    	layoutConfig: {
		    	    align : 'stretch',
		    	    pack  : 'start'
		    	},
			    renderTo: 'nodeLayout',
			    items: [dynGridPanel]
			});
			
			dynGridPanel.store.loadStore();
		    
		});
	</script>

</head>
	<body>
		<br><br>
		<br><br>
		<center><div id="nodeLayout"></div></center>
	</body>
</html>