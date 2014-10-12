app.ProductWidget = {
    initEvents: function() {
        $(document).on('click', '.ProductWidget .buy_product', app.Product.addToCart);
        $(document).on('click', '.ProductWidget .create_new', function() {
            app.Product.create("productwidget", $(this).closest('.app').attr('appid'));
        });
        $(document).on('click', '.ProductWidget .select_existing', app.ProductWidget.replace);
    },
    replace: function() {
        var test = new ProductPicker($(this), {
            "type": "single",
            "id": $(this).closest('.app').attr('appid')
        });
        test.load();
    },
    loadSettings: function(element, application) {
        var config = {
            application: application,
            draggable: true,
            title: "Settings",
            items: [
                {
                    icontype: "awesome",
                    icon: "fa-plus-circle",
                    iconsize: "30",
                    title: __f("Create a new product"),
                    click: function() {
                        app.Product.create("productwidget", application.attr('appid'));
                    }
                },
                {
                    icontype: "awesome",
                    icon: "fa-chevron-circle-down",
                    iconsize: "30",
                    title: __f("Use an existing product"),
                    click: app.ProductWidget.replace
                }
            ]
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
}

app.ProductWidget.initEvents();
