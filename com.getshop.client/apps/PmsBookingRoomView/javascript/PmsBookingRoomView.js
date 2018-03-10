app.PmsBookingRoomView = {
    init: function() {
        $(document).on('click', '.PmsBookingRoomView .orderpreview .close', this.closePreview);
        $(document).on('click', '.PmsBookingRoomView .orderpreview .closebutton', this.closePreview);
        $(document).on('click', '.PmsBookingRoomView .orderpreview .continue', this.continueToBooking);
        $(document).on('click', '.PmsBookingRoomView .menuarea .menuentry', this.menuClicked);
        $(document).on('click', '.PmsBookingRoomView .order_tab_menu', this.menuOrdersClicked);
        $(document).on('click', '.PmsBookingRoomView .editaddon', this.editAddonView);
        $(document).on('click', '.PmsBookingRoomView .bookinginformation .remove_guest', this.removeGuest);
        $(document).on('click', '.PmsBookingRoomView .bookinginformation .add_more_guests', this.addGuest);
        $(document).on('click', '.PmsBookingRoomView .payment_option_choice', this.paymentSelected);
        $(document).on('click', '.PmsBookingRoomView .pagenumber', this.changeLogIndex);
        $(document).on('click', '.PmsBookingRoomView .canUse', this.addSelectedClass);
        $(document).on('click', '.PmsBookingRoomView .removeselectedaddons', this.removeSelectedAddons);
        $(document).on('click', '.PmsBookingRoomView .addonsArea .toggleRemoveAddonCheckBox', this.toggleRemoveAddonCheckBox);
        $(document).on('click', '.PmsBookingRoomView .doeditaddonupdate', this.doEditAddonUpdate);
        $(document).on('click', '.PmsBookingRoomView .addselecteditemstocart', this.addSelectedItemsToCart);
        $(document).on('keyup', '.PmsBookingRoomView .unitprice_changed', this.unitPriceChanged);
        $(document).on('click', '.PmsBookingRoomView .topbuttons .toggleComment', this.toggleComment);
        $(document).on('click', '.PmsBookingRoomView .topbuttons .saveInvoiceNote', this.saveInvoiceNote);
        $(document).on('click', '.PmsBookingRoomView .massupdateprices', this.doMassUpdatePrices);
        $(document).on('click', '.PmsBookingRoomView .seechangesbutton', this.seeChangesOnBooking);
        $(document).on('click', '.PmsBookingRoomView .savetmproom', this.saveChangesOnRoom);
        $(document).on('click', '.PmsBookingRoomView .dosplitchange', this.dosplitchange);
        $(document).on('click', '.PmsBookingRoomView .tryundeleteroom', this.tryundeleteroom);
        $(document).on('click', '.PmsBookingRoomView .saveguestinformation', this.saveGuestInformation);
        $(document).on('click', '.PmsBookingRoomView .addcommentbutton', this.addcommentbutton);
        $(document).on('click', '.PmsBookingRoomView .deletecomment', this.deletecomment);
    },
    dosplitchange : function() {
        var event = thundashop.Ajax.createEvent('','setSplitChange',$(this), {
            "split" : $(this).is(':checked'),
            "roomid" : $(this).attr('roomid')
        });
        thundashop.Ajax.postWithCallBack(event, function() {
            app.PmsBookingRoomView.refresh(true);
        });
    },
    deletecomment : function() {
        var comment = $('.commenttext').val();
        thundashop.Ajax.simplePost($(this), 'deleteComment', {
            "time" : $(this).attr('time')
        });
    },
    addcommentbutton : function() {
        var comment = $('.commenttext').val();
        thundashop.Ajax.simplePost($(this), 'addComment', {
            "comment" : comment
        });
    },
    tryundeleteroom : function() {
        thundashop.Ajax.simplePost($(this),'deleteRoom', {});
    },
    saveGuestInformation : function() {
        var args = thundashop.framework.createGsArgs($('.guestinforows'));
        thundashop.Ajax.simplePost($(this), 'saveGuestInformation', args);
    },
    menuOrdersClicked: function() {
        var tab  = $(this).attr('orders_sub_tab');
        var roomId = $(this).closest('.bookingoverview_content_row').find('[gsname="roomId"]').val();
        
        app.PmsBookingRoomView.activateOrdersSubTab(tab, roomId);
        sessionStorage.setItem("selected_sub_order_tab_"+roomId, tab);
    },
    
    activateSelectedTabSubOrders: function(roomId) {
        var tab = sessionStorage.getItem("selected_sub_order_tab_"+roomId);
        
        if (!tab) {
            tab = "simple";
        } 
        
        app.PmsBookingRoomView.activateOrdersSubTab(tab, roomId);
    },
    
    activateOrdersSubTab: function(tab, roomId) {
        var div = $('.PmsBookingRoomView .bookingoverview_content_row [gsname="roomId"][value="'+roomId+'"]:first').closest('.PmsBookingRoomView');
        div.find('.tabcontent').removeClass('active');
        div.find('.tabcontent[orders_sub_tab="'+tab+'"]').addClass('active');
    },
    
    saveChangesOnRoom : function() {
        var event = thundashop.Ajax.createEvent('','saveRoom', $(this),{
            "roomid" : $(this).attr('roomid')
        });
        thundashop.Ajax.postWithCallBack(event, function(roomId) {
            $('.menuarea').attr('roomId', roomId)
            app.PmsBookingRoomView.refresh(true);
        });
    },

    seeChangesOnBooking : function() {
        if($('.changespanel').is(':visible')) {
            $('.changespanel').slideUp();
            return;
        }
        $('.changespanel').hide();
        var event = thundashop.Ajax.createEvent('','loadChangesPanel', $(this), {});
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.changespanel').html(res);
            $('.changespanel').slideDown();
        });
    },
    
    doMassUpdatePrices : function() {
        var type = $('.masseditpricestay').val();
        var value = $('.massupdatepricevalue').val();

        var event = thundashop.Ajax.createEvent('','updatePriceMatrixWithPeriodePrices', $(this), {
            "periodePriceType" : type,
            "periodePrice" : value
        });
        thundashop.Ajax.postWithCallBack(event, function(res){
            app.PmsBookingRoomView.refresh(true);
        });            
        
        $('.massupdatepricevalue').val('');
    },
    saveInvoiceNote: function() {
        var data = {
            invoicenote : $(this).closest('.paymentcomment').find('textarea').val()
        };
        
        var event= thundashop.Ajax.createEvent(null, "invoiceNoteChanged", this, data);
        event['synchron'] = true;
        var me = $(this);
        
        thundashop.Ajax.post(event, function() {
            me.closest('.topbuttons').find('.paymentcomment').hide();
        });
    },
    
    toggleComment: function() {
        var box = $(this).closest('.topbuttons').find('.paymentcomment');
        if (box.is(':visible')) {
            box.hide();
        } else {
            box.show();
        }
    },
    
    addSelectedItemsToCart: function() {
        var event = thundashop.Ajax.createEvent(null, "transferSelectedToCart", this, {});
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function (res) {
            var data = {
                orderUnderConstrcutionId : res
            }

            if (!$('[area="gs_modul_cart"]').is(':visible')) {
                thundashop.framework.toggleRightWidgetPanel('gs_modul_cart', data);
            } else {
                thundashop.framework.refreshRightWidget('gs_modul_cart', data)
            }
            
            app.PmsBookingRoomView.refreshCurrentTab();
        });
    },
    
    doEditAddonUpdate : function() {
        var panel = $(this).closest('.editaddonpanel');
        var args = thundashop.framework.createGsArgs(panel);
        args.type = $(this).attr('gstype');
        var event = thundashop.Ajax.createEvent('','updateAddons', $(this), args);
        thundashop.Ajax.postWithCallBack(event, function(res){
            app.PmsBookingRoomView.refresh();
        });
    },
    
    editAddonView : function() {
        var btn = $(this);
        var event = thundashop.Ajax.createEvent('','loadEditEvent', $(this), {
            "productId" : $(this).attr('productId')
        }); 
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.editaddonpanel').show();
            $('.editaddonpanel').html(res);
            $('.editaddonpanel').css('left', btn.position().left-$('.editaddonpanel').width());
            $('.editaddonpanel').css('top', btn.position().top+30);
        });
    },
    removeSelectedAddons : function() {
        var productIds = [];
        $(".fa-check-square").each(function() {
            productIds.push($(this).closest('.row').attr('productId'));
        });
        var event = thundashop.Ajax.createEvent('','removeSelectedAddons',$(this), {
            productIds : productIds
        });
        
        thundashop.Ajax.postWithCallBack(event, function() {
            app.PmsBookingRoomView.refresh();
        });
    },
    refresh : function(avoidSpinner) {
        var event = thundashop.Ajax.createEvent('','reloadApp',$('.PmsBookingRoomView'), {});
        if(!avoidSpinner) {
            $('.PmsBookingRoomView').html('<div style="text-align:center; padding: 20px; font-size: 40px;"><i class="fa fa-spin fa-spinner"></i></div>');
        }
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.PmsBookingRoomView').html(res);
        });
    },
    toggleRemoveAddonCheckBox : function() {
        if($(this).hasClass('fa-square-o')) {
            $(this).removeClass('fa-square-o');
            $(this).addClass('fa-check-square');
        } else {
            $(this).addClass('fa-square-o');
            $(this).removeClass('fa-check-square');
        }
        $('.removeselectedaddons').effect( "bounce", { times: 3 } );
    },
    
    accessCodeTabUpdated: function(res) {
        $('.PmsBookingRoomView .guestinformation[tab="accesscodes"]').html(res);
    },
    
    unitPriceChanged: function(e) {
        var data = thundashop.framework.createGsArgs($('.pricerows'));
        
        var event = thundashop.Ajax.createEvent('','updateDayPrices',$(this),data);
        thundashop.Ajax.postWithCallBack(event, function() {});
        
        if(typeof(app.PmsBookingRoomView.waitForReload) !== "undefined") {
            clearTimeout(app.PmsBookingRoomView.waitForReload);
        }
        
        app.PmsBookingRoomView.waitForReload = setTimeout(function() {
            $('.warningchangesonroom').show();
        }, "500");
    },
    
    addSelectedClass: function() {
        $('.PmsBookingRoomView .gs_selected').removeClass('gs_selected');
        $(this).addClass('gs_selected');
        app.PmsBookingRoomView.updateAvailability(true);
    },
    
    changeLogIndex: function() {
        var scope = $('.PmsBookingRoomView .datarow.active');
        var data = {
            selectedTab : "log",
            id: scope.find('[gsname="id"]').val(),
            roomId: scope.find('[gsname="roomId"]').val(),
            index : $(this).attr('index')
        }
        
        var event = thundashop.Ajax.createEvent(null, "renderTabContent", this, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, app.PmsBookingRoomView.tabChanged, data);
    },
    
    paymentSelected: function() {
        var appId = $(this).attr('payment');
        $('.payment_option_choice').removeClass('active');
        $('.payment_content').hide();
        $('.payment_content[payment="'+appId+'"]').show();
        $(this).closest('.payment_option_choice').addClass('active');
    },
    
    ordersTabChanged: function(res) {
        $('.PmsBookingRoomView .guestinformation[tab="orders"]').html(res);
        app.PmsSearchPaymentProcess.updatePaymentProcess();
        return false;
    },
        
    addGuest: function() {
        var cloned = $(this).closest('.bookinginformation').find('.guest_row.row_template').clone();
        
        var highest = 0;
        $('.guest_row').each(function() {
            var counter = parseInt($(this).attr('counter'));
            if (counter > highest) {
                highest = counter;
            }
        });
        
        highest++;
        cloned.removeClass('row_template');
        cloned.attr('counter', highest);
        cloned.css('display', 'block');
        
        var found = cloned.find('[temp_gsname="guestinfo_name"]'); found.removeAttr('temp_gsname'); found.attr('gsname', 'guestinfo_'+highest+'_name');
        var found = cloned.find('[temp_gsname="guestinfo_email"]'); found.removeAttr('temp_gsname'); found.attr('gsname', 'guestinfo_'+highest+'_email');
        var found = cloned.find('[temp_gsname="guestinfo_prefix"]'); found.removeAttr('temp_gsname'); found.attr('gsname', 'guestinfo_'+highest+'_prefix');
        var found = cloned.find('[temp_gsname="guestinfo_phone"]'); found.removeAttr('temp_gsname'); found.attr('gsname', 'guestinfo_'+highest+'_phone');
        var found = cloned.find('[temp_gsname="guestinfo_guestid"]'); found.removeAttr('temp_gsname'); found.attr('gsname', 'guestinfo_'+highest+'_guestid');
        
        $('.guestinforows').append(cloned);
    },
    
    removeGuest: function(e) {
        $(this).closest('.guest_row').remove();
        app.PmsBookingRoomView.updateGuestInformation();
    },
   
    updateAvailability: function(onlyPrices) {
        $('.PmsBookingRoomView .itemview').addClass('update_in_progress');
        
        var typeid = $('.PmsBookingRoomView .gs_selected').closest('.movetotype').attr('typeid');
        var itemId = $('.PmsBookingRoomView .gs_selected').attr('itemId');
        
        var data = {
            bookingid: $('.PmsBookingRoomView .itemview').attr('bookingid'),
            start : $('.PmsBookingRoomView .startdate').val() + " " + $('.PmsBookingRoomView .starttime').val(),
            end : $('.PmsBookingRoomView .enddate').val() + " " + $('.PmsBookingRoomView .endtime').val(),
            roomId: $('.PmsBookingRoomView .itemview').attr('roomid'),
        };
        
        data.typeId = typeid;
        data.itemId = itemId;
        
        var event = thundashop.Ajax.createEvent(null, "updateAvialablity", $('.PmsBookingRoomView'), data);
        thundashop.Ajax.postWithCallBack(event, function() {
            app.PmsBookingRoomView.refresh(true);
        });
    },
    
    contentSaved: function(res, target) {
        var scope = $(target).closest('.datarow_extended_content');
        scope.find('.bookingoverview_content_row').replaceWith(res);
        app.PmsBookingRoomView.checkForUnsettledAmount(scope);
        
        var event = thundashop.Ajax.createEvent(null, "refresh", target, {});
        thundashop.Ajax.post(event, function() {
            thundashop.common.closeModal();
        });
    },

    checkForUnsettledAmount: function(scope) {
        var data = {
            id: scope.find('[gsname="id"]').val(),
            roomId: scope.find('[gsname="roomId"]').val()
        }
        
        var event = thundashop.Ajax.createEvent(null, "hasUnsettledAmount", scope, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(res) {
            if (res === "YES") {
                $('.menuentry[tab="orders"]').addClass('unsettledamount'); 
            } else {
                $('.menuentry[tab="orders"]').removeClass('unsettledamount'); 
            }
        });
    },
   
    menuClicked: function() {
        if (!$(this).closest('.app').hasClass('PmsBookingRoomView')) {
            return;
        }
        
        var tab = $(this).attr('tab');
        var needAllSaved = $(this).attr('needAllSaved');
        
        if (needAllSaved === "true" && !$('.PmsBookingRoomView .datarow.active [gstype="submit"]').hasClass('disabled')) {
//            alert(__f("Please save your changes before you go to payment"));
//            return;
        }
    
        $('.PmsBookingRoomView .menuarea .menuentry').removeClass('active');
        $(this).addClass('active');
        
        $('.PmsBookingRoomView .workarea div[tab]').removeClass('active');
        $('.PmsBookingRoomView .workarea div[tab="'+tab+'"]').addClass('active');
        
        if ($(this).attr('clearTabContent') == "true") {
            var text = __f("Loading");
            $('.PmsBookingRoomView .workarea div[tab="'+tab+'"]').html("<div class='loaderspinner'><i class='fa fa-spin fa-spinner'></i><br/>"+text+"</div>");
        }
        
        var data = {
            selectedTab : tab,
            roomId : $(this).closest('.menuarea').attr('roomId'),
            id :  $(this).closest('.menuarea').attr('bookingEngineId'),    
        }
        
        var event = thundashop.Ajax.createEvent(null, "subMenuChanged", this, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, app.PmsBookingRoomView.tabChanged, data, true, true);
    },
    
    refreshCurrentTab: function() {
        var currentPmsRoomView =$('.PmsBookingRoomView:visible .menuentry.active');
        var tab = $(currentPmsRoomView).attr('tab');
        var data = {
            selectedTab : tab,
            roomId : $(currentPmsRoomView).closest('.menuarea').attr('roomId'),
            id :  $(currentPmsRoomView).closest('.menuarea').attr('bookingEngineId'),    
        }
        
        var event = thundashop.Ajax.createEvent(null, "subMenuChanged", currentPmsRoomView, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, app.PmsBookingRoomView.tabChanged, data, true, true);
    },
    
    tabChanged: function(res, args) {
        if (res) {
            $('.PmsBookingRoomView .workarea div[tab="'+args.selectedTab+'"]').html(res);
        }
    },
    
    continueToBooking: function() {
        thundashop.common.goToPage("checkout&changeGetShopModule=salespoint");
    },
    
    closePreview: function() {
        $('.PmsBookingRoomView .orderpreview').fadeOut();
    }
}

app.PmsBookingRoomView.init();