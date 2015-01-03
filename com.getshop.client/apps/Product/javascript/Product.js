app.Product = {
    init: function () {
        $(document).on('click', '.addProductToCart', app.Product.addProductToCart)
    },
    addProductToCart: function () {
        var data = {
            productId: $(this).attr('productId')
        }

        var button = $(this).parent();
        if ($('.gsmobilemenu').is(':visible')) {
                button.effect("transfer", {to: $('.gsmobilemenucart')}, 1000, function () {
                    var event = thundashop.Ajax.createEvent(null, "addProductToCart", this, data);
                    thundashop.Ajax.post(event);
                });
        } else {
            $('.gsarea[area="header"] .checkout_area').each(function () {
                var width = $(this).parent().width();
                $(this).parent().width(width);
                $(this).parent().css('position', 'fixed');
                var dom = this;
                button.effect("transfer", {to: $(this)}, 1000, function () {
                    $(dom).parent().css('position', 'relative');
                    var event = thundashop.Ajax.createEvent(null, "addProductToCart", this, data);
                    thundashop.Ajax.post(event);
                });
            });
        }
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
                    title: __f("Manage products in this list"),
                    click: app.Product.editProduct
                }
            ]
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    },
    editProduct: function (e, application) {
        var id = $(application).find('#productId').val();
        app.Products.gssinterface.editProduct(id);
    }
}

app.Product.init();