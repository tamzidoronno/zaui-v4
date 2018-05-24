(function ( $ ) {
    $.fn.getshopbooking = function( options ) {
        var box = $(this);
        getshop_endpoint = options.endpoint;
        getshop_viewmode = options.viewmode;
        getshop_terminalid = options.terminalid;
        getshop_nextPage = options.nextPage;
        sessionStorage.setItem('getshop_endpoint',options.endpoint);
        if(!options.language) {
            sessionStorage.setItem('getshop_language',"");
        } else {
            sessionStorage.setItem('getshop_language',options.language);
        }
        console.log(options);
        var jsendpoint = "https://www.getshop.com/";
        if(options.jsendpoint) {
            jsendpoint = options.jsendpoint;
        }
           
        $('<link/>', {
           rel: 'stylesheet',
           type: 'text/css',
           href: jsendpoint+'scripts/booking/bookingstyles.php'
        }).appendTo('head');
        
        $.ajax({
            "type": "get",
            async: false,
            "url": jsendpoint+'/scripts/booking/bookingcontent.php',
            success: function (form) {
                box.html("<span class='GslBooking'>" + form + "</span>");
            }
        });
        
    };

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
       href: 'https://www.getshop.com/js/daterangepicker/daterangepicker.css'
    }).appendTo('head');
    
}( jQuery ));
