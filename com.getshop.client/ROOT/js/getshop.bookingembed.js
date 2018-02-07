(function ( $ ) {
    $.fn.getshopbooking = function( options ) {
        var box = $(this);
        getshop_endpoint = options.endpoint;
        
        $.ajax({
            "type": "get",
            async: false,
            "url": 'https://www.getshop.com/scripts/booking/bookingcontent.php',
            success: function (form) {
                box.html("<span class='GslBooking'>" + form + "</span>");
            }
        });
        
    };
 
}( jQuery ));