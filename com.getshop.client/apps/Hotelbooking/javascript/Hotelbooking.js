app.Hotelbooking = {
    setSize : function() {
        var newHeight = $('.Hotelbooking .booking_page').outerHeight()+30;
        var top = (500 - newHeight)/2;
        $('.Hotelbooking .bookingbox').css('top',top+'px');
        $('.Hotelbooking .bookingbox').css('height',newHeight);
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
    checkAvailability : function() {
        var apparea =$(this).closest('.app'); 
        var data = {
            start : apparea.find('#start_date').val(),
            stop : apparea.find('#end_date').val(),
            roomType : apparea.find('.room_selection').val()
        }
        var event = thundashop.Ajax.createEvent('','checkavailability', $(this), data);
        thundashop.Ajax.postWithCallBack(event, function(result) {
            result = parseInt(result);
            if(result <= 0) {
                app.Hotelbooking.increaseSizeOnBox();
                $('.Hotelbooking .room_available').css('padding-top','20px');
                $('.Hotelbooking .error_on_check').hide();
                if(result === -1) {
                    $('.Hotelbooking .date_before_start').show();
                }
                if(result === -2) {
                    $('.Hotelbooking .end_before_start').show();
                }
            } else {
                app.Hotelbooking.goToPage(2);
            }
        });
    },
    goToPage : function(pagenumber) {
         window.history.pushState({url: "", ajaxLink: "pagenumber="+pagenumber}, "Title", "pagenumber="+pagenumber);
         thundashop.Ajax.doJavascriptNavigation("pagenumber="+pagenumber, null, true);
    },
    initEvents : function() {
        $(document).on('click', '.Hotelbooking .check_available_button', app.Hotelbooking.checkAvailability);
        $(document).on('change', '.Hotelbooking #ordertype', app.Hotelbooking.changeOrderType);
    }
    
}

app.Hotelbooking.initEvents();