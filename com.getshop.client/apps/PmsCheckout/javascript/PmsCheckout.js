app.PmsCheckout = {
    init: function() {
        $(document).on('change', '.PmsCheckout .matrix_input' , this.cartChanged);
        $(document).on('change', '.PmsCheckout .matrix_item' , this.cartChanged);
        $(document).on('change', '.PmsCheckout .itemcount' , this.cartChanged);
        $(document).on('change', '.PmsCheckout .itemprice' , this.cartChanged);
        $(document).on('change', '.PmsCheckout .addon_checked' , this.cartChanged);
        $(document).on('change', '.PmsCheckout .addon_count' , this.cartChanged);
        $(document).on('change', '.PmsCheckout .addon_price' , this.cartChanged);
        $(document).on('click', '.PmsCheckout .display_cart_filter' , this.loadCartFilter);
        $(document).on('click', '.PmsCheckout .removeitem' , this.removeCartItem);
        $(document).on('click', '.PmsCheckout [gsname="appendtoorder"]' , this.changeAppendToOrder);
    },
    changeAppendToOrder : function() {
        var orderId = $(this).val();
        if(orderId) {
            $('.paymentmethodselection').hide();
            $('[gsname="paymenttypeselection"]').hide();
        } else {
            $('.paymentmethodselection').show();
            $('[gsname="paymenttypeselection"]').show();
        }
    },
    orderCreationCompleted : function() {
        app.PmsBookingRoomView.refresh();
    },
    removeCartItem : function() {
        var item = $(this).closest('.cartitem');
        var itemid = item.attr('cartitemid');
        var event = thundashop.Ajax.createEvent('','removeItem',$(this), {
            "id" : itemid
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            item.slideUp();
            app.PmsCheckout.updateTotal();
        });
    },
    updateTotal : function() {
        var event = thundashop.Ajax.createEvent('','getTotalInCart', $('.PmsCheckout'), {});
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.PmsCheckout .summary span').html(res);
        });
    },
    loadCartFilter : function() {
        var area = $('.checkoutarea');
        var event = thundashop.Ajax.createEvent('','loadCartFilter',$(this), {
            
        });
        
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.cartfilterarea').remove();
            area.prepend('<div class="cartfilterarea">' + res + "</div>");
        });
    },
    cartChanged: function() {
        var row = $(this).closest('.entryrow');
        var matrixrow = $(this).closest('.pricematrix_row');
        var addonrow = $(this).closest('.addonlist_row');
        var data = {
            itemid : row.closest('.cartitem').attr('cartitemid'),
            checked : row.find('.item_checkbox').is(':checked'),
            addonid : addonrow.attr('addonid'),
            addonCount : addonrow.find('.addon_count').val(),
            addonPrice : addonrow.find('.addon_price').val(),
            matrixPrice : matrixrow.find('.matrix_price').val(),
            matrixDate : matrixrow.find('.matrix_price').attr('date')
        };
        var event = thundashop.Ajax.createEvent('','updateItem', $('.PmsCheckout'), data);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            app.PmsCheckout.updateTotal();
        });
    },
    
    createCartItem: function(row) {
        var matrixRows = $(row).find('.pricematrix_row');
        var addonRows = $(row).find('.pmsaddonsrow');
        
        var cartItem = {};

        cartItem.id = $(row).attr('cartItemId');

        if (matrixRows.length) {
            cartItem.priceMatrix = app.PmsCheckout.createPriceMatrix(matrixRows);
        } else if (addonRows.length) {
            cartItem.addons = app.PmsCheckout.createAddons(addonRows);
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
            row['value'] = $(this).find('.matrix_price').val();
            row['date'] = $(this).find('span').html();
            matrix.push(row);
        });
        
        return matrix;
    }
}

app.PmsCheckout.init();