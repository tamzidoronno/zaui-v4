app.SedoxProductSearcher = {
    init: function() {
        $(document).on('change', '.SedoxProductSearcher input', app.SedoxProductSearcher.search);
    },
            
    search: function() {
        data = {
            searchKey : $(this).val()
        };
        console.log(data);
        var event = thundashop.Ajax.createEvent(null, "searchProduct", this, data);
        thundashop.Ajax.post(event);
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
                    title: __f("Edit text content"),
                    click: app.ContentManager.editContent
                },
                {
                    icontype: "awesome",
                    icon: "fa-download",
                    iconsize : "30",
                    title: __f("Activate / Deactivate pdf button"),
                    click: app.ContentManager.activateDeactivatePDF
                }
                
            ]
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
};

app.SedoxProductSearcher.init();