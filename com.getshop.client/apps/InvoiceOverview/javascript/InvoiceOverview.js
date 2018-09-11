app.InvoiceOverview = {
    
    init : function() {
        $(document).on('click', '.InvoiceOverview .filterbycustomerbutton', function() {
            $('.searchcustomer').show();
            $(this).hide();
            $('[gsname="customer"]').focus();
        });
        $(document).on('click', '.InvoiceOverview .addusertofilter', app.InvoiceOverview.appendUserToFilter);
        $(document).on('click', '.InvoiceOverview .addeduser', app.InvoiceOverview.removeCustomerFromFilter);
        $(document).on('click', '.InvoiceOverview .createneworder', app.InvoiceOverview.loadCreateNewOrderView);
    },
    loadCreateNewOrderView : function() {
        $('.datarow').removeClass('active');
        $('.datarow_extended_content').hide();
        var event = thundashop.Ajax.createEvent('',"loadQuickUser",$(this), {});
        thundashop.Ajax.postWithCallBack(event, function(res){
            $('.neworderview').html(res);
            $('.InvoiceOverview .neworderview').slideDown();
        });
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