app.PmsBookingCalendar = {
    init : function() {
        $(document).on('click', '.PmsBookingCalendar .fa-arrow-left', app.PmsBookingCalendar.decreaseMonth);
        $(document).on('click', '.PmsBookingCalendar .fa-arrow-right', app.PmsBookingCalendar.decreaseMonth);
        $(document).on('click', '.PmsBookingCalendar .calendar .day', app.PmsBookingCalendar.selectDay);
    },
    selectDay : function() {
         var event = thundashop.Ajax.createEvent('','selectDay',$(this), {
            "time" : $(this).attr('time')
        });
        var box = $(this).closest('.applicationinner .calendar');
        thundashop.Ajax.postWithCallBack(event, function(result) {
            box.html(result);
        });
    },
    showSettings : function() {
        var event = thundashop.Ajax.createEvent('','showSettings',$(this), {});
        thundashop.common.showInformationBox(event, 'Settings');
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
                    click: app.PmsBookingCalendar.showSettings
                }
            ]
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    },
    decreaseMonth : function() {
        var month = "+1";
        if($(this).hasClass('fa-arrow-left')) {
            month = "-1";
        }
        var event = thundashop.Ajax.createEvent('','changeMonth',$(this), {
            "month" : month
        });
        var box = $(this).closest('.applicationinner .calendar');
        
        thundashop.Ajax.postWithCallBack(event, function(result) {
            box.html(result);
        });
    }
};

app.PmsBookingCalendar.init();