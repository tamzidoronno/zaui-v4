app.PmsCheckList = {
    init: function() {
        $(document).on('click', '.PmsCheckList .markorderasbillable', app.PmsCheckList.markOrderAsBillable)
    },
    
    markOrderAsBillable: function() {
        var data = {
            orderid : $(this).attr('orderid')
        };
        
        var me = this;
        var event = thundashop.Ajax.createEvent(null, "markOrderAsBillable", this, data);
        event['synchron'] = true;
        thundashop.Ajax.post(event, function(res) {
            $(me).closest('.errorrow').hide();
        }, null, true);
    }
}

app.PmsCheckList.init();