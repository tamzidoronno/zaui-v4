app.EventUserList = {
    init: function() {
        $(document).on('click', '.EventUserList .addcomment', app.EventUserList.showUserComments);
        $(document).on('click', '.EventUserList .showsettings', app.EventUserList.showSettings);
        $(document).on('change', '.EventUserList .participationstatus', app.EventUserList.participationStatusChanged);
    },
    
    participationStatusChanged: function() {
        thundashop.Ajax.simplePost(this, "setParticiationStatus", {
            userId : $(this).attr('userid'),
            eventId : $(this).attr('eventId'),
            status : $(this).val()
        });
    },
    
    showSettings: function() {
        var event = thundashop.Ajax.createEvent(null, "showSettings", this, {
            eventId: $(this).attr('eventId'),
            userId: $(this).attr('userId')
        });
        
        thundashop.common.showInformationBox(event, __f("Settings"));
    },
    
    showUserComments: function() {
        var event = thundashop.Ajax.createEvent(null, "showComments", this, {
            eventId: $(this).attr('eventId'),
            userId: $(this).attr('userId')
        });
        
        thundashop.common.showInformationBox(event, __f("Comments"));
    }
};

app.EventUserList.init();