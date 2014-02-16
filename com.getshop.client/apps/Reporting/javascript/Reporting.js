GetShop.Reporting = {
    init: function() {
        $('.Reporting .tracking').live('click', GetShop.Reporting.displaySessionData);
        $(document).live('click', GetShop.Reporting.hideInformationBox);
        $('.Reporting .wordlist .word').live('click', GetShop.Reporting.removeFilterPage);
    },
    
    formPosted : function(data) {
    },
            
    removeFilterPage : function(data) {
        $(this).attr('gsname','removeWord');
        thundashop.framework.submitFromElement($(this));
    },
            
    drawTracking : function(data) {
        $('.Reporting #reportingview').show();
        $('.Reporting #reportingview .reporting_inner').html(data);
    },
    
    drawChart: function(data, element, title) {
        if(!data) {
            alert('tset');
            return;
        }

        var dataresult = google.visualization.arrayToDataTable(data);
        var options = { 
            title: title,
            legend : {position: 'none'},
            pointSize : 2,
            backgroundColor : { stroke : '#efefef', fill : '#93e5e0' },
            theme : { chartArea: {width: '90%'} },
            colors : ['#286a5a']
        };
        var chart = new google.visualization.LineChart(document.getElementById(element));
        chart.draw(dataresult, options);
    },
        
    displaySessionData : function(event) {
        var start = $(this).attr('start');
        var end = $(this).attr('end');
        var id = $(this).attr('id');
        var data = {
            "startdate" : start,
            "stopdate" : end,
            "sessionId" : id
        };

        var event = thundashop.Ajax.createEvent("","displaySessionData", $(this), data);
        thundashop.Ajax.postWithCallBack(event, GetShop.Reporting.drawTracking);
    },
    hideInformationBox : function(e) {
        if($(e.target).hasClass('tracking')) {
            return;
        }

        if($('.Reporting #reportingview').is(':visible')) {
            $('.Reporting #reportingview').hide();
        }
    }
};

GetShop.Reporting.init();