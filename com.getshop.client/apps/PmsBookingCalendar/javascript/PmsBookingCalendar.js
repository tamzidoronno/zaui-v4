app.PmsBookingCalendar = {
    init : function() {
        $(document).on('click', '.PmsBookingCalendar .fa-arrow-left', app.PmsBookingCalendar.decreaseMonth);
        $(document).on('click', '.PmsBookingCalendar .fa-arrow-right', app.PmsBookingCalendar.decreaseMonth);
        $(document).on('click', '.PmsBookingCalendar .calendar .day', app.PmsBookingCalendar.selectDay);
    },
    selectDay : function() {
         var event = thundashop.Ajax.createEvent('','selectDay',$(this), {
            "time" : $(this).attr('time')
        });
        var box = $(this).closest('.applicationinner');
        thundashop.Ajax.postWithCallBack(event, function(result) {
            box.html(result);
        });
    },
    decreaseMonth : function() {
        var month = "+1";
        if($(this).hasClass('fa-arrow-left')) {
            month = "-1";
        }
        var event = thundashop.Ajax.createEvent('','changeMonth',$(this), {
            "month" : month
        });
        var box = $(this).closest('.applicationinner');
        
        thundashop.Ajax.postWithCallBack(event, function(result) {
            box.html(result);
        });
    }
};

app.PmsBookingCalendar.init();