<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>EUC Homepage</title>
		
		<link rel="stylesheet" type="text/css" href="../../../js/extjs/resources/css/ext-all.css" />
		
		<!-- Ext JS Imports -->
		<script type="text/javascript" src="../../../js/extjs/ext-all-debug.js"></script>
		
		<!-- EUC Imports -->
		<script type="text/javascript" src="../../../js/euc/config/config.js"></script>
		<script type="text/javascript" src="../../../js/euc/elements/iFrame.js"></script>
		
		<script type="text/javascript">
		
		var tabs;
				
	    Ext.onReady(function(){
	    	
	    	
	    	
	    	var eucIFramePanel = Ext.create('Ext.ux.SimpleIFrame', {
	    		src: 'homepage.jsp',
	    		 flex: 1,
	             layout: {                        
				 	type: 'vbox',
				    align: 'stretch'
				 },
				 lbar: [{
		                text: 'Upload File',
	                	handler:	function(btn, pressed){
	                		eucIFramePanel.setSrc("nodeTypeDefs.jsp");
				        }
		            },'-',{
		                text: 'Edit Existing Uploaded Files',
	                	handler:	function(btn, pressed){
	                		eucIFramePanel.setSrc("uploadedFiles.jsp");
				        }
		            },'-',{
		                text: 'Logout',
	                	handler:	function(btn, pressed){
       					  	window.location = "j_spring_security_logout";
				        }
		            }
		        ]
	    	});
	    	
	    	
	    	
	        var viewport = new Ext.container.Viewport({
	            layout: 'border',
	            items: [{
	            	region: 'north',
	                id: 'north-panel', 
	                split: false,
	                preventHeader: true,
	                height: 80,
	                collapsible: false,
	                margins: '0 0 0 5',
	                items: [{
	                    contentEl: 'north',
	                    preventHeader: true,
	                    border: false                    
	                }]
	            }, {
	                region: 'center',
	                id: 'center-panel', 
	                title: 'Work Area',
	                preventHeader: true,
	                split: true,
	                flex: 1,
	                layout: {                        
				        type: 'vbox',
				        align: 'stretch'
				    },
	                collapsible: true,
	                margins: '0 0 2 0',
	                items: [eucIFramePanel]
	            }
	            ]
	        });
	        
	    });
	    </script>
				
	</head>
	
	
	<body alink="000000" vlink="000000" link="000000">
	
	<div id="north">
		<br><br>
		<center><h1>End User Computing Interface</h1></center> 
		<br>			        
    </div>
       	
</body>
	
</html>