
//Imports
Ext.require(['Ext.data.*', 'Ext.grid.*', 'Ext.panel.*']);

/**
 * Data Store Class
 */
Ext.define('NodeStore', {
    extend: 'Ext.data.Store',
    constructor: function(config) {
        config = config || {};
        config.fields = readerFields;
        config.pageSize = 30,
        config.proxy = {
            type: 'ajax',
            url: jsonUrl,
            extraParams: {
            	method: "retrieveNodes",
    			nodeType: nodeTypeDyn,
    			searchString: 'rootNodeIdDyn@EQUAL@'+idDyn,
    			dir: 'asc'
			},
            reader: {
            	type: 'json',
            	root: 'nodes',
            	totalProperty: 'totalCount',
            	idProperty: 'id'
            }
        },
        config.listeners = {
			'load' : function(store, records, successful, operation, eOpts)  {
				if(successful && records.length == 0){
					Ext.Msg.alert('Load Message', 'No records available..', function(btn, text){if (btn == 'ok'){}});
				}else if(!successful){
					Ext.Msg.alert('Load Message', 'Business Service Error. Please contact the IT Helpdesk', function(btn, text){if (btn == 'ok'){}});
				}
			},
			'exception' : function(proxy, response, operation, eOpts ){
				Ext.Msg.alert('Load Message', 'Application Server Error. Please contact the IT Helpdesk', function(btn, text){if (btn == 'ok'){}});
			} 
		};
        // call the superclass's constructor
        NodeStore.superclass.constructor.call(this, config);
    },
    
    loadStore: function(){
    	this.load({
    		callback: function(records, operation, success) {
    			var listJSON = doJSON(operation.response.responseText);
    			var valid = listJSON.valid;
    			var warehouseSubmit = listJSON.warehouseSubmit;
    			
    			if("Y" == warehouseSubmit){
    				Ext.MessageBox.alert('Message', 'Records have already been submitted to the warehouse..', function(btn, text){
						var redirect = '../screens/uploadedFiles.jsp'; 
                        window.location = redirect;
					});
    			}
    			
    			if("Y" == valid){
    				submitToWarehouseButton.enable();
    			}else if("N" == valid){
    				submitToWarehouseButton.setDisabled(true);
    			}
    			idDynVersion = listJSON.root_node_version;
    	    }
    	});
    }
});

/**
 * Grid Panel Class
 */
Ext.define('NodeGridPanel', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.nodegridpanel',

    // override
    initComponent : function() {
    	
    	this.action = new Ext.ux.grid.RowActions
		(
			{
				header : 'Select',
				keepSelection : true,
				actions : [ {
					iconCls : 'icon-edit',
					qtip : 'Edit',
					tooltip : 'Modify',
					callback: function(grid, record, action, idx, col, e, target) {
						  popUpFormWindowRef = new Ext.Window({
					            width: 700,
					            height: 400,
					            closable: true,
					            title: 'Edit Record',
					            modal: 'true',
					            autoScroll: false,
					            constrain: true,
					            maximizable: true,
					            layout: 'fit',
					            items: [Ext.create('Ext.ux.SimpleIFrame', {
		 					    		 src: 'formPanelDyn.jsp?nodeType='+ nodeTypeDyn + '&nodeId='+ record.get('id'),
		 					    		 flex: 1,
		 					    		 autoScroll: false,
		 					             layout: {                        
		 								 	type: 'vbox',
		 								    align: 'stretch'
		 								 }})
									 ],
								listeners : {
						        	beforeclose : function() {
						        		grid.store.loadStore();
									}
								}	 
					        });
						 popUpFormWindowRef.show();
	    			}
				} ]
			}
		);
    	
    	// add the action column to the dynamically generated set of columns.
    	this.action.width = 90; 
		gridColumns.push(this.action);
    	
        // Pass in a column model definition
        this.columns = gridColumns;
        // Note the use of a storeId, this will register thisStore
        // with the StoreManager and allow us to retrieve it very easily.
        this.store = new NodeStore({
            storeId: 'nodeStore',
            url: jsonUrl
        });
        
        this.toolbar = new GridToolbar({});
        
        var defaultConfig = {
        		
        		dockedItems: [{
                    xtype: 'pagingtoolbar',
                    store: this.store,   // same store GridPanel is using
                    dock: 'bottom',
                    displayInfo: true
                }],
                tbar: this.toolbar,
                viewConfig: {
                	stripeRows: false,
                	trackOver: true,
                    getRowClass: function(record, rowIndex, rowParams, store){
                        return record.get("errors") ? "red-row" : "white-row";
                    },
                    listeners: {
                    	'itemmouseenter' : function(view, record, item, index, eventObject, options){  
                			if(record.get("errors")){
                				var errors = record.get("errors");
                				var details = errors.errorDetails;
                				tooltip.setToolTip(item, details);
                			}
                    	}
                    }                    	
                }
        }
        
        Ext.apply(this, defaultConfig);
        
        // finally call the superclasses implementation
        NodeGridPanel.superclass.initComponent.apply(this, arguments);
    }
});

var tooltip = Ext.create('Ext.tip.ToolTip', {
    trackMouse: true,
    anchor: 'bottom',
    width: '500',
    title: 'Errors: ',
    setToolTip: function(item, details){
    	var message = '<hr>';
		for(var i = 0; i<details.length; i++){
			detail = details[i];
			message = message + '<b>Field Name: </b>'+detail.fieldName+' <br><b>Description: </b>'+detail.description+ '<br><br>'; 
		}
    	this.setTarget(item);
    	this.update(message);
    }
});

/**
 * 
 */
var reValidateButton = Ext.create('Ext.Action', {
    text: 'Re-Validate All',
    icon: '../../../images/icons/accept.gif',
    handler: function(){
    	//Ext.Msg.alert('Re-Validate Pressed', 'OK', function(btn, text){
			   //if (btn == 'ok'){
    			   var waitBox = Ext.MessageBox.wait('Validating...', 'Re-Validate');
    	
				   Ext.Ajax.request({
						url : jsonUrl , 
						params : {method: "update", nodeType: nodeTypeDyn, id: idDyn, node_version: idDynVersion, subMethod: "reValidate"},
						method: 'GET',
						success: function ( result, request ) {
							waitBox.hide();
							var json = doJSON(result.responseText);
							if(json.success){
								Ext.MessageBox.alert('Success', 'Spreadsheet re-validated...', function(btn, text){
									dynGridPanel.store.loadStore();
								});
							}else{
								var responseText = result.responseText;
						    	var json = doJSON(responseText);
						    	if(json.errors && json.errors.busServiceExpMsg){
						    		Ext.MessageBox.alert('Message', json.errors.busServiceExpMsg, function(btn, text){
						    			dynGridPanel.store.loadStore();
						    		});
						    	}
							}
						},
						failure: function ( result, request) {
							waitBox.hide();
							Ext.Msg.alert('Message', 'Application Server Error. Please contact the IT Helpdesk', function(btn, text){if (btn == 'ok'){}}); 
						} 
					});
			   //}
		//});
    }
});

/**
 * 
 */
var submitToWarehouseButton = Ext.create('Ext.Action', {
    text: 'Submit To Warehouse',
    icon: '../../../images/icons/rss_go.png',
    disabled: true,
    handler: function(){
    	//Ext.Msg.alert('Submit To Warehouse Pressed', 'OK', function(btn, text){
			   //if (btn == 'ok'){
				   Ext.Ajax.request({
						url : jsonUrl , 
						params : {method: "update", nodeType: nodeTypeDyn, id: idDyn, node_version: idDynVersion, subMethod: "submitToWarehouse"},
						method: 'GET',
						success: function ( result, request ) {
							var json = doJSON(result.responseText);
							if(json.success){
								Ext.MessageBox.alert('Success', 'Submitted to warehouse...', function(btn, text){
									var redirect = '../screens/uploadedFiles.jsp'; 
			                        window.location = redirect;
								});
							}else{
								var responseText = result.responseText;
						    	var json = doJSON(responseText);
						    	if(json.errors && json.errors.busServiceExpMsg){
						    		Ext.MessageBox.alert('Message', json.errors.busServiceExpMsg, function(btn, text){
						    			dynGridPanel.store.loadStore();
						    		});
						    	}
							}
						},
						failure: function ( result, request) { 
							//This is called only if there is a server failure or timeouts
							Ext.Msg.alert('Message', 'Application Server Error. Please contact the IT Helpdesk', function(btn, text){if (btn == 'ok'){}}); 
						} 
					});
			   //}
		//});
    }
});

/**
 * Tool bar  
*/
GridToolbar = Ext.extend(Ext.toolbar.Toolbar, {
	constructor: function(config) {
		var config = {
			 items: [reValidateButton,submitToWarehouseButton]
		}
		GridToolbar.superclass.constructor.call(this, config);
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

