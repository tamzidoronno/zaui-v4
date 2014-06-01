app.SedoxAdmin = {
    init: function() {
        $(document).on('click', '.SedoxAdmin .sedox_admin_topmenu .entry', app.SedoxAdmin.topMenuClicked);
        $(document).on('change', '.SedoxAdmin #searchfield', app.SedoxAdmin.searchUsers);
        $(document).on('click', '.SedoxAdmin .userentrysearch', app.SedoxAdmin.showUserInformation);
        $(document).on('click', '.SedoxAdmin .savecredit', app.SedoxAdmin.updateUserCredit);
        $(document).on('click', '.SedoxAdmin .savedevelopers', app.SedoxAdmin.saveDevelopers);
        $(document).on('click', '.SedoxAdmin .saveusersettings', app.SedoxAdmin.saveUserSettings);
    },
    saveUserSettings: function() {
        var userId = $(this).attr('userId');
        
        var data = {
            userId : userId,
            allowNegativeCredit : $('.SedoxAdmin #allownegativecredit').is(':checked')
        };
        
        var event = thundashop.Ajax.createEvent("", "saveUserInfo", this, data);
        
        thundashop.Ajax.post(event, function() {
            thundashop.common.Alert("Success", "Saved");
        });
    },
    saveDevelopers: function() {
        ids = [];
        $('.sedoxsettings input:checked').each(function() {
            var id = $(this).attr('id');
            ids.push(id);
        });
        
        var data = {
            activeDevelopers : ids
        };
        
        var event = thundashop.Ajax.createEvent("", "toggleDevelopers", this, data);
        
        thundashop.Ajax.post(event, function() {
            thundashop.common.Alert("Success", "Saved");
        });
    },
    updateUserCredit: function() {
        var data = {
            userId : $(this).attr('userId'),
            desc : $('.SedoxAdmin #sedox_credit_description').val(),
            amount : $('.SedoxAdmin #amount').val()
        }
        
        var event = thundashop.Ajax.createEvent("", "updateCredit", this, data);
        thundashop.Ajax.postWithCallBack(event, function() {
            thundashop.common.Alert("Succes", "Credit updated");
            $('.SedoxAdmin #sedox_credit_description').val("");
            $('.SedoxAdmin #amount').val("");
        })
    },
    showUserInformation: function() {
        var data = {
            userId: $(this).attr('userId')
        }
        var event = thundashop.Ajax.createEvent("", "showUserInformation", this, data);
        thundashop.common.showInformationBox(event, "User information");
    },
    searchUsers: function() {
        var data = {
            searchString : $(this).val()
        };
        
        var event = thundashop.Ajax.createEvent("", "searchForUsers", this, data);
        thundashop.Ajax.post(event);
    },
    loadSettings: function(element, application) {
        var config = {
            draggable: true,
            app: true,
            application: application,
            title: "Settings",
            items: []
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    },
            
    topMenuClicked: function() {
        var changeTo = $(this).attr('changeto');
        var menu = $(this).parent().attr('menu');
        var data = { 
            menu : menu,
            changeTo : changeTo
        };
        
        var event = thundashop.Ajax.createEvent(null, "changeToSubMenu", this, data);
        thundashop.Ajax.post(event);
    }
};

app.SedoxAdmin.init();