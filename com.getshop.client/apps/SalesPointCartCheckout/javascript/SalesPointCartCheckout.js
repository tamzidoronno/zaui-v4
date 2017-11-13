app.SalesPointCartCheckout = {
    init: function() {
        $(document).on('change', '.SalesPointCartCheckout .matrix_input' , this.cartChanged);
        $(document).on('change', '.SalesPointCartCheckout .matrix_item' , this.cartChanged);
        $(document).on('change', '.SalesPointCartCheckout .itemcount' , this.cartChanged);
        $(document).on('change', '.SalesPointCartCheckout .itemprice' , this.cartChanged);
        $(document).on('change', '.SalesPointCartCheckout .addon_checked' , this.cartChanged);
        $(document).on('change', '.SalesPointCartCheckout .addon_count' , this.cartChanged);
        $(document).on('change', '.SalesPointCartCheckout .addon_price' , this.cartChanged);
    },
    
    cartChanged: function() {
        var data = {};
        
        data.cartItems = [];
        
        $('.SalesPointCartCheckout .cartitem.row').each(function() {
            var cartItem = app.SalesPointCartCheckout.createCartItem(this);
            data.cartItems.push(cartItem);
        });
        
        var event = thundashop.Ajax.createEvent(null, "updateCartAndPrice", this, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(res) {
            $('.SalesPointCartCheckout .summary span').html(res);
        });
        
    },
    
    createCartItem: function(row) {
        var matrixRows = $(row).find('.pricematrix_row');
        var addonRows = $(row).find('.pmsaddonsrow');
        
        var cartItem = {};

        cartItem.id = $(row).attr('cartItemId');

        if (matrixRows.length) {
            cartItem.priceMatrix = app.SalesPointCartCheckout.createPriceMatrix(matrixRows);
        } else if (addonRows.length) {
            cartItem.addons = app.SalesPointCartCheckout.createAddons(addonRows);
        } else {
            cartItem.count = $(row).find('.itemcount').val();
            cartItem.price = $(row).find('.itemprice').val();
        }
        
        return cartItem;
    },
    
    createAddons: function(addonRows) {
        var addons = [];
        
        addonRows.find('.addonlist_row').each(function() {
            var addon = {};
            addon['id'] = $(this).attr('addonid');
            addon['count'] = $(this).find('.addon_count').val();
            addon['price'] = $(this).find('.addon_price').val();
            addon['enabled'] = $(this).find('input[type="checkbox"]').is(':checked');
            addons.push(addon);
        });
        
        return addons;
    },
    
    createPriceMatrix: function(matrixRows) {
        var matrix = [];
        
        matrixRows.each(function() {
            var row = {};
            row['enabled'] = $(this).find('input[type="checkbox"]').is(':checked');
            row['value'] = $(this).find('input[type="textfield"]').val();
            row['date'] = $(this).find('span').html();
            matrix.push(row);
        });
        
        return matrix;
    }
}

app.SalesPointCartCheckout.init();