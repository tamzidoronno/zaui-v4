app.SalesPointViews = {
    init: function() {
        $(document).on('click', '.SalesPointViews .saveProductLists', app.SalesPointViews.saveProductList);
    },
    
    saveProductList: function() {
        var data = {
            productIds : []
        };
        
        $('.SalesPointViews .checkboxproductlist:checked').each(function() {
            data.productIds.push($(this).val());
        });
        
        thundashop.Ajax.simplePost(this, 'saveProductListToCurrentView', data);
    }
}

app.SalesPointViews.init();