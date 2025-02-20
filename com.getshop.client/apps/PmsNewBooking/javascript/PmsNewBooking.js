app.PmsNewBooking = {
    timeoutchangeprice : null,
    init : function() {
        $(document).on('change', '.PmsNewBooking select[gsname="type"]', function() {
            $('.PmsNewBooking [gsname="numberOfRooms"]').val(0);
        });
        
        $(document).on('keyup', '.PmsNewBooking [gsname="numberOfRooms"]', function() {
            $('.moreThenAvailableWarning').hide();
            var number = $(this).val();
            var type = $('select[gsname="type"]').val();
            var available = parseInt($('.availableroomscounter[roomtype="'+type+'"]').text());
            var typeName = $('.availabletypename[roomtype="'+type+'"]').text();
            if(number > available) {
                $(this).addClass('moreThenAvailable');
                $('.moreThenAvailableWarning').find('.maxrooms').html(available);
                $('.moreThenAvailableWarning').find('.numberofrooms').html(number);
                $('.moreThenAvailableWarning').find('.typename').html(typeName);
                $('.moreThenAvailableWarning').find('.roomsdiff').html((number-available));
                $('.moreThenAvailableWarning').fadeIn();
            }
        });
        $(document).on('click','.PmsNewBooking .selectuser',app.PmsNewBooking.selectUser);
        $(document).on('click','.PmsNewBooking .selecteduser .fa-trash-o',app.PmsNewBooking.removeSelectedUser);
        $(document).on('click','.PmsNewBooking .sameasbookerinfocheckbox',app.PmsNewBooking.markAsSameAsBooker);
        $(document).on('click','.PmsNewBooking .createOrderAndSendPaymentLinkCheckBox',app.PmsNewBooking.createOrderAndSendPaymentLinkCheckBox);
        $(document).on('keyup','.PmsNewBooking .roomtypecount',app.PmsNewBooking.countRoomsToAdd);
        $(document).on('keyup','.PmsNewBooking .guestrow input',app.PmsNewBooking.updateGuestRow);
        $(document).on('keyup','.PmsNewBooking .discountcode',app.PmsNewBooking.setDiscountCode);
        $(document).on('keyup','.PmsNewBooking .guestcounter',app.PmsNewBooking.changeGuestCount);
        $(document).on('click','.PmsNewBooking .totalcount',app.PmsNewBooking.selectAllRooms);
        $(document).on('click','.PmsNewBooking .newcustomertypebutton', app.PmsNewBooking.loadNewCustomerType);
        $(document).on('click','.PmsNewBooking .newcustomerbutton', app.PmsNewBooking.loadNewCustomerField);
        $(document).on('click','.PmsNewBooking .searchbrregbutton', app.PmsNewBooking.showBrregSearch);
        $(document).on('click','.PmsNewBooking .brregsearchresultrow', app.PmsNewBooking.selectBrregResult);
        $(document).on('click','.PmsNewBooking .addonoption', app.PmsNewBooking.addAddonToBooking);
        $(document).on('click','.PmsNewBooking .usedefaultpricescheckbox', app.PmsNewBooking.usedefaultpricescheckbox);
        $(document).on('click','.PmsNewBooking .guestrowhintentry', app.PmsNewBooking.setGuestInformation);
        $(document).on('click','.PmsNewBooking .usersuggestion', app.PmsNewBooking.selectUserAndCompleteBooking);
        $(document).on('keyup','.PmsNewBooking .roomprice', app.PmsNewBooking.changeRoomPrice);
    },
    selectUserAndCompleteBooking : function() {
        var event = thundashop.Ajax.createEvent('','completeBookingBySpecificUser',$(this),{
            "userid" : $(this).attr('userid')
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.specificsuggestions').html(res);
        });
        
    },
    
    setGuestInformation : function() {
        var box = $(this);
        var guestRow = $(this).closest('.guestrow');
        guestRow.find('.guestname').val(box.attr('name'));
        guestRow.find('.guestemail').val(box.attr('email'));
        guestRow.find('.guestprefix').val(box.attr('prefix'));
        guestRow.find('.guestphone').val(box.attr('phone'));
        var updating = app.PmsNewBooking.updateGuestRowByRow(guestRow);
        updating.done(function() {
           $('.guestrowhintentry').hide();
        });
        var event = thundashop.Ajax.createEvent('','addSuggestedUser', box, {
            "userid" : box.attr('userid')
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.specificsuggestions').html(res);
        });
    },
    
    createOrderAndSendPaymentLinkCheckBox : function() {
        var event = thundashop.Ajax.createEvent('','toggleCreateOrderAndSendPaymentLink',$(this),{
            "checked" : $(this).is(':checked')
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
        });
    },
    
    updateGuestRow : function() {
        var guestRow = $(this).closest('.guestrow');
        app.PmsNewBooking.updateGuestRowByRow(guestRow);
    },
    
    updateGuestRowByRow(guestRow) {
        var guestId = guestRow.attr('guestid');
        var roomId = guestRow.closest('.guestinformation').attr('roomid');
        var data = {};
        var promise = $.Deferred();
        data.name = guestRow.find('.guestname').val();
        data.email = guestRow.find('.guestemail').val();
        data.prefix = guestRow.find('.guestprefix').val();
        data.phone = guestRow.find('.guestphone').val();
        var event = thundashop.Ajax.createEvent('','saveGuestInformation',guestRow, {
            "guestinfo" : data,
            "guestid" : guestId,
            "roomid" : roomId
        });
        
        if(typeof(app.PmsNewBooking.updateGuestInfoTimer) !== "undefined") {
            clearTimeout(app.PmsNewBooking.updateGuestInfoTimer);
        }
        
        app.PmsNewBooking.updateGuestInfoTimer = setTimeout(function() {
            thundashop.Ajax.postWithCallBack(event, function(res) {
                guestRow.find('.guestrowhint').html(res);
                promise.resolve();
            });
        }, "200");
        return promise;
    },
    usedefaultpricescheckbox : function() {
        var checkbox = $(this);
        var event = thundashop.Ajax.createEvent('','toggleUseDefaultPrices', $(this), {
            "checked" : checkbox.is(':checked')
        });
        
        thundashop.Ajax.postWithCallBack(event, function() {
            if(checkbox.is(':checked')) {
                checkbox.closest('.section').find('.roomprice').attr('disabled','disabled');
            } else {
                checkbox.closest('.section').find('.roomprice').attr('disabled',null);
            }
        });
    },
    addAddonToBooking : function() {
        var remove = false;
        if($(this).hasClass('active')) {
            remove = true;
        }
        var productid = $(this).attr('productid');
        var event = thundashop.Ajax.createEvent('','addAddonToBooking', $(this), {
            "productid" : productid,
            "remove" : remove
        });
        thundashop.Ajax.post(event);
    },
    
    changeGuestCount : function() {
        var count = $(this).val();
        var roomId = $(this).attr('roomid');
        var event = thundashop.Ajax.createEvent('','updateGuestCount', $(this), {
            "roomid" : roomId,
            "count" : count
        });
        var row = $(this).closest('.datarow');
        thundashop.Ajax.postWithCallBack(event, function(res) {
            var obj = JSON.parse(res);
            row.find('.roomprice').val(obj.price);
            row.find('.col_cost').html(obj.price);
            row.find('.addonsarea').html(obj.addons);
            $('.totalcostindicator').html(obj.totalcost);
            $('.guestinformation[roomid="'+roomId+'"]').html(obj.guests);
        });
    },
    markAsSameAsBooker : function() {
        var count = $(this).is(':checked');
        var roomId = $(this).attr('roomid');
        var guestId = $(this).attr('guestid');
        var event = thundashop.Ajax.createEvent('','updateSameAsBooker', $(this), {
            "roomid" : roomId,
            "guestid" : guestId,
            "sameasbooker" : count
        });

        thundashop.Ajax.postWithCallBack(event, function(res) {
            var obj = JSON.parse(res);
            $('.guestinformation[roomid="'+roomId+'"]').html(obj.guests);
        });
    },
    changeRoomPrice : function() {
        if(typeof(app.PmsNewBooking.timeoutchangeprice) !== "undefined") {
            clearTimeout(app.PmsNewBooking.timeoutchangeprice);
        }
        var inputfield = $(this);
        app.PmsNewBooking.timeoutchangeprice = setTimeout(function() {
            var count = inputfield.val();
            var roomId = inputfield.attr('roomid');
            var event = thundashop.Ajax.createEvent('','updateRoomPrice', inputfield, {
                "roomid" : roomId,
                "newprice" : count
            });
            var row = inputfield.closest('.datarow');
            thundashop.Ajax.postWithCallBack(event, function(res) {
                var obj = JSON.parse(res);
                row.find('.roomprice').val(obj.price);
                row.find('.col_cost').html(obj.price);
                row.find('.addonsarea').html(obj.addons);
                $('.totalcostindicator').html(obj.totalcost);
            });
        }, "500");
    },
    setDiscountCode : function() {
        if(typeof(waitToInsertCode) !== "undefined") {
            console.log('clearing');
            clearTimeout(waitToInsertCode);
        }
        
        
        var input = $(this);
        waitToInsertCode = setTimeout(function() {
            var event = thundashop.Ajax.createEvent('','setDiscountCode', input, {
                "code" : input.val()
            });
            thundashop.Ajax.postWithCallBack(event, function(res) {
                console.log(res);
                if(res == "yes") {
                    input.css('border-color','green');
                } else {
                    input.css('border-color','none');
                }
            });
        }, "500");
    },
    existingSearchResult : function(res) {
        $('.existingsearchcustomerresult').html(res);
    },
    selectBrregResult : function() {
        var name = $(this).attr('name');
        var vatnumber = $(this).attr('vatnumber');
        $('[gsname="vatnumber"]').val(vatnumber);
        $('[gsname="name"]').val(name);
        $('.searchbrregarea').hide();
    },
    showBrregSearch : function() {
        $('.searchbrregarea').slideDown();
    },    
    loadNewCustomerField : function() {
        $('.nextstep').hide();
        $('.companytypeselection').slideDown();
    },
    loadNewCustomerType : function() {
        $('.selectedbutton').removeClass('selectedbutton');
        $(this).addClass('selectedbutton');
        $('.nextstep').hide();
        var type = $(this).attr('type');
        $('.'+type).show();
        $(window).scrollTop(1000000);
    },
    
    selectAllRooms : function() {
        $('.roomstoaddrow').each(function() {
            var count = $(this).find('.roomsleftfield').attr('original');
            $(this).find('.roomtypecount').val(count);
        });
        app.PmsNewBooking.countRoomsToAdd();
    },
    countRoomsToAdd : function() {
        var total = 0;
        $('.roomstoaddrow').each(function() {
            var count = 0;
            var row = $(this);
            var left = parseInt(row.find('.roomsleftfield').attr('original'));
            if(row.find('.roomtypecount').val()) {
                count = parseInt(row.find('.roomtypecount').val());
                total += count;
            }
            left = left - count;
            row.find('.roomsleftfield').text(left);
        });
        $('.totalcount').html(total);
    },
    removeSelectedUser : function() {
        $('.PmsNewBooking [gsname="nameofholder"]').val("");
        $('.PmsNewBooking [gsname="nameofholder"]').show();
        $('.PmsNewBooking .selecteduser').hide();
    },
    
    selectUser : function() {
        var userid = $(this).closest('tr').attr('userid');
        $('.PmsNewBooking [gsname="nameofholder"]').val(userid);
        $('.PmsNewBooking [gsname="nameofholder"]').hide();
        $('.PmsNewBooking .selecteduser').show();
        $('.PmsNewBooking .selecteduser').find('.name').html($(this).closest('tr').attr('name'));
    },
    reloadAvailability : function() {
        var event = thundashop.Ajax.createEvent('','reloadAvailability',$('.PmsNewBooking'), {
            "start" : $('#startdate').val(),
            "end" : $('#enddate').val()
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.availablerooms').html(res);
            var totalCounter = 0;
            $('.availableroomscounter').each(function() {
                var roomtype = $(this).attr('roomtype');
                var count = $(this).text();
                totalCounter += parseInt(count);
                $('.roomstoaddrow[roomtype="'+roomtype+'"]').find('.roomsleftfield').attr('original', count);
                $('.roomstoaddrow[roomtype="'+roomtype+'"]').find('.roomsleftfield').text(count);
            });
            $('.totalcounter').html(totalCounter);
        });
        
        var event = thundashop.Ajax.createEvent('','checkClosedRooms',$('.PmsNewBooking'), {});
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.closedroomwarning').html(res);
        });
    }
};

app.PmsNewBooking.init();