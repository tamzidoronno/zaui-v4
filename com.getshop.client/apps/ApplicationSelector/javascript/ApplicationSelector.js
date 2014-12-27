app.ApplicationSelector = {
    init: function() {
        $(document).on('click', '.gss_activateModule', app.ApplicationSelector.activateModele)
        $(document).on('click', '.gss_activate_theme', app.ApplicationSelector.activateTheme)
        $(document).on('click', '.gss_settings_application_modulemenulist_entry', app.ApplicationSelector.showModule)
    },
    
    showLastModule: function() {
        var moduleId = localStorage.getItem("gss_application_module_slected");
        if (!moduleId) {
            $('.gss_settings_application_modulemenulist_entry').first().attr('moduleid');
        }
        app.ApplicationSelector.activateModule(moduleId);
    },
    
    showModule: function() {
        var moduleId = $(this).attr('moduleid');
        app.ApplicationSelector.activateModule(moduleId);
    },
    
    activateModule: function(moduleId) {
        $('.innerappview[moduleid]').hide();
        $('.innerappview[moduleid="'+moduleId+'"]').show();
        $('.gss_module_selected').removeClass('gss_module_selected');
        $('.gss_settings_application_modulemenulist_entry[moduleid="'+moduleId+'"]').addClass('gss_module_selected');
        localStorage.setItem("gss_application_module_slected", moduleId);
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