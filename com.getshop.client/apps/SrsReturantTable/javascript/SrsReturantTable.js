app.SrsReturantTable =  {
    init: function() {
        $(document).on('click', '.SrsReturantTable .startpaymentprocess', app.SrsReturantTable.startPaymentProcess);
    },
    
    startPaymentProcess: function() {
        thundashop.framework.toggleRightWidgetPanel('gs_modul_cart', {
            reservationid : $(this).attr('reservationid')
        });
    }
}

app.SrsReturantTable.init();