app.ProductLists = {
    init: function() {
        $(document).on('click', '.ProductLists .setproductlist', app.ProductLists.setList);
        $(document).on('change', '#gss_filter_list_box_productlist', app.ProductLists.search);
        $(document).on('click', '.gss_addToList', app.ProductLists.addToList);
        $(document).on('click', '.gss_removeFromList', app.ProductLists.removeProductFromList);
        $(document).on('click', '#useForShowingSearchResult', app.ProductLists.useForShowingSearchResult);
    },
    
    loadSettings : function(element, application) {
         var config = {
            draggable: true,
            app : true,
            application: application,
            title: "Settings",
            items: [
                {
                    icontype: "awesome",
                    icon: "fa-list",
                    iconsize : "30",
                    title: __f("Add / Remove products to this list"),
                    click: app.ProductLists.goToList
                },
                {
                    icontype: "awesome",
                    icon: "fa-folder-open",
                    iconsize : "30",
                    title: __f("Go to product managment"),
                    click: app.Products.gssinterface.showProductManagement
                },
                {
                    text: "<span style='font-size: 22px;'><></span>",
                    click: function(papp1, papp) { app.ProductLists.setSlideView(papp, 0)},
                    title: __f("Set as slideview")
                },
                {
                    text: "<span style='font-size: 22px;'>1</span>",
                    click: function(papp1, papp) { app.ProductLists.setColumns(papp, 1)},
                    title: __f("Show as list")
                },
                {
                    text: "<span style='font-size: 22px;'>2</span>",
                    click: function(papp, papp) { app.ProductLists.setColumns(papp, 2); },
                    title: __f("Show as boxes")
                },
                {
                    text: "<span style='font-size: 22px;'>3</span>",
                    click: function(papp, papp) { app.ProductLists.setColumns(papp, 3); },
                    title: __f("Show as boxes")
                },
                {
                    text: "<span style='font-size: 22px;'>4</span>",
                    click: function(papp, papp) { app.ProductLists.setColumns(papp, 4); },
                    title: __f("Show as boxes")
                },
                {
                    text: "<span style='font-size: 22px;'>5</span>",
                    click: function(papp, papp) { app.ProductLists.setColumns(papp, 5); },
                    title: __f("Show as boxes")
                },
                {
                    text: "<span style='font-size: 22px;'>6</span>",
                    click: function(papp, papp) { app.ProductLists.setColumns(papp, 6); },
                    title: __f("Show as boxes")
                },
                {
                    text: "<span style='font-size: 22px;'>7</span>",
                    click: function(papp, papp) { app.ProductLists.setColumns(papp, 7); },
                    title: __f("Show as boxes")
                },
                {
                    text: "<span style='font-size: 22px;'>8</span>",
                    click: function(papp, papp) { app.ProductLists.setColumns(papp, 8); },
                    title: __f("Show as boxes")
                }
            ]
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    },
    
    setSlideView: function(app, columnsize) {
        debugger;
        var event = thundashop.Ajax.createEvent(null, "setSlideView", app, {});
        thundashop.Ajax.post(event)
    },
    
    setColumns: function(app, columnsize) {
        var event = thundashop.Ajax.createEvent(null, "setColumns", app, columnsize);
        thundashop.Ajax.post(event);
    },
    goToList: function(a,b,c) {
        var listId = $($(b).find('[listid]')[0]).attr('listid');
        if (!listId) {
            thundashop.common.Alert(__f("No list selected yet"), __f("Please select a list from the the list first"), true);
        } else {
            app.ProductLists.gssinterface.manageList(listId);
        }
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
        button.html(__f('Remove'));
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
    },
    
    useForShowingSearchResult: function() {
        var event = thundashop.Ajax.createEvent(null, "setAsSearchResultList", this, {});
        thundashop.Ajax.post(event);
    }
}

app.ProductLists.gssinterface = {
    manageList: function(listid) {
        getshop.Settings.showSettings();
        getshop.Settings.setApplicationId('f245b8ae-f3ba-454e-beb4-ecff5ec328d6', function() {
            var data = {
                gss_fragment : 'editlist',
                gss_view : 'gss_productwork_area',
                gss_value : listid
            }
            
            getshop.Settings.post({}, "gs_show_fragment", data);
        });
    },
    
    showListManagement: function() {
        getshop.Settings.showSettings();
        getshop.Settings.setApplicationId('f245b8ae-f3ba-454e-beb4-ecff5ec328d6', false);
    }
}

app.ProductLists.init();