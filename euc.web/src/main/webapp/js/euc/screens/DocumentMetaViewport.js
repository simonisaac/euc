

DocumentMetaViewport = Ext.extend(Ext.Viewport, {

    constructor: function(inConfig) {
	
    	this.grid = new DocumentMetaGridPanel({
					title: 'Document Metadata Management',
					flex:1
		});
    	//grid.store.load();
    	
		var defaultConfig = 
		{
	    	layout:'vbox',
	    	layoutConfig: {
	    	    align : 'stretch',
	    	    pack  : 'start'
	    	},
	        width:650,
	        height:400,
	        items: [this.grid]
        }; // eof default config
		
		var target = new Object();
		Ext.apply(target, inConfig);
		Ext.apply(target, defaultConfig);
		DocumentMetaViewport.superclass.constructor.call(this, target);
	}

});