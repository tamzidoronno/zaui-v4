app.PmsAddAddonsList = {
    init : function() {
        $(document).on('click', '.PmsAddAddonsList .addAddonsToRoom', app.PmsAddAddonsList.addAddonsToRoom);
    },
    addAddonsToRoom : function() {
        var event = thundashop.Ajax.createEvent('','addProductToBooking',$(this), {
            productId : $(this).attr('productid'),
            roomId : $('.menuarea').attr('roomid')
        });
        thundashop.Ajax.postWithCallBack(event, function() {
            app.PmsBookingRoomView.refresh();
        });
    }
};

app.PmsAddAddonsList.init();