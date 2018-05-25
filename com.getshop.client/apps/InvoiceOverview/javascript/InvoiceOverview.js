app.InvoiceOverview = {
    
    init : function() {
        $(document).on('click', '.InvoiceOverview .filterbycustomerbutton', function() {
            $('.searchcustomer').show();
            $(this).hide();
            $('[gsname="customer"]').focus();
        });
        $(document).on('click', '.InvoiceOverview .addusertofilter', app.InvoiceOverview.appendUserToFilter);
        $(document).on('click', '.InvoiceOverview .addeduser', app.InvoiceOverview.removeCustomerFromFilter);
    },
    
    appendUserToFilter : function() {
        var userid = $(this).attr('userid');
        var event = thundashop.Ajax.createEvent('','addCustomerToFilter',$(this),{
            "userid" : userid
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.usersfilterresult').html(res);
        });
    },
    
    removeCustomerFromFilter : function() {
        var userid = $(this).attr('userid');
        var event = thundashop.Ajax.createEvent('','removeCustomerFromFilter',$(this),{
            "userid" : userid
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.usersfilterresult').html(res);
        });
    },
    
    searchCustomerResult : function(res) {
        $('.customersearchresult').show();
        $('.customersearchresultinner').html(res);
    }
};

app.InvoiceOverview.init();