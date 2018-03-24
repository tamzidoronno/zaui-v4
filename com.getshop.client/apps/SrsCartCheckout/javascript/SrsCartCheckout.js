app.SrsCartCheckout = {
    init: function() {
        $(document).on('change', '.SrsCartCheckout .cartitem input', app.SrsCartCheckout.itemChanged);
    },
    
    itemChanged: function() {
        var row = $(this).closest('.cartitem');
        
        var data = {
            cartItemId: row.attr('cartitemid'),
            count : row.find('.count').val(),
            price : row.find('.price').val()
        }
        
        
        var event = thundashop.Ajax.createEvent(null, "updateCartItem", this, data);
        event['synchron'] = true;
        thundashop.Ajax.post(event, function(res) {
            $('.SrsCartCheckout .totalprice').html(res);
        });
    }
};

app.SrsCartCheckout.init();