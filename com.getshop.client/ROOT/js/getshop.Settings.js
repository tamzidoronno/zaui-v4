getshop.Settings = {
    init: function() {
		var me = this;
        $(document).on('click', '.store_settings_button', getshop.Settings.showSettings);
        $(document).on('click', '.gss_backtopage', getshop.Settings.showPage);
        $(document).on('click', '[gss_goto_app]', function() { me.setApplicationId(this); });
        $(document).on('click', '[gss_method]', function() { me.gssMethodInvoke(this); });
    },
	
	gssMethodInvoke: function(field) {
		var method = $(field).attr('gss_method');
		var model = {};
		if ($(field).attr('gss_model')) {
			model = getshop.Model[$(field).attr('gss_model')];
		}
		
		this.post(model, method);
	},
	
	setApplicationId: function(button) {
		localStorage.setItem("currentApp", $(button).attr('gss_goto_app'));
		this.post();
	},
	
	reload: function() {
		this.post();
	},
	
	getCurrentAppId: function() {
		var appId = localStorage.getItem("currentApp");
		if (!appId) {
			// App settings id for the DashBoard application
			return "b81bfb16-8066-4bea-a3c6-c155fa7119f8";
		}
		
		return appId;
	},
	
	post: function(data, method) {
		if (!data) {
			data = {};
		}
		
		data['appid'] = this.getCurrentAppId();
		data['gss_method'] = method;
		
		$.ajax({
            type: "POST",
            url: "/settingsnav.php",
			dataType: "json",
            data: data,
            context: document.body,
            success: function(response) {
                $('.gss_settings_inner.apparea').html(response['data']);
				getshop.Models.addWatchers(response['data']);
            },
            error: function(failure) {
				alert("Failed");
			}
        });
	},

    showSettings: function() {
        $('#skeleton').fadeOut(300, function() {
            $('#backsidesettings').fadeIn(300);
        });
    },
            
    showPage: function() {
        $('#backsidesettings').fadeOut(300, function() {
            $('#skeleton').fadeIn(300);
        });
    },
            
}

getshop.Settings.init();