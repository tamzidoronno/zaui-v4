app.ApplicationSelector = {
    init: function() {
        $(document).on('click', '.gss_activateModule', app.ApplicationSelector.activateModele)
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
    }
}

app.ApplicationSelector.init();