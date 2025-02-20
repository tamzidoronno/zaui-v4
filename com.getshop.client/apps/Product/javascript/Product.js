app.Product = {
    init: function () {
        $(document).on('click', '.addProductToCart', app.Product.addProductToCart);
        $(document).on('click', '.gshideproductaddedbox', app.Product.hideProductAddedBox);
        $(document).on('click', '.gsgotocart', app.Product.gsgotocart);
        PubSub.subscribe("backendviewloaded", function(a,b) {
           if(b === "gss_productwork_area") {
               getshop.Model.productmodel.categories = gsscategorylist;
               getshop.Model.productmodel.addedAttributes = gssproducsattributes;
           }
        });
    },
    gsgotocart : function() {
        $('.gsaddedtocartbox').fadeOut();
        thundashop.common.goToPage('cart');
        $(window).scrollTop(0);
    },
    hideProductAddedBox : function() {
        $('.gsaddedtocartbox').fadeOut();
    },
    
    getVariations: function(productid) {
        var variations = {};
        
        $('.gs_variation_selector[productid="'+productid+'"]').each(function() {
            variations[$(this).attr('optionid')] = $(this).find('.gs_variation_select').val();
        });
        
        return variations;
    },
    
    addProductToCart: function () {
        var productId = $(this).attr('productId');
        var data = {
            productId: productId,
            variations : app.Product.getVariations(productId)
        }
        
        var copies = $('.ProductCopies');
        if(copies.length > 0) {
            data['count'] = copies.find('.gscopiescount').val();
        }

        var button = $(this).parent();
        if ($('.gsmobilemenu').is(':visible')) {
            button.effect("transfer", {to: $('.gsmobilemenucart')}, 1000, function () {
                var event = thundashop.Ajax.createEvent(null, "addProductToCart", this, data);
                thundashop.Ajax.postWithCallBack(event, function() {
                    PubSub.publish("PRODUCT_ADDED_TO_CART");
                    if(typeof(autonavigatetocart) !== "undefined" && autonavigatetocart) {
                        window.location.href="/?page=cart";
                    } else {
                        thundashop.framework.reprintPage();
                    }
                });
            });
        } else {
            if($('.gsarea[area="header"] .checkout_area').length > 0) {
                $('.gsarea[area="header"] .checkout_area').each(function () {
                    var width = $(this).parent().width();
                    $(this).parent().width(width);
                    $(this).parent().css('position', 'fixed');
                    var dom = this;
                    button.effect("transfer", {to: $(this)}, 1000, function () {
                        $(dom).parent().css('position', 'relative');
                        var event = thundashop.Ajax.createEvent(null, "addProductToCart", this, data);
                        thundashop.Ajax.postWithCallBack(event, function() {
                            if(typeof(autonavigatetocart) !== "undefined" && autonavigatetocart) {
                                window.location.href="/?page=cart";
                            } else {
                                thundashop.framework.reprintPage();
                            }
                        });
                    });
                });
            } else {
                 var event = thundashop.Ajax.createEvent(null, "addProductToCart", this, data);
                thundashop.Ajax.postWithCallBack(event, function() {
                    PubSub.publish("PRODUCT_ADDED_TO_CART");
                    if(isMobile) {
                        $('.gsaddedtocartbox').css('left', "0px");
                    } else {
                        $('.gsaddedtocartbox').css('left', button.offset().left);
                    }
                    $('.gsaddedtocartbox').css('top', button.offset().top-$(this).scrollTop());
                    $('.gsaddedtocartbox').fadeIn();
                });
            }
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
