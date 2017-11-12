app.PmsReport = {
    switchBooking : function() {
        $('.PmsReport .pmsbutton.tab').removeClass('selected');
        $(this).addClass('selected');
        $('#coverageview').val($(this).attr('type'));
        $('.reportview').html('');
    },
    
    init : function() {
        $(document).on('click', '.PmsReport .pmsbutton.tab', app.PmsReport.switchBooking);
    }
};

app.PmsReport.init();