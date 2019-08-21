app.PmsGroupBookingHeader = {
    init : function() {
        $(document).on('click', '.PmsGroupBookingHeader .top_box', app.PmsGroupBookingHeader.changeArea);
        $(document).on('click', '.PmsGroupBookingHeader .addanotherguest', app.PmsGroupBookingHeader.addAnotherGuest);
        $(document).on('click', '.PmsGroupBookingHeader .removeguestrow', app.PmsGroupBookingHeader.removeGuestRow);
        $(document).on('click', '.PmsGroupBookingHeader .groupedactioncheckbox', app.PmsGroupBookingHeader.updateGroupedAction);
        $(document).on('click', '.manipulateroomoptions .shop_button', app.PmsGroupBookingHeader.doAction);
        $(document).on('click', '.PmsGroupBookingHeader .updateguestinformation', app.PmsGroupBookingHeader.updateGuestInformation);
        $(document).on('click', '.PmsGroupBookingHeader .startpaymentprocessallrooms', app.PmsGroupBookingHeader.startPaymentProcess);
        $(document).on('click', '.PmsGroupBookingHeader .setsameasbooker', app.PmsGroupBookingHeader.setSameAsBooker);
        $(document).on('change', '.PmsGroupBookingHeader [gsname="type"]', app.PmsGroupBookingHeader.checkIfCanAddRoom);
        $(document).on('change', '.PmsGroupBookingHeader [gsname="guestInfoOnRoom"]', app.PmsGroupBookingHeader.checkIfCanAddRoom);
        $(document).on('keyup', '.PmsGroupBookingHeader [gsname="count"]', app.PmsGroupBookingHeader.checkIfCanAddRoom);
        $(document).on('click', '.PmsGroupBookingHeader .domassupdate', app.PmsGroupBookingHeader.doMassUpdate);
        $(document).on('click', '.PmsGroupBookingHeader .doextendstay', app.PmsGroupBookingHeader.extendStay);
        $(document).on('click', '.PmsGroupBookingHeader .updatePricePanelbtn', app.PmsGroupBookingHeader.showUpdatePricePanel);
        $(document).on('click', '.PmsGroupBookingHeader .moveCategoryPanelbtn', app.PmsGroupBookingHeader.showMoveCategory);
        $(document).on('click', '.PmsGroupBookingHeader .addAddonsPanelbtn', app.PmsGroupBookingHeader.showAddAddonsPanel);
        $(document).on('click', '.PmsGroupBookingHeader .setnewpricebutton', app.PmsGroupBookingHeader.updateAllPrices);
        $(document).on('click', '.PmsGroupBookingHeader .addAddonsToRoom,.PmsGroupBookingHeader .removeAddonFromRoom', app.PmsGroupBookingHeader.addAddonsToRoom);
        $(document).on('click', '.PmsGroupBookingHeader .setsingleday', app.PmsGroupBookingHeader.setSingleDayAddons);
        $(document).on('click', '.PmsGroupBookingHeader .addfromdifferentroom', app.PmsGroupBookingHeader.showSearchAreaFindBooking);
        $(document).on('click', '.PmsGroupBookingHeader .importroom', app.PmsGroupBookingHeader.importRoom);
        $(document).on('click', '.PmsGroupBookingHeader .startmovecategory', app.PmsGroupBookingHeader.tryToMoveRoom);
        $(document).on('keyup', '.PmsGroupBookingHeader .searchaddaddonslist', app.PmsGroupBookingHeader.searchAddAddonsList);
    },
    tryToMoveRoom : function() {
        var toType = $('.movetoroomtype').val();
        $('.moveCategoryPanel').hide();
        var roomsToUpdate = [];
        $('.groupedactioncheckbox').each(function() {
            if($(this).is(':checked')) {
                var roomId = $(this).attr('roomid');
                $(this).closest('.col').attr('spinnerroomid',roomId);
                $(this).closest('.col').html("<i class='fa fa-spin fa-spinner'></i>");
                roomsToUpdate.push(roomId);
            }
        });
        var updatedCounter = 0;
        for(var k in roomsToUpdate) {
            var roomId = roomsToUpdate[k];
            var event = thundashop.Ajax.createEvent('','changeRoomCategory',$(this),{
                "roomId" : roomId,
                "totype" : toType
            });
            event.synchron = true;
            
            thundashop.Ajax.postWithCallBack(event, function(res) {
                res = JSON.parse(res);
                updatedCounter++;
                if(res.status===1) {
                    $('[spinnerroomid="'+res.roomid+'"]').html('<i class="fa fa-check"></i>');
                } else {
                    $('[spinnerroomid="'+res.roomid+'"]').html('<i class="fa fa-frown-o"></i>');
                }
            });
        }
        var checkifdonemoving = setInterval(function() {
            if(updatedCounter === roomsToUpdate.length) {
                $('.top_box.selected').click();
                clearTimeout(checkifdonemoving);
            }
        }, "100");
    },
    showMoveCategory : function() {
        $('.PmsGroupBookingHeader .moveCategoryPanel').show();
    },
    importRoom : function() {
        var row = $(this).closest('.row');
        var event = thundashop.Ajax.createEvent('','addExistingRoomToBooking',$(this), { "roomid" : $(this).attr('roomid') });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            if(res === "1") {
                alert('Room has been moved');
                row.fadeOut();
            }
        });
    },
    showSearchAreaFindBooking : function() {
        $('.findroomfrombooking').show();
    },
    setSingleDayAddons : function() {
        if($(this).is(':checked')) {
            $("[gsname='end']").hide();
        } else {
            $("[gsname='end']").show();
        }
    },
    searchAddAddonsList : function() {
        var val = $(this).val();
        $('.addonsrow').each(function() {
            if($(this).text().toLowerCase().indexOf(val.toLowerCase()) !== -1) {
                $(this).show();
            } else {
                $(this).hide();
            }
        });
    },
    showAddAddonsPanel : function() {
        $('.addAddonsPanel').show();
    },
    updateAllPrices : function() {
        var form = $('.updatepricepanel');
        var args = thundashop.framework.createGsArgs(form);
        
        var rooms = [];
        $('.groupedactioncheckbox').each(function() {
            if($(this).is(':checked')) {
                rooms.push($(this).attr('roomid'));
            }
        });
        args.rooms = rooms;
        
        var event = thundashop.Ajax.createEvent('','updatePriceByDateRange',$(this), args);
        thundashop.Ajax.post(event);
    },
    addAddonsToRoom : function() {
        var form = $('.addAddonsPanel');
        var args = thundashop.framework.createGsArgs(form);
        
        var rooms = [];
        $('.groupedactioncheckbox').each(function() {
            if($(this).is(':checked')) {
                rooms.push($(this).attr('roomid'));
            }
        });
        args.rooms = rooms;
        args.type = $(this).attr('type');
        args.productid = $(this).attr('productId');
        
        var event = thundashop.Ajax.createEvent('','addAddonsToRoom',$(this), args);
        thundashop.Ajax.post(event);
    },
    showUpdatePricePanel : function() {
        if($(this).hasClass('disabled')) {
            return;
        }
        $('.updatepricepanel').toggle();
    },
    extendStay : function() {
        var days = parseInt($('.extendstaydaysinput').val());
        var checkoutday = $('.checkoutdate').val();
        $('.roomsenddate').each(function () {
            if($(this).val() !== checkoutday) { return; }
            var date = moment.utc($(this).val(),"DD.MM.YYYY").local();
            date = date.add('days', days);
            var month = date.get('month')+1;
            var day = date.date();
            var year = date.get('year');
            if(day < 10) { day = "0" + day; }
            if(month < 10) { month = "0" + month; }
            var newdate = day + "." + month + "." + year;
            console.log(newdate);
            $(this).val(newdate);
        });
    },
    doMassUpdate : function() {
        var form = $(this).closest('.massupdatedateform');
        var date = form.find('.datecheck').val();
        var time = form.find('.timecheck').val();
        if($(this).attr('type') === "start") {
            $('.roomstartdate').val(date);
            $('.roomstarttime').val(time);
        } else {
            $('.roomsenddate').val(date);
            $('.roomsendtime').val(time);
        }
    },
    setSameAsBooker : function() {
        var row = $(this).closest('.roomrow');
        row.find('[gsname="name"]').val($('.edit_details_directprint [gsname="fullName"]').val());
        row.find('[gsname="email"]').val($('.edit_details_directprint [gsname="emailAddress"]').val());
        row.find('[gsname="prefix"]').val($('.edit_details_directprint [gsname="prefix"]').val());
        row.find('[gsname="phone"]').val($('.edit_details_directprint [gsname="cellPhone"]').val());
    },
    startPaymentProcess : function() {
        var event = thundashop.Ajax.createEvent('','startPaymentProcessAllRooms', $(this), {});
        thundashop.Ajax.postWithCallBack(event, function(res) {
            var data = {};
            thundashop.framework.showRightWidgetPanel('gs_modul_pmscart', data);
        });
    },
    checkIfCanAddRoom : function() {
        var form = $('.addroombox');
        var args = thundashop.framework.createGsArgs(form);
        var event = thundashop.Ajax.createEvent('','checkIfCanAdd', form, args);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            if(res === "no") {
                $('.notavailablerooms').show();
            } else {
                $('.notavailablerooms').hide();
            }
        });
    },
    refreshPayments : function() {
        $('[areatype="payments"]').click();
    },
    
    updateGuestInformation : function() {
        var data = {};
        $('.guestrow').each(function() {
            var rowId = $(this).closest('.roomrow').attr('roomid');
            if(!data[rowId]) {
                data[rowId] = [];
            }
            var guest = thundashop.framework.createGsArgs($(this));
            data[rowId].push(guest);
        });
        var event = thundashop.Ajax.createEvent('','massUpdateGuests', $(this), {
            "guests" : data,
            "bookingid" : $('#pmsbookingid').val()
        });
        
        thundashop.Ajax.postWithCallBack(event, function() {
            thundashop.common.Alert('Guest information updated');
        });
    },
    doAction : function() {
        if($(this).hasClass('disabled')) {
            thundashop.common.Alert('Failed','Please select a room first',true);
            return;
        }
        
        var rooms = [];
        $('.groupedactioncheckbox').each(function() {
            if($(this).is(':checked')) {
                rooms.push($(this).attr('roomid'));
            }
        });
        
        var type = $(this).attr('type');
        
        if($(this).attr('avoidaction') === "true") {
            return;
        }
        
        var count = 0;
        if(type === "updateGuestCount") {
            count = parseInt(prompt("Number of guest", ""));
            if(!count) {
                return;
            }
        }
        
        var event = thundashop.Ajax.createEvent('','doRoomsBookedAction', $(this), {
            "type" : type,
            "rooms" : rooms,
            "count" : count
        });
        
        thundashop.Ajax.post(event);
        
    },
    updateGroupedAction : function() {
        var checked = false;
        $('.groupedactioncheckbox').each(function() {
            if($(this).is(':checked')) {
                checked = true;
            }
        });

        var roomid = $(this).attr('roomid');
        if($(this).is(':checked')) {
            app.PmsSearchBooking.quickAddToStartCheckout(roomid);
        } else {
            app.PmsSearchBooking.quickRemoveToStartCheckout(roomid);
        }
        app.PmsSearchBooking.printAddedToCheckout();
         
        if(checked) {
            $('.manipulateroomoptions .shop_button').removeClass('disabled');
        } else {
            $('.manipulateroomoptions .shop_button').addClass('disabled');
        }
        
        app.PmsSearchBooking.saveSelectedRooms();
    },
    removeGuestRow : function() {
        $(this).closest('.guestrow').remove();
    },
    addAnotherGuest : function() {
        var row = $(this).closest('.roomrowouter').find('.roomrow');
        var template = $('.guestrowtemplate').find('.guestrow').clone();
        row.append(template);
    },
    changeArea : function() {
        var newArea = $('.booker_main_user');
        $('.PmsGroupBookingHeader .top_box').removeClass('selected');
        $(this).addClass('selected');
        
        var event = thundashop.Ajax.createEvent('','loadArea', $(this), {
            "area" : $(this).attr('areatype')
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            newArea.html(res);
        });
    }
}

app.PmsGroupBookingHeader.init();