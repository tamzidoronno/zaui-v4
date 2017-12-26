app.PmsSearchPaymentProcess = {
    init: function() {
        $(document).on('click', '.PmsSearchBooking .paymentprocess_icon', app.PmsSearchPaymentProcess.toggleShowProcess);
        $(document).on('click', '.PmsSearchBooking .toggle_payment_process_window', app.PmsSearchPaymentProcess.toggleShowProcess);
    },
    
    toggleShowProcess: function() {
        if ($(this).closest('.paymentprocess_icon_outer').hasClass('paymentprocess_icon_outer_cover')) {
            $(this).closest('.paymentprocess_icon_outer').removeClass('paymentprocess_icon_outer_cover');
        } else {
            $(this).closest('.paymentprocess_icon_outer').addClass('paymentprocess_icon_outer_cover', 500);
        }
    },
    
    updatePaymentProcess: function() {
        var pmsapp = $('.PmsSearchBooking');
        var event = thundashop.Ajax.createEvent(null, "renderPaymentProcess", pmsapp, {});
        event['synchron'] = true;
        thundashop.Ajax.post(event, app.PmsSearchPaymentProcess.receivedPaymentProcess);
    },
    
    receivedPaymentProcess: function(res) {
        if (res) {
            $('.payment_process_summary').html(res);
            $('.paymentprocess_icon').show();
            $('.paymentprocess_icon').effect( "shake", { direction: "left", times: 4, distance: 5}, 300 );
        } else {
            $('.paymentprocess_icon').hide();
            $('.paymentprocess_icon_outer_cover').removeClass('paymentprocess_icon_outer_cover');
        }
    }
};

app.PmsSearchPaymentProcess.init();