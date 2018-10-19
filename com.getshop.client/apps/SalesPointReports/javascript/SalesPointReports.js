app.SalesPointReports = {
    init: function() {
        $(document).on('click', '.SalesPointReports .leftmenu .entry[tab="listtab"]', app.SalesPointReports.historyMenuClicked);
        $(document).on('click', '.SalesPointReports .leftmenu .entry[tab="xreport"]', app.SalesPointReports.xReportMenuClicked);
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
    
    xReportMenuClicked: function() {
        var data = {
            tab : $(this).attr('tab')
        };
        
        var event = thundashop.Ajax.createEvent(null, "showXReport", this, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(res) {
            $('.SalesPointReports .reportarea').html(res);
        });
    }
};

app.SalesPointReports.init();