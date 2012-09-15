
//Imports
Ext.require(['Ext.data.*', 'Ext.grid.*', 'Ext.panel.*']);

/**
 * Data Store Class
 */
Ext.define('NodeStore', {
    extend: 'Ext.data.Store',
    constructor: function(config){
        config = config || {};
        config.fields = [
                         {name: 'id', mapping: 'id'},
                         {name: 'description', mapping: 'description'},
                         {name: 'type', mapping: 'type'},
                         {name: 'childNodeType', mapping: 'childNodeType'}
                         ];
        config.pageSize = 10;
        config.proxy = {
            type: 'ajax',
            url: jsonUrl,
            extraParams: {
    			nodeType: "excelInstructions",
    			method: "retrieveNodes",
    			searchString: 'nodeTypeDef@EQUAL@'+excelNodeTypeDyn,
    			dir: 'asc'
			},
            reader: {
            	type: 'json',
            	root: 'nodes',
            	totalProperty: 'totalCount',
            	idProperty: 'id'
            }
        };
        config.listeners = {
			'load' : function(store, records, successful, operation, eOpts)  {
				if(successful && records.length == 0){
					Ext.Msg.alert('Load Message', 'There are no Excel Instructions defined for the Definition ['+excelNodeTypeDyn+']', function(btn, text){if (btn == 'ok'){window.history.go(-1);}});
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
	    				//Ext.Msg.alert('Row Action', record.get('id'), function(btn, text){
         				   //if (btn == 'ok'){
         					  var redirect = 'upload.jsp?sheetId=' + record.get('id') + "&nodeType=" +record.get('type'); 
         					  window.location = redirect;
                           //}
	    				//})
	    			}
				} ]
			}
		);
    	
    	var gridColumns = [
                           {id: 'id', header: 'Sheet Id', dataIndex: 'id', width: 100},
                           {id: 'description', header: 'Description', dataIndex: 'description', flex: 1},
                           {id: 'type', header: 'Root Node Definition', dataIndex: 'type', width: 150},
                           {id: 'childNodeType', header: 'Node Defintion', dataIndex: 'childNodeType', width: 150}
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
                title: 'Select Excel Insturction : '+excelNodeTypeDyn
        }
        
        Ext.apply(this, defaultConfig);
        
        // finally call the superclasses implementation
        NodeGridPanel.superclass.initComponent.apply(this, arguments);
    }
});


