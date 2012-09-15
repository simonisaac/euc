

LoginFormPanel = Ext.extend(Ext.form.Panel, {
	
	initComponent: function(){

    	var defaultConfig = {
	        labelWidth: 75, // label settings here cascade unless overridden
	        frame:true,
	        title: 'Login ',
	        bodyStyle:'padding:5px 5px 0',
	        width: 350,
	        layout: 'anchor',
	        defaults: {
	        	width: 230,
	            anchor: '100%'
	        },
	        defaultType: 'textfield',
	        
	        items: [{
	                fieldLabel: 'User Name',
	                name: 'j_username',
	                allowBlank:false
	            },{
	                fieldLabel: 'Password',
	                name: 'j_password',
	                inputType: 'password',
	                allowBlank:false
	            }
	        ],
	
	        buttons: [{
	            text: 'Submit',
            	formBind: true, //only enabled once the form is valid
                disabled: true,
                handler: function() {
                    var form = this.up('form').getForm();
                    if (form.isValid()) {
                        form.submit({
                        	waitTitle:'Connecting', 
                            waitMsg:'Sending data...'
                        });
                    }
                }	
	        },{
	        	text: 'Reset',
	            handler: function() {
	            	this.up('form').getForm().reset();
	            }
	        }],
	        listeners : {
	    			afterrender : function(form, eOpts ){
	    				if(loginFailed && loginFailed == 'true'){
	    					Ext.Msg.alert('Login Failed', 'Please check username/password');
	    				}
	    			}
	    		}
	    } //End default
    	
    	// Config object has already been applied to 'this' so properties can 
        // be overriden here or new properties (e.g. items, tools, buttons) 
        // can be added, eg:
    	Ext.apply(this, defaultConfig);
    	
    	// Before parent code
    	 
        // Call parent (required)
    	LoginFormPanel.superclass.initComponent.apply(this, arguments);
 
        // After parent code
        // e.g. install event handlers on rendered component
	},
	
	// Override other inherited methods 
	onRender: function(){
	
	    // Before parent code
	
	    // Call parent (required)
		LoginFormPanel.superclass.onRender.apply(this, arguments);
	
	    // After parent code
	
	}
});
  