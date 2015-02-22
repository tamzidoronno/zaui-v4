app.GetShopDonate = {
    init: function() {
        $(document).on('click', '.GetShopDonate .donate', app.GetShopDonate.changeDonation);
        $(document).on('click', '.GetShopDonate .shop_button', app.GetShopDonate.doDonation);
    },
    
    doDonation: function() {
        var data = {
            productId : $('.donate_active').attr('gs_product')
        };
        
        var event = thundashop.Ajax.createEvent(null, "addProductToCart", this, data);
        thundashop.Ajax.postWithCallBack(event, function() {
            thundashop.common.goToPage("cart&cartCustomerId=401fef3c-0eed-4e5a-b4a5-126d48274829");
        });
    },
    
    changeDonation: function() {
        $('.donate_active').removeClass('donate_active');
        $(this).addClass('donate_active');
    }
};

app.GetShopDonate.init();