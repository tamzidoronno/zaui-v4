app.PmsBookingMultipleSelectionBooking = {
    init: function () {
        $(document).on('click', '.PmsBookingMultipleSelectionBooking .tablinks', app.PmsBookingMultipleSelectionBooking.chooseBooking);
        $(document).on('click', '.PmsBookingMultipleSelectionBooking .searchbooking', app.PmsBookingMultipleSelectionBooking.searchBooking);
    },  

    chooseBooking : function() {
        var area = $(this).attr('area');
        $('.activeTab').removeClass('activeTab');
        $(this).addClass('activeTab');
        $('.tabcontent').hide();
        $('#'+area).show();
    },
    searchBooking : function(){
            var next = $(this).attr('next_page');
            var app = $(this).closest('.app');
            var event = thundashop.Ajax.createEvent('','initBooking',$(this), {
                start : app.find('.start_date_input').val(),
                end : app.find('.end_date_input').val(),
            });
            thundashop.Ajax.postWithCallBack(event, function() {
                thundashop.common.goToPageLink(next);
            });
    }
};  
app.PmsBookingMultipleSelectionBooking.init();