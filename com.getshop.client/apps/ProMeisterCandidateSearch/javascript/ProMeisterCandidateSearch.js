app.ProMeisterCandidateSearch = {
    init: function() {
        $(document).on('click', '.ProMeisterCandidateSearch .search_for_users', app.ProMeisterCandidateSearch.search);
        $(document).on('keyup', '.ProMeisterCandidateSearch .search_text', app.ProMeisterCandidateSearch.keyUp);
        $(document).on('click', '.ProMeisterCandidateSearch .show_user_information', app.ProMeisterCandidateSearch.showUserInformation);
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