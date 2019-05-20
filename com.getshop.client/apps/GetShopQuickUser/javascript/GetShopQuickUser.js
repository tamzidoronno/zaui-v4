app.GetShopQuickUser = {
    init: function() {
        $(document).on('click', '.GetShopQuickUser .show_edit_user', this.toggleEditUser);
        $(document).on('click', '.GetShopQuickUser .show_change_user', this.toggleShowChangeUser);
        $(document).on('click', '.GetShopQuickUser .change_user_form [tab]', this.toggleTabClicked);
        $(document).on('click', '.GetShopQuickUser .searchbox .tab', this.changeTab);
        $(document).on('click','.GetShopQuickUser .searchbrregbutton', app.GetShopQuickUser.showBrregSearch);
        $(document).on('click','.GetShopQuickUser .brregsearchresultrow', app.GetShopQuickUser.selectBrregResult);
    },
    selectBrregResult : function() {
        var name = $(this).attr('name');
        var vatnumber = $(this).attr('vatnumber');
        $('[gsname="vatnumber"]').val(vatnumber);
        $('[gsname="companyname"]').val(name);
        $('.searchbrregarea').hide();
    },    
    searchResult : function(res) {
        if(res) {
            $('.brregsearchresult').html('');
            for(var k in res) {
                var company = res[k];
                var row = $('<div class="brregsearchresultrow" vatnumber="'+company.vatNumber+'" name="'+company.name+'"><span>' + company.vatNumber + "</span> - <span>" + company.name + "</span></div>");
                $('.brregsearchresult').append(row);
            }
        }
    },
    showBrregSearch : function() {
        $('.searchbrregarea').slideDown();
    },
    toggleTabClicked: function() {
        $('.GetShopQuickUser .change_user_form .tab_active').removeClass('tab_active');
        $(this).addClass('tab_active');
        var tab = $(this).attr('tab');
        $('.GetShopQuickUser .tab_contents .tab_content').hide();
        $('.GetShopQuickUser .tab_contents .tab_content[tab_content="'+tab+'"]').show();
        
        if(tab === "newuser") { $('.newuserinputfield').focus(); }
        if(tab === "existinguser") { $('.searchguestsfield').focus(); }
        if(tab === "company") { $('.searchcompanyfield').focus(); }
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
        $('.GetShopQuickUser .userNotSelected').removeClass('userNotSelected');
        
        if ($('.change_user_form').attr('invokeJavascriptFunctionAfterActions')) {
            var javascriptCallbackFunction = $('.change_user_form').attr('invokeJavascriptFunctionAfterActions');
            var callbackFunction = function(javascriptCallbackFunction) {
                var funtionBody = javascriptCallbackFunction + "();";
                var toExecute = new Function(funtionBody);
                toExecute();
            }
            callbackFunction(javascriptCallbackFunction);
        }
        
        return false;
    },
    
    userSearchResult: function(result) {
        $('.GetShopQuickUser .change_user_form .searchresult').html(result);
        return false;
    },
    
      
    userCreated: function(result) {
        app.GetShopQuickUser.updateFieldsAfterUserChangedOrCreated(result);
        app.GetShopQuickUser.closeChangeUser();
        $('.GetShopQuickUser .edit_details_of_user').slideDown();
        $('.GetShopQuickUser .userNotSelected').removeClass('userNotSelected');
        
        if ($('.change_user_form').attr('invokeJavascriptFunctionAfterActions')) {
            var javascriptCallbackFunction = $('.change_user_form').attr('invokeJavascriptFunctionAfterActions');
            var callbackFunction = function(javascriptCallbackFunction) {
                var funtionBody = javascriptCallbackFunction + "();";
                var toExecute = new Function(funtionBody);
                toExecute();
            }
            callbackFunction(javascriptCallbackFunction);
        }
    },
    
    updateFieldsAfterUserChangedOrCreated: function(result) {
        $('.GetShopQuickUser .edit_details_of_user').html(result);
        $('.GetShopQuickUser .edit_details_directprint').html(result);
        
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