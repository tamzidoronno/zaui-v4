CartManager = {
    init : function() {
        PubSub.subscribe("AJAXERROR", this.ajaxError, this);
        $(document).on('click', '.payment .method', CartManager.paymentMethodClicked);
        $(document).on('blur', '.CartManager .address input[name="emailAddress"]', CartManager.validateEmail);
        $(document).on('click', '.CartManager .usedifferentemail', CartManager.usedifferentemail);
        $(document).on('click', '.CartManager .agreeview', CartManager.loadTermsAndConditions);
    },
            
    paymentMethodClicked: function() {
        var id = $(this).attr('paymentid');
        $('.extendedinfo').hide();
        var html = $('.extendedinfo[extendedid='+id+']').text();
        $('.paymentdetails').hide();
        $('.paymentdetails.'+id).show();
        
        if (html)
            $('.extendedinfo[extendedid='+id+']').slideDown(200);

    },
    loadTermsAndConditions : function(event) {
        event.stopPropagation();
        event.preventDefault();
        var event = thundashop.Ajax.createEvent('','loadTermsAndConditions',$(this), {});
        thundashop.common.showInformationBox(event, __w("Terms and Conditions"));
    },
    ajaxError: function(msg, error) {
        if (error.responseText === "FAILED_TO_GET_SHIPMENT_BRING") {
            thundashop.common.Alert(__w("Failed"), __w("Was not able to get payment methods, please check your postcode"), true);
        }
        if (error.responseText === "PAYMENT_NOT_SELECTED") {
            thundashop.common.Alert(__w("Invalid payment method"),__w("Please select a valid payment method"), true);
        }
    },
    usedifferentemail : function() {
        var addressform = $('.CartManager .address');
        var loginform = $('.CartManager .loginform');
        loginform.fadeOut(function() {
            addressform.fadeIn(function() {
                addressform.find('input[name="emailAddress"]').val('');
                addressform.find('input[name="emailAddress"]').focus();
            });
        });
    },
            
    validateEmail : function() {
        var email = $(this).val();
        var td = $(this).parent();
        $('.CartManager .emailerror').remove();
        if(email.indexOf("@") === -1) {
            var error = $('<span class="emailerror">'+__w("Invalid email address") + "</span>");
            error.hide();
            td.append(error);
            error.fadeIn();
            return;
        }
        
        if($(this).attr('requirelogon') === "0") {
            return;
        }
        
        var event = thundashop.Ajax.createEvent('','checkEmail',$(this), { "email" : email });
        thundashop.Ajax.postWithCallBack(event, function(data) {
            if(data === "true") {
                var addressform = $('.CartManager .address');
                var loginform = $('.CartManager .loginform');
                addressform.fadeOut(function() {
                loginform.find('input[name="username"]').val(email);
                    loginform.fadeIn(function() {
                        loginform.find('input[name="password"]').focus();
                    });
                });
            }
        });
        
    },
    validateAddress : function() {
        var retval = true;
        
        $('.address input').each(function() {
            var value = $(this).val();
            if (value == "") {
                $(this).addClass('validate_error');
                retval = false;
            } else {
                $(this).removeClass('validate_error');
            }
        });
        
        
        if($('.address .emailerror').is(':visible')) {
            $('.address .emailerror').hide();
            $('.address .emailerror').fadeIn();
            retval = false;
        }
        
        if($('.address .password').is(':visible')) {
            var password = $('.address input[name="password"]').val();
            var repeat = $('.address input[name="repeat_password"]').val();
            $('.address input[name="password"]').removeClass('validate_error');
            $('.address input[name="repeat_password"]').removeClass('validate_error');

            if(password !== repeat || password === "") {
                $('.address input[name="password"]').addClass('validate_error');
                $('.address input[name="repeat_password"]').addClass('validate_error');
                thundashop.common.Alert(__w("Password missmatch"), __w("The passwords does not match."));
                retval = false;
            }
        }
        
        var terms = $('.address .termsandconditioncheckbox');
        $('.CartManager .invalid_terms').hide();
        if(terms.is(':visible') && !terms.is(':checked')) {
            $('.CartManager .invalid_terms').show();
            retval = false;
        }
        
        return retval;
    },
    
    validateMethod : function(type) {
        return $(type + ' .method.selected').size() == 1;
    },

    increaseProductCount: function(button) {
        var scope = this;
        if(this.cartTimeout !== undefined) {
            clearTimeout(this.cartTimeout);
        }
        
        var count = parseInt(button.parent().find('.counter').html(),10);
        var nextCount = count+1;
        this.cartTimeout = setTimeout(function() {
            scope.updateProductCount(nextCount, button);
        }, "1000");
        
        button.parent().find('.counter').html(nextCount);
    },
    
    decreaseProductCount: function(button) {
        var scope = this;
        
        if(this.cartTimeout !== undefined) {
            clearTimeout(this.cartTimeout);
        }
        
        var nextCount = parseInt(button.parent().find('.counter').html(),10);
        nextCount--;
        this.cartTimeout = setTimeout(function() {
            if (nextCount < 1)
                return;

            scope.updateProductCount(nextCount, button);
        }, "1000");
        
        if(nextCount >= 1) {
            button.parent().find('.counter').html(nextCount);
        }
    },
    
    updateProductCount: function(nextCount, button) {
        data = {
            cartItemId : button.attr('cartItemId'),
            count: nextCount
        }
        
        var event = thundashop.Ajax.createEvent('', 'updateProductCount', button, data);
        thundashop.Ajax.post(event);
    },
    
    placeOrder: function(target) {
        var data = {
            appId : $('.checkout .payment').find('.selected').attr('appid'),
            paymentMethod : $('.checkout .payment').find('.selected').attr('paymentid')
        };
        
        if ($('.extendedinfo') && $('.extendedinfo').length > 0 && $('.extendedinfo').is(':visible')) {
            $('.extendedinfo:visible').find('.selected').each(function() {
                var name = $(this).attr('name');
                var value = $(this).attr('value');
                data[name] = value;
            });
            
            $('.extendedinfo:visible').find('input:text').each(function() {
                var name = $(this).attr('name');
                var value = $(this).attr('value');
                data[name] = value;
            });
        }
        
        var event = thundashop.Ajax.createEvent('DeprecatedField', 'SaveOrder', target, data);
        event['synchron'] = "true";
        
        thundashop.Ajax.post(event, function(response) {
            if (response === "PLEASE_CHECK") {
                thundashop.common.Alert("Failed", "You need to make sure that everything is selected", true);
            } else {
                $('.CartManager').html(response);
            }
        });
    },

    showLoading: function() {
        $('#mycart').hide();
        $('#mycart-updating').show();
    },
            
    updateSmallCart: function() {
        CartManager.showLoading();
        var event = thundashop.Ajax.createEvent("CartManager", "updateSmallCart");
        event.synchron = "true";
        
        thundashop.Ajax.post(event, function(response) {
            $('#mycart-updating').hide();
            $('#mycart').html(response);
            $('#mycart').show();
        });
    },
    generateAddressData : function() {
        var cm = $('.CartManager');
        var data = {}
        cm.find('.address table').find('input').each(function() {
            var name = $(this).attr('name');
            var val = $(this).val();
            data[name] = val;
        });

        if(cm.find('.address .countrycode').is(':visible')) {
            data['countrycode'] = $('.CartManager .address .countrycode').val();
        }
        return data;
    },
            
    saveAddress: function() {
        var data = CartManager.generateAddressData();
        var event = thundashop.Ajax.createEvent("CartManager", "saveTempAddress", this, data);
        event['synchron'] = "true";
        thundashop.Ajax.post(event, function(){});
    }
}

$('.address #next').live('click', function() {
    var retval = CartManager.validateAddress();
    
    if (retval === true) {
        var addressInformation = CartManager.generateAddressData();

        var event = thundashop.Ajax.createEvent('DeprecatedField', 'LoadShipmentMethods', $('.checkout .address'), addressInformation);
        thundashop.Ajax.post(event);
    } 
});

$('.delivery #next').live('click', function() {
    var retval = CartManager.validateMethod('.delivery');
    retval = true;
    if (retval) {
        var data =  {
            shippingProduct : $('.checkout .delivery .selected').attr('type'),
            shippingType : $('.checkout .delivery .selected').attr('id')
        }
        var event = thundashop.Ajax.createEvent("", "saveShipping" , this, data);
        thundashop.Ajax.post(event);
    } else {
        thundashop.common.Alert(__w("Wrong delivery method"), __w("Please check your delivery options."), true);
    }
});


$('#previouse').live('click', function() {
    var event = thundashop.Ajax.createEvent("Depricated", "setPrev", this);
    thundashop.Ajax.post(event);
});


$('.checkout .delivery .method').live('click', function(e) {
    $('.checkout .delivery .method').removeClass('selected');
    $(this).addClass('selected');
    var method = $(this).attr('id');
    $('.checkout .checked_additional').hide();
    $('.checkout .checked_additional[method="'+method+'"]').show();
})

$('.checkout .payment .method').live('click', function(e) {
    $('.checkout .payment .method').removeClass('selected');
    $(this).addClass('selected');
})

$('.checkout #confirmorder').live('click', function() {
    debugger;
    var me = this;
    
    if ($(this).closest('.delivery').size() === 1) {
        var retval = CartManager.validateMethod('.delivery');
        retval = true;
        if (retval) {
            var data =  {
                shippingProduct : $('.checkout .delivery .selected').attr('type'),
                shippingType : $('.checkout .delivery .selected').attr('id')
            }
            var event = thundashop.Ajax.createEvent("", "saveShipping" , this, data);
            thundashop.Ajax.post(event, function() {
                CartManager.placeOrder(me);
            }, {}, true);
        } else {
            thundashop.common.Alert(__w("Wrong delivery method"), __w("Please check your delivery options."), true);
        }
    } else {
        var retval = CartManager.validateAddress();
        if(retval) {
            CartManager.placeOrder(me);
        }
    }

});

$('.checkout .delivery .shipmentproductrow').live('click', function(e) {
    $(this).find('input').attr('checked', true);
    var productname = $(this).attr('productname');
    var appName = $(this).closest('.checked_additional').attr('method');
    $('#'+appName).attr('type', productname);
})


$('.cart .up').live('click', function() {
    CartManager.increaseProductCount($(this));
});

$('.cart .down').live('click', function() {
    CartManager.decreaseProductCount($(this));
});

$(document).on(
    {
        mouseenter: function() {
            $(this).find('.removebutton').show();
        },
        mouseleave: function() {
            $(this).find('.removebutton').hide();
        }
    },
    '.CartManager .cart .product'   
);
    
$(document).on(
    { 
        blur: CartManager.saveAddress
    }, 
    '.CartManager .address input'
);
    
CartManager.init();