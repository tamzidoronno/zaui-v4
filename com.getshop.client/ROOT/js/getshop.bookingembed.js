(function ( $ ) {
    $.fn.getshopbooking = function( options ) {
        if(options.endpoint === "https://lofoten.booking.fasthotels.no/") { options.domain = "demo"; }
        if(options.endpoint === "https://svolver.booking.fasthotels.no/") { options.domain = "demo"; }
        if(options.endpoint === "http://lofoten.booking.fasthotels.no/") { options.domain = "demo"; }
        if(options.endpoint === "http://svolver.booking.fasthotels.no/") { options.domain = "demo"; }

        
        var box = $(this);
        getshop_endpoint = options.endpoint;
        getshop_viewmode = options.viewmode;
        getshop_terminalid = options.terminalid;
        getshop_nextPage = options.nextPage;
        getshop_successcallback = options.success;
        getshop_failurecallback = options.failure;
        getshop_websockethost = options.websocket;
        getshop_overridetranslation = options.translation;
        sessionStorage.setItem('getshop_endpoint',options.endpoint);
        sessionStorage.setItem('getshop_domain',options.domain);
        sessionStorage.setItem('getshop_booking_form_options', JSON.stringify(options));
        
        
        if(!options.language) {
            sessionStorage.setItem('getshop_language',"");
        } else {
            sessionStorage.setItem('getshop_language',options.language);
        }
        console.log(options);
        var jsendpoint = "https://system.getshop.com/";
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
            "url": jsendpoint+'scripts/booking/bookingcontent.php',
            success: function (form) {
                box.html("<span class='GslBooking'>" + form + "</span>");
            }
        });
        
    };

    $('<link/>', {
       rel: 'stylesheet',
       type: 'text/css',
       href: 'https://20420.getshop.com/icomoon/style.css?tyxklk'
    }).appendTo('head');

    $('<link/>', {
       rel: 'stylesheet',
       type: 'text/css',
       href: 'https://maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css'
    }).appendTo('head');

    $('<link/>', {
       rel: 'stylesheet',
       type: 'text/css',
       href: 'https://cdnjs.cloudflare.com/ajax/libs/jqueryui/1.12.1/jquery-ui.min.css'
    }).appendTo('head');

    $('<link/>', {
       rel: 'stylesheet',
       type: 'text/css',
       href: 'https://www.getshop.com/js/daterangepicker/daterangepicker.css'
    }).appendTo('head');
    
}( jQuery ));
