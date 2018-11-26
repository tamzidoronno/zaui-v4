app.GetShopPriceModel = {
    init : function() {
        $(document).on('keyup','.GetShopPriceModel .inputvalue',app.GetShopPriceModel.calculate);
        $(document).on('click','.GetShopPriceModel .inputvalue',app.GetShopPriceModel.calculate);
    },
    
    calculate : function() {
        var data = {
            'rooms' : parseInt($('.rooms').val()),
            'locks' : parseInt($('.locks').val()),
            'entrancelocks' : $('.entrancelocks').val(),
            'selfcheckinindoor' : $('.selfcheckinindoor').val(),
            'selfcheckinoutdoor' : $('.selfcheckinoutdoor').val(),
            'restaurantEntryPoints' : $('.restaurantEntryPoints').val(),
            'pgas' : $('.pgas').val(),
            'salespoints' : $('.salespoints').val(),
            'customwebsite' : $('.customwebsite').is(':checked'),
            'integrationtoaccounting' : $('.integrationtoaccounting').is(':checked'),
            'getshopdosetup' : $('.getshopdosetup').is(':checked'),
            'getshopinstalllocks' : $('.getshopinstalllocks').is(':checked'),
            'getshoptraining' : $('.getshoptraining').is(':checked'),
            'currency' : $('.currency').val()
        }
        
        if ($('[gsname="discounttotal"]')) {
            data.discounttotal = $('[gsname="discounttotal"]').val();
            data.discountlicense = $('[gsname="discountlicense"]').val();
        }
        
        var event = thundashop.Ajax.createEvent(null, 'calculate', this, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(res) {
            $('.GetShopPriceModel .summary').html(res);
        });
    }   
};

app.GetShopPriceModel.init();