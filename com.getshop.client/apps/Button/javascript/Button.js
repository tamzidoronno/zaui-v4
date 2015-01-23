app.Button = {
    init: function() {
        $(document).on('click', '.Button .select_product_add_to_cart_button_setup', app.Button.setProduct);
        $(document).on('click', '.Button .shop_button_saveNewText', app.Button.saveText);
        $(document).on('change', '.Button #setup_button_search_field', app.Button.searchForProducts);
        $(document).on('change', '.Button #setup_button_type_selector', app.Button.show);
        $(document).on('keyup', '.Button #filter_pages_list', app.Button.filterPages);
        $(document).on('click', '.Button .select_button_set_link_to_internal_page', app.Button.selectInternalPage);
    },
    
    filterPages: function() {
        var searchword = $(this).val();
        $('.Button').find('.outer_select_button_link_to_page').each(function() {
            $(this).show();
        });
        
        $('.Button').find('.outer_select_button_link_to_page').each(function() {
            var text = $(this).find('span').html();
            var result= text.search(new RegExp(searchword, "i"));
            if (result < 0) {
                $(this).hide();
            }
        });
    },
    
    selectInternalPage: function() {
        var data = {
            page_id: $(this).attr('page_id')
        };
        var event = thundashop.Ajax.createEvent(null, "setInternalPage", this, data);
        thundashop.Ajax.post(event, function() {
            alert(__f('Success, the button now links to the selected page'));
        });        
    },
    
    setProduct: function() {
        var data = {
            product_id: $(this).attr('product_id')
        };
        var event = thundashop.Ajax.createEvent(null, "setProductId", this, data);
        thundashop.Ajax.post(event, function() {
            app.Button.searchForProducts();
        });
    },
    
    searchForProducts: function() {
        var textField = $('.Button #setup_button_search_field');
        var data = {
            search: textField.val()
        };
        var event = thundashop.Ajax.createEvent(null, "searchForProduct", textField, data);
        event['sychron'] = true;
        thundashop.Ajax.postWithCallBack(event, function(result) {
            $('.Button .button_setup_searchresult').html(result);
        });
    },
    
    saveText: function() {
        var data = {
            text: $(this).closest('.Button').find('.button_text_content').html()
        };
        var event = thundashop.Ajax.createEvent(null, "saveText", this, data);
        thundashop.Ajax.post(event);
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
                    icon: "fa-edit",
                    iconsize : "30",
                    title: __f("Change text of button"),
                    click: app.Button.changeText
                },
                {
                    icontype: "awesome",
                    icon: "fa-gear",
                    iconsize : "30",
                    title: __f("Setup button"),
                    click: app.Button.setupButton
                }
            ]
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    },
    
    show: function() {
        var name =  $(this).find(':selected').attr('show_view')
        $('.Button .select_option_setup_button').hide();
        $('.Button .'+name).show();
    },
    
    setupButton: function(extra, application) {
        var event = thundashop.Ajax.createEvent(null, "showSetup", application, {});
        thundashop.common.showInformationBox(event, __f("Setup button"));
    },
    
    changeText: function(extra, application) {
        $(application).find('.button_text_content').attr('contenteditable', 'true');
        $(application).find('.shop_button_saveNewText').fadeIn();
        
        var el = $(application).find('.button_text_content')[0];
        app.Button.selectElementContents(el);
    },
    
    selectElementContents: function(el) {
        var range = document.createRange();
        range.selectNodeContents(el);
        var sel = window.getSelection();
        sel.removeAllRanges();
        sel.addRange(range);
    }

    
};

app.Button.init();