app.SalesPointReports = {
    init: function() {
        $(document).on('click', '.SalesPointReports .leftmenu .entry[tab="listtab"]', app.SalesPointReports.historyMenuClicked);
        $(document).on('click', '.SalesPointReports .leftmenu .entry[tab="salesreport"]', app.SalesPointReports.salesReportClicked);
        $(document).on('click', '.SalesPointReports .leftmenu .entry[tab="xreport"]', app.SalesPointReports.xReportMenuClicked);
        $(document).on('click', '.SalesPointReports .leftmenu .entry[tab="masterreport"]', app.SalesPointReports.showMasterReport);
        $(document).on('click', '.SalesPointReports .leftmenu .entry[tab="stock"]', app.SalesPointReports.showStockReport);
        $(document).on('click', '.SalesPointReports .leftmenu .entry[tab="taxcorrection"]', app.SalesPointReports.showTaxCorrection);
        $(document).on('click', '.SalesPointReports .taxcorrectionrow .fixcol', app.SalesPointReports.fixTaxCorrection);
        $(document).on('click', '.SalesPointReports .dotaxcorrection', app.SalesPointReports.doTaxCorrection);
    },
    
    doTaxCorrection: function() {
        if ($(this).attr('inprogress')) {
            alert("Already in progress, please wait until the progress has completed");
        }
        
        $(this).attr('inprogress', 'true');
        
        $(this).html('<span class="fa fa-spin fa-spinner"><span>');
        
        $('.SalesPointReports .taxcorrectionrow .fixcol').each(function() {
            var me = this;
            setTimeout(function() {
                $(me).click();
            }, 100);
        });
        
    },
    
    fixTaxCorrection: function() {
        $(this).html('<i class="fa fa-spin fa-spinner"></i>');
        var event = thundashop.Ajax.createEvent(null, "fixTaxCorrection", this, { orderid : $(this).attr('orderid') });
        event['synchron'] = true;
        var me = this;
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $(me).closest('.taxcorrectionrow').hide();
            
            var allClosed = true;
            
            $('.SalesPointReports .taxcorrectionrow').each(function() {
                
                var shouldIgnore = $(this).hasClass('tax_row_header') || $(this).hasClass('tax_row_footer');
                
                if (shouldIgnore)
                    return;
                
                if ($(this).is(':visible')) {
                    allClosed = false;
                }
            });
            
            if (allClosed) {
                $('.dotaxcorrection').html("All done");
            }
        });
    },
    
    showStockReport : function() {
        var data = {
            tab : $(this).attr('tab')
        };
        
        var event = thundashop.Ajax.createEvent(null, "showStockReport", this, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(res) {
            $('.SalesPointReports .reportarea').html(res);
        });
    },
    showTaxCorrection : function() {
        var data = {
            tab : $(this).attr('tab')
        };
        
        var event = thundashop.Ajax.createEvent(null, "showTaxCorrection", this, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(res) {
            $('.SalesPointReports .reportarea').html(res);
        });
    },
    
    salesReportClicked : function() {
           var data = {
            tab : $(this).attr('tab')
        };
        
        var event = thundashop.Ajax.createEvent(null, "showSalesReport", this, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(res) {
            $('.SalesPointReports .reportarea').html(res);
        });
    },
    historyMenuClicked: function() {
        var data = {
            tab : $(this).attr('tab')
        };
        
        var event = thundashop.Ajax.createEvent(null, "showReportList", this, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(res) {
            $('.SalesPointReports .reportarea').html(res);
        });
    },
    
    showMasterReport: function() {
        var data = {
            tab : $(this).attr('tab')
        };
        
        var event = thundashop.Ajax.createEvent(null, "showMasterReport", this, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(res) {
            $('.SalesPointReports .reportarea').html(res);
        });
    },
    
    xReportMenuClicked: function() {
        var data = {
            tab : $(this).attr('tab')
        };
        
        var event = thundashop.Ajax.createEvent(null, "showXReport", this, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(res) {
            $('.SalesPointReports .reportarea').html(res);
        });
    },
    
    addOrderToZRepport: function(incrementalorderid, password) {
        thundashop.Ajax.simplePost($('.SalesPointReports'), "", {
            addorder : incrementalorderid,
            password : password
        });
    },
    
    getpassword: function() {
        var pass = prompt("password");
        return {
            password : pass
        }
    }
};

app.SalesPointReports.init();