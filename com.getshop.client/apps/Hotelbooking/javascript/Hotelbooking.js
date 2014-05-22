app.Hotelbooking = {
    setSize : function() {
        var newHeight = $('.Hotelbooking .booking_page').outerHeight()+30;
        var top = (500 - newHeight)/2;
        $('.Hotelbooking .bookingbox').css('top',top+'px');
        $('.Hotelbooking .bookingbox').css('height',newHeight);
    },
    updateNumberOfRooms : function() {
        var price = parseInt($('.Hotelbooking .price').attr('price'));
        var price = price * parseInt($(this).val());
        $('.Hotelbooking .price').html(price);
    },
    changeOrderType : function() {
        var type = parseInt($(this).val());
        if(type === 1) {
            $('.Hotelbooking .orderfields.private').hide();
            $('.Hotelbooking .orderfields.company').show();
        } else {
            $('.Hotelbooking .orderfields.private').show();
            $('.Hotelbooking .orderfields.company').hide();
        }
        app.Hotelbooking.setSize();
    },
    updateRoomCount : function() {
        var count = $(this).val();
        var event = thundashop.Ajax.createEvent("","updateRoomCount", $(this), { "count": count});
        thundashop.Ajax.postWithCallBack(event, function() {
            
        });
    },
    checkAvailability : function() {
        var nextpage = $(this).attr('nextpage');
        var apparea =$(this).closest('.app'); 
        var data = {
            start : apparea.find('#start_date').val(),
            stop : apparea.find('#end_date').val(),
            roomProduct : apparea.find('.room_selection').val()
        };
        var event = thundashop.Ajax.createEvent('','checkavailability', $(this), data);
        thundashop.Ajax.postWithCallBack(event, function(result) {
            result = parseInt(result);
            if(result <= 0) {
                $('.Hotelbooking .room_available').css('padding-top','20px');
                $('.Hotelbooking .error_on_check').hide();
                if(result === -1) {
                    $('.Hotelbooking .date_before_start').show();
                }
                if(result === -2) {
                    $('.Hotelbooking .end_before_start').show();
                }
                app.Hotelbooking.setSize();
            } else {
                document.location.href='/?page='+nextpage;
            }
        });
    },
    changeBookingDate : function() {
        if($(this).hasClass('disabled')) {
            return;
        }
        var cal = $(this).closest('.booking_table');
        cal.find('.selected').removeClass('selected');
        $(this).addClass('selected');
        
        var event = thundashop.Ajax.createEvent("", "updateCalendarDate", $(this), {
            "type" : cal.attr('type'),
            "day" : $(this).attr('day'),
            "year" : cal.attr('year'),
            "month" : cal.attr('month')
        });
        
        thundashop.Ajax.post(event);
    },
    goToPage : function(pagenumber) {
         window.history.pushState({url: "", ajaxLink: "pagenumber="+pagenumber}, "Title", "pagenumber="+pagenumber);
         thundashop.Ajax.doJavascriptNavigation("pagenumber="+pagenumber, null, true);
    },
    initEvents : function() {
        $(document).on('click', '.Hotelbooking .check_available_button', app.Hotelbooking.checkAvailability);
        $(document).on('change', '.Hotelbooking #ordertype', app.Hotelbooking.changeOrderType);
        $(document).on('change', '.Hotelbooking .number_of_rooms', app.Hotelbooking.updateNumberOfRooms);
        $(document).on('click', '.Hotelbooking .selectbutton', app.Hotelbooking.setSelectedRoom);
        $(document).on('click', '.Hotelbooking .cal_field', app.Hotelbooking.changeBookingDate);
        $(document).on('change', '.Hotelbooking .number_of_rooms', app.Hotelbooking.updateRoomCount);
        $(document).on('blur', '.Hotelbooking .number_of_rooms', app.Hotelbooking.updateRoomCount);
    }
};

app.Hotelbooking.initEvents();