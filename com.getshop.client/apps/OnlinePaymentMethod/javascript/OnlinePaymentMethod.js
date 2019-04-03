app.OnlinePaymentMethod = {
    init: function() {
        $(document).on('change', '.OnlinePaymentMethod .gsniceselect1.selectguest', app.OnlinePaymentMethod.guestChanged);
    },
    
    linkSent: function(res) {
        $('.OnlinePaymentMethod .applicationinner').html(res);
    },
    
    guestChanged: function() {
        var data = {
            bookerEmail : $(this).val(),
            name: $(this).find(':selected').attr('name'),
            prefix : $(this).find(':selected').attr('prefix'),
            phone : $(this).find(':selected').attr('phone'),
            orderid : $(this).closest('.app').find('[gsname="orderid"]').val()
        }
        
        var me = this;
        $(this).closest('.app').find('[gsname="bookerEmail"]').val(data.bookerEmail);
        $(this).closest('.app').find('[gsname="prefix"]').val(data.prefix);
        $(this).closest('.app').find('[gsname="phone"]').val(data.phone);
        
        var event = thundashop.Ajax.createEvent(null, "updatePaymentLinkText", this, data);
        event['synchron'] = true;
        
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $(me).closest('.app').find("[gsname='emailMessage']").text(res);
        });
    },
    
}

app.OnlinePaymentMethod.init();