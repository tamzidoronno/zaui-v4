app.PmsCalendar = {
    mouseDown : null,
    
    init : function() {
        $(document).on('mouseover', '.PmsCalendar .available', app.PmsCalendar.mouseoverfield);
        $(document).on('mousedown', '.PmsCalendar .available', app.PmsCalendar.selectField);
        $(document).on('click', '.PmsCalendar .available.mobileentry', app.PmsCalendar.markMobileEntry);
        $(document).on('mousedown', '.PmsCalendar .continue_button', app.PmsCalendar.continueToForm);
        $(document).on('click', '.PmsCalendar .loadbookingonclick', app.PmsCalendar.loadBookingId);
        $(document).on('click', '.PmsCalendar .gotopage', app.PmsCalendar.gotopage);
        $(document).on('blur', '.PmsCalendar .changemonthmobile', app.PmsCalendar.changemonthmobile);
        $(document).on('mouseup', app.PmsCalendar.mouseup);
    },
    continueButtonMobile : function(element) {
        var start = $('.selecteddate').val() + " " + $('.starthour').val() + ":" + $('.startminute').val();
        var end = $('.selecteddate').val() + " " + $('.endhour').val() + ":" + $('.endminute').val();
        var typeId = $('.addbookingpopup').attr('typeid');
        
        var data = {
            "start" : start,
            "end" : end,
            "room" : typeId
        };
        
        var event = thundashop.Ajax.createEvent('','continueToForm',element, data);
        thundashop.Ajax.postWithCallBack(event, function() {
            var continueTo = $(".PmsCalendar .continue").val();
            thundashop.common.goToPage(continueTo);
            $(window).scrollTop(0);
        });
        
    },
    markMobileEntry : function() {
        $('.selected_periode').removeClass('selected_periode');
        $(this).addClass('selected_periode');
        var box = $('.addbookingpopup');
        box.find('.starthour').val($(this).attr('starttime'));
        box.find('.startminute').val($(this).attr('startminute'));
        box.find('.endhour').val($(this).attr('endtime'));
        box.find('.endminute').val($(this).attr('endminute'));
        
        app.PmsCalendar.continueButtonMobile($(this));
        
    },
    
    gotopage : function() {
        var pageid = $(this).attr('pageid');
        var curScroll = $(window).scrollTop();
        thundashop.common.goToPage(pageid);
        PubSub.subscribe('NAVIGATION_COMPLETED', function() {
            $(document).find('img').batchImageLoad({
                loadingCompleteCallback: function() {
                    $(document).scrollTop(curScroll);
                }
            });
        });
    },
    changemonthmobile : function() {
        thundashop.Ajax.simplePost($(this), 'setCalendarDay', {
            "day" : $(this).val()
        });
    },
    loadBookingId : function() {
        var data = {
            "bookingid" : $(this).attr('bookingid')
        }
        var instanceId = $(this).attr('instanceid');
        var event = thundashop.Ajax.createEvent('','showBookingInformation',instanceId,data);
        event.core.appname = "PmsManagement";
        thundashop.common.showInformationBoxNew(event,'Configuration');
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
            thundashop.common.goToPage(continueTo);
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