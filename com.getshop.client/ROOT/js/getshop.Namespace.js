thundashop = {};

thundashop.Namespace = {
    Register : function(namespace) {
        namespaceori = namespace;
        namespace = namespace.split('.');

        if(!window[namespace[0]]) window[namespace[0]] = {};

        var strFullNamespace = namespace[0];
        for(var i = 1; i < namespace.length; i++)
        {
            strFullNamespace += "." + namespace[i];
            eval("if(!window." + strFullNamespace + ")window." + strFullNamespace + "={};");
        }
    }
};

if (typeof(com) == "undefined") com = {} 
if (typeof(com.getshop) == "undefined") com.getshop = {}
if (typeof(com.getshop.app) == "undefined") com.getshop.app = {}
