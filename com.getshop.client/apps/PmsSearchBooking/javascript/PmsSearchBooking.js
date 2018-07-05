app.PmsSearchBooking = {
    init : function() {
        $(document).on('click','.GetShopModuleTable .quickfunction',app.PmsSearchBooking.doQuickFunction);
        $(document).on('click','.GetShopModuleTable .unsettledamountwarning',app.PmsSearchBooking.loadUnpaidPriceView);
        $(document).on('click','.GetShopModuleTable .toggleallrooms',app.PmsSearchBooking.toggleAllRooms);
        $(document).on('keyup','.PmsSearchBooking .tablefilterinput', app.PmsSearchBooking.filterRows);
        $(document).on('click','.PmsSearchBooking .startcheckout', app.PmsSearchBooking.addToStartCheckout);
        $(document).on('click','.PmsSearchBooking .addedtocheckout', app.PmsSearchBooking.removeFromStartCheckout);
    },
    removeFromStartCheckout : function() {
        var list = app.PmsSearchBooking.getAddedToCheckoutList();
        var toRemove = $(this).attr('roomid');
        var newList = [];
        for(var k in list) {
            var roomId = list[k];
            if(toRemove === roomId) {
                continue;
            }
            newList.push(roomId);
        }
        localStorage.setItem("addedtocheckout", JSON.stringify(newList));
        app.PmsSearchBooking.printAddedToCheckout();
    },
    printAddedToCheckout : function() {
        var list = app.PmsSearchBooking.getAddedToCheckoutList();
        var total = list.length;
        if(total > 0) {
            $('.continuetocheckout').show();
        } else {
            $('.continuetocheckout').hide();
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
    addToStartCheckout : function() {
        var added = app.PmsSearchBooking.getAddedToCheckoutList();
        var roomId = $(this).closest('.quickfunctions').attr('roomid');
        if(added.indexOf(roomId) >= 0) {
            return;
        }
        added.push(roomId);
        localStorage.setItem("addedtocheckout", JSON.stringify(added));
        app.PmsSearchBooking.printAddedToCheckout();
    },
    filterRows : function() {
        var filter = $('.PmsSearchBooking .tablefilterinput').val().toLowerCase();
        localStorage.setItem("filterKeyword", filter);
        $('.datarow').each(function(res) {
            if($(this).hasClass('attributeheader')) {
                return;
            }
            var text = $(this).find('.col_visitor').text().toLowerCase();
            text += " " + $(this).find('.col_room').text().toLowerCase();
            text += " " + $(this).find('.col_bookedfor').text().toLowerCase();
            if( text.indexOf(filter) >= 0){
                $(this).show();
            } else {
                $(this).hide();
            }
        });
    },
    toggleAllRooms : function() {
        if($(this).is(':checked')) {
            $('.groupedactioncheckbox').attr('checked','checked');
            $('.manipulateroomoptions .shop_button').removeClass('disabled');
        } else {
            $('.groupedactioncheckbox').attr('checked',null);
            $('.manipulateroomoptions .shop_button').addClass('disabled');
        }
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
