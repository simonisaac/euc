
//Imports
Ext.require(['Ext.data.*', 'Ext.grid.*', 'Ext.panel.*']);

/**
 * Data Store Class
 */
Ext.define('NodeStore', {
    extend: 'Ext.data.Store',
    constructor: function(config) {
        config = config || {},
        config.fields = [
                         {name: 'id', mapping: 'id'},
                         {name: 'type', mapping: 'type'},
                         {name: 'valid', mapping: 'valid'},
                         {name: 'dateCreated', mapping: 'dateCreated'},
                         {name: 'description', mapping: 'description'},
                         {name: 'childNodeType', mapping: 'childNodeType'}
                         ],
        config.pageSize = 10,
        config.proxy = {
            type: 'ajax',
            url: jsonUrl,
            extraParams: {
            	method: "retrieveNodes",
    			nodeType: 'uploadedNodes'
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
					Ext.Msg.alert('Load Message', 'There are no uploaded files..', function(btn, text){if (btn == 'ok'){}});
				}else if(!successful){
					Ext.Msg.alert('Load Message', 'Business Service Error. Please contact the IT Helpdesk', function(btn, text){if (btn == 'ok'){}});
				}
			},
			'exception' : function(proxy, response, operation, eOpts ){
				Ext.Msg.alert('Load Message', 'Application Server Error. Please contact the IT Helpdesk', function(btn, text){if (btn == 'ok'){}});
			} 
		}
        // call the superclass's constructor
        NodeStore.superclass.constructor.call(this, config);
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
				header : 'Select Type',
				keepSelection : true,
				actions : [ {
					iconCls : 'icon-edit',
					qtip : 'Select',
					tooltip : 'Select',
					callback: function(grid, record, action, idx, col, e, target) { 
	    				//Ext.Msg.alert('Row Action', record.get('id')+' - '+record.get('childNodeType'), function(btn, text){
         				   //if (btn == 'ok'){
         					  var redirect = '../elements/gridPanelDyn.jsp?nodeType='+record.get('childNodeType')+'&nodeId='+record.get('id'); 
         					  window.location = redirect;
                           //}
	    				//});
	    			}
				} ]
			}
		);
    	
    	var gridColumns = [
                           {id: 'id', header: 'ID', dataIndex: 'id', width: 50},
                           {id: 'type', header: 'Excel Type', dataIndex: 'type', width: 100},
                           {id: 'valid', header: 'Vaild', dataIndex: 'valid', width: 50},
                           {id: 'dateCreated', header: 'Date Created', dataIndex: 'dateCreated', width: 100},
                           {id: 'description', header: 'Description', dataIndex: 'description', flex : 1}
                           ]; 
    	
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
        
        var defaultConfig = {
        		
        		dockedItems: [{
                    xtype: 'pagingtoolbar',
                    store: this.store,   // same store GridPanel is using
                    dock: 'bottom',
                    displayInfo: true
                }],
                title: 'Select File To View'
        }
        
        Ext.apply(this, defaultConfig);
        
        // finally call the superclasses implementation
        NodeGridPanel.superclass.initComponent.apply(this, arguments);
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

