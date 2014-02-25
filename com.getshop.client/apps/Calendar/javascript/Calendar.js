Calendar = {
    animations : [],
            
    init: function() {
        PubSub.subscribe('navigation_complete', this.checkScrolling, this);
        PubSub.subscribe("setting_switch_toggled", this.onOffChanged, this);
    },
    
    expandDays: function() {
        $('.Calendar .calendar table .containsdata').hover(
            function() {
                var tdWidth = $(this).closest('td').width();
                $(this).find('.content .outerdiv div').each(function() {
                    if ($(this).attr('days')) {
                        var days = $(this).attr('days');
                        days = parseInt(days);
                        if (days > 1) {
                            width = tdWidth*days + (2*days);
                            var animation = $(this).animate({width: width}, 100);
                            Calendar.animations.push(animation);
                        } else {
                            $(this).width($(this).width());
                        }
                    }                    
                });
                $(this).addClass('hovering');
            },
            function() {
                for (var i in Calendar.animations) {
                    var animation = Calendar.animations[i];
                    animation.stop();
                }
                
                Calendar.animations = [];
                $(this).find('.content .outerdiv div').each(function() {
                    $(this).removeAttr('style');
                });
                $(this).removeClass('hovering');
            }
        );
    },
            
    onOffChanged: function(msg, data) {
        if (data.id === "allowadd" && data.state === "on") {
            $('#allowaddonsettings').slideDown();
        } 
        
        if (data.id === "allowadd" && data.state === "off") {
            $('#allowaddonsettings').slideUp();
        }
    },
            
    checkScrolling: function(msg, data) {        
        if (data && data.indexOf("&scroll_to_dayview=true") > -1) {
            var dayeventdiv = $('div.dayevents');
            var position = dayeventdiv.position();
            scroll(0,position.top);
        }
    },
    
    addAnotherDay: function(day, month, year) {
        var name = day+"/"+month+"/"+year;
        
        
        var total = $('<div/>');
        total.addClass('daytotal');
        var remove = $('<img/>');
        remove.attr('class', 'removeday inline');
        remove.attr('src', '/skin/default/images/closeinfobox.png');
        remove.css({'height' : '15px', 'width' : '15px', 'padding-top' : '3px', 'margin-right': '5px'});
        
        var element = $('<div/>');
        element.attr('class', 'inline extradayelement');
        element.attr('day', day);
        element.attr('month', month);
        element.attr('year', year);
        element.html(name);
        element.css({'line-height' : '20px'});
        total.prepend(remove);
        total.append(element);
        
        var timeHolder = $('<span/>');
        timeHolder.css({
           position: 'absolute',
           right: '0px',
           display: 'inline-block',
           'border-left': 'solid 1px #DDD', 
           'margin-left': '4px', 
           'padding-left': '5px'
        });
        
        var time = $('<input/>');
        time.css({'width': '55px', 'margin-left': '5px'});
        time.attr('type','textfield');
        time.attr('id', 'starttime');
        
        var starttime = $('#extradaysdiv').closest('.addevent').find('#eventstart').val()
        var stoptime = $('#extradaysdiv').closest('.addevent').find('#eventstop').val();
        time.val(starttime);
        
        var time2 = time.clone();
        time2.attr('id', 'stoptime');
        time2.val(stoptime);
        
        timeHolder.append('Clock: ');
        timeHolder.append(time);
        timeHolder.append(' - ');
        timeHolder.append(time2);
        
        total.append(timeHolder);
        $('#extradaysdiv').append(total);
    },
    
    getHexColor: function() {
        return "008000";
    },
    
    updateHistoryTab: function(entryId, dom) {
        var data = {
            entryid: entryId
        };
        
        var event = thundashop.Ajax.createEvent("", "getReminderHistory", dom, data);
        thundashop.Ajax.postWithCallBack(event, function(response) {
            $('.Calendar .all_reminder_history').html(response);
        }, true);
    }
};

Calendar.init();

$('.Calendar .removeday').live('click', function() {
    $(this).closest('.daytotal').remove();
});

$('.Calendar .addevent #save').live('click', function() {
    var data = {
        day : $(this).attr('day'),
        year : $(this).attr('year'),
        month : $(this).attr('month'),
        entryid : $(this).attr('entryid')
    }
    
    var extraDays = $(this).closest('.informationbox').find('.daytotal');
    var extraDaysData = [];
    
    $.each(extraDays, function(extraday, daytotal) {
        var el = $(daytotal).find('.extradayelement')
        var extraDay = {};
        extraDay['day'] = $(el).attr('day');
        extraDay['month'] = $(el).attr('month');
        extraDay['year'] = $(el).attr('year');
        extraDay['starttime'] = $(daytotal).find('#starttime').val();
        extraDay['stoptime'] = $(daytotal).find('#stoptime').val();
        extraDaysData.push(extraDay);
    });
    
    data.eventname = $('#eventname').val();
    data.eventstart = $('#eventstart').val();
    data.maxattendees = $('#maxattendies').val();
    data.eventstop = $('#eventstop').val();
    data.eventdescription = $('#eventdescription').val();
    data.eventlocation = $('#eventlocation').val();
    data.linkToPage = $('#linkToPage').attr('pageid');
    data.locationExtended = $('#locationextended').val();
    data.extraDays = extraDaysData;
    data.lockedForSignup = $('#lockEvent').is(":checked");
    
    data.color = Calendar.getHexColor();
    
    var event = thundashop.Ajax.createEvent('Calendar', 'registerEvent', $(this), data);
    thundashop.Ajax.post(event);
    thundashop.common.hideInformationBox(event);
});

$('.Calendar .deleteentry').live('click', function() {
    var data = {};
    data.entryId = $(this).attr('entryid');
    var event = thundashop.Ajax.createEvent('Calendar', 'deleteEntry', $(this), data);
    thundashop.Ajax.post(event);
});

$('.Calendar .editentry').live('click', function() {
    var data = {};
    data.entryid = $(this).attr('entryid');
    var event = thundashop.Ajax.createEvent('Calendar', 'showCalenderEvent', $(this), data);
    thundashop.common.showInformationBox(event, __f('Edit event'));
});

$('.Calendar .save_comment').live('click', function() {
    var data = {
        entryId : $(this).attr('entryId'),
        userId : $(this).attr('userId'),
        comment : $(this).parent().find('textarea').val()
    };
    
    var event = thundashop.Ajax.createEvent(null, "saveComment", this, data);
    thundashop.Ajax.post(event);
    thundashop.common.hideInformationBox();
});

$('.Calendar .deletecomment').live('click', function() {
    var data = {
        commentId: $(this).attr('commentId'),
        userId: $(this).attr('userId')
    }
    var event = thundashop.Ajax.createEvent(null, "deleteComment", this, data);
    thundashop.Ajax.post(event);
    $(this).closest('.commententry').fadeOut(function() {
        $(this).remove();
    });
})

$('.Calendar .addcomment').live('click', function() {
    var data = {
        userId : $(this).attr('userid'),
        entryId : $(this).attr('entryId')
    }
    
    var event = thundashop.Ajax.createEvent(null, 'showEvent', this, data);
    thundashop.common.showInformationBox(event, __f('Add comment'));
});

$('.Calendar .reminder').live('click', function() {
    var data = {};
    data.entryid = $(this).attr('entryid');
    var event = thundashop.Ajax.createEvent('Calendar', 'showSendReminder', $(this), data);
    thundashop.common.showInformationBox(event, __f("Send reminder"));
});

$('.Calendar .confirmentry').live('click', function() {
    var data = {};
    data.entryid = $(this).attr('entryid');
    var event = thundashop.Ajax.createEvent('Calendar', 'confirmEntry', $(this), data);
    thundashop.Ajax.post(event, function() {
        thundashop.common.Alert(__w("Success"), __w("The event has been confirmed."));
    });
});

$('.Calendar #sendreminderbysms').live('click', function() {
    var data = {};
    data.users = [];
    $(this).closest('#informationbox').find('[name=attendeestoremind]:checked').each(function() {
        data.users.push($(this).val());
    });
        
    data.text = $(this).closest('#informationbox').find('textarea').val();
    data.entryid = $(this).attr('entryid');
    data.subject = $('#remindersubject').val();
    
    var event = thundashop.Ajax.createEvent('Calendar', 'sendReminderBySms', $(this), data);
    var me = this;
    
    var callback = function() {
        Calendar.updateHistoryTab(data.entryid, me);
    };
    
    thundashop.Ajax.post(event, function(response) {
        callback();
        thundashop.common.Alert(__f('Reminder'), __f('Reminder sent by sms'));
    });
    
});

$('.Calendar #sendreminderbymail').live('click', function() {
    var data = {};
    data.users = [];
    $(this).closest('#informationbox').find('[name=attendeestoremind]:checked').each(function() {
        data.users.push($(this).val());
    });
        
    data.text = $(this).closest('#informationbox').find('textarea').val();
    data.entryid = $(this).attr('entryid');
    data.subject = $('#remindersubject').val();
    
    var event = thundashop.Ajax.createEvent('Calendar', 'sendReminderByEmail', $(this), data);
    var me = this;
    
    var callback = function() {
        Calendar.updateHistoryTab(data.entryid, me);
    };
    
    thundashop.Ajax.post(event, function(response) {
        callback();
        thundashop.common.Alert(__w('Reminder'), __w('Reminder sent by email'));
    });
});

$('.Calendar .addEvent').live('click', function() {
    var data = {
        day : $(this).attr('day'),
        year : $(this).attr('year'),
        month : $(this).attr('month')
    }
    
    var event = thundashop.Ajax.createEvent('Calendar', 'showCalenderEvent', $(this), data);
    thundashop.common.showInformationBox(event, __f('Create event'));
})

$('.Calendar .add_user_to_this_event').live('click', function() {
    var data = {
        fromEntryId: $(this).attr('currentEntryId'),
        toEntryId: $(this).attr('toEntryId'),
        userid: $(this).attr('userid')
    }
    
    var event = thundashop.Ajax.createEvent('Calendar', 'transferUser', $(this), data);
    thundashop.Ajax.post(event, function() {
        thundashop.common.hideInformationBox();
    });
});
$('.Calendar .transferUser').live('click', function() {
    var data = {
        userid: $(this).attr('userid'),
        entryid: $(this).attr('entryid')
    }
    
    var event = thundashop.Ajax.createEvent('Calendar', 'showTransferUser', $(this), data);
    thundashop.common.showInformationBox(event, __f("Transfer user from one event to another"));
});

$('.Calendar .remove').live('click', function() {
    var data = {
        userid: $(this).attr('userid'),
        entryid: $(this).attr('entryid')
    }
    
    var event = thundashop.Ajax.createEvent('Calendar', 'removeAttendees', $(this), data);
    thundashop.Ajax.post(event);
});

$('.Calendar .filters .filter').live('click', function() {
    var isSelected = $(this).hasClass('selected');
    var event = null;
    var data = {
        filter : $(this).text() 
    };
    
    if (!isSelected) {
        $(this).addClass('selected');
        event = thundashop.Ajax.createEvent('Calendar', 'applyFilter', $(this), data);
    } else {
        $(this).removeClass('selected');
        event = thundashop.Ajax.createEvent('Calendar', 'removeFilter', $(this), data);
    }
    
    thundashop.Ajax.post(event);
});
$(document).on('click', '.Calendar .acceptUser', function() {
    var data = {
        entryId: $(this).attr('entryId'),
        userId: $(this).attr('userId')
    };
    
    var event = thundashop.Ajax.createEvent('Calendar', 'transferUserFromWaitingList', $(this), data);
    thundashop.Ajax.post(event);
});