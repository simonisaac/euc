
Ext.require([
    'Ext.form.field.File',
    'Ext.form.Panel'
]);

UploadFormPanel = Ext.extend(Ext.form.Panel, {
	
	initComponent: function(){

    	var defaultConfig = {
   			width: 450,
   	        frame: true,
   	        title: 'File Upload Form',
   	        bodyPadding: '10 10 0',
   	        standardSubmit: true,


	   	    defaults: {
	   	    	anchor: '100%',
	            allowBlank: false,
	            msgTarget: 'side',
	            labelWidth: 75
	        },

	        items: [{
	            xtype: 'textfield',
	            fieldLabel: 'Description',
	            name: 'description'	
	        },{
	            xtype: 'filefield',
	            id: 'form-file',
	            emptyText: 'Select an excel file',
	            fieldLabel: 'File',
	            name: 'fileToUpload',
	            buttonText: 'Browse',
	            buttonConfig: {
	                iconCls: 'upload-icon'
	            }
	        }],
	
	        buttons: [{
	            text: 'Upload',
            	formBind: true, //only enabled once the form is valid
                disabled: true,
                handler: function() {
                    var form = this.up('form').getForm();
                    if (form.isValid()) {
                        form.submit({
                        	params : {
								nodeType : nodeTypeDyn,
								excelInstId : sheetIdDyn,
								payload : 'dummy',
								method : 'create',
								sessionToken : 'sessionId:11123'
							},
                        	waitTitle:'Connecting', 
                            waitMsg:'Sending data...',
                            success: function(form, action) {
                            	var nodeId = action.result.node.id;
                            	var nodeType = action.result.node.type;
                            	var redirect = '../elements/gridPanelDyn.jsp?nodeType='+nodeType+'&nodeId='+nodeId; 
	   		                    window.location = redirect;
                            },
                            failure: function(form, action) {
                            	if(action.result.errors && action.result.errors.busServiceExpMsg){
                            		Ext.MessageBox.alert('Message', action.result.errors.busServiceExpMsg, function(btn, text){
                            			form.reset();
                            		});
                            	}
                            }
                        });
                    }
                }	
	        },{
	        	text: 'Reset',
	            handler: function() {
	            	this.up('form').getForm().reset();
	            }
	        }]
	    } //End default
    
    	Ext.apply(this, defaultConfig);
    
    	UploadFormPanel.superclass.initComponent.apply(this, arguments);
	} 
});