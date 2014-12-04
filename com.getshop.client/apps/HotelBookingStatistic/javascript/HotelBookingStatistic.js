app.HotelBookingStatistic = {
    init: function() {
        $(document).on('click', '#searchforstatistic', app.HotelBookingStatistic.search)
    },
    
    search: function() {
        var data = {
            start: $('#start_date').val(),
            end: $('#end_date').val(),
        };
        
        var event = thundashop.Ajax.createEvent(null, "setDates", this, data);
        thundashop.Ajax.post(event);
    }
}

app.HotelBookingStatistic.init();