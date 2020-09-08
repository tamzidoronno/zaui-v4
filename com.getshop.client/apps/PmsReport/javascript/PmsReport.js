app.PmsReport = {
    switchBooking : function() {
        $('.PmsReport .pmsbutton.tab').removeClass('selected');
        $(this).addClass('selected');
        $('#coverageview').val($(this).attr('type'));
        $('.reportview').html('');
    },
    
    init : function() {
        $(document).on('click', '.PmsReport .pmsbutton.tab', app.PmsReport.switchBooking);
        $(document).on('click', '.PmsReport .includecustomerinfilter', app.PmsReport.includeInFilter);
        $(document).on('keyup', '.PmsReport .customersearch', app.PmsReport.searchCustomer);
        $(document).on('click', '.PmsReport .removecustomerfromfilter', app.PmsReport.removeCustomer);
        $(document).on('click', '.PmsReport .removecodefromfilter', app.PmsReport.removeDiscount);
        $(document).on('click', '.PmsReport .filterbycustomerbutton', app.PmsReport.toggleFilterByCustomer);
        $(document).on('click', '.PmsReport .column', app.PmsReport.loadDayInformation);
        $(document).on('click', '.PmsReport .closeDayInformation', app.PmsReport.closeDayInformation);
        $(document).on('click', '.PmsReport .displayto30list', app.PmsReport.toggleTop30List);
        $(document).on('click', '.PmsReport .displayorders', app.PmsReport.displayOrders);
        $(document).on('change', '.PmsReport [gsname="segment"]', app.PmsReport.changeSegments);
    },
    displayOrders : function() {
        var event = thundashop.Ajax.createEvent('','loadOrdersForRoomAndDay', $(this), {
            "pmsbookingroomid" : $(this).attr('pmsbookingroomid'),
            "day" : $(this).attr('day'),
            "price" : $(this).attr('price')
        });
        
        var btn = $(this);
        
        var view =  btn.find('.daypriceoverview');
        if(view.is(':visible')) {
            view.hide();
            return;
        }
        
        $('.daypriceoverview').hide();
        thundashop.Ajax.postWithCallBack(event, function(res) {
            view.toggle();
            view.html(res);
        });
    },
    closeDayInformation : function() {
        $('.PmsReport .informationoverlay').fadeOut();
    },
    loadDayInformation : function() {
        $('.PmsReport .informationoverlay').fadeIn();
        var day = $(this).attr('date');
        var event = thundashop.Ajax.createEvent('','loadDayIncomeReportEntry',$(this), {
            day : day
        });
        
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.PmsReport .informationoverlay .inneroverlay').html(res);
        });
        
    }, 
    changeSegments : function() {
        var val = $(this).val();
        if(val && val !== "") {
            $('.typeselectionboxes').hide();
            $('.typeselectionboxes input').each(function() {
                $(this).attr('checked',null);
            });
        } else {
            $('.typeselectionboxes').show();
        }
    },
    toggleTop30List : function() {
        $('.top30list').toggle();
    },
    toggleFilterByCustomer : function() {
        $('.customerfilter').toggle();
        $('.customerfilter input').focus();
    
        if($('.customerfilter').is(":visible")) {
            var event = thundashop.Ajax.createEvent('', 'loadTop30Customers', $(this), {});
            thundashop.Ajax.postWithCallBack(event, function(res) {
                    $('.top30customerfilter').html(res);
            });
        }
    },
    removeCustomer : function() {
        var row = $(this).closest('.selectedcustomerrow');
        var event = thundashop.Ajax.createEvent('','removeCustomer',$(this), {
            "userid" : $(this).attr('userid')
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            row.remove();
        });
    },
    removeDiscount : function() {
        var row = $(this).closest('.selectedcustomerrow');
        var event = thundashop.Ajax.createEvent('','removeCode',$(this), {
            "code" : $(this).attr('code')
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            row.remove();
        });
    },
    
    includeInFilter : function() {
        var event = thundashop.Ajax.createEvent('','includeCustomerToFilter',$(this), {
            "userid" : $(this).attr('userid')
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.filteredcustomerslist').html(res);
        });
    },
    searchCustomer : function() {
        var text = $(this).val();
        if(typeof(app.PmsReport.searching !== "undefined")) {
            clearTimeout(app.PmsReport.searching);
        }
        var scope = $(this);
        app.PmsReport.searching = setTimeout(function() {
            var event = thundashop.Ajax.createEvent('','searchCustomerToIncludeInFilter', scope, {
                "input" : text
            });
            thundashop.Ajax.postWithCallBack(event, function(res) {
                $('.filtercustomerresult').html(res);
            });
        }, "500");
    }
};

app.PmsReport.init();