app.EcommerceOrderList = {
    init : function() {
        $(document).on('click','.EcommerceOrderList .sendpaymentlink', app.EcommerceOrderList.sendPaymentLink);
        $(document).on('click','.EcommerceOrderList .sendemail', app.EcommerceOrderList.sendEmail);
        $(document).on('click','.EcommerceOrderList .closesendpaymentlink', app.EcommerceOrderList.closesendpaymentlink);
        $(document).on('click','.EcommerceOrderList .deleteOrder', app.EcommerceOrderList.deleteOrder);
        $(document).on('click','.EcommerceOrderList .creditOrder', app.EcommerceOrderList.creditOrder);
    },
    deleteOrder : function() {
        var confirmed = confirm("Are you sure you want to delete this order?");
        if(confirmed) {
            var orderid = $(this).attr('orderid');
            thundashop.Ajax.simplePost($(this), "deleteOrder", {
                "id" : orderid
            });
        }
    },
    creditOrder : function() {
        var confirmed = confirm("Are you sure you want to credit this order?");
        if(confirmed) {
            var orderid = $(this).attr('orderid');
            thundashop.Ajax.simplePost($(this), "creditOrder", {
                "id" : orderid
            });
        }
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
            "orderid" : orderid,
            "callback" : btn.attr('callback')
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            btn.parent().find('.sendpaymentlinkwindow').html(res);
            btn.parent().find('.sendpaymentlinkwindow').slideDown();
        });
    }
};
app.EcommerceOrderList.init();