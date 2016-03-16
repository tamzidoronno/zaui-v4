app.UserGroups = {
    
    init: function() {
        $(document).on('change', '#gss_search_for_user_to_add_to_group', app.UserGroups.search)
        $(document).on('click', '.gss_addUserToGroup', app.UserGroups.showSearchForm)
        PubSub.subscribe('GS_TOGGLE_CHANGED', app.UserGroups.toggleChanged);
    },
    
    toggleChanged: function(event, data) {
        if (data.model === "groupmodel" && data.attr === "usersRequireGroupReference") {
            if (data.val)  {
                $('#referencePlaceHolder').show();
                $('#usersRequireGroupReferenceValidationMin').show();
                $('#usersRequireGroupReferenceValidationMax').show();
            } else {
                $('#referencePlaceHolder').hide();
                $('#usersRequireGroupReferenceValidationMin').hide();
                $('#usersRequireGroupReferenceValidationMax').hide();
            }
        }
    },
    
    showSearchForm: function() {
        if ($('.gss_addUserForm').is(':visible')) {
            $('.gss_addUserForm').slideUp();
        } else {
            $('.gss_addUserForm').slideDown();
        }
    },
    
    search: function() {
        $('#gss_search_for_products_in_list').click();
    }
};

app.UserGroups.init();