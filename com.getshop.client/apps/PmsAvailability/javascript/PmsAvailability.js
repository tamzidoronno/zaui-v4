app.PmsAvailability = {
    init: function() {
        $(document).on('mouseenter', '.PmsAvailability .contains_booking', this.mouseOver);
        $(document).on('mouseleave', '.PmsAvailability .contains_booking', this.mouseOut);
        $(document).on('click', '.PmsAvailability .contains_booking', this.showMenuBox);
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