app.ApplicationSelector = {
    init: function() {
        $(document).on('click', '.gss_activateApplication', app.ApplicationSelector.activateApplication)
        $(document).on('click', '.gss_activateModule', app.ApplicationSelector.activateModele)
        $(document).on('click', '.gss_activate_theme', app.ApplicationSelector.activateTheme)
    },
    
    activateModele: function() {
        var data = {
            gss_method : 'activateModule',
            value : $(this).attr('moduleId')
        };
        
        getshop.Settings.doPost(data, null, function(response, field, data) {
            getshop.Settings.successfully(response, field, data);
            getshop.Settings.reloadCss();
        });
    },
    
    activateTheme: function() {
        var data = {
            gss_method : 'activateTheme',
            value : $(this).attr('themeId')
        };
        
        getshop.Settings.doPost(data, null, function(response, field, data) {
            getshop.Settings.successfully(response, field, data);
            getshop.Settings.reloadCss();
        });
    },
    
    addedApplication: function() {
        getshop.Settings.reloadCss();
    }
}

app.ApplicationSelector.init();