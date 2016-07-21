app.GetShopLockAdmin = {
    init: function() {
        $(document).on('change','.GetShopLockAdmin .connectlock', app.GetShopLockAdmin.connectLock);
    },
    connectLock : function() {
        var data = {
            "item" : $(this).val(),
            "lock" : $(this).attr('lockid')
        }
        var event = thundashop.Ajax.createEvent('','connectLock', $(this),data);
        thundashop.Ajax.postWithCallBack(event, function() {
            
        });
    }
};
app.GetShopLockAdmin.init();
