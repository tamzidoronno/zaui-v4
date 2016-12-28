app.PmsConfiguration = {
    selectedview : null,
    
    init: function () {
        $(document).on('change', '.PmsConfiguration .emailtypeselection', app.PmsConfiguration.changeEmailType);
        $(document).on('change', '.PmsConfiguration .smstypeselection', app.PmsConfiguration.changeSmsType);
        $(document).on('change', '.PmsConfiguration .admintypeselection', app.PmsConfiguration.changeAdminType);
        $(document).on('click', '.PmsConfiguration .changeview', app.PmsConfiguration.changeview);
        $(document).on('click', '.PmsConfiguration .addnewchannel', app.PmsConfiguration.addnewchannel);
        $(document).on('click', '.PmsConfiguration .addnewchannel', app.PmsConfiguration.addnewchannel);
        $(document).on('click', '.PmsConfiguration .removeInventoryForRoom', app.PmsConfiguration.removeInventoryForRoom);
        $(document).on('click', '.PmsConfiguration .removeChannel', app.PmsConfiguration.removeChannel);
        $(document).on('click', '.PmsConfiguration .inventoryitem .fa-plus-circle', app.PmsConfiguration.loadAllItemsAdded);
        $(document).on('click', '.PmsConfiguration .addInventoryItem', app.PmsConfiguration.addInventory);
        $(document).on('click', '.PmsConfiguration .loadproducts', app.PmsConfiguration.loadproducts);
        $(document).on('click', '.PmsConfiguration .addproducttoview', app.PmsConfiguration.addproducttoview);
        $(document).on('click', '.PmsConfiguration .removeProductFromMobileView', app.PmsConfiguration.removeProductFromMobileView);
        $(document).on('click', '.PmsConfiguration .loadcouponmoredates', app.PmsConfiguration.loadMoreDates);
        $(document).on('click', '.PmsConfiguration .addwebtext', app.PmsConfiguration.addWebText);
        $(document).on('change', '.PmsConfiguration .changeItemForRoom', app.PmsConfiguration.changeItemForRoom);
        $(document).on('change', '.PmsConfiguration .changechanneloncoupon', app.PmsConfiguration.changechanneloncoupon);
        $(document).on('keyup', '.PmsConfiguration .inventoryonroomcount', app.PmsConfiguration.updateInventoryOnRoomCount);
        
        $(document).on('click','.PmsConfiguration .togglerepeatbox', app.PmsConfiguration.closeTheRepeatBox);
        $(document).on('change','.PmsConfiguration .repeat_type', app.PmsConfiguration.changeRepeatType);

        $(document).on('click', '.PmsConfiguration #contractfield', function() {
            thundashop.common.activateCKEditor('contractfield', {
                autogrow : false
            });
        });
        $(document).on('click', '.PmsConfiguration #otherinstructionsfiled', function() {
            thundashop.common.activateCKEditor('otherinstructionsfiled', {
                autogrow : false
            });
        });
        $(document).on('click', '.PmsConfiguration #fireinstructions', function() {
            thundashop.common.activateCKEditor('fireinstructions', {
                autogrow : false
            });
        });
    },
    removeProductFromMobileView : function() {
        var btn = $(this);
        var event = thundashop.Ajax.createEvent('','removeproductfromview', $(this), {
            id : $(this).closest('tr').attr('viewid'),
            prodid : $(this).attr('prodid')
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            btn.closest('tr').find('.productsadded').html(res);
        });
        $('.productview').fadeOut();
    },
    addproducttoview : function() {
        var btn = $(this);
        var row = $("tr[viewid='" + app.PmsConfiguration.selectedview + "']");
        var event = thundashop.Ajax.createEvent('','addproducttoview', $(this), {
            id : app.PmsConfiguration.selectedview,
            prodid : $(this).attr('prodid')
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            row.find('.productsadded').html(res);
        });
        $('.productview').fadeOut();
    },
    loadproducts : function() {
        var btn = $(this);
        app.PmsConfiguration.selectedview = $(this).closest('tr').attr('viewid');
        var event = thundashop.Ajax.createEvent('','loadProducts', $(this), {});
        var view = $('.PmsConfiguration .productview');
        thundashop.Ajax.postWithCallBack(event, function(res) {
            console.log(res);
            view.html(res);
            view.css('left', btn.position().left);
            view.css('top', (btn.position().top+20));
            view.show();
        });
    },
    
    addWebText : function() {
        var comment = $(this);
        var oldText = comment.closest('td').find('.textfield').attr('title');
        console.log(oldText);
        var text = prompt("Enter a text to display", oldText);
        if(!text) {
            return;
        }
        var data = {
            "text" : text,
            "prodid" : $(this).attr('prodid')
        };
        var event = thundashop.Ajax.createEvent('','setWebText', $(this), data);
        thundashop.Ajax.postWithCallBack(event, function() {
            comment.addClass('added');
            comment.closest('td').find('.textfield').html(text);
            comment.closest('td').find('.textfield').attr('title', text);
        });
    },
    
    closeTheRepeatBox : function() {
        $('.addMoredatesPanel').fadeOut();
    },
    
    loadMoreDates : function() {
        var panel = $(this).closest('td').find('.addmoredatespanel');
        var event = thundashop.Ajax.createEvent('','loadCouponRepeatingDataPanel',$(this), {
            "id" : $(this).attr('data-couponid')
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            panel.html(res);
            panel.find('.addMoredatesPanel').fadeIn();
        });
    },

    changeRepeatType: function() {
        var type = $(this).val();
        $('.repeatrow').hide();
        if(type !== "3") {
            $('.repeatrow').show();
        } 
        $('.repeateachdaterow').hide();
        if(type === "1") {
            $('.repeateachdaterow').show();
        }
        
        $('.repeatoption').hide();
        $('.repeat_' + type).show();
    },
    closeRepeatBox : function() {
        var box = $('.PmsManagement .addMoredatesPanel');
        if(box.is(":visible")) {
            box.slideUp();
        } else {
            box.slideDown();
        }
    },    
    changechanneloncoupon : function() {
        thundashop.Ajax.simplePost($(this), 'changeChannel', {
            "id" : $(this).val(),
            "coupon" : $(this).attr('couponid')
        });
    },
    
    loadAllItemsAdded : function() {
        var event = thundashop.Ajax.createEvent('','loadRoomsAddedToList', $(this), {
            "productid" : $(this).attr('productid')
        });
        
        var panel = $(this).closest('.inventoryitem').find('.itemaddedtoroomlist');
        thundashop.Ajax.postWithCallBack(event, function(res) {
            panel.html(res);
        });
    },
    
    removeInventoryForRoom : function() {
        var event = thundashop.Ajax.createEvent('','removeInventoryForRoom', $(this), {
            "id" : $(this).attr('productid')
        });
        
        thundashop.Ajax.postWithCallBack(event, function() {
            app.PmsConfiguration.refreshRoomList();
        });
    },
    updateInventoryOnRoomCount : function() {
        var event = thundashop.Ajax.createEvent('','updateInventoryOnRoomCount', $(this), {
            "productid" : $(this).attr('productid'),
            "count" : $(this).val()
        });
        
        thundashop.Ajax.postWithCallBack(event, function() {
            app.PmsConfiguration.refreshRoomList();
        });
    },
    addInventory : function() {
        var event = thundashop.Ajax.createEvent('','addItemToSelectedRoom', $(this), {
            "id" : $(this).attr('itemid')
        });
        
        thundashop.Ajax.postWithCallBack(event, function() {
            app.PmsConfiguration.refreshRoomList();
        });
    },
    
    changeItemForRoom : function() {
        var event = thundashop.Ajax.createEvent('','setItem', $(this), {
            "item" : $(this).val()
        });
        
        thundashop.Ajax.postWithCallBack(event, function() {
            app.PmsConfiguration.refreshRoomList();
        });
    },
    refreshRoomList : function() {
        var event = thundashop.Ajax.createEvent('','printInventoryForSelectedRoom', $('.PmsConfiguration'), {});
        thundashop.Ajax.postWithCallBack(event, function(data) {
            $('.inventoryaddedlist').html(data);
        });
        var event = thundashop.Ajax.createEvent('','printRoomInventoryList', $('.PmsConfiguration'), {});
        thundashop.Ajax.postWithCallBack(event, function(data) {
            $('.allroominventorylist').html(data);
        });
    },
    removeChannel : function() {
        var channel = $(this).attr('channel');
        thundashop.Ajax.simplePost($(this), 'removeChannel', {
            "channel" : channel
        });
    },
    addnewchannel : function() {
        var channelName = prompt("Id for the new channel");
        if(!channelName) {
            return;
        }
        thundashop.Ajax.simplePost($(this), "addNewChannel", {
            "name" : channelName
        })
    },
    changeEmailType: function() {
        var newType = $('.PmsConfiguration .emailtypeselection').val();
        $('.PmsConfiguration .emailtype').hide();
        $('.PmsConfiguration .emailtype.'+newType).show();
    },
    changeSmsType: function() {
        var newType = $('.PmsConfiguration .smstypeselection').val();
        $('.PmsConfiguration .smstype').hide();
        $('.PmsConfiguration .smstype.'+newType).show();
    },
    changeAdminType: function() {
        var newType = $('.PmsConfiguration .admintypeselection').val();
        $('.PmsConfiguration .admintype').hide();
        $('.PmsConfiguration .admintype.'+newType).show();
    },
    changeview : function() {
        thundashop.common.destroyCKEditors();
        $('.pmsbutton.active').removeClass('active');
        $(this).addClass('active');
        $('.notificationpanel').hide();
        var newpanel = $(this).attr('data-panel');
        $('.'+newpanel).show();
        if($(this).attr('data-type') === "new") {
            $('.PmsConfiguration [data-include-for-type="old"]').hide();
        } else {
            $('.PmsConfiguration [data-include-for-type="old"]').show();
        }
        localStorage.setItem("pmsconfigtabselected", newpanel);
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
                    click: app.PmsConfiguration.showSettings
                }
            ]
        };

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
};
app.PmsConfiguration.init();