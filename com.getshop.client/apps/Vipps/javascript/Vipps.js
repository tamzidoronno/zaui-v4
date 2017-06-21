app.Vipps = {
    checkiforderhasbeencompletedtimout : null,
    init : function() {
        $(document).on('click', '.CartManager .startvippspayment', app.Vipps.startPayment);
        $(document).on('click', '.CartManager .cancelvippsorder', app.Vipps.cancelvippsorder);
    },
    cancelvippsorder : function() {
        var event = thundashop.Ajax.createEvent('','cancelVippsOrder', $('.CartManager'), {
            "orderid" : $(this).attr('orderid')
        });
        clearTimeout(app.Vipps.checkiforderhasbeencompletedtimout);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.vippspayment').show();
            $('.vippswaiting').hide();
            $('.vippsuccess').hide();
            $('.vippserror').hide();
        });
    },
    waitForCompletion : function(orderId) {
        $('.vippspayment').hide();
        $('.vippswaiting').show();
        console.log('waiting for order: ' + orderId + " to complete.");
        var event = thundashop.Ajax.createEvent('','checkIfOrderHasBeenCompleted', $('.CartManager'), {
            "orderid" : orderId
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            if(res !== "yes") {
                app.Vipps.checkiforderhasbeencompletedtimout = setTimeout(function() {
                    app.Vipps.waitForCompletion(orderId);
                }, "3000");
            } else {
                app.Vipps.showSuccess();
            }
        });
    },
    showSuccess : function() {
        $('.vippspayment').hide();
        $('.vippswaiting').hide();
        $('.vippsuccess').show();
    },
    displayError : function() {
        $('.vippserror').show();
    },
    startPayment : function() {
        var form = $(this).closest('[gstype="form"]');
        var data = thundashop.framework.createGsArgs(form);
        var event = thundashop.Ajax.createEvent('','startVippsPayment', $(this), data);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            res = JSON.parse(res);
            if(typeof(res.transactionInfo) !== "undefined") {
                var transactionId = res.transactionInfo.transactionId;
            }
            if(typeof(transactionId) === "undefined") {
                app.Vipps.displayError();
            } else {
                var orderId = res.orderId;
                app.Vipps.waitForCompletion(orderId);
            }
            console.log(res);
        });
    }
}
app.Vipps.init();