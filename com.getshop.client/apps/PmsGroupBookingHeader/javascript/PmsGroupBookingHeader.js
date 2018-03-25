app.PmsGroupBookingHeader = {
    init : function() {
        $(document).on('click', '.PmsGroupBookingHeader .top_box', app.PmsGroupBookingHeader.changeArea);
        $(document).on('click', '.PmsGroupBookingHeader .addanotherguest', app.PmsGroupBookingHeader.addAnotherGuest);
        $(document).on('click', '.PmsGroupBookingHeader .removeguestrow', app.PmsGroupBookingHeader.removeGuestRow);
        $(document).on('click', '.PmsGroupBookingHeader .groupedactioncheckbox', app.PmsGroupBookingHeader.updateGroupedAction);
        $(document).on('click', '.manipulateroomoptions .shop_button', app.PmsGroupBookingHeader.doAction);
        $(document).on('click', '.PmsGroupBookingHeader .updateguestinformation', app.PmsGroupBookingHeader.updateGuestInformation);
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
        
        var event = thundashop.Ajax.createEvent('','doRoomsBookedAction', $(this), {
            "type" : $(this).attr('type'),
            "rooms" : rooms
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
        if(checked) {
            $('.manipulateroomoptions .shop_button').removeClass('disabled');
        } else {
            $('.manipulateroomoptions .shop_button').addClass('disabled');
        }
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