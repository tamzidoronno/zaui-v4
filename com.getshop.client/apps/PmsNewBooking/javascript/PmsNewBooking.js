app.PmsNewBooking = {
    init : function() {
        $(document).on('keyup', '.PmsNewBooking [gsname="numberOfRooms"]', function() {
            $('.moreThenAvailableWarning').hide();
           var number = $(this).val();
           var type = $('select[gsname="type"]').val();
           var available = parseInt($('.availableroomscounter[roomtype="'+type+'"]').text());
           console.log(type + " : " + number + " : " + available);
           if(number > available) {
               $(this).addClass('moreThenAvailable');
            $('.moreThenAvailableWarning').slideDown();
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