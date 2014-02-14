app.OrderManager = {
    init : function() {
        $(document).on('change', '.OrderManager #orderstatus_selection', app.OrderManager.changeOrderStatus);
        
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