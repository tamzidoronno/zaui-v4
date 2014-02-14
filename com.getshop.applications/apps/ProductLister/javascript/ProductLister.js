app.ProductLister = {
    initEvents: function() {
        $(document).on('click', '.ProductLister .buy_product', app.Product.addToCart);
        $(document).on('click', '.ProductLister .fa-arrow-circle-left', app.ProductLister.slideRight);
        $(document).on('click', '.ProductLister .fa-arrow-circle-right', app.ProductLister.slideLeft);
        $(document).on('click', '.ProductLister .attributeselection_row', app.ProductLister.selectedAttribute);
        $(document).on('click', '.ProductLister .product .remove_product', app.ProductLister.removeProductFromList);
    },
    removeProductFromList : function() {
        var id = $(this).closest('.product').find('#ProductId').val();
        var event = thundashop.Ajax.createEvent('','removeProduct',$(this), {
            "productid" : id
        });
        thundashop.Ajax.post(event);
    },
    slideRight : function() {
        var row = $(this).closest('.rowview').find('.inner_product_container');
        var products = row.find('.product');
        var productWidth = $(products[0]).outerWidth();
        var step = row.attr('step');
        if (typeof(step) === "undefined") {
            step = 0;
        }
        step--;
        if (step < 0) {
            return;
        }
        row.attr('step', step);

        var row = $(this).closest('.rowview').find('.inner_product_container');
        productWidth += 20;
        row.animate({
            left: '+=' + productWidth
        }, 200, function() {
        });
    },
    selectedAttribute: function() {
        var data = {
            value: $(this).attr('id')
        };
        if ($(this).attr('count') === "0") {
            return;
        }
        var event = thundashop.Ajax.createEvent('', 'addFilter', $(this), data);
        thundashop.Ajax.post(event);
    },
    slideLeft: function() {
        var row = $(this).closest('.rowview').find('.inner_product_container');
        var width = $(this).closest('.rowview').outerWidth();
        var products = row.find('.product');
        var productWidth = $(products[0]).outerWidth();
        var productsShown = Math.floor(width / productWidth);
        var ticks = products.length - productsShown;
        var step = row.attr('step');
        if (typeof(step) === "undefined") {
            step = 0;
        }
        if (step >= ticks) {
            return;
        }
        step++;
        row.attr('step', step);
        productWidth += 20;
        row.animate({
            left: '-=' + productWidth
        }, 200, function() {
        });
    },
    editList : function() {
        var test = new ProductPicker($(this), {
            "type" : "list", 
            "id" : $(this).closest('.app').attr('appid')
        });
        test.load();
    },
    setColumn : function(xtra, app) {
        var count = xtra.count;
        
        $(this).closest('.app').find('.rowview').addClass('listview');
        $(this).closest('.app').find('.rowview').removeClass('rowview');
        
        var view = $(this).closest('.app').find('.listview');
        var oldcount = view.attr('column');
        if(oldcount !== undefined) {
            view.removeClass("c"+oldcount);
        }
        count = parseInt(count);
        if(count > 1) {
            view.addClass('boxed');
        } else {
            view.removeClass('boxed');
        }
        view.addClass("c"+ count);
        view.attr('column', count);
        
        var event = thundashop.Ajax.createEvent('','updateColumnCount',$(this),xtra);
        thundashop.Ajax.postWithCallBack(event, function() {});
    },
    makeListView : function() {
        $(this).closest('.app').find('.listview').addClass('rowview');
        $(this).closest('.app').find('.listview').removeClass('listview');
        var view = $(this).closest('.app').find('.rowview');
        view.removeClass('boxed');
        var event = thundashop.Ajax.createEvent('','setView',$(this),{"view":"rowview"});
        thundashop.Ajax.postWithCallBack(event, function() {});
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
                    iconsize : "30",
                    title: __f("Add a new product to the list"),
                    click: function() {
                        app.Product.create("productlist", application.attr('appid'));
                    },
                    extraArgs: {}
                },
                {
                    icontype: "awesome",
                    icon: "fa-chevron-circle-down", 
                   iconsize : "30",
                    title: __f("Add / remove existing products"),
                    click: app.ProductLister.editList,
                    extraArgs: {}
                },
                {
                    text: "1",
                    title: __f("1 column"),
                    iconsize: "30",
                    click: app.ProductLister.setColumn,
                    extraArgs: { "count" : 1 }
                },
                {
                    text: "2",
                    title: __f("2 columns"),
                    iconsize: "30",
                    click: app.ProductLister.setColumn,
                    extraArgs: { "count" : 2 }
                },
                {
                    text: "3",
                    title: __f("3 columns"),
                    iconsize: "30",
                    click: app.ProductLister.setColumn,
                    extraArgs: { "count" : 3 }
                },
                {
                    text: "4",
                    title: __f("4 columns"),
                    iconsize: "30",
                    click: app.ProductLister.setColumn,
                    extraArgs: { "count" : 4 }
                },
                {
                    icontype: "awesome",
                    icon: "fa fa-ellipsis-h",
                    iconsize : "30",
                    title: __f("List product in row view"),
                    click: app.ProductLister.makeListView,
                    extraArgs: {}
                }
            ]
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
}

app.ProductLister.initEvents();