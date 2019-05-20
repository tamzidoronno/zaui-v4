app.IntegratedPaymentTerminal = {
    init: function() {
        $(document).on('click', '.IntegratedPaymentTerminal .refresh', app.VerifoneTerminal.refresh);
    },
    
    refresh: function() {
        var me = $('.IntegratedPaymentTerminal');
        
        data = {
            orderid : $(me).find('#orderid').val()
        };
        
        var event = thundashop.Ajax.createEvent(null, "render", me, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(res) {
            $(me).find('.applicationinner').html(res);
        })
    }
}

app.IntegratedPaymentTerminal.init();