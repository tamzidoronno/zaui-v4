thundashop.ApplicationManager = {
    init: function() {
        PubSub.subscribe('setting_switch_toggled', this.onMessage, this);
    },
    
    onMessage: function(msg, data) {
        if (data.id == "toggle_application_sticky") {
            this.toggleApplicationStick(data)
        }
    },
    
    toggleApplicationStick : function(data) {
        if (data.state == "off") {
            data.config.sticky = "0";
        } else {
            data.config.sticky = "1";
        }
        var event = thundashop.Ajax.createEvent('ApplicationManager', 'setSticky', data.entry, data.config);
        thundashop.Ajax.post(event);
    }
}

thundashop.ApplicationManager.init();
