app.PmsAvailabilityTimeline = {
    init : function() {
        $(document).on('click', '.PmsAvailabilityTimeline .valueentry.full', app.PmsAvailabilityTimeline.loadBooking);
        $(document).on('click', '.PmsAvailabilityTimeline .loadBookingList', app.PmsAvailabilityTimeline.loadBookingList);
        $(document).on('click', '.PmsAvailabilityTimeline .checkallclosingroom', app.PmsAvailabilityTimeline.checkallclosingroom);
        $(document).on('click', '.PmsAvailabilityTimeline .checkallclosingtypes', app.PmsAvailabilityTimeline.checkallclosingtypes);
        $(document).on('mouseover', '.PmsAvailabilityTimeline .valueentry', app.PmsAvailabilityTimeline.mouseOver);
        $(document).on('mouseout', '.PmsAvailabilityTimeline .valueentry', app.PmsAvailabilityTimeline.mouseOut);
        $(document).on('click', '.PmsAvailabilityTimeline .closeRoomOptionsButton', app.PmsAvailabilityTimeline.closeRoomOptionsButton);
        $(document).on('change', '.PmsAvailabilityTimeline .changetypeonbookingselector', app.PmsAvailabilityTimeline.ifNeedMoveTypes);
    },
    
    ifNeedMoveTypes : function() {
        var count = $(this).find('option:selected').attr('count');
        var form = $(this).closest('[gstype="form"]');
        var roomId = form.find('[gsname="roomid"]').val();
        var warnMessage = $(this).closest('tr').find('.warnmovepeople');
        warnMessage.hide();
        if(count === "0") {
            var data = {
                "movetotype" : $(this).val(),
                "moveRoom" : roomId
            }
            var event = thundashop.Ajax.createEvent('','findTypesToMove', $(this), data);
            
            thundashop.Ajax.postWithCallBack(event, function(res) {
                warnMessage.html(res);
                warnMessage.fadeIn();
            });
        }
    },
    checkallclosingroom : function() {
        var checked = $(this).is(':checked');
        $('.closeforroom').each(function() {
            if(checked) {
                $(this).attr('checked','checked');
            } else {
                $(this).attr('checked',null);
            }
        });
    },
    checkallclosingtypes : function() {
        var checked = $(this).is(':checked');
        $('.closeforota').each(function() {
            if(checked) {
                $(this).attr('checked','checked');
            } else {
                $(this).attr('checked',null);
            }
        });
    },
    
    closeRoomOptionsButton : function() {
        var event = thundashop.Ajax.createEvent('','loadCloseRoomBox', $(this), {});
        thundashop.common.showInformationBoxNew(event, 'Close a room');
    },
    mouseOut : function() {
        $('.ui-tooltip').remove();
        if(typeof(pmsAvailabilityTimelineTimeout) !== "undefined") {
            clearTimeout(pmsAvailabilityTimelineTimeout);
        }
    },
    loadBookingList : function() {
        var event = thundashop.Ajax.createEvent('','loadBookingList', $(this), {
            "day" : $(this).attr('day'),
            "type" : $(this).attr('type')
        });
        thundashop.common.showInformationBoxNew(event, "Active rooms");
    },
    
    mouseOver : function() {
        if($(this).hasClass('available')) {
            return;
        }
        
        var bookingid = $(this).attr('bid');
        var from = $(this);
        if(!bookingid) {
            return;
        }
        
        pmsAvailabilityTimelineTimeout = setTimeout(function() {
            var event = thundashop.Ajax.createEvent('', 'loadHover', from, {
                "bookingid" : bookingid
            });
            thundashop.Ajax.postWithCallBack(event, function(res) {
                from.tooltip({ content: res });
                from.attr('title', res);
                from.tooltip("open");
            });
        }, "200");
    },
    showSettings : function() {
        var event = thundashop.Ajax.createEvent('','showSettings',$(this), {});
        thundashop.common.showInformationBoxNew(event, 'Settings');
    },
    
    loadBooking : function() {
        $('.ui-tooltip').remove();
        var data = {
            "bid" : $(this).attr('bid')
        }
        var instanceId = $('#bookinginstanceid').val();
        var event = thundashop.Ajax.createEvent('','showBookingOnBookingEngineId',instanceId,data);
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
                    click: app.PmsAvailabilityTimeline.showSettings
                }
            ]
        };

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
};

app.PmsAvailabilityTimeline.init();