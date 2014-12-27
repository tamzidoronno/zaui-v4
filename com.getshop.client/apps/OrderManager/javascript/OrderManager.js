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
            ['January', app.OrderManager.yearStats[1], 'color: #008ad2'],
            ['Feburary', app.OrderManager.yearStats[2], 'color: #008ad2'],
            ['March', app.OrderManager.yearStats[3], 'color: #008ad2'],
            ['April', app.OrderManager.yearStats[4], 'color: #008ad2'],
            ['May', app.OrderManager.yearStats[5], 'color: #008ad2'],
            ['June', app.OrderManager.yearStats[6], 'color: #008ad2'],
            ['July', app.OrderManager.yearStats[7], 'color: #008ad2'],
            ['August', app.OrderManager.yearStats[8], 'color: #008ad2'],
            ['September', app.OrderManager.yearStats[9], 'color: #008ad2'],
            ['Oktober', app.OrderManager.yearStats[10], 'color: #008ad2'],
            ['November', app.OrderManager.yearStats[11], 'color: #008ad2'],
            ['December', app.OrderManager.yearStats[12], 'color: #008ad2']
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