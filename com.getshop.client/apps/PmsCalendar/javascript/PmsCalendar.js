app.PmsCalendar = {
    mouseDown : null,
    
    init : function() {
        $(document).on('mouseover', '.PmsCalendar .available', app.PmsCalendar.mouseoverfield);
        $(document).on('mousedown', '.PmsCalendar .available', app.PmsCalendar.selectField);
        $(document).on('mousedown', '.PmsCalendar .continue_button', app.PmsCalendar.continueToForm);
        $(document).on('mouseup', app.PmsCalendar.mouseup);
    },
    selectField : function() {
        $('.startfield').removeClass('startfield');
        $('.selected_periode').removeClass('selected_periode');
        $(this).closest('.timecontainer').addClass('selected_row');

        app.PmsCalendar.mouseDown = true;
        if($(this).hasClass('selected_periode')) {
            $(this).removeClass('selected_periode');
        } else {
            $(this).addClass('selected_periode');
        }
        $(this).addClass('startfield');
        $('.continue_button').removeClass('disabled');
    },
    continueToForm : function() {
        if($(this).hasClass('disabled')) {
            return;
        }
        var row = $('.selected_row');
        var start = null;
        var end = null;
        row.find('.selected_periode').each(function() {
            if(!start) {
                start = $(this).attr('starttime');
            }
            end = $(this).attr('endtime');
        });
        
        var room = row.closest('.roomrow').attr('itemid');

        var data = {
            "start" : start,
            "end" : end,
            "room" : room
        };
        
        var event = thundashop.Ajax.createEvent('','continueToForm',$(this),data);
        var continueTo = $(this).attr('continue');
        thundashop.Ajax.postWithCallBack(event, function() {
            window.location.href=continueTo;
        });
    },
    mouseup : function() {
        app.PmsCalendar.mouseDown = false;
    },
    mouseoverfield : function() {
        if(!app.PmsCalendar.mouseDown) {
            return;
        }
        var row = $(this).closest('.timecontainer');
        if(!row.hasClass('selected_row')) {
            console.log('norow');
            return;
        }

        if(!$(this).parent().prev().find('.timeblock').hasClass('selected_periode') && !$(this).parent().next().find('.timeblock').hasClass('selected_periode')) {
            return;
        }
        if(!row.find('.selected_periode').length === 0) {
            return;
        }
        if(app.PmsCalendar.mouseDown) {
            $(this).addClass('selected_periode');
        }
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