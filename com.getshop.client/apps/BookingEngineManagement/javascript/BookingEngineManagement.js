app.BookingEngineManagement = {
    init: function () {
        $(document).on('click', '.BookingEngineManagement .deletegroupid', app.BookingEngineManagement.deleteBookingItemType);
        $(document).on('click', '.BookingEngineManagement .renameBookingItemButton', app.BookingEngineManagement.renameBookingItemButton);
        $(document).on('click', '.BookingEngineManagement .deletetype', app.BookingEngineManagement.deletetype);
    },
    renameBookingItemButton : function() {
        var currentName = $(this).closest('tr').find('.currentname').text();
        var id = $(this).closest('tr').attr('entryid');
        var type = $(this).closest('tr').attr('type');
        var name = prompt("New name of group", currentName);
        var event = thundashop.Ajax.createEvent('','updateName',$(this), {
            "name" : name,
            "entryid" : id,
            "type" : type
        });
        thundashop.Ajax.post(event);
    },
    deleteBookingItemType : function() {
        var confirmed = confirm("Are you sure you want to delete this item type?");
        if(confirmed) {
            var event = thundashop.Ajax.createEvent('','deleteBookingItemType',$(this), {
                "bookingitemtypeid" : $(this).attr('bookingitemtypeid')
            });
            thundashop.Ajax.post(event);
        }
    },
    deletetype : function() {
        var confirmed = confirm("Are you sure you want to delete this item?");
        if(confirmed) {
            var event = thundashop.Ajax.createEvent('','deletetype',$(this), {
                "entryid" : $(this).closest('tr').attr('entryid')
            });
            thundashop.Ajax.post(event);
        }
    },
    showSettings : function() {
        var event = thundashop.Ajax.createEvent('','showSettings',$(this), {});
        thundashop.common.showInformationBox(event,'Pms form settings');
    },
    loadSettings : function(element, application) {
         var config = {
            draggable: true,
            app : true,
            application: application,
            title: "Settings",
            items: [
                {
                    icontype: "awesome",
                    icon: "fa-edit",
                    iconsize : "30",
                    title: __f("Add / Remove products to this list"),
                    click: app.BookingEngineManagement.showSettings
                }
            ]
        };

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
};
app.BookingEngineManagement.init();