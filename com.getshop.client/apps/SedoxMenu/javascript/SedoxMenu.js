app.SedoxMenu = {
    init: function() {
        $(document).on('change', '.SedoxMenu #searchinputfield', app.SedoxMenu.search);
    },
    search: function() {
        var data = {
            searchKey : $('.SedoxMenu #searchinputfield').val()
        };
        
        var event = thundashop.Ajax.createEvent("", "searchProduct", this, data);
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
    }
};

app.SedoxMenu.init();