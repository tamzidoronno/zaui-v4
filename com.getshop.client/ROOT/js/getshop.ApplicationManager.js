thundashop.ApplicationManager = {
    init: function() {
        PubSub.subscribe('setting_switch_toggled', this.onMessage, this);
		$(document).on('click', '.gs_show_application_add_list', thundashop.ApplicationManager.showApplicationAddList)
		$(document).on('click', '.gs_add_applicationlist .gs_add_app_entry', thundashop.ApplicationManager.addApplication)
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
    },
	
	showApplicationAddList: function() {
		var menu = $(this).closest('.gscell').find('.gs_add_applicationlist');
		if (menu.is(':visible')) {
			menu.fadeOut(300);
		} else {
			menu.slideDown(300);
		}
	},
	
	addApplication: function() {
		var data = {
			cellId : $(this).closest('.gscell').attr('cellid'),
			appId : $(this).attr('appId')
		}
		
		var event = thundashop.Ajax.createEvent(null, "addApplicationToCell", this, data);
		thundashop.Ajax.post(event);
	}
}

thundashop.ApplicationManager.init();
