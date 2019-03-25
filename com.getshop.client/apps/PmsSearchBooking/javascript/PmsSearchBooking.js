app.PmsSearchBooking = {
    init : function() {
        $(document).on('click','.GetShopModuleTable .quickfunction',app.PmsSearchBooking.doQuickFunction);
        $(document).on('click','.GetShopModuleTable .unsettledamountwarning',app.PmsSearchBooking.loadUnpaidPriceView);
        $(document).on('click','.GetShopModuleTable .toggleallrooms',app.PmsSearchBooking.toggleAllRooms);
        $(document).on('keyup','.PmsSearchBooking .tablefilterinput', app.PmsSearchBooking.filterRows);
        $(document).on('click','.PmsSearchBooking .startcheckout', app.PmsSearchBooking.addToStartCheckout);
        $(document).on('click','.PmsSearchBooking .addedtocheckout', app.PmsSearchBooking.removeFromStartCheckout);
        $(document).on('click','.PmsSearchBooking .clearCheckoutProcess', app.PmsSearchBooking.clearCheckoutProcess);
        $(document).on('click','.PmsSearchBooking .continuetocheckout', app.PmsSearchBooking.startCheckoutProcess);
    },
    startCheckoutProcess : function() {
        if ($(this).hasClass('usenew')) {
            app.PmsSearchBooking.startNewCheckoutProcess();
            return;
        };
        
        var rooms = app.PmsSearchBooking.getAddedToCheckoutList();
        var event = thundashop.Ajax.createEvent('','startPaymentProcessForSelectedRooms', $(this), {
            "rooms" : rooms 
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            app.PmsSearchBooking.clearCheckoutProcess();
            $('.pmscheckoutforrooms').find('.innercheckout').html(res);
            $('.pmscheckoutforrooms').show();
        });
    },
    
    startNewCheckoutProcess: function() {
        var rooms = app.PmsSearchBooking.getAddedToCheckoutList();
        thundashop.framework.loadAppInOverLay("af54ced1-4e2d-444f-b733-897c1542b5a8", "3", { pmsBookingRoomId : rooms, state: 'bookings_summary'}); 
    },
    
    clearCheckoutProcess : function() {
        $('.checkoutview').hide();
        localStorage.setItem("addedtocheckout", JSON.stringify([]));
        app.PmsSearchBooking.printAddedToCheckout();
    },
    removeFromStartCheckout : function() {
        var toRemove = $(this).attr('roomid');
        app.PmsSearchBooking.quickRemoveToStartCheckout(toRemove);
        app.PmsSearchBooking.printAddedToCheckout();
    },
    printAddedToCheckout : function() {
        var list = app.PmsSearchBooking.getAddedToCheckoutList();
        var total = list.length;
        if(total > 0) {
            $('.continuetocheckout').show();
            $('.clearCheckoutProcess').show();
        } else {
            $('.continuetocheckout').hide();
            $('.clearCheckoutProcess').hide();
        }
        $('.totaladdedtocheckout').html(total);
        $('.addedtocheckout').hide();
        $('.quickfunctions .startcheckout').show();
        for(var k in list) {
            var roomId = list[k];
            $('.addedtocheckout[roomid="'+roomId+'"]').show();
            $('.quickfunctions[roomid="'+roomId+'"]').find('.startcheckout').hide();
        }
    },
    getAddedToCheckoutList : function() {
        var added = localStorage.getItem('addedtocheckout');
        if(!added) {
            added = [];
        } else {
            added = JSON.parse(added);
        }
        return added;
    },
    quickAddToStartCheckout : function(roomId) {
        var added = app.PmsSearchBooking.getAddedToCheckoutList();
        if(added.indexOf(roomId) >= 0) {
            return;
        }
        added.push(roomId);
        localStorage.setItem("addedtocheckout", JSON.stringify(added));
        
    },
    quickRemoveToStartCheckout : function(toRemove) {
        var list = app.PmsSearchBooking.getAddedToCheckoutList();
        var newList = [];
        for(var k in list) {
            var roomId = list[k];
            if(toRemove === roomId) {
                continue;
            }
            newList.push(roomId);
        }
        localStorage.setItem("addedtocheckout", JSON.stringify(newList));
    },
    addToStartCheckout : function() {
        var roomId = $(this).closest('.quickfunctions').attr('roomid');
        app.PmsSearchBooking.quickAddToStartCheckout(roomId);
        app.PmsSearchBooking.printAddedToCheckout();
    },
    filterRows : function() {
        var filter = $('.PmsSearchBooking .tablefilterinput').val().toLowerCase();
        localStorage.setItem("filterKeyword", filter);
        var shown = 0;
        $('.datarow').each(function(res) {
            if($(this).hasClass('attributeheader')) {
                return;
            }
            var text = $(this).find('.col_visitor').text().toLowerCase();
            text += " " + $(this).find('.col_room').text().toLowerCase();
            text += " " + $(this).find('.col_bookedfor').text().toLowerCase();
            if( text.indexOf(filter) >= 0){
                $(this).show();
                shown++;
            } else {
                $(this).hide();
            }
        });
        if(shown === 0) {
            $('.nothinginfilertodisplay').show();
        } else {
            $('.nothinginfilertodisplay').hide();
        }
    },
    toggleAllRooms : function() {
        if($(this).is(':checked')) {
            $('.groupedactioncheckbox').attr('checked','checked');
            $('.manipulateroomoptions .shop_button').removeClass('disabled');
            $('.groupedactioncheckbox').each(function() {
                var roomid = $(this).attr('roomid');
                app.PmsSearchBooking.quickAddToStartCheckout(roomid);
            });
        } else {
            $('.groupedactioncheckbox').attr('checked',null);
            $('.manipulateroomoptions .shop_button').addClass('disabled');
            $('.groupedactioncheckbox').each(function() {
                var roomid = $(this).attr('roomid');
                app.PmsSearchBooking.quickRemoveToStartCheckout(roomid);
            });
        }
        app.PmsSearchBooking.printAddedToCheckout();
    },
    loadUnpaidPriceView : function() {
        var event = thundashop.Ajax.createEvent('','loadUnpaidView',$(this), {
            "roomid" : $(this).attr('roomid')
        });
        var field = $(this).closest('.col');
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.unpaidroomview').remove();
            field.append('<div class="unpaidroomview dontExpand">' + res + "</div>");
        });
    },
    doQuickFunction : function() {
        var type = $(this).attr('type');
        var roomId = $(this).closest('.quickfunctions').attr('roomid');
        var event = thundashop.Ajax.createEvent('','loadQuickMenu',$(this), {
            "type" : type,
            "roomid" : roomId
        });
        var field = $(this).closest('.datarow').find('.quickmenuoption');
        field.show();
        thundashop.Ajax.postWithCallBack(event, function(res) {
            field.html(res);
        });
    }
};
app.PmsSearchBooking.init();
