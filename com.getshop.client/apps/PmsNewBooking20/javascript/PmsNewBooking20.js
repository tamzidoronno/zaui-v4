app.PmsNewBooking20 = {
    init : function() {
        $(document).on('click','.newbookingoption', app.PmsNewBooking20.goToStep);
    },
    goToStep : function() {
        var goto = $(this).attr('goto');
        
        $.when($('.PmsNewBooking20 .newbookingprocess').fadeOut(100))
                               .done(function() {
            $('.PmsNewBooking20 .newbookingprocess[for="'+goto+'"]').fadeIn();
        });
    }
};

app.PmsNewBooking20.init();