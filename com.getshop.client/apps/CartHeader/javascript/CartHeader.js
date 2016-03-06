app.CartHeader = {
    init : function() {
        PubSub.subscribe("PRODUCT_ADDED_TO_CART", app.CartHeader.updateCartCount);
    },
    
    updateCartCount : function() {
        var cart = $('.CartHeader');
        var event = thundashop.Ajax.createEvent('','getCartCount', cart,{});
        thundashop.Ajax.postWithCallBack(event, function(result) {
            cart.find('.counter').text(result);
        });
    }
};

app.CartHeader.init();