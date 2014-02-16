//Put your javascript here.
app.Product = {
    attributesModified: false,
    initEvents: function() {
        $(document).on('click', '.Product .product_attributes .create_attribute', app.Product.createAttribute);
        $(document).on('keyup', '.Product .product_attributes .create_container input', app.Product.createAttribute);
        $(document).on('keyup', '.Product .add_value_container input', app.Product.createAttributeValue);
        $(document).on('click', '.Product .add_value_container .create_attr_value', app.Product.createAttributeValue);
        $(document).on('click', '.Product .add_value_container .create_attr_value', app.Product.createAttributeValue);
        $(document).on('click', '.Product .attribute_value .remove', app.Product.removeAttributeValue);
        $(document).on('click', '.Product .attribute_value .rename', app.Product.renameAttributeValue);
        $(document).on('keyup', '.Product .attribute_value .renameform input', app.Product.renameAttributeValueConfirmed);
        $(document).on('click', '.Product .attribute_value .renameform .second_rename_button', app.Product.renameAttributeValueConfirmed);
        $(document).on('click', '.Product .attribute_value .name.gs_button', app.Product.addAttributeValueToProduct);
        $(document).on('click', '.Product .product_attributes .attrcontainer.added .attribute_entry .fa-trash-o', app.Product.removeAddedAttribute);
        $(document).on('click', '.Product .save_product', app.Product.saveProduct);
        $(document).on('click', '.Product .product_title .fa', app.Product.editProduct);
        $(document).on('change', '.Product .product_variation_selection', app.Product.updateProductPrice);
        $(document).on('click', '.Product .buy', app.Product.addToCart);
    },
    updateProductPrice: function() {
        var priceField = $('.Product .price_inner');
        var productPrice = parseInt(priceField.attr('price'));
        $('.Product .product_variation_selection').each(function() {
            var price = $('option:selected', $(this)).attr('price');
            if (price !== undefined) {
                price = parseInt(price);
                productPrice += price;
            }
        });
        $(this).removeClass('error');
        priceField.html(productPrice);

    },
    saveProduct: function() {
        var box = $(this).closest('#informationbox');
        
        var title = box.find('.gs_product_data[data="title"]').val();
        if(!title) {
            box.find('.gs_product_data[data="title"]').val(__f('Product title'));
        }

        data = {};
        box.find('.gs_product_data').each(function() {
            data[$(this).attr('data')] = $(this).val()
        });
        data["variations"] = app.Product.variations.getConfig();
        
        var all_attributes = [];
        box.find('.attrcontainer.selection .added_container').find('.attribute_entry').each(function() {
            var name = $(this).find('.name').html();
            $(this).find('.attribute_value').each(function() {
                if ($(this).css('display') !== "none") {
                    var value = $(this).find('.name').html();
                    var id = $(this).attr('id');
                    var attr = { 
                        "group": name,
                        "value": value,
                        "id": id
                    }
                    all_attributes.push(attr);
                }
            });
        });
        data['all_attributes'] = all_attributes;
        data['attributes_modified'] = app.Product.attributesModified;

        var attr_to_prod = [];
        box.find('.attrcontainer.added .added_container').find('.attribute_entry').each(function() {
            var name = $(this).find('.name').html();
            var value = $(this).find('.value').html();
            var obj = {
                "name": name,
                "value": value
            }
            attr_to_prod.push(obj);
        });
        data['attr_to_prod'] = attr_to_prod;

        var event = thundashop.Ajax.createEvent('', 'saveProduct', $(this), data);
        thundashop.Ajax.post(event, function() {
            thundashop.common.hideInformationBox();
        });
    },
    /**
     * Example one : ("asdfe-ff-ee-dd-eeer", "list"}
     * Example two: ("main_1", "area");
     * @param String target The target you would like to add the product to. appid or area name.
     * @param String subtype area / list, if area is specified it will add it to a product a productwidget at this area.
     */
    create : function(target, subtype) {
        var event = thundashop.Ajax.createEvent('','showPageLayoutSelection','',{
            "pagetype" : "product",
            "pageSubType" : subtype,
            "target" : target
        });
        thundashop.common.showInformationBox(event, __f('Create a new product'));
    },
    addToCart: function() {
        if($(this).attr('type') === "productlist") {
            var productOriginal = $(this).closest('.product');
        } else if ($(this).attr('type') === "productwidget") {
            var productOriginal = $(this).closest('.product');
        } else {
            var productOriginal = $(this).closest('.applicationinner');
        }
        var productId = productOriginal.find('#ProductId').val();
        var product = productOriginal.clone();
        var variations = [];
        
        var failed = false;
        
        productOriginal.find('.product_variation_selection').each(function() {
            if($(this).val() === "none") {
                $(this).addClass('error');
                failed = true;
            } else {
                $(this).removeClass('error');
            }
        });
        if(failed) {
            thundashop.common.Alert(__f("Variation needed"), __f("You need to select the variations before you can buy this product."), true);
            return;
        }
        
        product.html("");
        product.removeAttr('appsettingsid');
        product.removeAttr('class');
        product.removeAttr('appid');
        product.removeAttr('app');
        product.addClass('AnimationProductManager');
        $('body').append(product);
        product.css('position', 'absolute');
        product.css('border', 'solid 2px #000');
        product.css('width', productOriginal.width());
        product.css('height', productOriginal.height());
        product.css('left', productOriginal.offset().left);
        product.css('top', productOriginal.offset().top);
        product.css('filter', 'alpha(opacity=50)');
        product.css('opacity', '0.5');
        product.css('background-color', '#000');

        product.animate(
                {
                    height: '0px',
                    width: '0px',
                    left: $('#mycart').offset().left + 5,
                    top: $('#mycart').offset().top + 10
                },
        300, function() {
            product.remove();
        }
        );

        CartManager.showLoading();
        var data = {
            productid: productId,
            variations: variations
        };
        var event = thundashop.Ajax.createEvent("ProductManager", 'addProductToCart', productOriginal, data);
        thundashop.Ajax.post(event, function() {
            CartManager.updateSmallCart();
        });
    },
    removeAddedAttribute: function() {
        $(this).closest('.attribute_entry').fadeOut(function() {
            $(this).remove();
        });
    },
    createAttribute: function(event) {
        if ($(this).is('input') && event.keyCode !== 13) {
            return;
        }
        app.Product.attributesModified = true;

        var container = $(this).closest('.attrcontainer');
        var row = container.find('.template');
        if (container.find('.create_container input').val() === "") {
            return;
        }

        row = row.clone();
        row.removeClass('template');
        row.find('.name').html(container.find('.create_container input').val());
        container.find('.added_container').prepend(row);
        row.fadeIn();
        container.find('.create_container input').val('');
    },
    addAttributeValueToProduct: function(event) {
        var name = $(this).closest('.attribute_entry').find('.name').html();
        var value = $(this).html();

        var container = $('#informationbox.Product .attrcontainer.added');
        var row = container.find('.template');

        //First check if the attribute already exists.
        var exists = false;
        container.find('.attribute_entry').each(function() {
            if ($(this).find('.name').html() === name) {
                $(this).find('.value').html(value);
                exists = true;
                return;
            }
        });
        if (exists) {
            return;
        }
        row = row.clone();
        row.removeClass('template');
        row.find('.name').html(name);
        row.find('.value').html(value);
        container.find('.added_container').prepend(row);
        row.fadeIn();
    },
    editProduct: function() {
        app.Product.attributesModified = false;
        var event = thundashop.Ajax.createEvent('', 'loadEditProduct', $(this), {});
        thundashop.common.showInformationBox(event, __f("Edit product"));
    },
    renameAttributeValueConfirmed: function() {
        if ($(this).is('input') && event.keyCode !== 13) {
            return;
        }
        app.Product.attributesModified = true;
        var form = $(this).closest('.renameform');
        var newText = form.find('input').val();
        var oldText = $(this).closest('.attribute_value').find('.name').html();
        $(this).closest('.attribute_value').find('.name').html(newText);
        form.remove();


        $('#informationbox .attrcontainer.added .value').each(function() {
            if ($(this).html() === oldText) {
                $(this).html(newText);
            }
        });


    },
    renameAttributeValue: function() {
        var renamevalue = $(this).closest('.attribute_value').find('.name').html();
        var renameform = $('<span class="renameform"><input type="text" value="' + renamevalue + '"><span class="gs_button small second_rename_button">' + __f("Rename") + '</span></span>')
        $(this).closest('.attribute_value').append(renameform);
    },
    createAttributeValue: function(event) {
        if ($(this).is(':input') && event.keyCode !== 13) {
            return;
        }
        app.ProductManager.attributesManipulated = true;

        var container = $(this).closest('.attribute_entry');
        var attrcontainer = $(this).closest('.attrcontainer');
        var value = container.find('input').val();
        if (value === "") {
            return;
        }

        //Check if this one already exists.
        var found = false;
        container.find('.attribute_value').each(function() {
            if ($(this).find('.name').html() === value) {
                found = true;
            }
        });

        if (found) {
            return;
        }

        container.find('input').val('');

        var button = attrcontainer.find('.template .attribute_value');
        button = button.clone();
        button.find('.name').html(value);
        button.attr('new', 'true');
        container.find('.attribute_value_row').prepend(button);
        button.fadeIn();
    },
    removeAttributeValue: function() {
        if (!confirm(__f("Are you sure you want to remove this?"))) {
            return;
        }
        app.Product.attributesModified = true;

        app.ProductManager.attributesManipulated = true;
        $(this).closest('.attribute_value').fadeOut(function() {
            var name = $(this).closest('.attribute_entry').find('.name').html();
            var value = $(this).closest('.attribute_value').find('.name').html();
            $(this).remove();
            $('#informationbox .attrcontainer.added .attribute_entry').each(function() {
                if ($(this).find('.name').html() === name && $(this).find('.value').html() === value) {
                    $(this).remove();
                }
            });
        });
    }
}

app.Product.initEvents();
