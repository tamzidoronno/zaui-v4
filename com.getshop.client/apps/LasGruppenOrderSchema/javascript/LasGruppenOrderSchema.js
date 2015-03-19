app.LasGruppenOrderSchema = {
    init: function() {
        $(document).on('change', '.LasGruppenOrderSchema [name="security"]', app.LasGruppenOrderSchema.securityChanged);
        $(document).on('change', '.LasGruppenOrderSchema [name="shippingtype"]', app.LasGruppenOrderSchema.shipmentChanged);
        $(document).on('change', '.LasGruppenOrderSchema input[required]', app.LasGruppenOrderSchema.requiredFieldChanged);
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
                    
                    if (!$('#invoice_company_name').val())
                        $('#invoice_company_name').val(company.name);
                    
                    if (!$('#invoice_address').val())
                        $('#invoice_address').val(company.streetAddress);
                    
                    if (!$('#invoice_postcode').val())
                        $('#invoice_postcode').val(company.postnumber);
                        
                    if (!$('#invoice_cellphone').val())
                        $('#invoice_cellphone').val()
                    
                    if (!$('#invoice_emailaddress').val())
                        $('#invoice_cellphone').val()
                }
            });
        }
    },
    
    changeDeliveryInformation: function() {
        if ($("#samedeliveryasinvoice").is(":checked")) {
            app.LasGruppenOrderSchema.shangeDeliveryInformation(); 
        } else {
            $('.deliveryinformationdiv table input').removeAttr('disabled');
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
    
    next: function() {
        var currentPage = $(this).closest('.orderpage');
        var pageNumber = currentPage.attr('pageNumer');
        pageNumber++;
        var validated = app.LasGruppenOrderSchema.validatePage(currentPage);
        if (!validated) {
            alert('For å kunne gå videre må du først rette opp i de røde feltene');
            return;
        }
        
        if ($('.LasGruppenOrderSchema [pageNumer="'+pageNumber+'"').length) {
            $(this).closest('.orderpage').hide();
            $('.LasGruppenOrderSchema [pageNumer="'+pageNumber+'"').show();
        } else {
            alert('Ferdig?');
        }
    },
    
    validatePage: function(page) {
        var validated = true;
        
        page.find('input').each(function() {
            if ($(this).attr('required') && !$(this).val()) {
                $(this).addClass('required');
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
            $('.order_page4 .next').html('Print');
            $('.signaturesecurity').show();
        }
    },
    
    addKeyRow: function() {
        var row = $('table tr.keys_template_row').clone();
        row.removeClass('keys_template_row');
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
        }
    },
    
    addCylinderRow: function() {
        var row = $('table tr.cylinder_template_row').clone();
        row.removeClass('cylinder_template_row');
        $('.cylinder_setup table').append(row);
    }
}

app.LasGruppenOrderSchema.init();