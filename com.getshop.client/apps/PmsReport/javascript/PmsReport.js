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
        $(document).on('click', '.PmsReport .filterbycustomerbutton', app.PmsReport.toggleFilterByCustomer);
        $(document).on('click', '.PmsReport .displayto30list', app.PmsReport.toggleTop30List);
        $(document).on('click', '.PmsReport .top30customerbutton', app.PmsReport.top30CustomersLoading);
        $(document).on('change', '.PmsReport [gsname="segment"]', app.PmsReport.changeSegments);
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