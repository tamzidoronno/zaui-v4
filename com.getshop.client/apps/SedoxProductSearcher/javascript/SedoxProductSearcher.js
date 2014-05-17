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
            items: []
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
};

app.SedoxProductSearcher.init();