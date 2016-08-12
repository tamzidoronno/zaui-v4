app.PmsConfiguration = {
    init: function () {
        $(document).on('change', '.PmsConfiguration .emailtypeselection', app.PmsConfiguration.changeEmailType);
        $(document).on('change', '.PmsConfiguration .smstypeselection', app.PmsConfiguration.changeSmsType);
        $(document).on('change', '.PmsConfiguration .admintypeselection', app.PmsConfiguration.changeAdminType);
        $(document).on('click', '.PmsConfiguration .changeview', app.PmsConfiguration.changeview);
        $(document).on('click', '.PmsConfiguration .addnewchannel', app.PmsConfiguration.addnewchannel);
        $(document).on('click', '.PmsConfiguration .removeChannel', app.PmsConfiguration.removeChannel);
        $(document).on('click', '.PmsConfiguration #contractfield', function() {
            thundashop.common.activateCKEditor('contractfield', {
                autogrow : false
            });
        });
        $(document).on('click', '.PmsConfiguration #otherinstructionsfiled', function() {
            thundashop.common.activateCKEditor('otherinstructionsfiled', {
                autogrow : false
            });
        });
        $(document).on('click', '.PmsConfiguration #fireinstructions', function() {
            thundashop.common.activateCKEditor('fireinstructions', {
                autogrow : false
            });
        });
    },
    removeChannel : function() {
        var channel = $(this).attr('channel');
        thundashop.Ajax.simplePost($(this), 'removeChannel', {
            "channel" : channel
        });
    },
    addnewchannel : function() {
        var channelName = prompt("Id for the new channel");
        if(!channelName) {
            return;
        }
        thundashop.Ajax.simplePost($(this), "addNewChannel", {
            "name" : channelName
        })
    },
    changeEmailType: function() {
        var newType = $('.PmsConfiguration .emailtypeselection').val();
        $('.PmsConfiguration .emailtype').hide();
        $('.PmsConfiguration .emailtype.'+newType).show();
    },
    changeSmsType: function() {
        var newType = $('.PmsConfiguration .smstypeselection').val();
        $('.PmsConfiguration .smstype').hide();
        $('.PmsConfiguration .smstype.'+newType).show();
    },
    changeAdminType: function() {
        var newType = $('.PmsConfiguration .admintypeselection').val();
        $('.PmsConfiguration .admintype').hide();
        $('.PmsConfiguration .admintype.'+newType).show();
    },
    changeview : function() {
        thundashop.common.destroyCKEditors();
        $('.pmsbutton.active').removeClass('active');
        $(this).addClass('active');
        $('.notificationpanel').hide();
        var newpanel = $(this).attr('data-panel');
        $('.'+newpanel).show();
        localStorage.setItem("pmsconfigtabselected", newpanel);
    },
    showSettings : function() {
        var event = thundashop.Ajax.createEvent('','showSettings',$(this), {});
        thundashop.common.showInformationBoxNew(event,'Pms form settings');
    },
    loadSettings : function(element, application) {
         var config = {
            draggable: true,
            app : true,
            application: application,
            title: "Settings",
            items: [
                {
                    icontype: "awesome",
                    icon: "fa-edit",
                    iconsize : "30",
                    title: __f("Add / Remove products to this list"),
                    click: app.PmsConfiguration.showSettings
                }
            ]
        };

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
};
app.PmsConfiguration.init();