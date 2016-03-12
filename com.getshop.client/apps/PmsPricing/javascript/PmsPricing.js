app.PmsPricing = {
    init: function () {
        $(document).on('click', '.PmsPricing .updatePricingTable', app.PmsPricing.updatePricingTable);
        $(document).on('click', '.PmsPricing .setnewprices', app.PmsPricing.setnewprices);
        $(document).on('keyup', '.PmsPricing .dateinput', app.PmsPricing.updateDate);
        $(document).on('change', '.PmsPricing .dateinput', app.PmsPricing.updateDate);
    },
    updateDate : function() {
        var date = $(this).val();
        $(this).closest('td').find('.priceinput').attr('date',date);
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
    setnewprices : function() {
        if($(this).find('.fa-spin').length > 0) {
            return;
        }
        
        $(this).html('<i class="fa fa-spin fa-spinner"></i>');
        
        var data = {};
        var prices = {};
        $('.pricetableview tr').each(function() {
            var itemid = $(this).attr('itemtype');
            var itemPriceRow = {};
            $(this).find('.priceinput').each(function() {
                var price = $(this).val();
                if(price) {
                    var date = $(this).attr('date');
                    itemPriceRow[date] = price;
                }
            });
            prices[itemid] = itemPriceRow;
        });
        
        data['prices'] = prices;
        data['prices_ex_taxes'] = $('.pricesextaxes').is(':checked');
        data['privatePeopleDoNotPayTaxes'] = $('.privatePeopleDoNotPayTaxes').is(':checked');
        data['pricetype'] = $('.pricetypeselection').val();
        
        var event = thundashop.Ajax.createEvent('','setNewPrices',$(this), data);
        thundashop.Ajax.post(event);
    },
    showSettings : function() {
        var event = thundashop.Ajax.createEvent('','showSettings',$(this), {});
        thundashop.common.showInformationBoxNew(event,'Pms form settings');
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