app.UserGroups = {
    
    init: function() {
        $(document).on('change', '#gss_search_for_user_to_add_to_group', app.UserGroups.search)
    },
    
    search: function() {
        $('#gss_search_for_products_in_list').click();
    }
};

app.UserGroups.init();