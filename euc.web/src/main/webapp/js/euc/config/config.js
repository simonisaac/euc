
var jsonUrl = '/euc.web/json'
var homepageUrl = 'container.jsp'
var loginUrl = 'j_spring_security_check'	
	
	
/**
 * DO NOT REMOVE THIS FUNCTION
 */	
Ext.apply(Ext,{
 isObject: function(value) {
    	var toString = Object.prototype.toString;
		if(toString.call(null) === '[object Object]'){
			return value !== null && value !== undefined && toString.call(value) === '[object Object]' && value.ownerDocument === undefined;
		}else{
			return toString.call(value) === '[object Object]';
		}
    }
});
