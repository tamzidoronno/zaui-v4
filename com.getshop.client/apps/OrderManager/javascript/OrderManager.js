app.OrderManager = {
    init : function() {
        $(document).on('change', '#gss_order_filter_search_text', app.OrderManager.filterSearch);   
        $(document).on('click', '.gss_overview_order', app.OrderManager.showOrder);   
        $(document).on('click', '.gss_order_back_to_orders', app.OrderManager.showOverview);   
        $(document).on('click', '.gss_changeOrderLine', app.OrderManager.changeOrderLine);   
        $(document).on('click', '.gss_order_view_select_payement_method', app.OrderManager.changePaymentType);   
        $(document).on('click', '.gss_mark_order_as_paid', app.OrderManager.markOrderAsPaid);   
        $(document).on('click', '.gss_changePaymentType', function() {
            $('.gss_orderview_available_payments').slideDown();
        });   
    },
    
    markOrderAsPaid: function() {
        var orderId = $(this).attr('orderId');
        var data = {
            gss_fragment: 'orderview',
            gss_view: 'gss_orderview',
            value: orderId
        }
        
        getshop.Settings.post(data, "markOrderAsPaid");
    },
    
    changePaymentType: function() {
        var newPayemntIdType = $(this).attr('payementMethodId');
        var orderId = $(this).closest('.orderoverview').attr('orderid');
     
        var data = {
            gss_fragment: 'orderview',
            gss_view: 'gss_orderview',
            value: orderId,
            newPayemntIdType: newPayemntIdType
        }
        
        getshop.Settings.post(data, "changePaymentType");
    },
    
    changeOrderLine: function() {
        var cartItem = $(this).closest('.gss_order_line').attr('cartItemId');
        var newValue = prompt(__f("Please enter the new value for this orderline"));
        var orderId = $(this).closest('.orderoverview').attr('orderid');
        
        if (!newValue) {
            return;
        }
        
        if (newValue === "" || isNaN(newValue)) {
            alert(__f('You did not enter a valid number'));
            return;
        }
        
        var data = {
            gss_fragment: 'orderview',
            gss_view: 'gss_orderview',
            value: orderId,
            cartItemId: cartItem,
            price : newValue
        }
        
        getshop.Settings.post(data, "updateOrderLine");
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

app.OrderManager.gssinterface = {
    showOrder: function(orderId) {
        getshop.Settings.showSettings();
        getshop.Settings.setApplicationId('27716a58-0749-4601-a1bc-051a43a16d14', function () {
            var data = {
                gss_fragment: 'orderview',
                gss_view: 'gss_orderview',
                gss_value: orderId
            }

            getshop.Settings.post({}, "gs_show_fragment", data);
            app.OrderManager.showOrder();
        });
    }
}


app.OrderManager.init();