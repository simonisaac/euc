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
	<script type="text/javascript" src="../../../js/euc/screens/uploadedFiles.js"></script>

	<style type="text/css">
	    .complete .x-tree-node-anchor span {
	        text-decoration: line-through;
	        color: #777;
	    }
	</style>
	
	<!-- Put it together -->
	<script type="text/javascript">
	
		Ext.onReady(function(){
		
			var grid = new NodeGridPanel({
				layout: {                        
			        type: 'fit',
			        align: 'stretch'
			    },
				flex:1
			});
			
			Ext.create('Ext.panel.Panel', {
			    preventHeader: true,
			    border: false,
			    autoScroll: true,
			    width: 700,
			    layout: {                        
			        type: 'fit',
			        align: 'stretch'
			    },
			    renderTo: 'nodeLayout',
			    items: [grid]
			});
			
		    grid.store.load();
		    
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