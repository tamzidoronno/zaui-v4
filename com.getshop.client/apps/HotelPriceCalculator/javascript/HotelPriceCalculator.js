app.HotelPriceCalculator = {
    initEvents : function() {
        $(document).on('click','.check_price_button', app.HotelPriceCalculator.generatePrice);
        $(document).on('click','.tryagain', app.HotelPriceCalculator.showTryAgain);
    },
    showTryAgain : function() {
        
        var event = thundashop.Ajax.createEvent('','retry',$(this), {});
        thundashop.Ajax.postWithCallBack(event, function(result) {
            $('.pricecalcarea').html(result);
        });
    },
    generatePrice : function() {
        var container = $(this).closest('.app');
        
        var data = {
            "start" : container.find('.start_date').val(),
            "end" : container.find('.end_date').val(),
            "product" : container.find('.selectroom').val()
        }
        
        var event = thundashop.Ajax.createEvent('','generatePrice',$(this), data);
        thundashop.Ajax.postWithCallBack(event, function(result) {
            $('.pricecalcarearesult').html(result);
        });
    }
};

app.HotelPriceCalculator.initEvents();