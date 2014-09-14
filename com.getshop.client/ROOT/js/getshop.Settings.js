getshop.Settings = {
    init: function() {
        $(document).on('click', '.store_settings_button', getshop.Settings.showSettings);
        $(document).on('click', '.gs_backtopage', getshop.Settings.showPage);
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