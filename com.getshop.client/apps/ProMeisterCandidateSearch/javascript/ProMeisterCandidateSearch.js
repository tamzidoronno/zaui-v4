app.ProMeisterCandidateSearch = {
    init: function() {
        $(document).on('click', '.ProMeisterCandidateSearch .search_for_users', app.ProMeisterCandidateSearch.search);
        $(document).on('keyup', '.ProMeisterCandidateSearch .search_text', app.ProMeisterCandidateSearch.keyUp);
        $(document).on('click', '.ProMeisterCandidateSearch .show_user_information', app.ProMeisterCandidateSearch.showUserInformation);
        $(document).on('click', '.ProMeisterCandidateSearch .companyrow .companyname', app.ProMeisterCandidateSearch.toggleShow);
    },
    
    toggleShow: function() {
        var div = $(this).closest('.companyrow').find('.companyUsers');
        
        var data = {
            companyId : $(this).attr('companyId'),
            visible : div.is(':visible') ? "false" : "true"
        }
        
        var event = thundashop.Ajax.createEvent(null, "toggleVisibility", this, data);
        thundashop.Ajax.post(event, null, {}, false, true);
        
        if (div.is(':visible')) {
            div.slideUp();
        } else {
            div.slideDown();
        }
    },
    
    showUserInformation: function() {
        var userInfo = $(this).closest('.userrow').find('.userInformation');
        
        if (userInfo.is(':visible')) {
            userInfo.slideUp();
        } else {
            userInfo.slideDown();
        }
    },
    
    keyUp: function(e) {
        if (e.keyCode === 13) {
            $('.ProMeisterCandidateSearch .search_for_users').click();
        }
    },
    
    search: function() {
        var data = {
            txt : $('.ProMeisterCandidateSearch .search_text').val()
        };
        
        thundashop.Ajax.simplePost(this, "searchForUsers", data);
    }
};

app.ProMeisterCandidateSearch.init();