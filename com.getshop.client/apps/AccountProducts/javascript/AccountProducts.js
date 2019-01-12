app.AccountProducts = {
    init: function() {
        $(document).on('click' , '.AccountProducts .saveaccountinginfo' , app.AccountProducts.saveAccountingPrimitive);
    },
    
    saveAccountingPrimitive: function() {
        var data = {};
        
        $('.AccountProducts .product_row').each(function() {
            var productId = $(this).attr('productid');
            var accountingId = $(this).attr('accountingid');
            if (!data[productId]) {
                data[productId] = [];
            }
            
            data[productId].push({
                accountingNumber : $(this).find('.accountingnumber').val(),
                taxGroupNumber : $(this).attr('taxgroup'),
                id : accountingId
            });
        });
        
        thundashop.Ajax.simplePost(this, "savePrimitiveAccounting", data);
    }
}

app.AccountProducts.init();