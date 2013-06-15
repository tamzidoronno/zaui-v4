thundashop.Administration = {
    enableSMS: function(password, toggle) {
        var event = thundashop.Ajax.createEvent("Administration", "enableSMS", false, { "type": "GetShopAdministration", "password" : password, "toggle" : toggle });
        thundashop.Ajax.post(event);
    },
    enableExtendedMode : function(password, toggle) {
        var event = thundashop.Ajax.createEvent("Administration", "enableExtendedMode", false, { "type": "GetShopAdministration", "password" : password, "toggle" : toggle });
        thundashop.Ajax.post(event);
    },
        
    attachUserToPartner : function(password, userId, partner) {
        var event = thundashop.Ajax.createEvent("Administration", "connectUserToPartner", false, { "userId": userId, "password" : password, "partner" : partner });
        thundashop.Ajax.post(event);
    },
    setVIS : function(password, toggle) {
        var event = thundashop.Ajax.createEvent("Administration", "setVIS", false, { "password": password, "toggle" : toggle });
        thundashop.Ajax.post(event);
    }
}
