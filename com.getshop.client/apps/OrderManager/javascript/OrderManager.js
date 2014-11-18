app.OrderManager = {
    init : function() {
        $(document).on('change', '.OrderManager #orderstatus_selection', app.OrderManager.changeOrderStatus);
        $(document).on('click', '.OrderManager .collectionOrder', app.OrderManager.collectOrder);
    },
    collectOrder : function() {
        var event = thundashop.Ajax.createEvent('','collect',$(this), {
            "orderid" : $(this).attr('orderid')
        });
        thundashop.Ajax.post(event);
    },
    
    changeOrderStatus : function() {
        var status = $(this).val();
        var oid = $(this).closest('.OrderManager').find('#current_orderid').val();
        var event = thundashop.Ajax.createEvent('','ChangeOrderStatus',$(this), { "orderid" : oid, "status" : status });
        thundashop.Ajax.postWithCallBack(event, function() {
            thundashop.common.Alert(__f("Status changed"), __f("The order status has been updated"));
        });
    }
};
app.OrderManager.init();