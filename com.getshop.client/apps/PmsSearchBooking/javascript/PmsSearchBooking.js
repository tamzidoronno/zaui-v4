app.PmsSearchBooking = {
    init : function() {
        $(document).on('click','.GetShopModuleTable .quickfunction',app.PmsSearchBooking.doQuickFunction);
        $(document).on('click','.GetShopModuleTable .unsettledamountwarning',app.PmsSearchBooking.loadUnpaidPriceView);
        $(document).on('click','.GetShopModuleTable .toggleallrooms',app.PmsSearchBooking.toggleAllRooms);
    },
    toggleAllRooms : function() {
        if($(this).is(':checked')) {
            $('.groupedactioncheckbox').attr('checked','checked');
            $('.manipulateroomoptions .shop_button').removeClass('disabled');
        } else {
            $('.groupedactioncheckbox').attr('checked',null);
            $('.manipulateroomoptions .shop_button').addClass('disabled');
        }
    },
    loadUnpaidPriceView : function() {
        var event = thundashop.Ajax.createEvent('','loadUnpaidView',$(this), {
            "roomid" : $(this).attr('roomid')
        });
        var field = $(this).closest('.col');
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.unpaidroomview').remove();
            field.append('<div class="unpaidroomview dontExpand">' + res + "</div>");
        });
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
