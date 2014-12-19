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
    },
    
    drawChart: function(chartDiv) {
        var data = new google.visualization.DataTable();
        data.addColumn('string', 'Month');
        data.addColumn('number');
        data.addColumn({type: 'string', role: 'style'});
        data.addRows([
            ['January', 40340, 'color: #008ad2'],
            ['Feburary', 65710, 'color: #008ad2'],
            ['March', 52408, 'color: #008ad2'],
            ['April', 65090, 'color: #008ad2'],
            ['May', 75440, 'color: #008ad2'],
            ['June', 90850, 'color: #008ad2'],
            ['July', 88465, 'color: #008ad2'],
            ['August', 87040, 'color: #008ad2'],
            ['September', 80720, 'color: #008ad2'],
            ['Oktober', 78295, 'color: #008ad2'],
            ['November', 21680, 'color: #008ad2'],
            ['December', 11200, 'color: #008ad2']
        ]);

        // Set chart options
        var options = {
            'title': 'Your sales',
            'width': 1120,
            'height': 200
        };

        var chart = new google.visualization.ColumnChart(chartDiv);
        chart.draw(data, options);
    }   
};
app.OrderManager.init();