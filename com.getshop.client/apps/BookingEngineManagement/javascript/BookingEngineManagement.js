app.BookingEngineManagement = {
    init: function () {
        $(document).on('click', '.BookingEngineManagement .deletegroupid', app.BookingEngineManagement.deleteBookingItemType);
        $(document).on('click', '.BookingEngineManagement .editBookingItemButton', app.BookingEngineManagement.editBookingItemButton);
        $(document).on('click', '.BookingEngineManagement .deletetype', app.BookingEngineManagement.deletetype);
        $(document).on('change', '.BookingEngineManagement .itemTypeSelector', app.BookingEngineManagement.setItemType);
        $(document).on('change', '.BookingEngineManagement .selectedProductForGroup', app.BookingEngineManagement.selectedProductForGroup);
        $(document).on('click', '.BookingEngineManagement .editbookingitemtype', app.BookingEngineManagement.editbookingitemtype);
        $(document).on('click', '.BookingEngineManagement .configureBookingFields', app.BookingEngineManagement.configureBookingFields);
        $(document).on('click', '.BookingEngineManagement .configureOpeningHours', app.BookingEngineManagement.configureOpeningHours);
        $(document).on('change','.BookingEngineManagement .repeat_type', app.BookingEngineManagement.changeRepeatType);
        $(document).on('click','.BookingEngineManagement .configureSorting', app.BookingEngineManagement.configureSorting);
        $(document).on('click','.BookingEngineManagement .savesorting', app.BookingEngineManagement.savesorting);
    },
    savesorting: function() {
        var counter = 0;
        var result = [];
        $('#sortable li').each(function() {
            result.push($(this).attr('itemid'));
            counter++;
        });
        var data = {
            "sortlist" : result
        }
        thundashop.Ajax.simplePost($(this), 'setNewSorting', data);
        thundashop.common.hideInformationBox();
    },
    
     changeRepeatType: function() {
        var type = $(this).val();
        $('.repeatrow').hide();
        if(type !== "0") {
            $('.repeatrow').show();
        } 
        $('.repeateachdaterow').hide();
        if(type === "1") {
            $('.repeateachdaterow').show();
        }
        
        $('.repeatoption').hide();
        $('.repeat_' + type).show();
    },
    configureSorting: function() {
        var data = {}
        var event = thundashop.Ajax.createEvent('','configureItemSorting', $(this), data);
        thundashop.common.showInformationBoxNew(event, 'Opening hours configuration');
    },
    configureOpeningHours : function() {
        var data = {
            "typeid" : $(this).closest('tr').attr('entryid')
        }
        var event = thundashop.Ajax.createEvent('','configureOpeningHours', $(this), data);
        thundashop.common.showInformationBoxNew(event, 'Opening hours configuration');
    },
    configureBookingFields : function() {
        var data = {
            "typeid" : $(this).closest('tr').attr('entryid')
        }
        var event = thundashop.Ajax.createEvent('','configureBookingFields', $(this), data);
        thundashop.common.showInformationBoxNew(event, 'Booking form fields configuration');
    },
    editbookingitemtype : function() {
        var data = {
            "typeid" : $(this).closest('tr').attr('entryid')
        }
        var event = thundashop.Ajax.createEvent('','loadTypeSettings', $(this), data);
        thundashop.common.showInformationBoxNew(event, 'Item type configuration');
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
    editBookingItemButton : function() {
        var data = {
            "typeid" : $(this).closest('tr').attr('entryid')
        }
        var event = thundashop.Ajax.createEvent('','loadItemSettings', $(this), data);
        thundashop.common.showInformationBoxNew(event, 'Item type configuration');
        
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
        thundashop.common.showInformationBoxNew(event,'Pms form settings');
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