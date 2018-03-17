app.EcommerceOrderList = {
    init : function() {
        $(document).on('click','.EcommerceOrderList .sendpaymentlink', app.EcommerceOrderList.sendPaymentLink);
        $(document).on('click','.EcommerceOrderList .sendemail', app.EcommerceOrderList.sendEmail);
        $(document).on('click','.EcommerceOrderList .closesendpaymentlink', app.EcommerceOrderList.closesendpaymentlink);
    },
    sendEmail : function() {
        var btn = $(this);
        var roomId = $(this).attr('roomid');
        var orderid = $(this).attr('orderid');
        var event = thundashop.Ajax.createEvent('','loadSendEmail',$(this),{
            "roomid" : roomId,
            "orderid" : orderid
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            btn.parent().find('.sendpaymentlinkwindow').html(res);
            btn.parent().find('.sendpaymentlinkwindow').slideDown();
        });
    },
    closesendpaymentlink : function() {
        $('.sendpaymentlinkwindow').hide();
    },
    sendPaymentLink : function() {
        var btn = $(this);
        var roomId = $(this).attr('roomid');
        var orderid = $(this).attr('orderid');
        var event = thundashop.Ajax.createEvent('','loadPaymentLinkConfig',$(this),{
            "roomid" : roomId,
            "orderid" : orderid
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            btn.parent().find('.sendpaymentlinkwindow').html(res);
            btn.parent().find('.sendpaymentlinkwindow').slideDown();
        });
    }
};
app.EcommerceOrderList.init();