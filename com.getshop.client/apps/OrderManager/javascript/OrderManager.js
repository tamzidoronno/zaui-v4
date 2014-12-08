app.OrderManager = {
    init : function() {
        $(document).on('change', '#gss_order_filter_search_text', app.OrderManager.filterSearch);   
        $(document).on('click', '.gss_overview_order', app.OrderManager.showOrder);   
        $(document).on('click', '.gss_order_back_to_orders', app.OrderManager.showOverview);   
    },
    
    filterSearch: function() {
        if (!$('.gss_outer_orderoverview').is(':visible')) {
            app.OrderManager.showOverview();
        }
        
        $('#gss_order_filter_search_button').click();
    },
    
    showOrder: function() {
        app.OrderManager.widthOfOrderOverview = $('.gss_outer_orderoverview').width();

        $('.gss_outer_orderoverview').animate({ width: '0px'}, 300, function() { 
            $(this).hide();
            $('.gss_outer_order_content').slideDown();
        });
    },
    
    showOverview: function() {
        $('.gss_outer_order_content').slideUp(300, function() {
            $(this).hide();
            $('.gss_outer_orderoverview').show();
            $('.gss_outer_orderoverview').animate({ width: app.OrderManager.widthOfOrderOverview}, 300, function() { 

            });
        });
    }
        
};
app.OrderManager.init();