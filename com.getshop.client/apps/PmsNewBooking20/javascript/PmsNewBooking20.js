app.PmsNewBooking20 = {
    init : function() {
        $(document).on('click','.newbookingoption', app.PmsNewBooking20.goToStep);
    },
    continueToStep2 : function() {
        app.PmsNewBooking20.doGoToStep('newbookingstep2');
    },
    userSavedUpdated : function() {
        $('.PmsNewBooking20 .GetShopQuickUser .change_user_form, .PmsNewBooking20 .GetShopQuickUser .edit_details_of_user').show();
    },
    goToStep : function() {
        var goto = $(this).attr('goto');
        app.PmsNewBooking20.doGoToStep(goto);
    },
    doGoToStep : function(goto) {
        $.when($('.PmsNewBooking20 .newbookingprocess').fadeOut(100))
                               .done(function() {
            $('.PmsNewBooking20 .newbookingprocess[for="'+goto+'"]').fadeIn();
        });
    }
};

app.PmsNewBooking20.init();