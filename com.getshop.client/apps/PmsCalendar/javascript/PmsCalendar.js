app.PmsCalendar = {
    init : function() {
        $(document).on('click','.PmsCalendar .dayslot.free', app.PmsCalendar.changeTime);
        $(document).on('click','.PmsCalendar .continue_button', app.PmsCalendar.reserveBooking);
        $(document).on('click','.PmsCalendar .topheader .fa-arrow-left', app.PmsCalendar.changeCalendarMonth);
        $(document).on('click','.PmsCalendar .topheader .fa-arrow-right', app.PmsCalendar.changeCalendarMonth);
        $(document).on('keyup','.PmsCalendar .starttime input', app.PmsCalendar.inputChanged);
        $(document).on('keyup','.PmsCalendar .endtime input', app.PmsCalendar.inputChanged);
        $(document).on('change','.PmsCalendar .roomselectiononday', app.PmsCalendar.changeRoom);
    },
    changeRoom : function() {
        var room = $(this).val();
        var day = $(this).attr('day');
        var page = $(this).attr('page');
        
        window.location.href = "/?page=" + page + "&day=" + day + "&roomName=" + room;
    },
    
    inputChanged : function() {
        var failed = false;
        $('.PmsCalendar .starttime input').each(function() {
            if(!$(this).val()) {
                failed = true;
            }
        });
        $('.PmsCalendar .endtime input').each(function() {
            if(!$(this).val()) {
                failed = true;
            }
        });
        
        if(failed) {
            $('.PmsCalendar').find('.continue_button').addClass('disabled');
            $('.needtimetext').show();
        } else {
            $('.PmsCalendar').find('.continue_button').removeClass('disabled');
            $('.needtimetext').hide();
        }
    },
    changeCalendarMonth: function() {
        var header= $(this).closest('.topheader');
        var direction = "up";
        if($(this).hasClass('fa-arrow-left')) {
            direction = "down";
        }
        var event = thundashop.Ajax.createEvent('','changeCalendarMonth',$(this),{
            "direction" : direction,
            "page" : header.attr('page'),
            "roomName" : header.attr('roomName')
        });
        thundashop.Ajax.post(event);
    },
    reserveBooking : function() {
        if($(this).hasClass('disabled')) {
            return;
        }
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
        app.PmsCalendar.inputChanged();
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