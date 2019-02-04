app.SalesPointTabPayment = {
    init: function() {
        $(document).on('change', '.SalesPointTabPayment #receivedpayment', app.SalesPointTabPayment.calculateCashRefund);
        $(document).on('change', '.SalesPointTabPayment .itemlines input', app.SalesPointTabPayment.overviewChanged);
        $(document).on('keyup', '.SalesPointTabPayment .filterpayonroomlist', app.SalesPointTabPayment.filterPayOnRoomList);
        $(document).on('keyup', '.SalesPointTabPayment .giftcardcode', app.SalesPointTabPayment.checkGiftCardCode);
        $(document).on('click', '.SalesPointTabPayment .startpayment', app.SalesPointTabPayment.startPayment);
        $(document).on('click', '.SalesPointTabPayment .debugaction', app.SalesPointTabPayment.postDebugMessage);
    },
    
    checkGiftCardCode: function() {
        var giftCardCode = $(this).val();
        var data = {
            code : giftCardCode
        };
        var event = thundashop.Ajax.createEvent(null, "getRemainingGiftCard", this, data);
        event['sychron'] = true;
        thundashop.Ajax.post(event, function(res) {
            var container = $('.giftcard .giftcardvalue');
            container.attr('value', res);
            
            if (!res) {
                container.html('Card not found, please check gift card code');
            } else {
                container.html('Remaining value: ' + res);
            }
        }, [], true, true);
    },
    
    checkGiftCardCodeBeforePost: function() {
        var total = $('.SalesPointTabPayment .giftcard .total').attr('value');
        var remainingOnGiftCard = $('.SalesPointTabPayment .giftcardvalue').attr('value');
        total = parseInt(total, 10);
        remainingOnGiftCard = parseInt(remainingOnGiftCard, 10);
        var enough = remainingOnGiftCard > total;
        
        if (!enough) {
            alert("Not enough credit on given gift card");
            return false;
        }
        
        var extraData = {
            giftCardCode : $('.SalesPointTabPayment .giftcard .giftcardcode').val()
        }
        
        return extraData;
    },
    
    filterPayOnRoomList: function() {
        var searchText = $(this).val();
        
        if (!searchText) {
            $('.roomrow').show();
        }
        
        $('.roomrow').each(function() {
            var text = $(this).html();
            text = text.toLowerCase();
            if (!text.includes(searchText)) {
                $(this).hide();
            }
        })
    },
    
    calculateCashRefund: function() {
        var total = $('.completepayment').attr('total');
        var diff = $(this).val() - total;
        $('.completepayment .torefund').html(diff);
    },
    
    startPayment: function() {
        var data = app.SalesPointTabPayment.getChargingData();
        data.payementId = $(this).attr('paymentid');
        var event = thundashop.Ajax.createEvent(null, "startPayment", this, data);
        event.firstLoad = true;
        thundashop.Ajax.post(event);
    },
    
    getChargingData: function() {
        var data = {};
        data.items = [];
        
        $('.SalesPointTabPayment .itemlines').each(function() {
            var cartItem = {
                cartitemid : $(this).attr('cartitemid'),
                count : $(this).find('.count input').val(),
                price : $(this).find('.price input').val(),
                active : $(this).find('.checkbox input').is(':checked')
            }
            data.items.push(cartItem);
        });
        
        return data;
    },
    
    
    overviewChanged: function() {
        var data = app.SalesPointTabPayment.getChargingData();
        
        var event = thundashop.Ajax.createEvent(null, "getTotalForItems", this, data);
        thundashop.Ajax.post(event, function(res) {
            $('.SalesPointTabPayment .totalrow .price').html(res);
        })
        
    },
    
    fetchTerminalMessages: function() {
        var event = thundashop.Ajax.createEvent(null, "getPaymentProcessMessage", $('.SalesPointTabPayment'), {});
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(res) {
            $('.SalesPointTabPayment .paymentprocess_area').html(res);
        }, null, true, true);
    },
    
    nullFunction: function(res) {
        // Used for not reprinting.
    },
    
    postDebugMessage: function() {
        var data = {
            action : $(this).attr('action')
        };
        
        var event = thundashop.Ajax.createEvent(null, "handleDebugActions", this, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(res) {
            console.log("done");
        }, [], true);
    }
};

app.SalesPointTabPayment.init();