app.GetShopLockAdmin = {
    init: function() {
        $(document).on('change','.GetShopLockAdmin .connectlock', app.GetShopLockAdmin.connectLock);
        $(document).on('click','.GetShopLockAdmin .loadlocklist', app.GetShopLockAdmin.loadLockList);
        $(document).on('click','.GetShopLockAdmin .changezwaveid', app.GetShopLockAdmin.changeZWaveId);
    },
    changeZWaveId : function() {
        var newId = prompt("New z-wave id");
        if(!newId) {
            return;
        }
        var lockid = $(this).attr('lockid');
        var event = thundashop.Ajax.createEvent('','changeZwaveid',$(this), {
            "newzwaveid" : newId,
            "lockid" : lockid
        });
        var td = $(this);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            td.html(newId);
        });
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
