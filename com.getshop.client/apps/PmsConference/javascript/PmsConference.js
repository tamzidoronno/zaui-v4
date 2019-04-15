app.PmsConference = {
    init : function() {
        $(document).on('keyup','.PmsConference .updaterowkeyup', app.PmsConference.updateEventRow);
        $(document).on('change','.PmsConference .updaterowselect', app.PmsConference.updateEventRow);
        $(document).on('click','.PmsConference .openevent', app.PmsConference.openEvent);
        $(document).on('click','.PmsConference .openconference', app.PmsConference.openConference);
        $(document).on('click','.PmsConference .canaddevent', app.PmsConference.loadQuickAddToConference);
        $(document).on('click','.PmsConference .removeguest', app.PmsConference.removeGuestFromEvent);
        $(document).on('keyup','.PmsConference .updatevententryrow', app.PmsConference.updateEventEntryRow);
        $(document).on('change','.PmsConference .choosemonthdropdown', app.PmsConference.changeTimePeriode);
    },
    removeGuestFromEvent : function() {
        var row = $(this).closest('tr');
        var event = thundashop.Ajax.createEvent('','removeGuestFromEvent',$(this), {
            guestId : $(this).attr('guestid'),
            eventId : $(this).attr('eventid')
        });
        thundashop.Ajax.postWithCallBack(event, function() {
            row.remove();
        });
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
        var eventid = $(this).attr('eventid');
        var event = thundashop.Ajax.createEvent('','openEvent',$('.PmsConference'), { "eventid" : eventid});
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.conferenceoverview').hide();
            $('.eventoverview').show();
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
    openConference : function() {
        var conferenceid = $(this).attr('conferenceid');
        app.PmsConference.openConferenceById(conferenceid);
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
        thundashop.Ajax.postWithCallBack(event, function() {});
    },
    updateEventEntryRow : function() {
        var row = $(this).closest('.evententryrow');
        var data = thundashop.framework.createGsArgs(row);
        var event = thundashop.Ajax.createEvent('','updateEventEntry', $(this), data);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            
        });
    }
};
app.PmsConference.init();