app.PsmConfigurationAddons = {
    init : function() {
        $(document).on('click', '.PsmConfigurationAddons .doSaveSettings', app.PsmConfigurationAddons.saveSettings);
        $(document).on('click', '.PsmConfigurationAddons .onlyavailableforitem', app.PsmConfigurationAddons.selectOnlyForItem);
        $(document).on('click', '.PsmConfigurationAddons .readdaddon', app.PsmConfigurationAddons.readdaddon);
    },
    readdaddon : function() {
        var event = thundashop.Ajax.createEvent('','readAddons',$(this), {
            "productId" : $(this).closest('.settingsForProduct').attr('productid')
        });
        thundashop.Ajax.postWithCallBack(event, function() {
            thundashop.common.Alert('Updated','Update completed');
        });
    },
    selectOnlyForItem : function() {
        if($(this).hasClass('selectedItem')) {
            $(this).removeClass('selectedItem');
        } else {
            $(this).addClass('selectedItem');
        }
    },
    saveSettings : function() {
        var data = {};
        var btn = $(this);
        $('.PsmConfigurationAddons .settingsForProduct').each(function() {
            var form = $(this);
            var onlyForItems = [];
            form.find('.onlyavailableforitem').each(function() {
                if($(this).hasClass('selectedItem')) {
                    onlyForItems.push($(this).attr('itemid'));
                }
            });
            var dataToSave = thundashop.framework.createGsArgs(form);
            dataToSave.onlyForItems = onlyForItems;
            console.log(dataToSave);
            data[form.attr('productid')] = dataToSave;
        });
        var toPost = {};
        toPost['products'] = data;
        var event = thundashop.Ajax.createEvent('','saveProductConfig',btn,toPost);
        thundashop.Ajax.post(event);
    }
};

app.PsmConfigurationAddons.init();