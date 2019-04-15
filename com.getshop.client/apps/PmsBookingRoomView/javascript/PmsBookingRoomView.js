app.PmsBookingRoomView = {
    init: function() {
        $(document).on('click', '.PmsBookingRoomView .orderpreview .close', this.closePreview);
        $(document).on('change', '.PmsBookingRoomView .changediscountcode', this.changeCouponCode);
        $(document).on('change', '.PmsBookingRoomView .changesegment', this.changeSegment);
        $(document).on('click', '.PmsBookingRoomView .opengroup', this.openGroup);
        $(document).on('click', '.PmsBookingRoomView .adduserdescription', this.changeUserDescription);
        $(document).on('click', '.PmsBookingRoomView .addordernote', this.addOrderNote);
        $(document).on('click', '.PmsBookingRoomView .addaddonsbutton', this.loadAddAddonsArea);
        $(document).on('click', '.PmsBookingRoomView .orderpreview .closebutton', this.closePreview);
        $(document).on('click', '.PmsBookingRoomView .orderpreview .continue', this.continueToBooking);
        $(document).on('click', '.PmsBookingRoomView .menuarea .menuentry', this.menuClicked);
        $(document).on('click', '.PmsBookingRoomView .listpaymentbutton', this.listPaymentButton);
        $(document).on('click', '.PmsBookingRoomView .displaylogbutton', this.listLogEntries);
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
        $(document).on('click', '.PmsBookingRoomView .showroomstoselect', this.showRoomsToSelect);
        $(document).on('click', '.PmsBookingRoomView .checkallitems', this.checkallitems);
        $(document).on('click', '.PmsBookingRoomView .continuepaymentprocess', this.continuePaymentProcess);
        $(document).on('click', '.PmsBookingRoomView .createafterstaybtn', this.createOrderAfterStay);
        $(document).on('click', '.PmsBookingRoomView .checkinguest', this.checkInCheckOutGuest);
        $(document).on('click', '.PmsBookingRoomView .checkoutguest', this.checkInCheckOutGuest);
        $(document).on('click', '.PmsBookingRoomView .saveaddons', this.saveAddonsOnRoom);
        $(document).on('click', '.PmsBookingRoomView .expandmessage', this.expandmessage);
        $(document).on('click', '.PmsBookingRoomView .debugaction', this.postDebugMessage);
        $(document).on('click', '.PmsBookingRoomView .row_payment_status_line .toggle_action_menu', this.toggleActionMenu);
        $(document).on('click', '.PmsBookingRoomView .markaspaidwindow .innner_area .closebutton', this.hideMarkAsPaidWindow)
        $(document).on('click', '.PmsBookingRoomView .ordermenu .showmarkorderaspaid', this.showPaymentWindow)
        $(document).on('click', '.PmsBookingRoomView .collapsable_shadowbox .colheader', this.toggleCollapse)
        $(document).on('change', '.PmsBookingRoomView .filterbymonth', this.filterOrdersByMonth)
        $(document).on('click', '.PmsBookingRoomView .showOrderSummary', this.showOrderSummary);
        $(document).on('click', '.PmsBookingRoomView .connectGuestToConference', this.showAddConferencePanel);
        $(document).on('click','.PmsBookingRoomView .attachguesttoevent', app.PmsBookingRoomView.attachGuestToConference);
        $(document).on('click','.PmsBookingRoomView .removeConferenceFromGuest', app.PmsBookingRoomView.removeGuestToConference);
    },
    removeGuestToConference : function(res) {
        var guestid = $(this).closest('.guest_row').attr('guestid');
        var area = $(this).closest('.guestconferenceinformation');
        var eventid = $(this).attr('eventid')
        var event = thundashop.Ajax.createEvent('','removeConferenceFromGuest',$(this), {
            "guestid" : guestid,
            "eventid" : eventid
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            console.log(res);
            area.html(res);
        });
    },
    attachGuestToConference : function() {
        var eventid = $(this).attr('eventid');
        var guestid = $(this).closest('.guest_row').attr('guestid');
        var area = $(this).closest('.guest_row').find('.guestconferenceinformation');
        var event = thundashop.Ajax.createEvent('','attachGuestToEvent',$(this), {
            "eventid" : eventid,
            "guestid" : guestid
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            area.html(res);
            $('.addguesttoconferencepanel').hide();
        });
    },
    showAddConferencePanel : function() {
        var panel = $(this).closest('.guest_row').find('.addguesttoconferencepanel');
        var event = thundashop.Ajax.createEvent('','loadConferenceEvents',$(this), {
            "guestid" : $(this).closest('.guest_row').attr('guestid')
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            panel.html(res);
            panel.show();
        });
    },
    showOrderSummary: function() {
        thundashop.framework.loadAppInOverLay("af54ced1-4e2d-444f-b733-897c1542b5a8", "3", {
            orderId: $(this).attr('orderid'),
            state: 'paymentoverview'
        });
    },
    
    filterOrdersByMonth: function() {
        var date = $(this).val();
        var roomId = $(this).closest('.app').find('input[gsname="roomId"]').val();;
        
        $('.PmsBookingRoomView .row[date]').each(function() {
            if ($(this).attr('date').indexOf(date) !== -1) {
                $(this).removeClass('hidden');
            } else {
                $(this).addClass('hidden');
            }
        });
        
        sessionStorage.setItem('last_selected_orders_detailed_'+roomId, date);
    },
    
    selectLastOrdersDetailedMonth: function(roomId) {
        var lastSelected = sessionStorage.getItem('last_selected_orders_detailed_'+roomId);
        if (!lastSelected) {
            $(".PmsBookingRoomView .filterbymonth option:last").attr("selected", "selected");
        } else {
            $(".PmsBookingRoomView .filterbymonth option[value='"+lastSelected+"']").attr("selected", "selected");
        }
        
        $(".PmsBookingRoomView .filterbymonth").change();
    },
    
    showChangeDatePanel : function(type) {
        $('.changedatesinformation').hide();
        $('.changedatespanel').show();
        if(type === "checkin") {
            $('.changedatespanel .startdate').focus();
        }
        if(type === "checkout") {
            $('.changedatespanel .enddate').focus();
        }
    },
    toggleCollapse: function(e) {
        if ($(e.target).hasClass('shop_button')) {
            return;
        }
        var collapse = $(this).closest('.collapsable_shadowbox');
        var content = collapse.find('.collapsable_content');
        if (content.is(':visible')) {
            collapse.find('.collapser.open').show();
            collapse.find('.collapser.closed').hide();
            content.slideUp();
        } else {
            collapse.find('.collapser.open').hide();
            collapse.find('.collapser.closed').show();
            content.slideDown();
        }
    },
    
    markAsPaidCompleted: function() {
        $(this).closest('.app').find('.markaspaidwindow').fadeOut();
        var refreshing = app.PmsBookingRoomView.refresh(true);
        refreshing.done(function() {
            $('.menuentry[tab="orderstab"]').click();
        });
    },
    
    showPaymentWindow: function() {
        var data = {
            orderid : $(this).attr('orderid')
        };
        
        var event = thundashop.Ajax.createEvent(null, "showMarkAsPaidWindow", this, data);
        event['synchron'] = true;
        
        $(this).closest('.app').find('.markaspaidwindow').fadeIn();
        var me = $(this);
        
        thundashop.Ajax.postWithCallBack(event, function(res) {
            me.closest('.app').find('.markaspaidwindow .inner_work_area').html(res);
        });
    },
    
    hideMarkAsPaidWindow: function() {
        $(this).closest('.app').find('.markaspaidwindow').fadeOut();
    },
    
    toggleActionMenu: function() {
        var menu = $(this).closest('.col').find('.ordermenu');
        var visible = $(menu).is(':visible');
        
        $('.ordermenu').hide();
        if (!visible) {
            menu.show();
        }
    },
    
    // Needs to be here for the Verifone Payment Process
    nullFunction: function() {
        
    },
    
    fetchVerifoneTerminalMessages: function() {
        var event = thundashop.Ajax.createEvent(null, "getPaymentProcessMessage", $('.PmsBookingRoomView'), {});
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(res) {
            $('.PmsBookingRoomView .verifonestatus').html(res);
        }, null, true, true);
    },
    
    fetchTerminalMessages: function() {
        var event = thundashop.Ajax.createEvent(null, "getPaymentOrderProcessMessage", $('.PmsBookingRoomView'), {});
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(res) {
            $('.PmsBookingRoomView .verifonestatus').html(res);
        }, null, true, true);
    },
    
    postDebugMessage: function() {
        var data = {
            action : $(this).attr('action')
        };
        
        var event = thundashop.Ajax.createEvent(null, "handleDebugActions", this, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(res) {
            console.log("done");
        }, [], true);
    },
    
    changeUserDescription : function() {
        var text = $('.PmsBookingRoomView .userdescription').text();
        var comment = prompt("Please enter a comment", text);
        
        if (!comment) {
            return;
        }

        $('.PmsBookingRoomView .userdescription').html(comment);
        var event = thundashop.Ajax.createEvent('','saveUserDescription',$(this), {
            "comment" : comment
        });
        thundashop.Ajax.post(event);
    },
    addOrderNote : function() {
        var text = $('.PmsBookingRoomView .userdescription').text();
        var comment = prompt("Please enter a comment", text);
        if (!comment) {
            return;
        }
        $('.PmsBookingRoomView .ordernote').html(comment);
        var event = thundashop.Ajax.createEvent('','saveOrderNote',$(this), {
            "comment" : comment
        });
        thundashop.Ajax.post(event);
    },
    changeCouponCode : function() {
        var newCode = $(this).val();
        var event = thundashop.Ajax.createEvent('','changeDiscountCode',$(this), {
            "code" : newCode
        });
        thundashop.Ajax.postWithCallBack(event, function() {});
    },
    changeSegment : function() {
        var newSegment = $(this).val();
        var event = thundashop.Ajax.createEvent('','segmentDiscountCode',$(this), {
            "segment" : newSegment
        });
        thundashop.Ajax.postWithCallBack(event, function() {});
    },
    updateAddNewRoomDropDown : function() {
        var event = thundashop.Ajax.createEvent('','reloadAvailableRooms', $('.PmsBookingRoomView'), {
            "start" : $('.PmsBookingRoomView .addanotherroompopup input[gsname="start"]').val(),
            "end" : $('.PmsBookingRoomView .addanotherroompopup input[gsname="end"]').val()
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.PmsBookingRoomView .addanotherroompopup select[gsname="type"]').html(res);
        });
    },
    loadAddAddonsArea : function() {
        var event = thundashop.Ajax.createEvent('','printAddAddonsArea',$(this), {});
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.PmsBookingRoomView .addAddonsArea').hide();
            $('.addAddonsAreaInner').html(res);
            $('.PmsBookingRoomView .addAddonsArea').fadeIn();
            if($('.getshoptableoverlaybody').length > 0) {
                $('.getshoptableoverlaybody').css('height',$('.getshoptableoverlaybody').get(0).scrollHeight+50);
            }
        });
    },
    listPaymentButton : function() {
        var event = thundashop.Ajax.createEvent("", "removeGroupList", $(this), {});
        thundashop.Ajax.postWithCallBack(event, function() {
            var refreshing = app.PmsBookingRoomView.refresh(true);
            refreshing.done(function() {
                $('.menuentry[tab="orderstab"]').click();
            });
        });
    },
    listLogEntries : function() {
        var event = thundashop.Ajax.createEvent("", "removeGroupList", $(this), {});
        thundashop.Ajax.postWithCallBack(event, function() {
            var refreshing = app.PmsBookingRoomView.refresh(true);
            refreshing.done(function() {
                $('.menuentry[tab="log"]').click();
            });
        });
    },
    openGroup : function() {
        thundashop.common.closeModal();
        var path = location.pathname;
        if(path == "/bookings.html") {
            path = "/";
        }
        window.location.href=path+'?page=groupbooking&bookingId=' + $(this).attr('bookingid');
    },
    expandmessage : function() {
        $(this).closest('.messagelogentry').find('.messagelogtext').css('max-height','100%');
    },
    
    saveAddonsOnRoom : function() {
        var toPost = {};
        $('.addonitemrow').each(function() {
            var addonId = $(this).attr('addonid');
            var row = thundashop.framework.createGsArgs($(this));
            toPost[addonId] = row;
        });
        
        var event = thundashop.Ajax.createEvent('','saveAddonItems',$(this),toPost);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            thundashop.common.Alert('Saved');
            app.PmsBookingRoomView.refresh(true);
        });
    },
    
    checkInCheckOutGuest : function() {
        var roomId = $(this).closest('.itemview').attr('roomid');
        var event = thundashop.Ajax.createEvent('','checkinoutguest', $(this), {
           roomId : roomId 
        });
        thundashop.Ajax.post(event);
    },
    
    createOrderAfterStay : function() {
        var event = thundashop.Ajax.createEvent('','toggleCreateAfterStay',$(this), {});
        thundashop.Ajax.postWithCallBack(event, function() {
            app.PmsBookingRoomView.refresh(true);
        });
    },
    continuePaymentProcess : function() {
        var form = $('.paymentprocess');
        var args = thundashop.framework.createGsArgs(form);
        args['multipleadd'] = true;

        var event = thundashop.Ajax.createEvent(null, "transferSelectedToCart", this, args);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function (res) {
            var data = {
                orderUnderConstrcutionId : res
            }
            $('.grouppaymentprocess').hide();
            app.PmsBookingRoomView.loadCheckout();
        });
    },
    checkallitems : function() {
        var checked = $(this).is(':checked');
        if(checked) {
            $('.itemtopay').attr('checked','checked');
        } else {
            $('.itemtopay').attr('checked',null);
        }
    },
    showRoomsToSelect: function() {
        $('.PmsBookingRoomView .roomstoselect').html('<div style="text-align:center; padding: 20px; font-size: 40px;"><i class="fa fa-spin fa-spinner"></i></div>');
        $('.PmsBookingRoomView .roomstoselect').show();
        $('.PmsBookingRoomView .outerstay').hide();
        
        var data = {}
        var event = thundashop.Ajax.createEvent(null, 'showItemView', $('.PmsBookingRoomView'), data);
        event['synchron'] = true;
        
        
        thundashop.Ajax.post(event, function(res) {
            $('.PmsBookingRoomView .roomstoselect').html(res);
        })
    },
    
    dosplitchange : function() {
        var event = thundashop.Ajax.createEvent('','setSplitChange',$(this), {
            "split" : $(this).is(':checked'),
            "roomid" : $(this).attr('roomid')
        });
        thundashop.Ajax.postWithCallBack(event, function() {
            var setSplit = app.PmsBookingRoomView.refresh(true);
            setSplit.done(function() {
                app.PmsBookingRoomView.showRoomsToSelect();
            });
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
        args.language = $('#roomlanguage').val();
        args.countrycode = $('#countrycodeselection').val();
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
        thundashop.Ajax.post(event);
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
        var box = $(this).closest('.masseditpricesbox');
        var type = box.find('.masseditpricestay').val();
        var value =  box.find('.massupdatepricevalue').val();

        var event = thundashop.Ajax.createEvent('','updatePriceMatrixWithPeriodePrices', box, {
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
        
        if ($(this).closest('.room_view_outer').attr('usenewpayment') == "1") {
            thundashop.framework.loadAppInOverLay("af54ced1-4e2d-444f-b733-897c1542b5a8", "3", { pmsBookingRoomId : [$(this).attr('roomid')], state: 'clear'}); 
            return;
        }
        
        if($(this).attr('isgroup') === "yes") {
            var btn = $(this);
            var paymentpanel = $('.grouppaymentprocess');
            var event = thundashop.Ajax.createEvent('','loadGroupPayment',$(this), {
                "roomid" : btn.attr('roomid')
            });
            thundashop.Ajax.postWithCallBack(event, function(res) {
                paymentpanel.html(res);
                paymentpanel.show();
                if($('.getshoptableoverlaybody').length > 0) {
                    $('.getshoptableoverlaybody').css('height',$('.getshoptableoverlaybody').get(0).scrollHeight+50);
                }
            });
        } else {
            var event = thundashop.Ajax.createEvent(null, "transferSelectedToCart", this, {});
            event['synchron'] = true;

            thundashop.Ajax.post(event, function (res) {
                app.PmsBookingRoomView.loadCheckout();
            });
        }
    },
    
    loadCheckout : function() {
        var event = thundashop.Ajax.createEvent('','loadCheckout',$('.PmsBookingRoomView'), {});
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $(".pmsroomcheckoutview.checkoutview").hide();
            $('.menuentry[tab="orderstab"]').click();
            $('.pmsroomcheckoutview .checkoutviewinner').html(res);
            $(".pmsroomcheckoutview.checkoutview").fadeIn();
            if($('.getshoptableoverlaybody').length > 0) {
                $('.getshoptableoverlaybody').css('height',$('.getshoptableoverlaybody').get(0).scrollHeight+50);
            }
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
        var addonIds = [];
        $(".fa-check-square").each(function() {
            addonIds.push($(this).attr('addonid'));
        });
        var event = thundashop.Ajax.createEvent('','removeSelectedAddons',$(this), {
            addonIds : addonIds
        });
        
        thundashop.Ajax.postWithCallBack(event, function() {
            app.PmsBookingRoomView.refresh();
        });
    },
    refresh : function(avoidSpinner) {
        var view = null;
        $('.PmsBookingRoomView').each(function() {
            if($(this).is(':visible')) {
                view = $(this);
            }
        });
        
        if (!view)
            return;
        
        var event = thundashop.Ajax.createEvent('','reloadApp',view, {});
        if(!avoidSpinner) {
//            view.html('<div style="text-align:center; padding: 20px; font-size: 40px;"><i class="fa fa-spin fa-spinner"></i></div>');
        }
        var deferred = $.Deferred();
        thundashop.Ajax.postWithCallBack(event, function(res) {
            view.html(res);
            if($('.getshoptableoverlaybody').length > 0) {
                $('.getshoptableoverlaybody').css('height','auto');
            }
            deferred.resolve();
        });
        return deferred;
    },
    toggleRemoveAddonCheckBox : function() {
        if($(this).hasClass('forGroup')) {
            var productId = $(this).closest('.row').attr('productid');
            if($(this).hasClass('fa-square-o')) {
                $(".toggleRemoveAddonCheckBox[productid='"+productId+"']").removeClass('fa-square-o');
                $(".toggleRemoveAddonCheckBox[productid='"+productId+"']").addClass('fa-check-square');
                $(this).removeClass('fa-square-o');
                $(this).addClass('fa-check-square');
            } else {
                $(".toggleRemoveAddonCheckBox[productid='"+productId+"']").addClass('fa-square-o');
                $(".toggleRemoveAddonCheckBox[productid='"+productId+"']").removeClass('fa-check-square');
                $(this).addClass('fa-square-o');
                $(this).removeClass('fa-check-square');
            }
        } else {
            if($(this).hasClass('fa-square-o')) {
                $(this).removeClass('fa-square-o');
                $(this).addClass('fa-check-square');
            } else {
                $(this).addClass('fa-square-o');
                $(this).removeClass('fa-check-square');
            }
        }
        $('.removeselectedaddons').effect( "bounce", { times: 3 } );
    },
    
    accessCodeTabUpdated: function(res) {
        $('.PmsBookingRoomView .guestinformation[tab="accesscode"]').html(res);
    },
    
    pgaTabUpdated: function(res) {
        $('.PmsBookingRoomView .guestinformation[tab="pga"]').html(res);
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
        
        var event = thundashop.Ajax.createEvent('','createGuest',$(this), {});
        thundashop.Ajax.postWithCallBack(event, function(res) {
            cloned.attr('guestid', res);
            cloned.find('.guestidfield').val(res);
        });
        
        $('.guestinforows').append(cloned);
    },
    
    removeGuest: function(e) {
        $(this).closest('.guest_row').remove();
        app.PmsBookingRoomView.updateGuestInformation();
    },
   
    updateAvailability: function(onlyPrices) {
        var view = null;
        $('.PmsBookingRoomView').each(function() {
            if($(this).is(':visible')) {
                view = $(this);
            }
        });
        if(!view) {
            alert('wtf');
        }
        view.find('.itemview').addClass('update_in_progress');
        
        var typeid = view.find('.gs_selected').closest('.movetotype').attr('typeid');
        var itemId = view.find('.gs_selected').attr('itemId');
        
        var data = {
            bookingid: view.find('.itemview').attr('bookingid'),
            start : view.find('.startdate').val() + " " + view.find('.starttime').val(),
            end : view.find('.enddate').val() + " " + view.find('.endtime').val(),
            roomId: view.find('.itemview').attr('roomid'),
        };
        
        data.typeId = typeid;
        data.itemId = itemId;
        
        var event = thundashop.Ajax.createEvent(null, "updateAvialablity", view, data);
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