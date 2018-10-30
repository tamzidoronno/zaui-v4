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
        $(document).on('click', '.InvoiceOverview .sendemail', app.InvoiceOverview.sendEmail);
        $(document).on('click', '.InvoiceOverview .closesendpaymentlink', app.InvoiceOverview.closePaymentLinkWindow);
    },
    doneSendingInvoice : function() {
        thundashop.framework.reprintPage();
    },
    doneSendingEHF : function(res) {
        if(res) {
            thundashop.common.Alert("Error", res, true);
        } else {
            thundashop.framework.reprintPage();
        }
    },
    closePaymentLinkWindow : function() {
        $('.sendpaymentlinkwindow').slideUp();
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
    sendEmail : function() {
        var btn = $(this);
        var roomId = $(this).attr('roomid');
        var orderid = $(this).attr('orderid');
        $('.currentsentdate').removeClass('currentsentdate');
        $(this).closest('.datarow').find('.sentdate').addClass('currentsentdate');
        var event = thundashop.Ajax.createEvent('','loadSendEmail',$(this),{
            "roomid" : roomId,
            "id" : orderid
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            btn.parent().find('.sendpaymentlinkwindow').html(res);
            btn.parent().find('.sendpaymentlinkwindow').slideDown();
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