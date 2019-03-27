//Put your javascript here.
app.PayOnDelivery = {
    
    init: function() {
        $(document).on('change', '.PayOnDelivery #receivedpayment', app.PayOnDelivery.calculateCashRefund);
    },
    
    calculateCashRefund: function() {
        var total = $('.completepayment').attr('total');
        var diff = $(this).val() - total;
        diff = Math.round(diff * 100) / 100
        $('.completepayment .torefund').html(diff);
    },
    
    markAsPaidCompleted: function(res) {
        thundashop.Ajax.closeOverLay3();
    }
}

app.PayOnDelivery.init();

