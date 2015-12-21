app.PmsConfiguration = {
    init: function () {
        $(document).on('click', '.PmsConfiguration .changeview', app.PmsConfiguration.changeview);
        $(document).on('click', '.PmsConfiguration #contractfield', function() {
            thundashop.common.activateCKEditor('contractfield', {
                autogrow : false
            });
        });
    },
    changeview : function() {
        thundashop.common.destroyCKEditors();
        $('.pmsbutton.active').removeClass('active');
        $(this).addClass('active');
        $('.notificationpanel').hide();
        var newpanel = $(this).attr('data-panel');
        $('.'+newpanel).show();
    },
    showSettings : function() {
        var event = thundashop.Ajax.createEvent('','showSettings',$(this), {});
        thundashop.common.showInformationBox(event,'Pms form settings');
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