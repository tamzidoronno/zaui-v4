thundashop.Administration = {
    enableSMS: function(password, toggle) {
        var event = thundashop.Ajax.createEvent("Administration", "enableSMS", false, { "type": "GetShopAdministration", "password" : password, "toggle" : toggle });
        thundashop.Ajax.post(event);
    },
    toggleDeactivation: function(password) {
        var event = thundashop.Ajax.createEvent("Administration", "toggleLockDown", false, { "type": "GetShopAdministration", "password" : password });
        thundashop.Ajax.post(event);
    },
    enableExtendedMode : function(password, toggle) {
        var event = thundashop.Ajax.createEvent("Administration", "enableExtendedMode", false, { "type": "GetShopAdministration", "password" : password, "toggle" : toggle });
        thundashop.Ajax.post(event);
    },
    updateUserCounter : function(password, counter) {
        var event = thundashop.Ajax.createEvent("Administration", "updateCounter", false, { "password" : password, "counter" : counter });
        thundashop.Ajax.post(event);
    },
    attachUserToPartner : function(password, userId, partner) {
        var event = thundashop.Ajax.createEvent("Administration", "connectUserToPartner", false, { "userId": userId, "password" : password, "partner" : partner });
        thundashop.Ajax.post(event);
    },
    setVIS : function(password, toggle) {
        var event = thundashop.Ajax.createEvent("Administration", "setVIS", false, { "password": password, "toggle" : toggle });
        thundashop.Ajax.post(event); 
   },
    setBigStockCreditAccount: function(password, credit) {
        var event = thundashop.Ajax.createEvent("Administration", "setBigStockCreditAccount", false, { "password": password, "credit" : credit });
        thundashop.Ajax.post(event);
    },
    setThemeApplication: function(appId) {
        var event = thundashop.Ajax.createEvent('','addApplicationDirect',$(this),{
            "appId" : appId
        });
        thundashop.Ajax.post(event);
    },
    toggleAccessToModule: function(moduleId, password) {
        var event = thundashop.Ajax.createEvent('','toggleAccessToModule',$(this),{
            "moduleId" : moduleId,
            "password" : password
        });
        
        thundashop.Ajax.post(event);
    },
    upgradeUserToGetShopAdmin: function(password) {
        var event = thundashop.Ajax.createEvent('','upgradeToGetShopAdmin',$(this),{
            "password" : password
        });
        thundashop.Ajax.post(event);
    },
    toggleShowBookingErrors: function(password) {
        var event = thundashop.Ajax.createEvent('','toggleShowBookingErrors',$(this),{
            "password" : password
        });
        thundashop.Ajax.post(event);
    },
    unlockOrder: function(password, orderid) {
        var event = thundashop.Ajax.createEvent('','unlockOrder',$(this),{
            "password" : password,
            "orderid" : orderid
        });
        thundashop.Ajax.post(event);
    },
    
    toggleSideBar: function(sidebarname) {
        var postEvent = thundashop.Ajax.createEvent(null, "toggleSideBar", this, { name : sidebarname });
        thundashop.Ajax.post(postEvent);
    },
    
    toggleSideBarGetShopModules: function() {
        this.toggleSideBar('leftsidebar');
    },
    
    toggleActivationOfRemoteEditing: function() {
        thundashop.Ajax.simplePost(null, "toggleRemoteEditing", {});
    }
}
