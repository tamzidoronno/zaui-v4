app.SalesPointReports = {
    init: function() {
        $(document).on('click', '.SalesPointReports .leftmenu .entry[tab="listtab"]', app.SalesPointReports.historyMenuClicked);
        $(document).on('click', '.SalesPointReports .leftmenu .entry[tab="salesreport"]', app.SalesPointReports.salesReportClicked);
        $(document).on('click', '.SalesPointReports .leftmenu .entry[tab="xreport"]', app.SalesPointReports.xReportMenuClicked);
        $(document).on('click', '.SalesPointReports .leftmenu .entry[tab="masterreport"]', app.SalesPointReports.showMasterReport);
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