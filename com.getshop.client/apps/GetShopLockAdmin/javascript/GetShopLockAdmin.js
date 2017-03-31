app.GetShopLockAdmin = {
    init: function() {
        $(document).on('change','.GetShopLockAdmin .connectlock', app.GetShopLockAdmin.connectLock);
        $(document).on('click','.GetShopLockAdmin .loadlocklist', app.GetShopLockAdmin.loadLockList);
    },
    loadLockList : function() {
        var id = $(this).attr('lock');
        var event = thundashop.Ajax.createEvent('','loadLockList', $(this), {
            "id" : id,
            "source" : $(this).attr('source')
        });
        thundashop.common.showInformationBoxNew(event, 'Lock list');
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
