app.PaymentSettingsSetup = {
    init : function() {
        $(document).on('click','.PaymentSettingsSetup .quickchoosepaymentmethod', app.PaymentSettingsSetup.choosePaymentMethod);
    },
    choosePaymentMethod : function() {
        var id = $(this).attr('appid');
        $('.paymentsetupconfig').hide();
        $('.paymentsetupconfig[appid="'+id+'"]').fadeIn();
    }
};
app.PaymentSettingsSetup.init();