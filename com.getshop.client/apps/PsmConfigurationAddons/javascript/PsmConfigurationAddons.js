app.PsmConfigurationAddons = {
    init : function() {
        $(document).on('click', '.PsmConfigurationAddons', app.PsmConfigurationAddons.saveSettings);
    },
    saveSettings : function() {
        var data = {};
        var btn = $(this);
        $('.PsmConfigurationAddons .doSaveSettings').each(function() {
            var form = $(this);
            var dataToSave = thundashop.framework.createGsArgs(form);
            data[form.attr('productid')] = dataToSave;
        });
        console.log(data);
        var event = thundashop.Ajax.createEvent('','saveProductConfig',btn,data);
        thundashop.Ajax.post(event);
    }
};

app.PsmConfigurationAddons.init();