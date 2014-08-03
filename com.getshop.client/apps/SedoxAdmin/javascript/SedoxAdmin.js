app.SedoxAdmin = {
    init: function() {
        $(document).on('click', '.SedoxAdmin .sedox_admin_topmenu .entry', app.SedoxAdmin.topMenuClicked);
        $(document).on('change', '.SedoxAdmin #searchfield', app.SedoxAdmin.searchUsers);
        $(document).on('click', '.SedoxAdmin .userentrysearch', app.SedoxAdmin.showUserInformation);
        $(document).on('click', '.showUserInformationSedox', app.SedoxAdmin.showUserInformation);
        $(document).on('click', '.SedoxAdmin .savecredit', app.SedoxAdmin.updateUserCredit);
        $(document).on('click', '.SedoxAdmin .savedevelopers', app.SedoxAdmin.saveDevelopers);
        $(document).on('click', '.SedoxAdmin .saveusersettings', app.SedoxAdmin.saveUserSettings);
        $(document).on('change', '.SedoxAdmin #slavesearchtextfield', app.SedoxAdmin.searchForSlavesToAdd);
        $(document).on('change', '.SedoxAdmin .extraincometext', app.SedoxAdmin.addCreditForSlave);
        $(document).on('click', '.SedoxAdmin .addusertomaster', app.SedoxAdmin.addUserToMaster);
        $(document).on('click', '.SedoxAdmin i.removeuser', app.SedoxAdmin.removeUserFromMaster);
        $(document).on('click', '.SedoxAdmin .showuser', app.SedoxAdmin.changeToUser);
        $(document).on('change', '.SedoxAdmin #togglepassiveslave', app.SedoxAdmin.togglePassiveChanged);
    },
            
    togglePassiveChanged: function() {
        var masterId = $(this).closest('.useroverview').attr('userid');

        var data = {
            isPassiveSlave : $(this).is(":checked"),
            userId : masterId
        }
        
        var event = thundashop.Ajax.createEvent("", "toggleSlave", this, data);

        thundashop.Ajax.postWithCallBack(event, function(result) {
            thundashop.common.Alert("Success", "Saved");
        });
    },
            
    changeToUser: function() {
        var userId = $(this).attr('userid');
        app.SedoxAdmin.updateInfoBox(userId);
    },
            
    addCreditForSlave: function() {
        var slaveId = $(this).attr('userid');
      
        var data = {
            amount: $(this).val(),
            slave: slaveId
        };

        var event = thundashop.Ajax.createEvent("", "addCreditToSlave", this, data);

        thundashop.Ajax.postWithCallBack(event, function(result) {
            thundashop.common.Alert("Success", "Saved");
            app.SedoxAdmin.updateInfoBox(masterId);
        }, true);
    },
            
    removeUserFromMaster: function() {
        var masterId = $(this).closest('.useroverview').attr('userid');
        var slaveId = $(this).attr('userid');
      
        var data = {
            master: masterId,
            slave: slaveId
        };

        var event = thundashop.Ajax.createEvent("", "removeSlaveToMaster", this, data);

        thundashop.Ajax.postWithCallBack(event, function(result) {
            app.SedoxAdmin.updateInfoBox(masterId);
        });
    },
            
    addUserToMaster: function() { 
        var masterId = $(this).closest('.useroverview').attr('userid');
        var slaveId = $(this).attr('userid');
        
        var data = {
            master: masterId,
            slave: slaveId
        };

        var event = thundashop.Ajax.createEvent("", "addSlaveToMaster", this, data);

        thundashop.Ajax.postWithCallBack(event, function(result) {
            app.SedoxAdmin.updateInfoBox(masterId);
        });
    },
            
    searchForSlavesToAdd: function() {
        var data = {
            text : $('.SedoxAdmin #slavesearchtextfield').val()
        }
        
        var event = thundashop.Ajax.createEvent("", "searchForSlaves", $('.SedoxAdmin'), data);
        
        thundashop.Ajax.postWithCallBack(event, function(result) {
            $('.SedoxAdmin .add_slave_result').html(result);
        });
    },
    saveUserSettings: function() {
        var userId = $(this).attr('userId');
        
        var data = {
            userId : userId,
            allowNegativeCredit : $('.SedoxAdmin #allownegativecredit').is(':checked'),
            allowWindowsApplication : $('.SedoxAdmin #allowwindowsapp').is(':checked')
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
            
    updateInfoBox: function(userId) {
        var data = {
            userId: userId
        }
        
        var event = thundashop.Ajax.createEvent("", "showUserInformation", $('.SedoxAdmin'), data);
        thundashop.common.showInformationBox(event, "User information");
    },
            
    showUserInformation: function() {
        app.SedoxAdmin.updateInfoBox($(this).attr('userId'));
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