app.GiftCard = {
    init : function() {
        $(document).on('keyup','.giftcardinput', app.GiftCard.loadGiftCardPrice);
    },
    loadGiftCardPrice : function() {
        var val = $(this).val();
        var amount = $(this).attr('amount');
        var event = thundashop.Ajax.createEvent('','findGiftCardPrice',$(this), {
            code : val,
            amount : amount
        });
        
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.coderesultarea').html(res);
        });
    }
};

app.GiftCard.init();