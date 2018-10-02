app.EcommerceOrderList = {
    init : function() {
        $(document).on('click','.EcommerceOrderList .sendpaymentlink', app.EcommerceOrderList.sendPaymentLink);
        $(document).on('click','.EcommerceOrderList .sendemail', app.EcommerceOrderList.sendEmail);
        $(document).on('click','.EcommerceOrderList .closesendpaymentlink', app.EcommerceOrderList.closesendpaymentlink);
        $(document).on('click','.EcommerceOrderList .deleteOrder', app.EcommerceOrderList.deleteOrder);
        $(document).on('click','.EcommerceOrderList .creditOrder', app.EcommerceOrderList.creditOrder);
    },
    
    showSendingEhfMessage: function() {
        $('.sendehfbox').hide(); 
        $('.sendingehf').show();
    },
    
    refreshOrderRow : function(orderId) {
        var event = thundashop.Ajax.createEvent('','reloadRow',$('[hiddenorderid="'+orderId+'"]'), {
            "id" : orderId
        });
        
        thundashop.Ajax.postWithCallBack(event, function(res) {
            res = JSON.parse(res);
            var row = $('[hiddenorderid="'+orderId+'"]').closest('.datarow_inner');
            for(var k in res) {
                row.find("[index='"+k+"']").html(res[k]);
            }
        });
    },
    deleteOrder : function() {
        var confirmed = confirm("Are you sure you want to delete this order?");
        if(confirmed) {
            var row = $(this).closest('.datarow');
            var orderid = $(this).attr('orderid');
            var event = thundashop.Ajax.createEvent('','deleteOrder', $(this), {
                "id" : orderid
            });
            thundashop.Ajax.postWithCallBack(event, function() {
                row.remove();
            });
        }
    },
    creditOrder : function() {
        var confirmed = confirm("Are you sure you want to credit this order?");
        if(confirmed) {
            var orderid = $(this).attr('orderid');
            var event = thundashop.Ajax.createEvent('','creditOrder', $(this), {
                "id" : orderid
            });
            thundashop.Ajax.postWithCallBack(event, function() {
                thundashop.framework.reloadOverLayType1or2();
            })
        }
    },
    doneSendingPaymentLink : function() {
        $('.currentsentdate').html('<i class="fa fa-check"></i> Just sent');
        $('.currentsentdate').closest('.datarow').find('.sendpaymentlinkwindow').slideUp();
    },
    doneSendingPaymentLinkEhf : function(res) {
        if (!res) {
            $('.currentsentdate').html('<i class="fa fa-check"></i> Just sent');
            $('.currentsentdate').closest('.datarow').find('.sendpaymentlinkwindow').slideUp();    
        } else {
            $('.currentsentdate').closest('.datarow').find('.sendingehf').html(res);    
        }
        
        
    },
    sendEmail : function() {
        var btn = $(this);
        var roomId = $(this).attr('roomid');
        var orderid = $(this).attr('orderid');
        $('.currentsentdate').removeClass('currentsentdate');
        $(this).closest('.datarow').find('.sentdate').addClass('currentsentdate');
        var event = thundashop.Ajax.createEvent('','loadSendEmail',$(this),{
            "roomid" : roomId,
            "id" : orderid
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
        var bookingId = $(this).attr('bookingid');
        var orderid = $(this).attr('orderid');
        $('.currentsentdate').removeClass('currentsentdate');
        $(this).closest('.datarow').find('.sentdate').addClass('currentsentdate');
        var event = thundashop.Ajax.createEvent('','loadPaymentLinkConfig',$(this),{
            "roomid" : roomId,
            "id" : orderid,
            "callback" : btn.attr('callback')
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            btn.parent().find('.sendpaymentlinkwindow').html(res);
            btn.parent().find('.sendpaymentlinkwindow').slideDown();
        });
    }
};
app.EcommerceOrderList.init();