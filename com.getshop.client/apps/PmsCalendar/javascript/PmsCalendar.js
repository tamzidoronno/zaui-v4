app.PmsCalendar = {
    init : function() {
        $(document).on('click','.PmsCalendar .dayslot.free', app.PmsCalendar.changeTime);
        $(document).on('click','.PmsCalendar .continue_button', app.PmsCalendar.reserveBooking);
    },
    reserveBooking : function() {
        var continuehref = $(this).attr('continue');
         PubSub.subscribe('NAVIGATION_COMPLETED', function() {
             window.location.href=continuehref;
         });
    },
    changeTime : function() {
        var start = $(this).attr('start');
        var end = $(this).attr('end');
        var startdate = new Date();
        startdate.setTime(start*1000);
        var hour =startdate.getHours();
        if(hour < 10) {
            hour = "0" + hour;
        }
        $('.starttime .hour').val(hour);
        
        var min =startdate.getMinutes();
        if(min < 10) {
            min = "0" + min;
        }
        $('.starttime .minute').val(min);
        
        var enddate = new Date();
        enddate.setTime(end*1000);
        var hour =enddate.getHours();
        if(hour < 10) {
            hour = "0" + hour;
        }
        $('.endtime .hour').val(hour);
        
        var min =enddate.getMinutes();
        if(min < 10) {
            min = "0" + min;
        }
        $('.endtime .minute').val(min);
    },
    showSettings : function() {
        var event = thundashop.Ajax.createEvent('','showSettings',$(this), {});
        thundashop.common.showInformationBoxNew(event, 'Settings');
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

app.PmsCalendar.init();