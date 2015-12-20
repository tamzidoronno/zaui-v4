app.BookingEngineManagement = {
    init: function () {
        $(document).on('click', '.BookingEngineManagement .deletegroupid', app.BookingEngineManagement.deleteBookingItemType);
        $(document).on('click', '.BookingEngineManagement .renameBookingItemButton', app.BookingEngineManagement.renameBookingItemButton);
        $(document).on('click', '.BookingEngineManagement .deletetype', app.BookingEngineManagement.deletetype);
        $(document).on('change', '.BookingEngineManagement .itemTypeSelector', app.BookingEngineManagement.setItemType);
        $(document).on('change', '.BookingEngineManagement .selectedProductForGroup', app.BookingEngineManagement.selectedProductForGroup);
        $(document).on('click', '.BookingEngineManagement .editbookingitemtype', app.BookingEngineManagement.editbookingitemtype);
    },
    editbookingitemtype : function() {
        var data = {
            "typeid" : $(this).closest('tr').attr('entryid')
        }
        var event = thundashop.Ajax.createEvent('','loadTypeSettings', $(this), data);
        thundashop.common.showInformationBox(event, 'Item type configuration');
    },
    selectedProductForGroup : function() {
        var productId = $(this).val();
        var typeid = $(this).closest('tr').attr('entryid');
        var event = thundashop.Ajax.createEvent('','setProductToItemType',$(this), {
            "productid" : productId,
            "typeid" : typeid
        });
        thundashop.Ajax.post(event);
    },
    setItemType : function() {
        var id = $(this).closest('tr').attr('entryid');
        var itemTypeId = $(this).val();
        var event = thundashop.Ajax.createEvent('','updateType',$(this), {
            "id" : id,
            "itemTypeId" : itemTypeId
        });
        thundashop.Ajax.post(event);
        
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