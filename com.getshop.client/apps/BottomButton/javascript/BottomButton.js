app.BottomButton = {
     loadSettings: function(element, application) {
        var config = {
            draggable: true,
            application: application,
            title: __f("Settings"),
            items: [
                {
                    icontype: "awesome",
                    icon: "fa-arrows",
                    iconsize : "30",
                    title: __f("Edit link to button"),
                    click: function() {
                        app.BottomButton.edit(application);
                    }
                }
            ]
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    },
    
    edit: function(application) {
        var event = thundashop.Ajax.createEvent(null, "edit", application, {});
        thundashop.common.showInformationBox(event, __f("Edit button"));
    }
};