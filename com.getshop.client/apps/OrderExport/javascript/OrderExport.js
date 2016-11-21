app.OrderExport = {
    init : function() {
        $(document).on('click', '.gssaddaccounting', app.OrderExport.addAccountingType);
        $(document).on('click', '.integrationselection', app.OrderExport.integrationselection);
        $(document).on('click', '.gss_addfiltertoconfig', app.OrderExport.addfiltertoconfig);
        $(document).on('click', '.gssremovepaymenttype', app.OrderExport.removepaymenttype);
        $(document).on('click', '.deletetransferconfig', app.OrderExport.deletetransferconfig);
        $(document).on('click', '.gss_downloadaccountingfile', app.OrderExport.downloadFile);
        $(document).on('click', '.viewstatsforfile', app.OrderExport.viewstatsforfile);
    },
    
    viewstatsforfile : function() {
         var data = {
            "configId" : $(this).val()
        }
        var event = thundashop.Ajax.createEvent('','printStatistics','ns_13270e94_258d_408d_b9a1_0ed3bbb1f6c9\\OrderExport', data);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.orderdistrstats').html(res);
        });
    },
    
    downloadFile : function() {
        var configid = $(this).attr('configid');
        var start = $(".gss_accdownloadstart[configid='"+configid+"']").val();
        var end = $(".gss_accdownloadend[configid='"+configid+"']").val();
        window.open('/scripts/accountingnew.php?configid=' + configid + "&start=" + start + "&end=" + end);
        app.OrderExport.refresh();
    },
    refresh : function() {
        $('[gss_goto_app="13270e94-258d-408d-b9a1-0ed3bbb1f6c9"]').click();
    },
    deletetransferconfig : function() {
        var confirmed = confirm("Are you sure you want to delete this configuration?");
        if(!confirmed) {
            return;
        }
        var data = {
            "configid" : $(this).attr('configid')
        }
        var event = thundashop.Ajax.createEvent('','removeTransferConfig','ns_13270e94_258d_408d_b9a1_0ed3bbb1f6c9\\OrderExport', data);
        thundashop.Ajax.postWithCallBack(event, function() {
            app.OrderExport.refresh();
        });
        
    },
    removepaymenttype : function() {
        var data = {
            "type" : $(this).attr('type'),
            "status" : $(this).attr('status'),
            "configid" : $(this).attr('configid')
        }
        var event = thundashop.Ajax.createEvent('','removePaymentTypeToConfig','ns_13270e94_258d_408d_b9a1_0ed3bbb1f6c9\\OrderExport', data);
        thundashop.Ajax.postWithCallBack(event, function() {
            app.OrderExport.refresh();
        });
        
    },
    addfiltertoconfig : function() {
        var container = $(this).closest('.gssaddpaymentcontainer');
        var data = {
            "configid" : $(this).attr('configid'),
            "paymentmethod" : container.find('.gsspaymentmethod').val(),
            "paymentstatus" : container.find('.gsspaymentstatus').val()
        }
        var event = thundashop.Ajax.createEvent('','addPaymentTypeToConfig','ns_13270e94_258d_408d_b9a1_0ed3bbb1f6c9\\OrderExport', data);
        thundashop.Ajax.postWithCallBack(event, function() {
            app.OrderExport.refresh();
        });
    },
    integrationselection : function() {
        $('.selectedintegration').removeClass('selectedintegration');
        $(this).addClass('selectedintegration');
        var type = $(this).attr('type');
        $('.configfield').hide();
        $('.configfield[type="'+type+'"]').show();
        localStorage.setItem("savedTabOrderExport", type);
    },
    addAccountingType : function() {
        var data = {
            "newtype" : $('.gssaddaccountingtype').val()
        }
        var event = thundashop.Ajax.createEvent('','addNewConfig','ns_13270e94_258d_408d_b9a1_0ed3bbb1f6c9\\OrderExport', data);
        thundashop.Ajax.postWithCallBack(event, function() {
            app.OrderExport.refresh();
        });
    }
};

app.OrderExport.init();