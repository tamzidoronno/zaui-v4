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
        thundashop.framework.toggleRightWidgetPanel('gs_modul_cart');
    },
    removeCartItem : function() {
        var item = $(this).closest('.cartitem');
        var itemid = item.attr('cartitemid');
        var event = thundashop.Ajax.createEvent('','removeItem',$(this), {
            "id" : itemid
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            item.slideUp();
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
        var data = {};
        
        data.cartItems = [];
        data.roomId = $(this).closest('.PmsCheckout').attr('roomid');
        
        if (data.roomId) {
            $(this).closest('.PmsCheckout[roomid="'+data.roomId+'"]').find('.cartitem.row').each(function() {
                var cartItem = app.PmsCheckout.createCartItem(this);
                data.cartItems.push(cartItem);
            });
        } else {
            $(this).closest('.PmsCheckout').find('.cartitem.row').each(function() {
                var cartItem = app.PmsCheckout.createCartItem(this);
                data.cartItems.push(cartItem);
            });
        }
        
        var event = thundashop.Ajax.createEvent(null, "updateCartAndPrice", this, data);
        event['synchron'] = true;
        
        var me = $(this);
        thundashop.Ajax.post(event, function(res, args) {
            if (args && typeof(args.roomId) !== "undefined")  {
                $('.PmsCheckout[roomid="'+args.roomId+'"] .summary span').html(res);
            } else {
                me.closest('.PmsCheckout').find('.summary span').html(res);
            }
            
            app.PmsBookingRoomView.refreshCurrentTab();
            
        }, data);
        
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