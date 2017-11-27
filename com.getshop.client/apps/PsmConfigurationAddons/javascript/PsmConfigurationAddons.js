app.PsmConfigurationAddons = {
    init : function() {
        $(document).on('click', '.PsmConfigurationAddons .doSaveSettings', app.PsmConfigurationAddons.saveSettings);
    },
    saveSettings : function() {
        var data = {};
        var btn = $(this);
        $('.PsmConfigurationAddons .settingsForProduct').each(function() {
            var form = $(this);
            var dataToSave = thundashop.framework.createGsArgs(form);
            data[form.attr('productid')] = dataToSave;
        });
        var toPost = {};
        toPost['products'] = data;
        var event = thundashop.Ajax.createEvent('','saveProductConfig',btn,toPost);
        thundashop.Ajax.post(event);
    }
};

app.PsmConfigurationAddons.init();