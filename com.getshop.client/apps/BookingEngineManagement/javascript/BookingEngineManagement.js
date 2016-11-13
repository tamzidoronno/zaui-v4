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
        $(document).on('click','.BookingEngineManagement .configureTypeSorting', app.BookingEngineManagement.configureTypeSorting);
        $(document).on('click','.BookingEngineManagement .savesorting', app.BookingEngineManagement.savesorting);
        $(document).on('click','.BookingEngineManagement .savetypesorting', app.BookingEngineManagement.savetypesorting);
        $(document).on('click','.BookingEngineManagement .uploadTypeImage', app.BookingEngineManagement.uploadBoxClick);
        $(document).on('click','.BookingEngineManagement .removeImageFromType', app.BookingEngineManagement.removeImageFromType);
        $(document).on('click','.BookingEngineManagement .makeimgasdefault', app.BookingEngineManagement.makeimgasdefault);
    },
    removeImageFromType : function() {
        var data = {
            "typeId" : $(this).attr('typeId'),
            "fileId" : $(this).attr('fileId')
        }
        var event = thundashop.Ajax.createEvent('','removeImageFromType', $(this), data);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.imagearea').html(res);
        });
    },
    makeimgasdefault : function() {
        var data = {
            "typeId" : $(this).attr('typeId'),
            "fileId" : $(this).attr('fileId')
        }
        var event = thundashop.Ajax.createEvent('','makeImageDefault', $(this), data);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.imagearea').html(res);
        });
    },
    uploadBoxClick: function () {
        $('#getshop_select_files_link').remove();
        $('#your-files').remove();
        var button = $(this);
        var curText = button.html();
        app.BookingEngineManagement.currentTypeId = "";
        var selectDialogueLink = $('<a href="" id="getshop_select_files_link">Select files</a>');
        var fileSelector = $('<input type="file" id="your-files" multiple/>');

        selectDialogueLink.click(function () {
            fileSelector.click();
        });
        $('body').append(fileSelector);
        $('body').append(selectDialogueLink);

        var control = document.getElementById("your-files");
        var me = this;

        control.addEventListener("change", function () {
            button.html('<i class="fa fa-spin fa-spinner"></i>');
            fileSelector.remove();
            app.BookingEngineManagement.imageSelected(control.files, button.closest('.app'));
        });

        selectDialogueLink.click();
        selectDialogueLink.remove();
    },
    imageSelected: function (files, application) {
        var file = files[0];
        var fileName = file.name;

        var reader = new FileReader();

        reader.onload = function (event) {
            var dataUri = event.target.result;

            var data = {
                typeId: application.find('.uploadTypeImage').attr('typeid'),
                fileBase64: dataUri,
                fileName: fileName
            };

            var event = thundashop.Ajax.createEvent('','saveTypeImage', application, data);

            thundashop.Ajax.postWithCallBack(event, function(res) {
                $('.imagearea').html(res);
            });
        };

        reader.onerror = function (event) {
            console.error("File could not be read! Code " + event.target.error.code);
        };

        reader.readAsDataURL(file);
    },
    configureTypeSorting : function() {
        var data = {}
        var event = thundashop.Ajax.createEvent('','configureTypeSorting', $(this), data);
        thundashop.common.showInformationBoxNew(event, 'Opening hours configuration');
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
    savetypesorting: function() {
        var counter = 0;
        var result = [];
        $('#sortable li').each(function() {
            result.push($(this).attr('typeid'));
            counter++;
        });
        var data = {
            "sortlist" : result
        }
        thundashop.Ajax.simplePost($(this), 'setNewTypeSorting', data);
        thundashop.common.hideInformationBox();
    },
    
     changeRepeatType: function() {
         var toChange = $(this).closest('.itemrow');
        var type = $(this).val();
        toChange.find('.repeatrow').hide();
        if(type !== "0") {
            toChange.find('.repeatrow').show();
        } 
        toChange.find('.repeateachdaterow').hide();
        if(type === "1") {
            toChange.find('.repeateachdaterow').show();
        }
        
        toChange.find('.repeatoption').hide();
        toChange.find('.repeat_' + type).show();
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