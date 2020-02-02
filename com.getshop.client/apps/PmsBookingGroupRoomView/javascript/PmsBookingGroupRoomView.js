lastScrollTopPosition = null;

app.PmsBookingGroupRoomView = {
    timeoutsearchconference : null,
    init : function() {
        $(document).on('click', '.PmsBookingGroupRoomView .changeRoomByEvent', this.changeRoom);
        $(document).on('click', '.PmsBookingGroupRoomView .changeEventOnConference', this.changeConference);
        $(document).on('click', '.PmsBookingGroupRoomView .menubutton', this.changeSubType);
        $(document).on('click', '.PmsBookingGroupRoomView .saveguestinformation', this.saveGuestInformation);
        $(document).on('click', '.PmsBookingGroupRoomView .bookinginformation .remove_guest', this.removeGuest);
        $(document).on('click', '.PmsBookingGroupRoomView .bookinginformation .add_more_guests', this.addGuest);
        $(document).on('click', '.PmsBookingGroupRoomView .selectsameasbooker', this.selectSameAsBooker);
        $(document).on('click', '.PmsBookingGroupRoomView .connectGuestToConference', this.showAddConferencePanel);
        $(document).on('click', ' .PmsBookingGroupRoomView .attachguesttoevent', this.attachGuestToConference);
        $(document).on('click', '.PmsBookingGroupRoomView .removeConferenceFromGuest', this.removeGuestToConference);
        $(document).on('click', '.PmsBookingGroupRoomView .changedates', this.changedates);
        $(document).on('click', '.PmsBookingGroupRoomView .checkinguest', this.checkInCheckOutGuest);
        $(document).on('click', '.PmsBookingGroupRoomView .checkoutguest', this.checkInCheckOutGuest);
        $(document).on('click', '.PmsBookingGroupRoomView .reinstatestay', this.reinstateStay);       
        $(document).on('click', '.PmsBookingGroupRoomView .massupdateprices', this.doMassUpdatePrices);
        $(document).on('keyup', '.PmsBookingGroupRoomView .unitprice_changed', this.showSavePriceButton);
        $(document).on('click', '.PmsBookingGroupRoomView .savePriceButton', this.savePrices);
        $(document).on('click', '.PmsBookingGroupRoomView .showroomstoselect', this.showRoomsToSelect);
        $(document).on('click', '.PmsBookingGroupRoomView .canUse', this.chooseRoomToUse);
        $(document).on('click', '.PmsBookingGroupRoomView .addonsArea .toggleRemoveAddonCheckBox', this.toggleRemoveAddonCheckBox);
        $(document).on('click', '.PmsBookingGroupRoomView .removeselectedaddons', this.removeSelectedAddons);
        $(document).on('click', '.PmsBookingGroupRoomView .addaddonsbutton', this.loadAddAddonsArea);
        $(document).on('click', '.PmsBookingGroupRoomView .saveaddons', this.saveAddonsOnRoom);
        $(document).on('click', '.PmsBookingGroupRoomView .displaypreview', this.displayConfirmationPreview);
        $(document).on('click', '.PmsBookingGroupRoomView .sendconfirmationbutton', this.sendConfirmation);
        $(document).on('click', '.PmsBookingGroupRoomView .startpaymentprocessbtn', this.addSelectedItemsToCart);
        $(document).on('click', '.PmsBookingGroupRoomView .showOrderSummary', this.showOrderSummary);
        $(document).on('click', '.PmsBookingGroupRoomView .row_payment_status_line .toggle_action_menu', this.toggleActionMenu);
        $(document).on('click', '.PmsBookingGroupRoomView .toggleDisabledGuest', this.toggleDisabledGuest);
        $(document).on('click', '.PmsBookingGroupRoomView .doChangeDiscount', this.doChangeCouponCode);
        $(document).on('click', '.PmsBookingGroupRoomView .editcomment', this.startEditComment);
        $(document).on('click', '.PmsBookingGroupRoomView .savecomment', this.saveComment);
        $(document).on('click', '.PmsBookingGroupRoomView .adduserdescription', this.changeUserDescription);
        $(document).on('click', '.PmsBookingGroupRoomView .addordernote', this.addOrderNote);
        $(document).on('click', '.PmsBookingGroupRoomView .opengroup', this.openGroup);
        $(document).on('click', '.PmsBookingGroupRoomView .addsuggestionarrow', this.addSuggestedRow);
        $(document).on('click', '.PmsBookingGroupRoomView .showPreviewFixOrder', this.showPreviewFixOrder);
        $(document).on('click', '.PmsBookingGroupRoomView .addanotherroomtogroup', this.loadAddGroup);
        $(document).on('click', '.PmsBookingGroupRoomView .addanotherevent', this.loadAddEvent);
        $(document).on('click', '.PmsBookingGroupRoomView .openconference', this.toggleConference);
        $(document).on('click', '.PmsBookingGroupRoomView .conference_activities .addExtraActivity', this.addExtraActivity);
        $(document).on('click', '.PmsBookingGroupRoomView .conference_activities .saveActivities', this.saveActivities);
        $(document).on('click', '.PmsBookingGroupRoomView .conference_activities .deleteactivity', this.deleteActivity);
        $(document).on('click', '.PmsBookingGroupRoomView .conference_activities .deleteEvent', this.deleteEvent);
        $(document).on('click', '.PmsBookingGroupRoomView .saveconferencetitle', this.saveEventTitle);
        $(document).on('click', '.PmsBookingGroupRoomView .craeteconference', this.loadCreateConferencePanel);
        
        $(document).on('keyup', '.PmsBookingGroupRoomView [searchtype]', this.searchGuests);
        $(document).on('keyup', '.PmsBookingGroupRoomView .searchconferencetitle', this.searchConference);
        $(document).on('change', '.PmsBookingGroupRoomView .changediscountcode', this.changeCouponCode);
        $(document).on('change', '.PmsBookingGroupRoomView .changesegment', this.changeSegment);
        $(document).on('change', '.PmsBookingGroupRoomView .changechannel', this.changeChannel)
    },
    loadCreateConferencePanel : function() {
        $('.createconferencepanel').slideDown();
    },
    
    searchConference : function() {
        var value = $(this).val();
        var event = thundashop.Ajax.createEvent('','findConference', $(this), {
            "keyword" : value,
            "roomid" : app.PmsBookingGroupRoomView.getRoomId()
        });
        if(app.PmsBookingGroupRoomView.timeoutsearchconference != null) {
            clearTimeout(app.PmsBookingGroupRoomView.timeoutsearchconference);
        }
        app.PmsBookingGroupRoomView.timeoutsearchconference = setTimeout(function() {
            thundashop.Ajax.postWithCallBack(event, function(res) {
                $('.searchconferencearea').html(res);
            });
        }, "500");
    },
    conferenceDeleted : function() {
        app.PmsBookingGroupRoomView.toggleMainArea("rooms");
    },
    conferenceSelected : function(roomId) {
        app.PmsBookingGroupRoomView.toggleMainArea("conference");
        $('.createconferencepanel').fadeOut();
    },
    
    saveEventTitle : function() {
        $('.saveconferencetitlebutton').show();
    },
    deleteEvent: function() {
        var me = this;
        var res = confirm("Are you sure you want to delete this?");
        
        if (res) {
            thundashop.Ajax.simplePost(me, 'deleteEvent', {
                confid : $(this).closest('.outarea').attr('confid'),
                eventid : $(this).attr('eventid')
            });
            
            $(this).closest('.row').remove();
        }
    },
    saveActivities: function() {
        
        var activities = [];
        
        $('.activity').each(function() {
            if ($(this).hasClass('template')) {
                return;
            }
            
            var activity = {
                activityid : $(this).attr('activityid'),
                text: $(this).find('[gsname="text"] input').val(),
                to: $(this).find('[gsname="to"] input').val(),
                from: $(this).find('[gsname="from"] input').val(),
                count: $(this).find('[gsname="count"] input').val(),
                extendedText: $(this).find('[gsname="extendedText"]').val(),
            }
            
            activities.push(activity);
        });
        
        var event = thundashop.Ajax.createEvent('','saveEventActivities',$(this), {
            "activites" : activities,
            "roomid" : app.PmsBookingGroupRoomView.getRoomId(),
            "eventid" : latestOverLayLoadingEvent.data.eventid
        });
        thundashop.Ajax.postWithCallBack(event, function() {
            app.PmsBookingGroupRoomView.refresh()
        });
    },
    deleteActivity: function() {
        $(this).closest('.activity.row').remove();
    },
    
    addExtraActivity: function() {
        var theapp = $(this).closest('.app');
        
        var toAdd = $(theapp.find('.template.activity')[0]).clone()[0];
        $(toAdd).removeClass('template');
        
        theapp.find('.activities').append(toAdd);
    },
    
    toggleConference : function() {
        
        if( $(".flip-card").hasClass('fliptoconference')) {
            app.PmsBookingGroupRoomView.toggleMainArea("rooms");
        } else {
            app.PmsBookingGroupRoomView.toggleMainArea("conference");
        }
    },
    toggleMainArea : function(type) {
        if(type == "rooms") {
            $(".flip-card").removeClass("fliptoconference");
            latestOverLayLoadingEvent.data.mainarea = "rooms";
            latestOverLayLoadingEvent.data.subsection = "guests";
            history.pushState(latestOverLayLoadingEvent, "page 2", "#roomid="+latestOverLayLoadingEvent.data.id + "&mainarea=rooms&subsection=guests");
        } else {
            $(".flip-card").addClass("fliptoconference");
            latestOverLayLoadingEvent.data.mainarea = "conference";
            latestOverLayLoadingEvent.data.subsection = "conference_overview";
            history.pushState(latestOverLayLoadingEvent, "page 2", "#roomid="+latestOverLayLoadingEvent.data.id + "&mainarea=conference&subsection=conference_overview");
        }
        setTimeout(function() {
            app.PmsBookingGroupRoomView.refresh();
        }, "1000");
    },
    loadAddGroup : function() {
        $('.addanotherroompopup').show();
        var event = thundashop.Ajax.createEvent('','loadCategoryAvailability',$(this), {
            "start" : $(this).attr('start'),
            "end" : $(this).attr('end'),
            "roomid" : app.PmsBookingGroupRoomView.getRoomId()
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.addanotherroompopup').html(res);
        });
    },
    eventDeleted : function() {
        latestOverLayLoadingEvent.data.eventid = "";
        app.PmsBookingGroupRoomView.refresh();
    },
    loadAddEvent : function() {
        $('.addanotherroompopup').show();
        var event = thundashop.Ajax.createEvent('','loadAddEvent',$(this), {
            "roomid" : app.PmsBookingGroupRoomView.getRoomId(),
            "eventid" : app.PmsBookingGroupRoomView.getEventId()
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.addanotherroompopup').html(res);
        });
    },
    getEventId : function() {
        return latestOverLayLoadingEvent.data.eventid;
    },
    showPreviewFixOrder: function() {
        thundashop.framework.loadAppInOverLay("af54ced1-4e2d-444f-b733-897c1542b5a8", "3", {
            orderId: $(this).attr('orderid'),
            bookingid : $(this).attr('bookingid'),
            state: 'prewviewfixorder'
        });
    },
    
    addSuggestedRow : function() {
        var row = $(this).closest('.guest_row');
        var suggestionrow = $(this).closest('.guestsuggestionrow');
        var counter = row.attr('counter');
        row.find('[gsname="guestinfo_'+counter+'_name"]').val(suggestionrow.find('.guestsuggestionname').text());
        row.find('[gsname="guestinfo_'+counter+'_email"]').val(suggestionrow.find('.guestsuggestionemail').text());
        row.find('[gsname="guestinfo_'+counter+'_prefix"]').val(suggestionrow.find('.guestsuggestionphoneprefix').text());
        row.find('[gsname="guestinfo_'+counter+'_phone"]').val(suggestionrow.find('.guestsuggestionphonenumber').text());
        $(this).closest('.searchsuggestions').html('');
    },
    searchGuests : function() {
        if(typeof(app.PmsBookingRoomView.searchguesttimeout) !== "undefined") {
            clearTimeout(app.PmsBookingRoomView.searchguesttimeout);
        }
        
        var row = $(this).closest('.guest_row');
        var keyword = $(this).val();
        row.find('.selectsameasbooker').hide();
        var type = $(this).attr('searchtype');
        app.PmsBookingRoomView.searchguesttimeout = setTimeout(function() {
            var searchEvent = thundashop.Ajax.createEvent('','searchForGuest',row, {"keyword" : keyword, "type" : type});
            thundashop.Ajax.postWithCallBack(searchEvent, function(res) {
                row.find('.searchsuggestions').html(res);
            });
        }, "300");
    },
     openGroup : function() {
        thundashop.common.closeModal();
        var path = location.pathname;
        if(path == "/bookings.html") {
            path = "/";
        }
        var path = 
        window.open(path+'?page=groupbooking&bookingId=' + $(this).attr('bookingid'), "_new");
    },
    changeUserDescription : function() {
        var text = $('.PmsBookingRoomView .userdescription').text();
        var comment = prompt("Please enter a comment", text);
        
        if (!comment) {
            return;
        }

        $('.PmsBookingRoomView .userdescription').html(comment);
        var event = thundashop.Ajax.createEvent('','saveUserDescription',$(this), {
            "comment" : comment,
            "roomid" : app.PmsBookingGroupRoomView.getRoomId()
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
            "comment" : comment,
            "roomid" : app.PmsBookingGroupRoomView.getRoomId()
        });
        thundashop.Ajax.post(event);
    },
    saveComment : function() {
       var commentToSave = $('.commentToSave');
       var commentRow = commentToSave.closest('.commentrow');
       var data = {};
       data.id = commentRow.attr('commentid');
       data.roomid = app.PmsBookingGroupRoomView.getRoomId();
       data.text = commentRow.find('.commenttext').text();
       var event = thundashop.Ajax.createEvent('','updateComment',$(this), data);
       thundashop.Ajax.postWithCallBack(event, function(res) {
            thundashop.framework.reloadOverLayType1or2();
       });
    },
    
    startEditComment : function() {
        $(this).closest('.commentrow').find('.commenttext').attr('contenteditable','true');
        $(this).closest('.commentrow').find('.commenttext').focus();
        $(this).addClass('commentToSave');
        $('.savecomment').show();
    },
    changeCouponCode : function() {
        $('.checkifneedupdateprices').show();
    },
    doChangeCouponCode : function() {
        var newCode = $('.changediscountcode').val();
        var event = thundashop.Ajax.createEvent('','changeDiscountCode',$(this), {
            "code" : newCode,
            "updateprices" : $(this).attr('updateprices'),
            "roomid" : app.PmsBookingGroupRoomView.getRoomId()
        });
        thundashop.Ajax.postWithCallBack(event, function() {
            thundashop.framework.reloadOverLayType1or2();
        });
    },
    eventAdded : function(res) {
        if(!res) {
            alert('Failed to create event');
        } else {
            latestOverLayLoadingEvent.data.eventid = res.trim();
            app.PmsBookingGroupRoomView.refresh();
        }
    },
    changeSegment : function() {
        var newSegment = $(this).val();
        var event = thundashop.Ajax.createEvent('','segmentDiscountCode',$(this), {
            "segment" : newSegment,
            "roomid" : app.PmsBookingGroupRoomView.getRoomId()
        });
        thundashop.Ajax.postWithCallBack(event, function() {});
    },
    changeChannel : function() {
        var val = $(this).val();
        if(val === "getshop_new_source") {
            val = prompt("Name of source");
        }
        var event = thundashop.Ajax.createEvent('','changeChannel',$(this), {
            "channel" : val,
            "roomid" : app.PmsBookingGroupRoomView.getRoomId()
        });
        thundashop.Ajax.post(event);
    },
    toggleDisabledGuest : function() {
        var btn = $(this);
        var event = thundashop.Ajax.createEvent('','toggleDisabledGuest', $(this), {
            "disabledGuest" : btn.hasClass('isdisabled'),
            "guestid" : btn.closest('.guest_row').attr('guestid'),
            "roomid" : app.PmsBookingGroupRoomView.getRoomId()
        });
        
        thundashop.Ajax.postWithCallBack(event, function(res) {
            if(btn.hasClass('isdisabled')) {
                btn.removeClass('isdisabled');
            } else {
                btn.addClass('isdisabled');
            }
        });
    },
    addSelectedItemsToCart: function() {
        $('.menubutton[subsection="payments"]').click();
        thundashop.framework.loadAppInOverLay("af54ced1-4e2d-444f-b733-897c1542b5a8", "3", { pmsBookingRoomId : [app.PmsBookingGroupRoomView.getRoomId()], state: 'clear'}); 
    },
    toggleActionMenu: function() {
        var menu = $(this).closest('.col').find('.ordermenu');
        var visible = $(menu).is(':visible');
        
        $('.ordermenu').hide();
        if (!visible) {
            menu.show();
        }
    },
    
    showOrderSummary: function() {
        thundashop.framework.loadAppInOverLay("af54ced1-4e2d-444f-b733-897c1542b5a8", "3", {
            orderId: $(this).attr('orderid'),
            state: 'paymentoverview'
        });
    },
    sendConfirmation: function() {
        var event = thundashop.Ajax.createEvent('','sendConfirmation',$(this), {
            content : $('[gsname="confirmationemailcontent"]').val(),
            title : $('[gsname="confirmationemailtitle"]').val(),
            email : $('.emailRecipient').val(),
            msgid : $('.confirmationEmailTemplate').val(),
            roomid : app.PmsBookingGroupRoomView.getRoomId()
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            alert('Confirmation sent');
            $('.menubutton[subsection="messages"]').click();
        });
    },    
    saveAddonsOnRoom : function() {
        var toPost = {};
        $('.addonitemrow').each(function() {
            var addonId = $(this).attr('addonid');
            var row = thundashop.framework.createGsArgs($(this));
            toPost[addonId] = row;
        });
        
        toPost.roomid = app.PmsBookingGroupRoomView.getRoomId();
        
        var event = thundashop.Ajax.createEvent('','saveAddonItems',$(this),toPost);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            thundashop.common.Alert('Saved');
            thundashop.framework.reloadOverLayType2();
        });
    },    
    accessCodeTabUpdated : function() {
        thundashop.framework.reloadOverLayType2();
    },
    refresh : function() {
        thundashop.framework.reloadOverLayType2();
    },
    displayConfirmationPreview : function() {
        var event = thundashop.Ajax.createEvent('','loadConfirmationPreview',$(this), {
            content : $('[gsname="confirmationemailcontent"]').val(),
            title : $('[gsname="confirmationemailtitle"]').val(),
            msgid : $('.confirmationEmailTemplate').val(),
            roomid : app.PmsBookingGroupRoomView.getRoomId()
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.previewarea').html(res);
            $('.previewarea').fadeIn();
            $(window).scrollTop($(window).scrollTop()+50);
        });
    },    
    loadAddAddonsArea : function() {
        var event = thundashop.Ajax.createEvent('','printAddAddonsArea',$(this), {
            "roomid" : app.PmsBookingGroupRoomView.getRoomId()
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.PmsBookingRoomView .addAddonsArea').hide();
            $('.addAddonsAreaInner').html(res);
            $('.PmsBookingGroupRoomView .addAddonsArea').fadeIn();
            if($('.getshoptableoverlaybody').length > 0) {
                $('.getshoptableoverlaybody').css('height',$('.getshoptableoverlaybody').get(0).scrollHeight+50);
            }
        });
    },
    removeSelectedAddons : function() {
        var addonIds = [];
        $(".fa-check-square").each(function() {
            addonIds.push($(this).attr('addonid'));
        });
        var event = thundashop.Ajax.createEvent('','removeSelectedAddons',$(this), {
            addonIds : addonIds,
            "roomid" : app.PmsBookingGroupRoomView.getRoomId()
        });
        
        thundashop.Ajax.postWithCallBack(event, function() {
            thundashop.framework.reloadOverLayType2();
        });
    },
    chooseRoomToUse : function() {
        var data = {};
        data.roomid = app.PmsBookingGroupRoomView.getRoomId();
        data.item = $(this).attr('itemid');
        
        var event = thundashop.Ajax.createEvent('','chooseBookingItem',$(this), data);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            thundashop.framework.reloadOverLayType2();
        });
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
    
    showRoomsToSelect: function() {
        $('.PmsBookingGroupRoomView .roomstoselect').html('<div style="text-align:center; padding: 20px; font-size: 40px;"><i class="fa fa-spin fa-spinner"></i></div>');
        $('.PmsBookingGroupRoomView .roomstoselect').show();
        $('.PmsBookingGroupRoomView .outerstay').hide();
        
        var data = {}
        data.roomid = app.PmsBookingGroupRoomView.getRoomId();
        var event = thundashop.Ajax.createEvent(null, 'showItemView', $('.PmsBookingGroupRoomView'), data);
        event['synchron'] = true;
        
        
        thundashop.Ajax.post(event, function(res) {
            $('.PmsBookingGroupRoomView .roomstoselect').html(res);
        })
    },    
    savePrices : function() {
        var prices = {};
         $('.unitprice_changed').each(function(res) {
            prices[$(this).attr('gsname')] = $(this).val()
        });
        var data = {};
        data.prices = prices;
        data.roomid = app.PmsBookingGroupRoomView.getRoomId()
        
        var event = thundashop.Ajax.createEvent('','updatePrices',$(this), data);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            thundashop.framework.reloadOverLayType2();
        });
    },
    showSavePriceButton : function() {
        $('.PmsBookingGroupRoomView .savePriceButton').css('display','block');
    },
    doMassUpdatePrices : function() {
        var box = $(this).closest('.masseditpricesbox');
        var type = box.find('.masseditpricestay').val();
        var value =  box.find('.massupdatepricevalue').val();

        var event = thundashop.Ajax.createEvent('','updatePriceMatrixWithPeriodePrices', box, {
            "periodePriceType" : type,
            "periodePrice" : value,
            "roomid" : app.PmsBookingGroupRoomView.getRoomId()
        });
        thundashop.Ajax.postWithCallBack(event, function(res){
            thundashop.framework.reloadOverLayType2();
         });            
        
        $('.massupdatepricevalue').val('');
    },
    checkInCheckOutGuest : function() {
        var roomId = app.PmsBookingGroupRoomView.getRoomId();
        var event = thundashop.Ajax.createEvent('','checkinoutguest', $(this), {
           roomid : roomId 
        });
        thundashop.Ajax.post(event);
    }, 
    reinstateStay : function() {
        var minutes = prompt("How many minutes do you want to increase the stay for?");
        if(!minutes) {
            return;
        }
        
        var event = thundashop.Ajax.createEvent('','reinstateStay',$(this), {
            "minutes" : minutes,
            "roomid" : app.PmsBookingGroupRoomView.getRoomId()
        });
        thundashop.Ajax.post(event);
    },
    changedates : function() {
        var event = thundashop.Ajax.createEvent('','updateStayTime', $('.PmsBookingGroupRoomView'),{
            "startdate" : $('.PmsBookingGroupRoomView .changedatespanel .startdate').val(),
            "starttime" : $('.PmsBookingGroupRoomView .changedatespanel .starttime').val(),
            "enddate" : $('.PmsBookingGroupRoomView .changedatespanel .enddate').val(),
            "endtime" : $('.PmsBookingGroupRoomView .changedatespanel .endtime').val(),
            "roomtypeanditem" : $('.PmsBookingGroupRoomView .changedatespanel .roomtypeanditem').val(),
            "roomid" : app.PmsBookingGroupRoomView.getRoomId()
        });
        thundashop.Ajax.postWithCallBack(event, function() {
            thundashop.framework.reloadOverLayType2();
        });
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
    validateStayPeriode : function(res) {
        var data = {
            "startdate" : $('.PmsBookingGroupRoomView .changedatespanel .startdate').val(),
            "starttime" : $('.PmsBookingGroupRoomView .changedatespanel .starttime').val(),
            "enddate" : $('.PmsBookingGroupRoomView .changedatespanel .enddate').val(),
            "endtime" : $('.PmsBookingGroupRoomView .changedatespanel .endtime').val(),
            "roomtypeanditem" : $('.PmsBookingGroupRoomView .changedatespanel .roomtypeanditem').val(),
            "roomid" : app.PmsBookingGroupRoomView.getRoomId()
        };
        
        var event = thundashop.Ajax.createEvent('','canChangeStay', $('.PmsBookingGroupRoomView'),data);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.warningstayperiode').html(res);
        });
    },
    removeGuestToConference : function(res) {
        var guestid = $(this).closest('.guest_row').attr('guestid');
        var area = $(this).closest('.guestconferenceinformation');
        var eventid = $(this).attr('eventid')
        var event = thundashop.Ajax.createEvent('','removeConferenceFromGuest',$('.PmsBookingGroupRoomView'), {
            "guestid" : guestid,
            "eventid" : eventid,
            "roomid" : app.PmsBookingGroupRoomView.getRoomId()
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            console.log(res);
            area.html(res);
        });
    },
    getRoomId : function() {
        return latestOverLayLoadingEvent.data.id;
    },
    attachGuestToConference : function() {
        var eventid = $(this).attr('eventid');
        var guestid = $(this).closest('.guest_row').attr('guestid');
        var area = $(this).closest('.guest_row').find('.guestconferenceinformation');
        var event = thundashop.Ajax.createEvent('','attachGuestToEvent',$('.PmsBookingGroupRoomView'), {
            "eventid" : eventid,
            "guestid" : guestid,
            "roomid" : app.PmsBookingGroupRoomView.getRoomId()
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            area.html(res);
            $('.addguesttoconferencepanel').hide();
        });
    },    
    showAddConferencePanel : function() {
        var panel = $(this).closest('.guest_row').find('.addguesttoconferencepanel');
        var event = thundashop.Ajax.createEvent('','loadConferenceEvents',$('.PmsBookingGroupRoomView'), {
            "guestid" : $(this).closest('.guest_row').attr('guestid'),
            "roomid" : app.PmsBookingGroupRoomView.getRoomId()
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            panel.html(res);
            panel.show();
        });
    },
    
    selectSameAsBooker : function() {
        var event = thundashop.Ajax.createEvent('','getBookerInformation', $('.PmsBookingGroupRoomView'), {});
        var row = $(this).closest('.guest_row');
        
        event.data.roomid = latestOverLayLoadingEvent.data.id;
        
        thundashop.Ajax.postWithCallBack(event,function(res) {
            res = JSON.parse(res);
            var counter = row.attr('counter');
            row.find('[gsname="guestinfo_'+counter+'_name"]').val(res.name);
            row.find('[gsname="guestinfo_'+counter+'_email"]').val(res.email);
            row.find('[gsname="guestinfo_'+counter+'_prefix"]').val(res.prefix);
            row.find('[gsname="guestinfo_'+counter+'_phone"]').val(res.phone);
        });
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
    saveGuestInformation : function() {
        var args = thundashop.framework.createGsArgs($('.guestinforows'));
        args.language = $('#roomlanguage').val();
        args.countrycode = $('#countrycodeselection').val();
        args.roomid = latestOverLayLoadingEvent.data.id;
        
        
        thundashop.Ajax.simplePost($('.PmsBookingGroupRoomView'), 'saveGuestInformation', args);
    },
    changeRoom : function() {
        lastScrollTopPosition = $('.roomsingroupinformation').scrollTop();
        latestOverLayLoadingEvent.data.id = $(this).attr('roomid');
       thundashop.framework.reloadOverLayType2();
       if(!latestOverLayLoadingEvent.data.subsection) {
           latestOverLayLoadingEvent.data.subsection = "guests";
       }
       history.pushState(latestOverLayLoadingEvent, "page 2", "#roomid="+latestOverLayLoadingEvent.data.id + "&subsection="+latestOverLayLoadingEvent.data.subsection);
    },
    changeConference : function() {
        latestOverLayLoadingEvent.data.eventid = $(this).attr('eventid');
       thundashop.framework.reloadOverLayType2();
       if(!latestOverLayLoadingEvent.data.subsection) {
           latestOverLayLoadingEvent.data.subsection = "guests";
       }
       history.pushState(latestOverLayLoadingEvent, "page 2", "#roomid="+latestOverLayLoadingEvent.data.id + "&subsection="+latestOverLayLoadingEvent.data.subsection+"&confereceventid=" +latestOverLayLoadingEvent.data.eventid);
    },
    changeSubType : function() {
       latestOverLayLoadingEvent.data.subsection = $(this).attr('subsection');
       thundashop.framework.reloadOverLayType2();
       history.pushState(latestOverLayLoadingEvent, "page 2", "#roomid="+latestOverLayLoadingEvent.data.id + "&subsection="+latestOverLayLoadingEvent.data.subsection);
    }
};

app.PmsBookingGroupRoomView.init();

window.onpopstate = function(event) {
    if(!event.state)  {
        thundashop.common.closeOverLaysMouse();
    } else {
        latestOverLayLoadingEvent = event.state;
        if(!$('.gsoverlay2').is(':visible')) {
            getshop.showOverlay("2");
        }
        thundashop.framework.reloadOverLayType2();
    }
};
