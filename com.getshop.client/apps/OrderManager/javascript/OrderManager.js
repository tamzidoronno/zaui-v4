app.OrderManager = {
    init : function() {
        $(document).on('change', '#gss_order_filter_search_text', app.OrderManager.filterSearch);   
        $(document).on('click', '.gss_overview_order', app.OrderManager.showOrderSmall);   
        $(document).on('click', '.gss_show_order_action', app.OrderManager.showOrder);   
        $(document).on('click', '.gss_order_back_to_orders', app.OrderManager.showOverview);   
        $(document).on('click', '.gss_changeOrderLine', app.OrderManager.changeOrderLine);   
        $(document).on('click', '.gss_changeOrderCount', app.OrderManager.changeOrderCount);   
        $(document).on('click', '.gss_addOrderItem', app.OrderManager.addOrderItem);   
        $(document).on('click', '.gss_order_view_select_payement_method', app.OrderManager.changePaymentType);   
        $(document).on('click', '.gss_mark_order_as_paid', app.OrderManager.markOrderAsPaid);   
        $(document).on('click', '.gss_mark_resendtoaccounting', app.OrderManager.resendToAccounting);   
        $(document).on('click', '.gsscheckallorders', app.OrderManager.checkallorders);   
        $(document).on('click', '.creditinvoice', app.OrderManager.creditOrder);   
        $(document).on('click', '.gss_setneworderstate', app.OrderManager.setNewOrderState);   
        $(document).on('click', '.gss_orderview .lineStartDate,.gss_orderview .lineEndDate,.gss_orderview .additionalMetaData', app.OrderManager.setNewData);   
        $(document).on('click', '#updateinvoiceinfo', app.OrderManager.updateInvoiceInformation);
        $(document).on('click', '.gsspayorder', app.OrderManager.payorder);
        $(document).on('click', '.gsspaywithcard', app.OrderManager.payWithCard);
        $(document).on('click', '.gss_changePaymentType', function() {
            $('.gss_orderview_available_payments').slideDown();
        });   
    },
    
    setNewData : function() {
        var res = prompt("New value", $(this).text());
        if(!res) {
            return;
        }
        var orderId = $('.orderoverview').attr('orderid');
        var data = {
            gss_fragment: 'orderview',
            gss_view: 'gss_orderview',
            "value" : orderId,
            "newValue" : res,
            "itemid" : $(this).closest('.gss_order_line').attr('cartitemid'),
            "type" : $(this).attr('gstype')
        }
        getshop.Settings.post(data, "updateAdditionalData"); 
    },
    
    setNewOrderState : function() {
        var orders = [];
        $('.gssordercheckbox').each(function() {
            var id = $(this).attr('orderid');
            if($(this).is(':checked')) {
                orders.push(id);
            }
        });
        
        var data = {
            "orders" : orders,
            "state" : $('.gss_neworderstate').val()
        };
        
        //Post this data, and refresh the filtered orderlist.
        getshop.Settings.post(data, "updateOrderState"); 
        
    },
    checkallorders : function() {
        if($(this).is(':checked')) {
            $('.gssordercheckbox').attr('checked','checked');
        } else {
            $('.gssordercheckbox').attr('checked',null);
        }
    },
    showOrderSmall: function(event) {
        if($(event.target).is(':checkbox')) {
            return;
        }

        var smallView = $(this).closest('.gss_overview_order_outer').find('.gss_small_order_listview');
        if (smallView.is(':visible')) {
            smallView.slideUp();
        } else {
            $('.gss_small_order_listview').slideUp();
            smallView.slideDown();
        }
        
    },
    
    payorder : function() {
        var orderid = $(this).attr('orderid');
    $('#backsidesettings').fadeOut(0, function () {
            $('#gsbody').fadeIn(300);
            var event = thundashop.Ajax.createEvent(null, "unsetShowingSettings", null, {});
            thundashop.Ajax.postWithCallBack(event, function() {
                window.location.href="/?page=cart&payorder=" + orderid;
            });
        });
    },
    creditOrder : function() {
        var orderId = $(this).attr('orderId');
        
        var data = {
            gss_fragment: 'orderview',
            gss_view: 'gss_orderview',
            value: orderId,
        }
        
        getshop.Settings.post(data, "creditOrder");
    },
    payWithCard : function() {
        var orderId = $(this).attr('orderId');
        var card = $('#gssselectedcard').val();
        var data = {
            gss_fragment: 'orderview',
            gss_view: 'gss_orderview',
            value: orderId,
            card : card
        }
        
        getshop.Settings.post(data, "payWithCard");
    },
    updateInvoiceInformation : function() {
        var text = $('#invoiceinformationbox').val();
         var orderId = $('.orderoverview').attr('orderid');
        var data = {
            gss_fragment: 'orderview',
            gss_view: 'gss_orderview',
            "text" : text,
            value: orderId,
            "orderId" : orderId
        }
         getshop.Settings.post(data, "updateInvoiceInformation");
         thundashop.common.Alert('Updated', "information has been added");
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
    resendToAccounting: function() {
        var confirmed = confirm("Are you sure you want to resend this order to the accounting system?");
        if(!confirmed) {
            return;
        }
        var orderId = $(this).attr('orderId');
        var data = {
            gss_fragment: 'orderview',
            gss_view: 'gss_orderview',
            value: orderId
        }
        
        getshop.Settings.post(data, "resendToAccounting");
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
    
    addOrderItem : function() {
        var orderId = $(this).closest('.orderoverview').attr('orderid');
        var productId = $('#addproductitem').val();
        var data = {
            gss_fragment: 'orderview',
            gss_view: 'gss_orderview',
            productId: productId,
            value: orderId
        }
        
        getshop.Settings.post(data, "addItemToOrder");
    },
    
    changeOrderCount : function() {
        var cartItem = $(this).closest('.gss_order_line').attr('cartItemId');
        var newValue = prompt(__f("Please enter the new count for this order line"));
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
            count : newValue
        }
        
        getshop.Settings.post(data, "updateOrderCount");
    },
    
    updateMultipleOrderStates : function() {
        var cartItem = $(this).closest('.gss_order_line').attr('cartItemId');
        var newValue = prompt(__f("Please enter the new count for this order line"));
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
            count : newValue
        }
        
        getshop.Settings.post(data, "updateOrderCount");
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