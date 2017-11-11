app.PmsAvailability = {
    init: function() {
        $(document).on('mouseenter', '.PmsAvailability .contains_booking', this.mouseOver);
        $(document).on('mouseleave', '.PmsAvailability .contains_booking', this.mouseOut);
        $(document).on('click', '.PmsAvailability .contains_booking', this.showMenuBox);
        $(document).on('click', '.PmsAvailability .canUse', this.addSelectedClass);
    },
    
    updateAvailability: function() {
        $('.PmsAvailability .itemview').addClass('update_in_progress');
        
        var data = {
            bookingid: $('.PmsAvailability .itemview').attr('bookingid'),
            start : $('.PmsAvailability .startdate').val(),
            end : $('.PmsAvailability .enddate').val()
        };
        
        var event = thundashop.Ajax.createEvent(null, "loaditemview", $('.PmsAvailability'), data);
        
        event['synchron'] = true;
        thundashop.Ajax.post(event, function(res) {
            $('.PmsAvailability .itemview').html(res);
            $('.PmsAvailability .itemview').removeClass('update_in_progress');
        });
    },
    
    addSelectedClass: function() {
        $('.PmsAvailability .gs_selected').removeClass('gs_selected');
        $(this).addClass('gs_selected');
    },
    
    showMenuBox: function(event) {
        var bookingId = $(this).attr('bookingId');
        $('.PmsAvailability .bookingoverview').css('left', event.pageX + "px");
        $('.PmsAvailability .bookingoverview').css('top', (event.pageY-300) + "px");
        $('.PmsAvailability .bookingoverview').fadeIn();
    
        var data = {
            bookingid : bookingId,
            tab: ""
        };
        
        var event = thundashop.Ajax.createEvent(null, "showBookingInformation", this, data);
        event['synchron'] = true;
        thundashop.Ajax.post(event, function(res) {
            $('.PmsAvailability .bookingoverview .tab_content').html(res);
        });
    },
    
    mouseOver: function() {
        var bookingId = $(this).attr('bookingId');
        $('.PmsAvailability .contains_booking[bookingid="'+bookingId+'"]').addClass('mouseover');
    },
    
    mouseOut: function() {
        var bookingId = $(this).attr('bookingId');
        $('.PmsAvailability .contains_booking[bookingid="'+bookingId+'"]').removeClass('mouseover');
    }
    
}

app.PmsAvailability.init();