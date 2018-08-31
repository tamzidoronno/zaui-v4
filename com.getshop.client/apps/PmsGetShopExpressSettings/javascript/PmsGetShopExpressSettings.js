app.PmsGetShopExpressSettings = {
    init : function() {
        $(document).on('click', '.PmsGetShopExpressSettings .loadproducts', app.PmsGetShopExpressSettings.loadproducts);
        $(document).on('click', '.PmsGetShopExpressSettings .addproducttoview', app.PmsGetShopExpressSettings.addproducttoview);
        $(document).on('click', '.PmsGetShopExpressSettings .removeProductFromMobileView', app.PmsGetShopExpressSettings.removeProductFromMobileView);
    },
    loadproducts : function() {
        var btn = $(this);
        app.PmsGetShopExpressSettings.selectedview = $(this).closest('tr').attr('viewid');
        var event = thundashop.Ajax.createEvent('','loadProducts', $(this), {});
        var view = $('.PmsGetShopExpressSettings .productview');
        thundashop.Ajax.postWithCallBack(event, function(res) {
            view.html(res);
            view.css('left', btn.position().left);
            view.css('top', (btn.position().top+20));
            view.show();
        });
    },
    addproducttoview : function() {
        var btn = $(this);
        var row = $("tr[viewid='" + app.PmsGetShopExpressSettings.selectedview + "']");
        var event = thundashop.Ajax.createEvent('','addproducttoview', $(this), {
            id : app.PmsGetShopExpressSettings.selectedview,
            prodid : $(this).attr('prodid')
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            row.find('.productsadded').html(res);
        });
        $('.productview').fadeOut();
    },
    removeProductFromMobileView : function() {
        var btn = $(this);
        var event = thundashop.Ajax.createEvent('','removeproductfromview', $(this), {
            id : $(this).closest('tr').attr('viewid'),
            prodid : $(this).attr('prodid')
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            btn.closest('tr').find('.productsadded').html(res);
        });
        $('.productview').fadeOut();
    }
};
app.PmsGetShopExpressSettings.init();


