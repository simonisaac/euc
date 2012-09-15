/**
 * 
 * @class NodeTreePanel
 * @extends Ext.tree.TreePanel
 * 
 * 
 */

ActivityFormPanelDyn = Ext.extend(Ext.form.Panel, {
	
    constructor: function(config) {

		var defaultConfig = {
			url:jsonUrl,
	        frame:true,
	        title: 'Node Form Panel',
	        bodyStyle:'padding:5px 5px 0',
	        items: formItemsDyn,
	        
	        fieldDefaults: {
	            labelAlign: 'left',
	            labelWidth: 150,
	            anchor: '95%'
	        },
	        listeners : {
	        	beforerender : function() {
					this.load({
			    		url:this.url,
			    		waitMsg:'Loading...',
			    		params:{method:'load', nodeType:nodeTypeDyn, id:nodeIdDyn},
			    		success: this.onSuccessLoad,
			    		failure: this.onFailureLoad
			    	});
				}
			},
	        buttons: [
		        {
		        	text:'Submit'
	                ,formBind:true
	                ,scope:this
	                ,handler: function(){
						this.submit();
					}
		        }
		    ]
		}; // eof config
	
		
		Ext.QuickTips.init();
		Ext.QuickTips.enable();
		
		// invalid markers to sides
	    Ext.form.Field.prototype.msgTarget = 'side';
	
		// Create a new config object containing our computed properties
		// *plus* whatever was in the config parameter.
        config = Ext.apply(defaultConfig, config);

        ActivityFormPanelDyn.superclass.constructor.call(this, config);
    },

    /**
     * Submits the form. Called after Submit buttons is clicked
     * @private
     */
    submit:function() {
        this.getForm().submit({
             url:this.url
            ,scope:this
            ,success:this.onSuccessSubmit
            ,failure:this.onFailureSubmit
            ,
			params : {
				method 		: 'update',
				nodeType 	: nodeTypeDyn,
				rootNodeId 	: rootNodeId,
				node_version 	: nodeIdDynVersion,
				root_node_version : rootNodeIdVersion
			},
			waitMsg : 'Saving...'
        });
    }, // eo function submit
    
    /**
     * Success handler
     * @param {Ext.form.BasicForm} form
     * @param {Ext.form.Action} action
     * @private
     */
    onSuccessLoad:function(form, action) {
    	var responseText = action.response.responseText;
    	var json = doJSON(responseText);

    	if(json.data && json.data.node_version && json.data.parentNodeId){
    		nodeIdDynVersion = json.data.node_version;
    		rootNodeId = json.data.parentNodeId;
    		rootNodeIdVersion = json.data.root_node_version;
    	}
    	
    	if(json.errors && json.errors.busServiceExpMsg){
    		Ext.MessageBox.alert('Message', json.errors.busServiceExpMsg, function(btn, text){});
    	}else if(json.errors){
    		form.markInvalid( json.errors );
    	}
    	
    } // eo function onSuccess
    
    /**
     * Success handler
     * @param {Ext.form.BasicForm} form
     * @param {Ext.form.Action} action
     * @private
     */
    ,onSuccessSubmit:function(form, action) {
    	
    	parent.popUpFormWindowRef.close();
    	
    	/*Ext.MessageBox.alert('Message', 'Form submitted successfully.', function(btn, text){
    		if (btn == 'ok'){
    			parent.popUpFormWindowRef.close();
    		}
		});*/
    } // 

    /**
     * Failure handler
     * @param {Ext.form.BasicForm} form
     * @param {Ext.form.Action} action
     * @private
     */
    ,onFailureSubmit:function(form, action) {
    	var responseText = action.response.responseText;
    	var json = doJSON(responseText);
    	if(json.errors && json.errors.busServiceExpMsg){
    		Ext.MessageBox.alert('Message', json.errors.busServiceExpMsg, function(btn, text){
    			parent.popUpFormWindowRef.close();
    		});
    	}
    }
    
    /**
     * Failure handler
     * @param {Ext.form.BasicForm} form
     * @param {Ext.form.Action} action
     * @private
     */
    ,onFailureLoad:function(form, action) {
		/*Ext.MessageBox.alert('Message', 'Business Service Error. Please contact the IT Helpdesk.', function(btn, text){
			parent.popUpFormWindowRef.close();
		});*/
    }
    
});

function doJSON(stringData) {
	try {
		var jsonData = Ext.JSON.decode(stringData);
		return jsonData;
	}
	catch (err) {
		Ext.MessageBox.alert('ERROR', 'Could not decode ' + stringData);
	}
}
