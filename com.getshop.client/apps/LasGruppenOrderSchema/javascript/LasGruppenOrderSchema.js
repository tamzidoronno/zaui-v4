app.LasGruppenOrderSchema = {
    addedRows: false,
    isCompany: false,
    
    init: function() {
        $(document).on('change', '.LasGruppenOrderSchema [name="security"]', app.LasGruppenOrderSchema.securityChanged);
        $(document).on('change', '.LasGruppenOrderSchema [name="shippingtype"]', app.LasGruppenOrderSchema.shipmentChanged);
        $(document).on('change', '.LasGruppenOrderSchema input[required]', app.LasGruppenOrderSchema.requiredFieldChanged);
        $(document).on('change', '.LasGruppenOrderSchema input[type_email]', app.LasGruppenOrderSchema.requiredFieldChanged);
        $(document).on('change', '.LasGruppenOrderSchema .radio_required input', app.LasGruppenOrderSchema.requiredFieldChanged);
        $(document).on('change', '.LasGruppenOrderSchema #samedeliveryasinvoice', app.LasGruppenOrderSchema.changeDeliveryInformation);
        $(document).on('change', '.LasGruppenOrderSchema .keyandcylinders', app.LasGruppenOrderSchema.keyandcylinders);
        $(document).on('change', '.LasGruppenOrderSchema #companyid', app.LasGruppenOrderSchema.checkCompany);
        $(document).on('click', '.LasGruppenOrderSchema .add_key_row', app.LasGruppenOrderSchema.addKeyRow);
        $(document).on('click', '.LasGruppenOrderSchema .add_cylinder_row', app.LasGruppenOrderSchema.addCylinderRow);
        $(document).on('click', '.LasGruppenOrderSchema .button_area .prev', app.LasGruppenOrderSchema.prev);
        $(document).on('click', '.LasGruppenOrderSchema .button_area .next', app.LasGruppenOrderSchema.next);
        
        $(document).on('change', '#invoice_company_name', app.LasGruppenOrderSchema.changeDeliveryInformation);
        $(document).on('change', '#invoice_address', app.LasGruppenOrderSchema.changeDeliveryInformation);
        $(document).on('change', '#invoice_postcode', app.LasGruppenOrderSchema.changeDeliveryInformation);
        $(document).on('change', '#invoice_cellphone', app.LasGruppenOrderSchema.changeDeliveryInformation);
        $(document).on('change', '#invoice_emailaddress', app.LasGruppenOrderSchema.changeDeliveryInformation);
    },
    
    validateEmail: function(email) {
        var re = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(email);
    },
    
    checkCompany: function() {
        var text = $('#companyid').val();
        if (text.length === 9) {
            var data = {
                number : text
            }
            var event = thundashop.Ajax.createEvent(null, "getBrReg", this, data);
            thundashop.Ajax.postWithCallBack(event, function(res) {
                if (res) {
                    var company = JSON.parse(res);
                    
                    console.log(company);
                    if (!$('#invoice_company_name').val())
                        $('#invoice_company_name').val(company.name);
                    
                    if (!$('#invoice_address').val())
                        $('#invoice_address').val(company.streetAddress);
                    
                    if (!$('#invoice_postcode').val())
                        $('#invoice_postcode').val(company.postnumber + " " + company.city);
                        
                    if (!$('#invoice_cellphone').val())
                        $('#invoice_cellphone').val()
                    
                    if (!$('#invoice_emailaddress').val())
                        $('#invoice_cellphone').val()
                    
                    app.LasGruppenOrderSchema.isCompany = true;
                } else {
                    app.LasGruppenOrderSchema.isCompany = false;
                }
            });
        }
    },
    
    changeDeliveryInformation: function() {
        if ($("#samedeliveryasinvoice").is(":checked")) {
            app.LasGruppenOrderSchema.shangeDeliveryInformation(); 
        } else {
            $('.deliveryinformationdiv table input').removeAttr('disabled');
            $('.deliveryinformationdiv table input').val("");
        }
    }, 
    
    shangeDeliveryInformation: function() {
        $('.deliveryinformationdiv table input').attr('disabled','disabled');
        $('#delivery_company_name').val($('#invoice_company_name').val());
        $('#delivery_address').val($('#invoice_address').val());
        $('#delivery_postcode').val($('#invoice_postcode').val());
        $('#delivery_cellphone').val($('#invoice_cellphone').val());
        $('#delivery_emailaddress').val($('#invoice_emailaddress').val());
    },
    
    requiredFieldChanged: function() {
        $(this).removeClass('required');
        $(this).closest('.radio_required').removeClass('radio_required');
    },
    
    keyandcylinders: function() {
        var show = $(this).is(":checked");
        var val = $(this).attr('gs_value');
        if (val === "1") {
            if (show) {
                $('.keys_setup').show();
            } else {
                $('.keys_setup').hide();
            }
        }
        
        if (val === "2") {
            if (show) {
                $('.cylinder_setup').show();
            } else {
                $('.cylinder_setup').hide();
            }
        }
    },
    
    isValidOrderLines: function() {
        return $('.keyandcylinders').is(':checked');
    },
    
    setupSecurityPage: function() {
        $('.nosecurityneeded').hide();
        $('.securityneeded').hide();
        
        if ($('#selection_key').is(':checked')) {
            $('.securityneeded').show();
        } else {
            $('.nosecurityneeded').show();
            $('.order_page4 .next').html('Send');
            $('.order_page4 .next').show();
        }
    },
    
    next: function() {
        var currentPage = $(this).closest('.orderpage');
        var pageNumber = currentPage.attr('pageNumer');
        pageNumber++;
        var validated = app.LasGruppenOrderSchema.validatePage(currentPage);
        if (!validated) {
            alert('For å kunne gå videre må du først rette opp i de røde feltene');
            return;
        }
        
        app.LasGruppenOrderSchema.addFirstRows();
        
        if (pageNumber === 3) {
            if (!app.LasGruppenOrderSchema.isValidOrderLines()) {
                alert('Du må minst velge nøkler, sylindrer eller begge');
                return;
            }
            app.LasGruppenOrderSchema.setupShippinhOptions();
        }
        
        if (pageNumber === 4) {
            app.LasGruppenOrderSchema.setupSecurityPage();
        }
        
        if (pageNumber === 5) {
            
            
            if ($('#signature').is(':checked')) {
                alert('Last ned pdf  Kommer');
                return;
            } else {
                if (!app.LasGruppenOrderSchema.checkPinCodeLength()) {
                    alert('Pinkoden må være minimum 6 tall/bokstaver');
                    return;
                }
            }
        }
        
        if ($('.LasGruppenOrderSchema [pageNumer="'+pageNumber+'"').length) {
            $(this).closest('.orderpage').hide();
            $('.LasGruppenOrderSchema [pageNumer="'+pageNumber+'"').show();
        }
    },
    
    checkPinCodeLength: function() {
        if (!$('#pincode_textfield').is(':visible')) {
            return true;
        }
        var pinCode = $('#pincode_textfield').val();
        
        if (pinCode.length > 5) {
            return true;
        }
        
        return false;
    },
    
    setupShippinhOptions: function() {
        $('.order_page3 .shipping_div_option').show();

        if (app.LasGruppenOrderSchema.isCompany) {
            $('#shipping_mypack').hide();
        } else {
            $('#shipping_express').hide();
            $('#shipping_bedriftspakke').hide();
        }

        if ($('#cylindersoption').is(':checked')) {
            $('#shipping_rekomandert').hide();
        }
    },
    
    addFirstRows: function() {
        if (!app.LasGruppenOrderSchema.addedRows) {
            app.LasGruppenOrderSchema.addKeyRow();
            app.LasGruppenOrderSchema.addCylinderRow();
            app.LasGruppenOrderSchema.addedRows = true;
        }
    },
    
    validatePage: function(page) {
        var validated = true;
        
        $('.helptext').remove();
        
        page.find('input').each(function() {
            if ($(this).attr('required') && $(this).is(':visible') && !$(this).val()) {
                $(this).addClass('required');
                validated = false;
            }
            
            if ($(this).val() && $(this).attr('type_email') && !app.LasGruppenOrderSchema.validateEmail($(this).val())) {
                $(this).addClass('required');
                $(this).parent().append('<span class="helptext">* Sett inn en gyldig epost addresse</span>');
                validated = false;
            }
            
            if ($(this).val() && $(this).attr('type_number') && isNaN($(this).val())) {
                $(this).addClass('required');
                $(this).parent().append('<span class="helptext">* Kun tall</span>');
                validated = false;
            }
        });
        
        page.find('div[radio_required="true"]').each(function() {
            var radioValidated = false;
            
            if (!$(this).is(':visible')) {
                return;
            }
            
            $(this).find('input[type="radio"]').each(function() {
                if ($(this).is(':checked') && $(this).is(':visible')) {
                    radioValidated = true;
                }
            });
            
            if (!radioValidated) {
                $(this).addClass('radio_required');
                validated = false;
            }
        });
        
        return validated;
    },
    
    prev: function() {
        var currentPage = $(this).closest('.orderpage');
        var pageNumber = currentPage.attr('pageNumer');
        pageNumber--;
        $(this).closest('.orderpage').hide();
        $('.LasGruppenOrderSchema [pageNumer="'+pageNumber+'"').show();
    },
    
    securityChanged: function() {
        $('.pincodesetup').hide();
        $('.signaturesecurity').hide();
        $('.order_page4 .next').show();
        
        var selectedval = $(this).attr('gs_value');
        if (selectedval === "1") {
            $('.order_page4 .next').html('Send');
            $('.pincodesetup').show();
        }
        if (selectedval === "2") {
            $('.order_page4 .next').html('Last ned PDF');
            $('.signaturesecurity').show();
        }
    },
    
    addKeyRow: function() {
        var row = $('table tr.keys_template_row').clone();
        row.removeClass('keys_template_row');
        row.find('.systemnumber').val($('#main_system_number').val());
        $('.keys_setup table').append(row);
    },
    
    shipmentChanged: function() {
        $('.select_stores').hide();
        $('.specialsending').hide();
        $('.extratext_shipping').hide();
        $('.shippinginformation').hide();
        
        var selectedval = $(this).attr('gs_value');
        
        if (selectedval === "1") {
            $('.extratext_shipping_1').show();
            $('.shippinginformation').show();
        }
        
        if (selectedval === "2") {
            $('.extratext_shipping_2').show();
            $('.shippinginformation').show();
        }
        
        if (selectedval === "3") {
            $('.extratext_shipping_3').show();
            $('.shippinginformation').show();
        }
        
        if (selectedval === "4") {
            $('.select_stores').show();
        }
        if (selectedval === "5") {
            $('.specialsending').show();
            $('.shippinginformation').show();
        }
    },
    
    addCylinderRow: function() {
        var row = $('table tr.cylinder_template_row').clone();
        row.removeClass('cylinder_template_row');
        row.find('.systemnumber').val($('#main_system_number').val());
        $('.cylinder_setup table').append(row);
    }
}

app.LasGruppenOrderSchema.init();