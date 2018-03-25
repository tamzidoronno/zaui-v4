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
        $(document).on('keyup','.PmsNewBooking [gsname="nameofholder"]',app.PmsNewBooking.checkForExisting);
        $(document).on('click','.PmsNewBooking .selectuser',app.PmsNewBooking.selectUser);
        $(document).on('click','.PmsNewBooking .selecteduser .fa-trash-o',app.PmsNewBooking.removeSelectedUser);
    },
    
    removeSelectedUser : function() {
        $('.PmsNewBooking [gsname="nameofholder"]').val("");
        $('.PmsNewBooking [gsname="nameofholder"]').show();
        $('.PmsNewBooking .selecteduser').hide();
    },
    
    selectUser : function() {
        var userid = $(this).closest('tr').attr('userid');
        $('.PmsNewBooking [gsname="nameofholder"]').val(userid);
        $('.PmsNewBooking [gsname="nameofholder"]').hide();
        $('.PmsNewBooking .selecteduser').show();
        $('.PmsNewBooking .selecteduser').find('.name').html($(this).closest('tr').attr('name'));
    },
    
    checkForExisting : function() {
        var text = $(this).val();
        if(text.length < 3) {
            return;
        }
        
        var event = thundashop.Ajax.createEvent('','checkForExisiting',$(this), {
            "text" : text
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.PmsNewBooking .alreadyexists').html(res);
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