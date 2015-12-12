app.PmsPricing = {
    init: function () {
        $(document).on('click', '.PmsPricing .updatePricingTable', app.PmsPricing.updatePricingTable);
    },
    updatePricingTable : function() {
        $('.pricecheckbox').each(function() {
            if($(this).is(':checked')) {
                var item = $(this).closest('tr').attr('itemid');
                var price = $(this).closest('tr').find('.priceinput').val();
                var weekday = $(this).attr('weekday');
                $('tr[itemtype="'+item+'"]').find('.priceinput[weekday="'+weekday+'"]').val(price);
            }
        });
    },
    showSettings : function() {
        var event = thundashop.Ajax.createEvent('','showSettings',$(this), {});
        thundashop.common.showInformationBox(event,'Pms form settings');
    },
    loadSettings : function(element, application) {
         var config = {
            draggable: true,
            app : true,
            application: application,
            title: "Settings",
            items: [
                {
                    icontype: "awesome",
                    icon: "fa-edit",
                    iconsize : "30",
                    title: __f("Add / Remove products to this list"),
                    click: app.PmsPricing.showSettings
                }
            ]
        };

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
};
app.PmsPricing.init();