app.GetShopPriceModel = {
    init : function() {
        $(document).on('keyup','.GetShopPriceModel .inputvalue',app.GetShopPriceModel.calculate);
        $(document).on('keyup','.GetShopPriceModel .searchlead',app.GetShopPriceModel.searchLead);
        $(document).on('click','.GetShopPriceModel .inputvalue',app.GetShopPriceModel.calculate);
        $(document).on('click','.GetShopPriceModel .connecttolead',app.GetShopPriceModel.connectToLead);
    },
    connectToLead : function() {
        var event = thundashop.Ajax.createEvent('','connectToLead', $(this), {
            "leadid" : $(this).attr('leadid')
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.leadlist').html("");
        });
    },
    searchLead : function() {
        if(typeof(app.GetShopPriceModel.timeout) !== "undefined") {
            clearTimeout(app.GetShopPriceModel.timeout);
        }
        var btn = $(this);
        app.GetShopPriceModel.timeout = setTimeout(function() {
            var event = thundashop.Ajax.createEvent('','searchLead',btn,{
                "keyword" : btn.val()
            });
            thundashop.Ajax.postWithCallBack(event, function(res) {
                $('.leadlist').html(res);
            });
        }, "500");
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
            'directbylocks' : $('.directbylocks').is(':checked'),
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