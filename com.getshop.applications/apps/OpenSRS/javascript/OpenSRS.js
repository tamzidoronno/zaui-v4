thundashop.app.opensrs = {};

thundashop.app.opensrs.printResult = function(data) {
    $('.domain_selection').html(data);
    
}

thundashop.app.opensrs.removeDomainName = function(target) {
    var domain = target.attr('addr');
    var event = thundashop.Ajax.createEvent("", "removeDomainName", target, {"domain" : domain});
    thundashop.Ajax.post(event);
}

thundashop.app.opensrs.savePrimaryDomain = function(target) {
    var address = target.closest('.OpenSRS').find('.transfer_domain_name').val();
    var event = thundashop.Ajax.createEvent('', 'savePrimaryDomain', target, {
        'domain' : address
    });
    thundashop.Ajax.post(event);
}

thundashop.app.opensrs.checkAvailability = function(input) {
    var domain = input.val();
    var event = thundashop.Ajax.createEvent('', 'checkDomain', input, {
        'domain' : domain
    });
    input.closest('.OpenSRS').find('.step3').hide();
    input.closest('.OpenSRS').find('.step4').hide();
    
    input.closest('.OpenSRS').find('.step2').show();
    input.closest('.OpenSRS').find('.domain_selection').html('<img src="/skin/app/OpenSRS/images/ajaxloader.gif">');
    thundashop.Ajax.postWithCallBack(event, thundashop.app.opensrs.printResult);    
}

thundashop.app.opensrs.display_contact_information = function(price) {
    $('.OpenSRS').find('.step3').show();
    $('.OpenSRS .contact_information').fadeIn();
    
    $('.OpenSRS #paypal_amount_1').val(price);
    var domain = $('.OpenSRS .step1 .domaininput').val();
    $('.OpenSRS #paypal_item_name_1').val($('.OpenSRS #paypal_item_name_1').val() + " " + domain);
}

thundashop.app.opensrs.validate_contact_form = function() {
    var valid = true;
    $('.OpenSRS_inner .input_data').each(function(e) {
        if($(this).val().trim().length == 0) {
            valid = false;
        }
    });
    
    if(valid) {
        thundashop.app.opensrs.display_payment_information();
    } else {
        $('.OpenSRS_inner .step4').hide();
    }
}


thundashop.app.opensrs.updateContactInformation = function(target) {
    var data = {};
    target.closest('.OpenSRS').find('.input_data').each(function(e) {
        data[$(this).attr('reg')] = $(this).val();
    });
    data['domain'] = target.closest('.OpenSRS').find('.domaininput').val();
    var event = thundashop.Ajax.createEvent('', 'saveContactData', target, data);
    var response = thundashop.Ajax.postSynchron(event);
    return response;
}


thundashop.app.opensrs.display_payment_information = function() {
    $('.OpenSRS_inner .all_fields_error').hide();
    $('.OpenSRS_inner .step4').show();
    $('.OpenSRS_inner .selected_contact_information').fadeIn();
}

$('.OpenSRS_inner').live('keyup', function(e) {
    var target = $(e.target);
    if(e.keyCode == 13) {
        if(target.hasClass('textinput')) {
            thundashop.app.opensrs.checkAvailability(target);
        }
    }
    thundashop.app.opensrs.validate_contact_form(); 
});

$('.OpenSRS_inner').live('click', function(e) {
    var target = $(e.target); 
    if(target.hasClass('domain_check')) {
        thundashop.app.opensrs.checkAvailability(target.closest('.OpenSRS').find('.textinput'));
    }
    if(target.hasClass('save_transfer')) {
        thundashop.app.opensrs.savePrimaryDomain(target);
    }
    if(target.hasClass('deletedomain')) {
        thundashop.app.opensrs.removeDomainName(target);
    }
    
    if(target.hasClass('payment_button')) {
        var response = thundashop.app.opensrs.updateContactInformation(target);
        if(response == "OK") {
            $('.OpenSRS #paypalform').submit();
        }
    }
});

