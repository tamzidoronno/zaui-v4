app.PmsAvailabilityTimeline = {
    init : function() {
        $(document).on('click', '.PmsAvailabilityTimeline .valueentry.full', app.PmsAvailabilityTimeline.loadBooking);
        $(document).on('mouseover', '.PmsAvailabilityTimeline .valueentry.full', app.PmsAvailabilityTimeline.mouseOver);
    },
    mouseOver : function() {
        if(typeof(pmsAvailabilityTimelineTimeout) !== "undefined") {
            clearTimeout(pmsAvailabilityTimelineTimeout);
        }
        
        var bookingid = $(this).attr('bid');
        var from = $(this);
        $('.ui-tooltip').remove();
        
        pmsAvailabilityTimelineTimeout = setTimeout(function() {
            var event = thundashop.Ajax.createEvent('', 'loadHover', from, {
                "bookingid" : bookingid
            });
            thundashop.Ajax.postWithCallBack(event, function(res) {
                from.tooltip({ content: res });
                from.attr('title', res);
                from.tooltip("open");
            });
        }, "500");
    },
    showSettings : function() {
        var event = thundashop.Ajax.createEvent('','showSettings',$(this), {});
        thundashop.common.showInformationBoxNew(event, 'Settings');
    },
    
    loadBooking : function() {
        var data = {
            "time" : $(this).attr('time'),
            "itemid" : $(this).attr('itemid')
        }
        var instanceId = $('#bookinginstanceid').val();
        var event = thundashop.Ajax.createEvent('','showInfoBoxForBookingAtTime',instanceId,data);
        event.core.appname = "PmsManagement";
        thundashop.common.showInformationBoxNew(event,'Configuration');
    },
    
    loadSettings: function (element, application) {
        var config = {
            draggable: true,
            app: true,
            application: application,
            title: "Settings",
            items: [
                {
                    icontype: "awesome",
                    icon: "fa-edit",
                    iconsize: "30",
                    title: __f("Show settings"),
                    click: app.PmsBookingSummary.showSettings
                }
            ]
        };

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
};

app.PmsAvailabilityTimeline.init();