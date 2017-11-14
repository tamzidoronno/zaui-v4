app.SalesPointSelectPayment = {
    orderCreated: function(res) {
        var createByGetShopModule = $('#createByGetShopModule').val();
        if (createByGetShopModule) {
            thundashop.common.goToPage(null, null, "?changeGetShopModule="+createByGetShopModule);
        }
    }
};