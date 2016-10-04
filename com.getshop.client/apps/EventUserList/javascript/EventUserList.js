app.EventUserList = {
    init: function() {
        $(document).on('click', '.EventUserList .addcomment', app.EventUserList.showUserComments);
        $(document).on('click', '.EventUserList .showsettings', app.EventUserList.showSettings);
        $(document).on('click', '.EventUserList .downloadReport', app.EventUserList.downloadReport);
        $(document).on('change', '.EventUserList .participationstatus', app.EventUserList.participationStatusChanged);
        $(document).on('mouseenter', '.EventUserList .showspecialinfo', app.EventUserList.showspecialinfo);
        $(document).on('mouseleave', '.EventUserList .showspecialinfo', app.EventUserList.hidespecialinfo);
        
        $(document).on('mouseenter', '.EventUserList .companyInfoSpan', app.EventUserList.showExtraCompanyInfo);
        $(document).on('mouseleave', '.EventUserList .companyInfoSpan', app.EventUserList.hideExtraCompanyInfo);
    },
    
    showExtraCompanyInfo: function() {
        $(this).find('.companyextrainfo').show();
    },
    
    hideExtraCompanyInfo: function() {
        $(this).find('.companyextrainfo').hide();
    },
    
    downloadReport: function() {
        alert("OKE");
    },
    
    showspecialinfo: function() {
        $(this).find('.user_special_comments').show()
    },
    
    hidespecialinfo: function() {
        $('.EventUserList .user_special_comments').hide();
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