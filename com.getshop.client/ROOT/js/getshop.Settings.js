getshop.Settings = {
    init: function() {
		var me = this;
        $(document).on('click', '.store_settings_button', getshop.Settings.showSettings);
        $(document).on('click', '.gss_backtopage', getshop.Settings.showPage);
        $(document).on('click', '[gss_goto_app]', function() { me.setApplicationId(this); });
    },
	
	setApplicationId: function(button) {
		this.appId = $(button).attr('gss_goto_app');
		this.post();
	},
	
	post: function(data) {
		if (!data) {
			data = {};
		}
		
		data['appid'] = this.appId;
		
		$.ajax({
            type: "POST",
            url: "/settingsnav.php",
			dataType: "json",
            data: data,
            context: document.body,
            success: function(response) {
                $('.gss_settings_inner.apparea').html(response['data']);
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