app.PmsNewBooking20 = {
    updateGuestInfoTimer : null,
    
    init : function() {
        $(document).on('click','.PmsNewBooking20 .newbookingoption', app.PmsNewBooking20.goToStep);
        $(document).on('click','.PmsNewBooking20 .increaseroomcounter', app.PmsNewBooking20.increaseRoomCounter);
        $(document).on('click','.PmsNewBooking20 .decreaseroomcounter', app.PmsNewBooking20.decreaseRoomCounter);
        $(document).on('click','.PmsNewBooking20 .addsuggestionarrow', app.PmsNewBooking20.addSuggestion);
        $(document).on('click','.PmsNewBooking20 .editpriceonroombutton', app.PmsNewBooking20.toggleEditPrice);
        $(document).on('keyup','.PmsNewBooking20 .updateguestinfofield', app.PmsNewBooking20.updateGuestInfo);
    },
    toggleEditPrice : function() {
        $(this).closest('.pricetoroom').hide();
        $(this).closest('.roomfooterarea').find('.editpriceonroom').show();
        $(this).closest('.roomfooterarea').find('.editpriceonroom').find('.editroompriceinput').focus();
        $(this).closest('.roomfooterarea').find('.editpriceonroom').find('.editroompriceinput').select();
    },
    updateGuestInfo : function() {
        var updatingRoomVal = $(this).val();
        var updatingRoomType = $(this).attr('fortype');
        var area = $(this).closest('.eachguestarea');
        
        if(app.PmsNewBooking20.updateGuestInfoTimer !== null) {
            clearTimeout(app.PmsNewBooking20.updateGuestInfoTimer);
        }
        app.PmsNewBooking20.updateGuestInfoTimer = setTimeout(function() {
            app.PmsNewBooking20.saveGuestInformation();
            var searchSuggestions = thundashop.Ajax.createEvent('','findSuggestion', $('.PmsNewBooking20'), {
                "value" : updatingRoomVal,
                "type" : updatingRoomType
            });
            thundashop.Ajax.postWithCallBack(searchSuggestions, function(res) {
                area.find('.guestionsuggestionarea').html(res);
            });
        }, 300);
    },
    bookingCompleted : function(res) {
        window.location.href='/pms.php?page=a90a9031-b67d-4d98-b034-f8c201a8f496&loadBooking='+res;
    },
    
    saveGuestInformation : function() {
        var data = {};
        $('.guestinformationrow').each(function() {
            var guestinfo = {};
            guestinfo.name = $(this).find('.nameinput').val();
            guestinfo.email = $(this).find('.emailinput').val();
            guestinfo.prefix = $(this).find('.prefixinput').val();
            guestinfo.phone = $(this).find('.phoneinput').val();
            guestinfo.guestid = $(this).attr('guestid');
            data[guestinfo.guestid] = guestinfo;
        });
        var event = thundashop.Ajax.createEvent('','saveGuestInformation', $('.PmsNewBooking20'), data);
        thundashop.Ajax.postWithCallBack(event, function(res) {});
    },
    addSuggestion : function() {
        var row = $(this).closest('.guestsuggestionrow');
        var area = $(this).closest('.eachguestarea');
        area.find('.nameinput').val(row.find('.guestsuggestionname').text());
        area.find('.emailinput').val(row.find('.guestsuggestionemail').text());
        area.find('.prefixinput').val(row.find('.guestsuggestionphoneprefix').text());
        area.find('.phoneinput').val(row.find('.guestsuggestionphonenumber').text());
        area.find('.guestionsuggestionarea').hide();
        app.PmsNewBooking20.saveGuestInformation();
    },
    roomsAddedToBooking : function() {
        $('.PmsNewBooking20 .availablerooms').html('');
        app.PmsNewBooking20.reloadAddedRoomsList();
    },
    increaseRoomCounter : function() {
        var counter = $(this).closest('.counterrow').find('.roomcount').val();
        counter++;
        $(this).closest('.counterrow').find('.roomcount').val(counter);
    },
    reloadAddedRoomsList : function() {
        var event = thundashop.Ajax.createEvent('','loadRoomsAddedToBookingList',$('.PmsNewBooking20'), {});
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.roomsaddedarea').html(res);
        });
    },
    decreaseRoomCounter : function() {
        var counter = $(this).closest('.counterrow').find('.roomcount').val();
        counter--;
        if(counter < 0) {
            counter = 0;
        }
        $(this).closest('.counterrow').find('.roomcount').val(counter);
    },
    setLastPage : function() {
        var lastPage = localStorage.getItem('newbookinglastpage');
        if(typeof(lastPage) !== "undefined" && lastPage) {
            app.PmsNewBooking20.doGoToStep(lastPage);
        }
    },
    continueToStep2 : function() {
        app.PmsNewBooking20.doGoToStep('newbookingstep2');
    },
    userSavedUpdated : function() {
        $('.PmsNewBooking20 .GetShopQuickUser .change_user_form, .PmsNewBooking20 .GetShopQuickUser .edit_details_of_user').show();
    },
    searchCustomerResult : function(res) {
        $('.customersearcharea').html(res);
    },
    goToStep : function() {
        var goto = $(this).attr('goto');
        app.PmsNewBooking20.doGoToStep(goto);
    },
    loadSelectedBooking : function() {
        var event = thundashop.Ajax.createEvent('','loadCurrentBooking', $('.PmsNewBooking20'), {});
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.roomsaddedarea').html(res);
        });
    },
    
    doGoToStep : function(goto) {
        localStorage.setItem("newbookinglastpage", goto);
        $('.PmsNewBooking20 .newbookingprocess').hide();
        $('.PmsNewBooking20 .newbookingprocess[for="'+goto+'"]').fadeIn();
        if(goto === "newbookingstep2") {
            app.PmsNewBooking20.loadSelectedBooking();
        }
        if(goto === "search") {
            $('.searchcustomerinput').focus();
        }
    }
};

app.PmsNewBooking20.init();