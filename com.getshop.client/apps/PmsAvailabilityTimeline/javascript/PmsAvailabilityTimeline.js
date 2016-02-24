app.PmsAvailabilityTimeline = {
    init : function() {
        $(document).on('click', '.PmsAvailabilityTimeline .valueentry.full', app.PmsAvailabilityTimeline.loadBooking);
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