app.ProductLists = {
    init: function() {
        $(document).on('click', '.ProductLists .setproductlist', app.ProductLists.setList);
        $(document).on('change', '#gss_filter_list_box_productlist', app.ProductLists.search);
        $(document).on('click', '.gss_addToList', app.ProductLists.addToList);
        $(document).on('click', '.gss_removeFromList', app.ProductLists.removeProductFromList);
    },
    
    removeProductFromList: function() {
        var data = {
            listId : $(this).attr('listId'),
            productId : $(this).attr('productId')
        }
        
        data['gss_method'] = "removeFromProductList";
        $(this).html($(this).html()+"...");
        getshop.Settings.doPost(data, $(this).parent(), app.ProductLists.removedSuccessfully);
    },
    
    removedSuccessfully: function(response, field, data) {
        app.ProductLists.search();
        $(field).slideUp(100, function() {
            $(this).remove();
            var contains = $(".gss_products_in_list").find('.gss_add_product_row').length;
            if (!contains) {
                $('.gss_no_products_in_list').removeClass('gss_no_products_in_list_hidden');
            }
        });
    },
    
    addToList: function() {
        var data = {
            listId : $(this).attr('listId'),
            productId : $(this).attr('productId')
        }
        
        data['gss_method'] = "addProductToList";
        getshop.Settings.doPost(data, $(this).parent(), app.ProductLists.addedSuccesfully);
        
        $('.gss_no_products_in_list').addClass('gss_no_products_in_list_hidden');
    },
    
    addedSuccesfully : function(response, field, data) {
        var button = $(field).find('.gss_addToList');
        button.html('Remove');
        button.removeClass('gss_addToList');
        button.addClass('gss_removeFromList');
        $('.gss_products_in_list').append(field);
    },
    
    search: function() {
        $('#gss_search_for_products_in_list').click();
    },
    
    setList: function() {
        var event = thundashop.Ajax.createEvent(null, "setList", this, $(this).attr('listid'));
        thundashop.Ajax.post(event);
    }
}

app.ProductLists.init();