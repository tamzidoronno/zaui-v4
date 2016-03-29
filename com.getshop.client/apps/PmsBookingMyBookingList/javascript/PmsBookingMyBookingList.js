app.PmsBookingMyBookingList = {
    init : function() {
        $(document).on('click', '.PmsBookingMyBookingList .deleteroom', app.PmsBookingMyBookingList.deleteRoom);
        $(document).on('click', '.PmsBookingMyBookingList .editroom', app.PmsBookingMyBookingList.editRoom);
        $(document).on('change', '.PmsBookingMyBookingList #chooseuser', app.PmsBookingMyBookingList.switchUser);
        $(document).on('change', '.PmsBookingMyBookingList .changeGuestCount', app.PmsBookingMyBookingList.changeGuestCount);
        $(document).on('click', '.PmsBookingMyBookingList .showorderbutton', app.PmsBookingMyBookingList.showOrder);
    },
    showOrder : function() {
        thundashop.common.hideInformationBox();
        app.OrderManager.gssinterface.showOrder($(this).attr('orderid'));
    },
    changeGuestCount : function() {
        app.PmsBookingMyBookingList.setGuestCountTable($(this).val());
    },
    setGuestCountTable : function(count) {
        for(var i = 0; i < 10; i++) {
            if(count > i) {
                $('tr[countnumber="'+i+'"]').show();
            } else {
                $('tr[countnumber="'+i+'"]').hide();
            }
        }
    },
    switchUser : function() {
        thundashop.Ajax.simplePost($(this), "setTmpUser", {
            "userid" : $(this).val()
        });
    },
    editRoom : function() {
        var data = {
            "roomid" : $(this).closest('tr').attr('roomid'),
            "bookingid" : $(this).closest('tr').attr('bookingid')
        }
        var event = thundashop.Ajax.createEvent('','loadEditRoom', $(this), data);
        var room = $('.PmsBookingMyBookingList .editroomform');
        room.css('left', $(this).position().left + 20);
        room.css('top', $(this).position().top);
        thundashop.Ajax.postWithCallBack(event, function(data) {
            $('.editroomform').html(data);
            room.slideDown();
        });
        
        
    },
    
    deleteRoom : function() {
        var confirmed = confirm("Are you sure you want to delete this room?");
        if(!confirmed) {
            return;
        }
        var data = {
            "roomid" : $(this).closest('tr').attr('roomid'),
            "bookingid" : $(this).closest('tr').attr('bookingid')
        }
        thundashop.Ajax.simplePost($(this), 'deleteRoom', data);
    },
    loadSettings: function (element, application) {
        var config = {
            draggable: true,
            app: true,
            showSettings : true,
            application: application,
            title: "Settings",
            items: []
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
};

app.PmsBookingMyBookingList.init();