(function ( $ ) {
    $.fn.getshopbooking = function( options ) {
        var box = $(this);
        getshop_endpoint = options.endpoint;
        getshop_viewmode = options.viewmode;
        getshop_terminalid = options.terminalid;
        localStorage.setItem('getshop_endpoint',options.endpoint);
        
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
(function ( $ ) {
        $.fn.getshopbookingfront = function( options ) {
        var box = $(this);
        localStorage.setItem('getshop_endpoint',options.endpoint);
        getshop_endpoint = options.endpoint;
        getshop_urltonextpage = options.nextpage;
        $.ajax({
            "type": "get",
            async: false,
            "url": 'https://www.getshop.com/scripts/booking/bookingcontentfront.php',
            success: function (form) {
                box.html("<span class='GslBookingFront'>" + form + "</span>");
            }
        });
        
    };

}( jQuery ));

$('<link/>', {
   rel: 'stylesheet',
   type: 'text/css',
   href: 'https://s3.amazonaws.com/icomoon.io/135206/GetShopIcons/style.css?tyxklk'
}).appendTo('head');

$('<link/>', {
   rel: 'stylesheet',
   type: 'text/css',
   href: '//maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css'
}).appendTo('head');

$('<link/>', {
   rel: 'stylesheet',
   type: 'text/css',
   href: 'https://www.getshop.com/scripts/booking/bookingstyles.php'
}).appendTo('head');

$('<link/>', {
   rel: 'stylesheet',
   type: 'text/css',
   href: 'https://www.getshop.com/js/daterangepicker/daterangepicker.css'
}).appendTo('head');
