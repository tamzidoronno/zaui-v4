app.PmsBookingMyBookingList = {
    init : function() {
        $(document).on('click', '.PmsBookingMyBookingList .deleteroom', app.PmsBookingMyBookingList.deleteRoom);
    },
    deleteRoom : function() {
        var confirmed = confirm("Are you sure you want to delete this room?");
        if(!confirmed) {
            return;
        }
        var data = {
            "roomid" : $(this).closest('tr').attr('roomid'),
            "bookingid" : $(this).closest('tr').attr('bookingid')
        }
        thundashop.Ajax.simplePost($(this), 'deleteRoom', data);
    },
    loadSettings: function (element, application) {
        var config = {
            draggable: true,
            app: true,
            showSettings : true,
            application: application,
            title: "Settings",
            items: []
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
};

app.PmsBookingMyBookingList.init();