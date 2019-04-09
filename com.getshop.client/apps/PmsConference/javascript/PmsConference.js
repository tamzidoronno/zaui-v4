app.PmsConference = {
    init : function() {
        $(document).on('keyup','.PmsConference .updaterowkeyup', app.PmsConference.updateEventRow);
        $(document).on('change','.PmsConference .updaterowselect', app.PmsConference.updateEventRow);
        $(document).on('click','.PmsConference .openevent', app.PmsConference.openEvent);
        $(document).on('click','.PmsConference .openconference', app.PmsConference.openConference);
        $(document).on('keyup','.PmsConference .updatevententryrow', app.PmsConference.updateEventEntryRow);
    },
    entryDeleted : function(id) {
        $('.evententryrow[entryid="'+id+'"]').remove();
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