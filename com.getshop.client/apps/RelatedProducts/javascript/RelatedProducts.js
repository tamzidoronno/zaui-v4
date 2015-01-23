app.RelatedProducts = {
    init: function() {
        $(document).on('change', '.RelatedProducts #searchforproducts', app.RelatedProducts.searchForProducts)
        $(document).on('click', '.RelatedProducts .add_product_as_related', app.RelatedProducts.addProductAsRelated)
        $(document).on('click', '.RelatedProducts .remove_product_from_list', app.RelatedProducts.removeRelatedProduct);
    },
    
    removeRelatedProduct: function() {
        var data = {
            productId :  $(this).attr('product_id')
        }
        
        var event = thundashop.Ajax.createEvent(null, "removeRelatedProduct", this, data);
        thundashop.Ajax.post(event);
    },
    
    addProductAsRelated: function() {
        var data = {
            productId :  $(this).attr('product_id')
        }
        
        var event = thundashop.Ajax.createEvent(null, "addProductAsRelated", this, data);
        thundashop.Ajax.post(event);
    },
    
    searchForProducts: function() {
        var textField = $('.RelatedProducts #searchforproducts');
        var text = textField.val();
        var data = {
            searchWord : text
        }
        
        var event = thundashop.Ajax.createEvent(null, "searchForProducts", textField, data);
        thundashop.Ajax.postWithCallBack(event, app.RelatedProducts.productsReceived);
    },
    
    productsReceived: function(response) {
        $('.RelatedProducts .add_related_product_result').html(response);
    }
}

app.RelatedProducts.init();