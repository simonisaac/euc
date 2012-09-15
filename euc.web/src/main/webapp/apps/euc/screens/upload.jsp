<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>EUC File Upload</title>
		
		<link rel="stylesheet" type="text/css" href="../../../js/extjs/resources/css/ext-all.css" />
		
		<!-- Ext JS Imports -->
		<script type="text/javascript" src="../../../js/extjs/ext-all-debug.js"></script>
		
		<!-- EUC Imports -->
		<script type="text/javascript" src="../../../js/euc/config/config.js"></script>
		<script type="text/javascript" src="../../../js/euc/screens/upload.js"></script>
	
		<script type="text/javascript">
		
		<%
			String nodeType = request.getParameter("nodeType");
			String sheetId = request.getParameter("sheetId");
			if (nodeType == null || sheetId == null) {
		        out.println("Parameter(s) not supplied");
		    } else {
		        //
		    }
		%>
		
		var nodeTypeDyn = "<%=nodeType%>";
		var sheetIdDyn = "<%=sheetId%>";
	
		Ext.onReady(function(){
		    
			 Ext.QuickTips.init();

			 // turn on validation errors beside the field globally
			 Ext.form.Field.prototype.msgTarget = 'side';
			 
			 var uploadFormPanel = new UploadFormPanel({
				 url : jsonUrl
			 });
			 uploadFormPanel.render("layout");
			 
		});
	</script>
	
	</head>
	
	<body>
	
		<br><br>
		<br><br>
		
		<table align="center">
			<tr><td>
				<div id="layout"></div>
			</td></tr>
		</table>
		
		
	
	</body>
</html>