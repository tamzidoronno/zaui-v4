app.PmsSearchBooking = {
    init : function() {
        $(document).on('click','.GetShopModuleTable .quickfunction',app.PmsSearchBooking.doQuickFunction);
    },
    doQuickFunction : function() {
        var type = $(this).attr('type');
        var roomId = $(this).closest('.quickfunctions').attr('roomid');
        var event = thundashop.Ajax.createEvent('','loadQuickMenu',$(this), {
            "type" : type,
            "roomid" : roomId
        });
        var field = $(this).closest('.datarow').find('.quickmenuoption');
        field.show();
        thundashop.Ajax.postWithCallBack(event, function(res) {
            field.html(res);
        });
    }
};
app.PmsSearchBooking.init();