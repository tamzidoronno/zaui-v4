app.PmsEventOverview = {
    mouseDown : null,
    
    init : function() {
        $(document).on('click', '.PmsEventOverview .calendarSettings .fa-cog', app.PmsEventOverview.openSettings);
        $(document).on('click', '.PmsEventOverview #calendarConfiguration li', app.PmsEventOverview.toggleVisibility);
        $(document).on('click', '.PmsEventOverview .loadbookingonclick', app.PmsEventOverview.loadBookingId);
        $(document).on('mouseenter', '.PmsEventOverview .timecontainer .weekdaycontainer', app.PmsEventOverview.expandDate);
        $(document).on('mouseleave', '.PmsEventOverview .timecontainer .weekdaycontainer', app.PmsEventOverview.contractDate);
        $(document).on('mouseover', '.PmsEventOverview .loadbookingonclick .occupied', app.PmsEventOverview.loadBookingInformation);
        $(document).on('mouseover', '.PmsEventOverview .timecontainer .available', app.PmsEventOverview.mouseoverfield);
        $(document).on('mousedown', '.PmsEventOverview .timecontainer .available', app.PmsEventOverview.selectField);
        $(document).on('mousedown', '.PmsEventOverview .continue', app.PmsEventOverview.continueForm);
        $(document).on('mouseup', app.PmsEventOverview.mouseup);
        
    },
    expandDate : function(){
        $(this).closest('.timecontainer').find('.weekdaycontainer').css('width','7%');
        $(this).addClass('expanded');
    },
    contractDate : function(){
        $('.weekdaycontainer').removeClass('expanded');
        $('.weekdaycontainer').css('width','14.285%');
    },
    openSettings : function() {
        $(this).toggleClass('active');
        $('#calendarConfiguration').toggle();
    },
    toggleVisibility : function(){
        var btn = $(this);
        if(btn.hasClass('toggleoccupied')){
            btn.toggleClass('active');
            $('.PmsEventOverview .occupied').toggleClass('hidden');
        } else if(btn.hasClass('togglenotconfirmed')){
            btn.toggleClass('active');
            $('.PmsEventOverview .notconfirmed').toggleClass('hidden');
        } else if(btn.hasClass('toggleopenforpublic')){
            btn.toggleClass('active');
            $('.PmsEventOverview .openforpublic').toggleClass('hidden');
        } else if(btn.hasClass('toggleavailable')){
            btn.toggleClass('active');
            $('.PmsEventOverview .available').toggleClass('hidden');
        } else if(btn.hasClass('togglenotavailable')){
            btn.toggleClass('active');
            $('.PmsEventOverview .not_available').toggleClass('hidden');
        }
    },
    loadBookingInformation : function() {
        $('.PmsEventOverview .highlighted').removeClass('highlighted');
        var roomid = $(this).closest('.outerblock').attr('roomid');
        var box = $(this).closest('.timecontainer');
        box.find('.outerblock').each(function() {
            var boxRoomId = $(this).attr('roomid');
            if(boxRoomId === roomid) {
                $(this).addClass('highlighted');
            }
        });
    },
    loadBookingId : function() {
        var data = {
            "bookingid" : $(this).attr('bookingid'),
            "roomid" : $(this).attr('roomid')
        }
        var instanceId = $(this).attr('instanceid');
        var event = thundashop.Ajax.createEvent('','showBookingInformation',instanceId,data);
        event.core.appname = "PmsManagement";
        thundashop.common.showInformationBoxNew(event,'Configuration');
    },
    
    selectField : function() {
        $('.selected_periode').removeClass('selected_periode');
        $(this).closest('.dayrow').addClass('selected_row');

        app.PmsEventOverview.mouseDown = true;
        if($(this).hasClass('selected_periode')) {
            $(this).removeClass('selected_periode');
        } else {
            $(this).addClass('selected_periode');
        }
        $(this).addClass('startfield');
        $('.continue_button').removeClass('disabled');
        
        var panel = $('.PmsEventOverview .timeselectionpanel');
        panel.fadeIn();
        panel.css('left', $(this).offset().left);
        panel.css('top', $(this).offset().top+20);
        $('.timeselectionpanel').find('.startTime').html($(this).attr('starttimehuman'));
        $('.timeselectionpanel').find('.endTime').html($(this).attr('endtimehuman'));

    },
    continueForm : function() {
        var row = $('.selected_periode').closest('.dayrow');
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
        
        var room = row.attr('itemid');

        var data = {
            "start" : start,
            "end" : end,
            "room" : room
        };
        var event = thundashop.Ajax.createEvent('','continueToForm',$('.PmsEventOverview'),data);
        var continueTo = $(".PmsEventOverview .continue").val();

        thundashop.Ajax.postWithCallBack(event, function() {
            thundashop.common.goToPage(continueTo);
        });
    },
    mouseup : function() {
        $('.startfield').removeClass('startfield');
        $('.selected_row.mouseover').removeClass('mouseover');
        $('.selected_row').removeClass('selected_row');
        app.PmsEventOverview.mouseDown = false;
        var panel = $('.PmsEventOverview .timeselectionpanel');
        panel.fadeOut();
        $(document).tooltip('enable');
        app.PmsEventOverview.continueForm();
    },
    mouseoverfield : function() {
        if(!app.PmsEventOverview.mouseDown) {
            return;
        }
        $(document).tooltip('disable');

        var row = $(this).closest('.dayrow');
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
        
        var panel = $('.PmsEventOverview .timeselectionpanel');
        panel.show();
        panel.css('left', $(this).offset().left);
        panel.css('top', $(this).offset().top+20 - $(window).scrollTop());
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
            showSettings : true,
            application: application,
            title: "Settings",
            items: []
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
}
app.PmsEventOverview.init();