app.GetShopQuickUser = {
    init: function() {
        $(document).on('click', '.GetShopQuickUser .show_edit_user', this.toggleEditUser);
        $(document).on('click', '.GetShopQuickUser .show_change_user', this.toggleShowChangeUser);
        $(document).on('click', '.GetShopQuickUser .change_user_form [tab]', this.toggleTabClicked);
        $(document).on('click', '.GetShopQuickUser .searchbox .tab', this.changeTab);
    },
    
    toggleTabClicked: function() {
        $('.GetShopQuickUser .change_user_form .tab_active').removeClass('tab_active');
        $(this).addClass('tab_active');
        var tab = $(this).attr('tab');
        $('.GetShopQuickUser .tab_contents .tab_content').hide();
        $('.GetShopQuickUser .tab_contents .tab_content[tab_content="'+tab+'"]').show();
    },
    
    toggleEditUser: function() {
        app.GetShopQuickUser.closeChangeUser();
        var div = $('.GetShopQuickUser .edit_details_of_user');
        if (div.is(':visible')) {
            div.slideUp();
        } else {
            div.slideDown();
        }
    },
    
    toggleShowChangeUser: function() {
        $('.GetShopQuickUser .edit_details_of_user').hide();
        var div = $('.GetShopQuickUser .change_user_form');
        if (div.is(':visible')) {
            app.GetShopQuickUser.closeChangeUser();
        } else {
            div.slideDown();
        }
    },
   
    closeChangeUser: function() {
        $('.GetShopQuickUser .change_user_form').slideUp();
        $('.GetShopQuickUser .searchresult').html("");
    },
    
    userChanged: function(result) {
        app.GetShopQuickUser.updateFieldsAfterUserChangedOrCreated(result);
        app.GetShopQuickUser.closeChangeUser();
        return false;
    },
    
    userSearchResult: function(result) {
        $('.GetShopQuickUser .change_user_form .searchresult').html(result);
        return false;
    },
    
      
    userUpdated: function(result) {
        app.GetShopQuickUser.updateFieldsAfterUserChangedOrCreated(result);
        app.GetShopQuickUser.toggleEditUser();
        return false;
    },
        
    userCreated: function(result) {
        app.GetShopQuickUser.updateFieldsAfterUserChangedOrCreated(result);
        app.GetShopQuickUser.closeChangeUser();
        $('.GetShopQuickUser .edit_details_of_user').slideDown();
    },
    
    updateFieldsAfterUserChangedOrCreated: function(result) {
        $('.GetShopQuickUser .edit_details_of_user').html(result);
        
        var name = $(result).find('.fullName').val();
        $('.GetShopQuickUser span.booked_for').html(name);
        $('.GetShopQuickUser').closest('.datarow').find('.booked_for').html(name);
        
    },
  
    changeTab: function() {
        var tabName = $(this).attr('tab');
        $('.GetShopQuickUser .searchbox .tab').removeClass('active');
        $('.GetShopQuickUser .searchbox .tab_content').removeClass('active');
        
        $('.GetShopQuickUser .searchbox .tab[tab="'+tabName+'"]').addClass('active');
        $('.GetShopQuickUser .searchbox .tab_content[tab="'+tabName+'"]').addClass('active');
    }
}

app.GetShopQuickUser.init();