app.PmsNewBooking = {
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
    },
    reloadAvailability : function() {
        var event = thundashop.Ajax.createEvent('','reloadAvailability',$('.PmsNewBooking'), {
            "start" : $('#startdate').val(),
            "end" : $('#enddate').val()
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.availablerooms').html(res);
        });
    }
};

app.PmsNewBooking.init();