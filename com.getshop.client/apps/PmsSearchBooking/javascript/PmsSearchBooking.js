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
        $(document).on('click', '.PmsSearchBooking .payment_option_choice', this.paymentSelected);
        $(document).on('click', '.PmsSearchBooking .pagenumber', this.changeLogIndex);
        $(document).on('change', '.PmsSearchBooking [gsname]', this.formChanged);
    },
    
    changeLogIndex: function() {
        var scope = $('.PmsSearchBooking .datarow.active');
        var data = {
            selectedTab : "log",
            id: scope.find('[gsname="id"]').val(),
            roomId: scope.find('[gsname="roomId"]').val(),
            index : $(this).attr('index')
        }
        
        var event = thundashop.Ajax.createEvent(null, "renderTabContent", this, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, app.PmsSearchBooking.tabChanged, data);
    },
    
    formChanged: function() {
        $('.PmsSearchBooking .datarow.active [gstype="submit"]').removeClass('disabled');
    },
    
    paymentSelected: function() {
        var appId = $(this).attr('payment');
        $('.payment_option_choice').removeClass('active');
        $('.payment_content').hide();
        $('.payment_content[payment="'+appId+'"]').show();
        $(this).closest('.payment_option_choice').addClass('active');
    },
    
    ordersTabChanged: function(res) {
        $('.PmsSearchBooking .guestinformation[tab="orders"]').html(res);
        app.PmsSearchPaymentProcess.updatePaymentProcess();
        return false;
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
        app.PmsSearchBooking.formChanged();
    },
    
    removeGuest: function(e) {
        $(this).closest('.guest_row').remove();
        app.PmsSearchBooking.formChanged();
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
    
    contentSaved: function(res, target) {
        var scope = $(target).closest('.datarow_extended_content');
        scope.find('.bookingoverview_content_row').replaceWith(res);
        app.PmsSearchBooking.checkForUnsettledAmount(scope);
    },
//    
    checkForUnsettledAmount: function(scope) {
        var data = {
            id: scope.find('[gsname="id"]').val(),
            roomId: scope.find('[gsname="roomId"]').val()
        }
        
        var event = thundashop.Ajax.createEvent(null, "hasUnsettledAmount", scope, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(res) {
            if (res === "YES") {
                $('.menuentry[tab="orders"]').addClass('unsettledamount'); 
            } else {
                $('.menuentry[tab="orders"]').removeClass('unsettledamount'); 
            }
        });
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
    
        var tab = $(this).attr('tab');
        
        if (tab === "orders" && !$('.PmsSearchBooking .datarow.active [gstype="submit"]').hasClass('disabled')) {
            alert(__f("Please save your changes before you go to payment"));
            return;
        }
    
        $('.PmsSearchBooking .menuarea .menuentry').removeClass('active');
        $(this).addClass('active');
        
        $('.PmsSearchBooking .workarea div[tab]').removeClass('active');
        $('.PmsSearchBooking .workarea div[tab="'+tab+'"]').addClass('active');
        
        if ($(this).attr('clearTabContent') == "true") {
            var text = __f("Loading");
            $('.PmsSearchBooking .workarea div[tab="'+tab+'"]').html("<div class='loaderspinner'><i class='fa fa-spin fa-spinner'></i><br/>"+text+"</div>");
        }
        
        var data = {
            selectedTab : tab,
            roomId : $(this).closest('.menuarea').attr('roomId'),
            id :  $(this).closest('.menuarea').attr('bookingEngineId'),    
        }
        
        var event = thundashop.Ajax.createEvent(null, "subMenuChanged", this, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, app.PmsSearchBooking.tabChanged, data, true, true);
    },
    
    tabChanged: function(res, args) {
        if (res) {
            $('.PmsSearchBooking .workarea div[tab="'+args.selectedTab+'"]').html(res);
        }
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
    }
}

app.PmsSearchBooking.init();