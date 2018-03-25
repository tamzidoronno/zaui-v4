app.SrsReturantTable =  {
    init: function() {
        $(document).on('click', '.SrsReturantTable .startpaymentprocess', app.SrsReturantTable.startPaymentProcess);
    },
    
    startPaymentProcess: function() {
        thundashop.framework.toggleRightWidgetPanel('gs_modul_cart');
    }
}

app.SrsReturantTable.init();