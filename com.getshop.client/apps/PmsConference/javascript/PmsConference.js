/* try to fix broken conferences and events */
function reconnectConferenceWithBooking(conferenceId)
{

}


app.PmsConference = {
    currentInput : null,
    disableAutoReload : false,
    
    init : function() {
        $(document).on('keyup','.PmsConference .updaterowkeyup', app.PmsConference.updateEventRow);
        $(document).on('change','.PmsConference .updaterowselect', app.PmsConference.updateEventRow);
        $(document).on('click','.PmsConference .openevent', app.PmsConference.openEvent);
        $(document).on('click','.PmsConference .openconference', app.PmsConference.openConference);
        $(document).on('click','.PmsConference .canaddevent', app.PmsConference.loadQuickAddToConference);
        $(document).on('click','.PmsConference .removeguest', app.PmsConference.removeGuestFromEvent);
        $(document).on('click','.PmsConference .extendedtextinput', app.PmsConference.showExtendedTextOverlay);
        $(document).on('click','.PmsConference .updateextendedtext', app.PmsConference.updateExtendedText);
        $(document).on('click','.PmsConference .downloadreport', app.PmsConference.downloadReport);
        $(document).on('click','.PmsConference .removeEntry', app.PmsConference.removeEventEntry);
        $(document).on('keyup','.PmsConference .updatevententryrow', app.PmsConference.updateEventEntryRow);
        $(document).on('change','.PmsConference .choosemonthdropdown', app.PmsConference.changeTimePeriode);
    },
    removeEventEntry : function() {
        var btn = $(this);
        var confirmed = confirm("Are you sure you want to delete this entry? It can not be restored");
        if(!confirmed) {
            return;
        }
        var id = $(this).attr('entryid');
        var event = thundashop.Ajax.createEvent('','deleteEntry',$(this), {
            "id" : id
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            btn.closest('.evententryrow').remove();
        });
    },
    downloadReport : function(res) {
        var type = $(this).attr('type');
        var start = $('.startdate').val();
        var end = $('.enddate').val();
        window.open("/scripts/pmsconferencereport.php?type="+type+"&start="+start+"&end="+end);
    },
    guestAdded : function(res) {
        $('.guestlistarea').html(res);
    },
    updateExtendedText : function() {
        var text = $('.extendedtextoverlay .extendextarea').val();
        app.PmsConference.currentInput.val(text);
        $('.extendedtextoverlay').hide();
        console.log(app.PmsConference.currentInput.closest('.evententryrow'));
        app.PmsConference.updateSpecifiedEventRow(app.PmsConference.currentInput.closest('.evententryrow'));
    },
    showExtendedTextOverlay : function() {
       app.PmsConference.currentInput = $(this);
       $('.extendedtextoverlay .extendextarea').val($(this).val());
       $('.extendedtextoverlay').show();
       $('.extendedtextoverlay .extendextarea').focus();
    },
    removeGuestFromEvent : function() {
        var area = $(this).closest('.guestlistarea');
        var event = thundashop.Ajax.createEvent('','removeGuestFromEvent',$(this), {
            guestId : $(this).attr('guestid'),
            eventId : $(this).attr('eventid')
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            area.html(res);
        });
    },
    addEvent : function(res) {
        if(res) {
            $('.addeventfailed').html(res);
        } else {
            window.location.reload();
        }
    },
    
    loadQuickAddToConference : function(e) {
        var box = $('.PmsConference .addeventbox');
        box.show();
        box.css('left', e.clientX);
        box.css('top', e.clientY);
        box.find('[gsname="starttime"]').val($(this).attr('from'));
        box.find('[gsname="endtime"]').val($(this).attr('to'));
        box.find('[gsname="date"]').val($(this).attr('date'));
        box.find('[gsname="itemid"]').val($(this).attr('itemid'));
    },
    changeTimePeriode : function() {
        var event = thundashop.Ajax.createEvent('','changeToMonth',$(this), {
            "time" : $(this).val()
        });
        thundashop.Ajax.post(event);
    },
    entryDeleted : function(id) {
        $('.evententryrow[entryid="'+id+'"]').remove();
    },
    allConferencesLoaded : function(res) {
        var view = $('.conferencelist');
        view.show();
        view.html(res);
    },
    openEvent : function() {
        app.PmsConference.disableAutoReload = true;
        var eventid = $(this).attr('eventid');
        var event = thundashop.Ajax.createEvent('','openEvent',$('.PmsConference'), { "eventid" : eventid});
        $('.eventoverview').show();
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.conferenceoverview').hide();
            $('.eventoverview').html(res);
        });
    },
    openConferenceById : function(conferenceid) {
        var event = thundashop.Ajax.createEvent('','openConference',$('.PmsConference'), { "conferenceid" : conferenceid});
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.conferenceoverview').show();
            $('.eventoverview').hide();
            $('.conferenceoverview').html(res);
        });
    },
    reconnectConferenceWithBooking: function(conferenceid) {
        var event = thundashop.Ajax.createEvent('','reconnectConferenceWithBooking',$('.PmsConference'), { "conferenceid" : conferenceid});
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.conferenceoverview').show();
            $('.eventoverview').hide();
            $('.conferenceoverview').html(res);
        });
    },

    openConference : function() {
        var conferenceid = $(this).attr('conferenceid');
        app.PmsConference.openConferenceById(conferenceid);
        app.PmsConference.disableAutoReload = true;
    },
    loadEditItem : function(res) {
        $('.edititembox').html(res);
        $('.edititembox').show();
    },
    eventDeleted: function(res) {
        $('.eventrow[eventid="'+res+'"]').hide();
    },
    userSavedUpdated : function() {
        $('.PmsConference .GetShopQuickUser .change_user_form, .PmsConference .GetShopQuickUser .edit_details_of_user').show();
    },
    continueToConferenceView : function(res) {
        $('.addconferencepanel').hide();
        app.PmsConference.openConferenceById(res);
    },
    updateEventRow : function() {
        var row = $(this).closest('.eventrow');
        var data = thundashop.framework.createGsArgs(row);
        var event = thundashop.Ajax.createEvent('','updateEventRow',$('.PmsConference'),data);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.failedtoupdateevent').html(res);
        });
    },
    updateSpecifiedEventRow : function(row) {
        var data = thundashop.framework.createGsArgs(row);
        var event = thundashop.Ajax.createEvent('','updateEventEntry', row, data);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            
        });
    },
    updateEventEntryRow : function() {
        var row = $(this).closest('.evententryrow');
        app.PmsConference.updateSpecifiedEventRow(row);
    }
};
app.PmsConference.init();