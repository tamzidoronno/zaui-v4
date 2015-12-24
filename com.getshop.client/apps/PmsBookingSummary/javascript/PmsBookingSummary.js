app.PmsBookingSummary = {
    init : function() {
        $(document).on('click','.PmsBookingSummary .addaddonsbutton', app.PmsBookingSummary.addAddon);
        $(document).on('click','.PmsBookingSummary .removeAddon', app.PmsBookingSummary.removeAddon);
    },
    removeAddon : function() {
        var data = {
            itemtypeid : $(this).attr('itemtypeid')
        };
        var event = thundashop.Ajax.createEvent('','removeAddon', $(this),data);
        thundashop.Ajax.post(event);
    },
    addAddon : function() {
        var data = {
            itemtypeid : $(this).closest('.itemrow').attr('itemid')
        };
        var event = thundashop.Ajax.createEvent('','addAddon', $(this),data);
        thundashop.Ajax.post(event);
    },
    showSettings : function() {
        var event = thundashop.Ajax.createEvent('','showSettings',$(this), {});
        thundashop.common.showInformationBoxNew(event, 'Settings');
    },
    loadSettings: function (element, application) {
        var config = {
            draggable: true,
            app: true,
            application: application,
            title: "Settings",
            items: [
                {
                    icontype: "awesome",
                    icon: "fa-edit",
                    iconsize: "30",
                    title: __f("Show settings"),
                    click: app.PmsBookingSummary.showSettings
                }
            ]
        };

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
};

app.PmsBookingSummary.init();