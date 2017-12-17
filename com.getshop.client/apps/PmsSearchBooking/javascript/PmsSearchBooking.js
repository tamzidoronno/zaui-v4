app.PmsSearchBooking = {
    init: function() {
        $(document).on('click', '.PmsSearchBooking .searchbox .tab', this.changeTab);
        $(document).on('click', '.PmsSearchBooking .orderpreview .close', this.closePreview);
        $(document).on('click', '.PmsSearchBooking .orderpreview .closebutton', this.closePreview);
        $(document).on('click', '.PmsSearchBooking .orderpreview .continue', this.continueToBooking);
        $(document).on('click', '.PmsSearchBooking .menuarea .menuentry', this.menuClicked);
        $(document).on('click', '.PmsSearchBooking .bookinginformation .show_edit_user', this.toggleEditUser);
        $(document).on('click', '.PmsSearchBooking .bookinginformation .show_change_user', this.toggleShowChangeUser);
        $(document).on('click', '.PmsSearchBooking .bookinginformation .change_user_form [tab]', this.toggleTabClicked);
        $(document).on('click', '.PmsSearchBooking .bookinginformation .remove_guest', this.removeGuest);
        $(document).on('click', '.PmsSearchBooking .bookinginformation .add_more_guests', this.addGuest);
    },
    
    addGuest: function() {
        var cloned = $(this).closest('.bookinginformation').find('.guest_row.row_template').clone();
        
        var highest = 0;
        $('.guest_row').each(function() {
            var counter = parseInt($(this).attr('counter'));
            if (counter > highest) {
                highest = counter;
            }
        });
        
        highest++;
        cloned.removeClass('row_template');
        cloned.attr('counter', highest);
        cloned.css('display', 'block');
        
        var found = cloned.find('[temp_gsname="guestinfo_name"]'); found.removeAttr('temp_gsname'); found.attr('gsname', 'guestinfo_'+highest+'_name');
        var found = cloned.find('[temp_gsname="guestinfo_email"]'); found.removeAttr('temp_gsname'); found.attr('gsname', 'guestinfo_'+highest+'_email');
        var found = cloned.find('[temp_gsname="guestinfo_prefix"]'); found.removeAttr('temp_gsname'); found.attr('gsname', 'guestinfo_'+highest+'_prefix');
        var found = cloned.find('[temp_gsname="guestinfo_phone"]'); found.removeAttr('temp_gsname'); found.attr('gsname', 'guestinfo_'+highest+'_phone');
        
        $('.guestinfo').append(cloned);
    },
    
    removeGuest: function() {
        $(this).closest('.guest_row').remove();
    },
    
    toggleTabClicked: function() {
        $('.PmsSearchBooking .bookinginformation .change_user_form .tab_active').removeClass('tab_active');
        $(this).addClass('tab_active');
        var tab = $(this).attr('tab');
        $('.PmsSearchBooking .change_user_form .tab_contents .tab_content').hide();
        $('.PmsSearchBooking .change_user_form .tab_contents .tab_content[tab_content="'+tab+'"]').show();
    },
    
    userCreated: function(result) {
        app.PmsSearchBooking.updateFieldsAfterUserChangedOrCreated(result);
        app.PmsSearchBooking.closeChangeUser();
        $('.PmsSearchBooking .bookinginformation .edit_details_of_user').slideDown();
    },
    
    toggleShowChangeUser: function() {
        $('.PmsSearchBooking .bookinginformation .edit_details_of_user').hide();
        var div = $('.PmsSearchBooking .bookinginformation .change_user_form');
        if (div.is(':visible')) {
            app.PmsSearchBooking.closeChangeUser();
        } else {
            div.slideDown();
        }
    },
   
    closeChangeUser: function() {
        $('.PmsSearchBooking .bookinginformation .change_user_form').slideUp();
        $('.PmsSearchBooking .searchresult').html("");
    },
    
    toggleEditUser: function() {
        app.PmsSearchBooking.closeChangeUser();
        var div = $('.PmsSearchBooking .bookinginformation .edit_details_of_user');
        if (div.is(':visible')) {
            div.slideUp();
        } else {
            div.slideDown();
        }
    },
    
    userSearchResult: function(result) {
        $('.PmsSearchBooking .change_user_form .searchresult').html(result);
        return false;
    },
    
    userChanged: function(result) {
        app.PmsSearchBooking.updateFieldsAfterUserChangedOrCreated(result);
        app.PmsSearchBooking.closeChangeUser();
        return false;
    },
    
    updateFieldsAfterUserChangedOrCreated: function(result) {
        $('.PmsSearchBooking .bookinginformation .edit_details_of_user').html(result);
        
        var name = $(result).find('.fullName').val();
        $('.PmsSearchBooking .bookinginformation span.booked_for').html(name);
        $('.PmsSearchBooking .bookinginformation').closest('.datarow').find('.booked_for').html(name);
        
    },
    
    userUpdated: function(result) {
        app.PmsSearchBooking.updateFieldsAfterUserChangedOrCreated(result);
        app.PmsSearchBooking.toggleEditUser();
        return false;
    },
    
    menuClicked: function() {
        $('.PmsSearchBooking .menuarea .menuentry').removeClass('active');
        $(this).addClass('active');
        
        var tab = $(this).attr('tab');
        
        $('.PmsSearchBooking .workarea div[tab]').removeClass('active');
        $('.PmsSearchBooking .workarea div[tab="'+tab+'"]').addClass('active');
        
        var event = thundashop.Ajax.createEvent(null, "subMenuChanged", this, { selectedTab: tab });
        event['synchron'] = true;
        thundashop.Ajax.post(event, null, null, true, true);
    },
    
    continueToBooking: function() {
        thundashop.common.goToPage("checkout&changeGetShopModule=salespoint");
    },
    
    closePreview: function() {
        $('.PmsSearchBooking .orderpreview').fadeOut();
    },
    
    changeTab: function() {
        var tabName = $(this).attr('tab');
        $('.PmsSearchBooking .searchbox .tab').removeClass('active');
        $('.PmsSearchBooking .searchbox .tab_content').removeClass('active');
        
        $('.PmsSearchBooking .searchbox .tab[tab="'+tabName+'"]').addClass('active');
        $('.PmsSearchBooking .searchbox .tab_content[tab="'+tabName+'"]').addClass('active');
    },
    
    goToSalesPoint: function(res) {
        $('.PmsSearchBooking .orderpreview').fadeIn();
        $('.PmsSearchBooking .orderpreview .content').html(res.previewhtml);
//        console.log(res);
//        ;
    }
}

app.PmsSearchBooking.init();