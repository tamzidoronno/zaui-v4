app.PmsCalendar = {
    mouseDown : null,
    
    init : function() {
        $(document).on('mouseover', '.PmsCalendar .available', app.PmsCalendar.mouseoverfield);
        $(document).on('mousedown', '.PmsCalendar .available', app.PmsCalendar.selectField);
        $(document).on('mousedown', '.PmsCalendar .continue_button', app.PmsCalendar.continueToForm);
        $(document).on('mouseup', app.PmsCalendar.mouseup);
    },
    selectField : function() {
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
        
        var panel = $('.PmsCalendar .timeselectionpanel');
        panel.fadeIn();
        panel.css('left', $(this).offset().left);
        panel.css('top', $(this).offset().top+80);
        $('.timeselectionpanel').find('.startTime').html($(this).attr('starttimehuman'));
        $('.timeselectionpanel').find('.endTime').html($(this).attr('endtimehuman'));

    },
    continueToForm : function() {
        var row = $('.selected_periode').closest('.timecontainer');
        var start = null;
        var end = null;
        row.find('.selected_periode').each(function() {
            if(!start) {
                start = $(this).attr('starttime');
            }
            end = $(this).attr('endtime');
        });
        
        if(!start) {
            return;
        }
        
        var room = row.closest('.roomrow').attr('itemid');

        var data = {
            "start" : start,
            "end" : end,
            "room" : room
        };
        
        var event = thundashop.Ajax.createEvent('','continueToForm',$('.PmsCalendar'),data);
        var continueTo = $(".PmsCalendar .continue").val();
        thundashop.Ajax.postWithCallBack(event, function() {
            window.location.href=continueTo;
        });
    },
    mouseup : function() {
        $('.startfield').removeClass('startfield');
        $('.selected_row.mouseover').removeClass('mouseover');
        $('.selected_row').removeClass('selected_row');
        app.PmsCalendar.mouseDown = false;
        var panel = $('.PmsCalendar .timeselectionpanel');
        panel.fadeOut();
        $(document).tooltip('enable');
        app.PmsCalendar.continueToForm();
    },
    mouseoverfield : function() {
        if(!app.PmsCalendar.mouseDown) {
            return;
        }
        $(document).tooltip('disable');

        var row = $(this).closest('.timecontainer');
        if(!row.hasClass('selected_row')) {
            console.log('norow');
            return;
        }
        $('.available.mouseover').removeClass('mouseover');
        $(this).addClass('mouseover');
        
        var foundMouseOver = false;
        var foundFirst = false;
        $('.selected_periode').removeClass('selected_periode');
        var startBlock = null;
        var endBlock = null;
        
        row.find('.timeblock').each(function() {
            if($(this).hasClass('startfield')) {
                foundFirst = true;
                if(!startBlock) {
                    startBlock = $(this);
                } else {
                    endBlock = $(this);
                }
            }
            if(!foundFirst && foundMouseOver) {
                if(!startBlock) {
                    startBlock = $(this);
                }
                $(this).addClass('selected_periode');
            }
            if(foundFirst && !foundMouseOver) {
                $(this).addClass('selected_periode');
                endBlock = $(this);
            }
            if($(this).hasClass('mouseover')) {
                foundMouseOver = true;
            }
        });
        
        var panel = $('.PmsCalendar .timeselectionpanel');
        panel.show();
        panel.css('left', $(this).offset().left);
        panel.css('top', $(this).offset().top+80 - $(window).scrollTop());
        $('.timeselectionpanel').find('.startTime').html(startBlock.attr('starttimehuman'));
        $('.timeselectionpanel').find('.endTime').html(endBlock.attr('endtimehuman'));
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