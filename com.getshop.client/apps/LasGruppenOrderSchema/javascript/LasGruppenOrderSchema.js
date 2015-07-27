app.LasGruppenOrderSchema = {
    addedRows: false,
    inProgress: false,
    isCompany: false,
    addedCount: 0,
    
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
        $(document).on('click', '.LasGruppenOrderSchema .login_button', app.LasGruppenOrderSchema.loginClicked);
        $(document).on('click', '.LasGruppenOrderSchema .loginform .dologin', app.LasGruppenOrderSchema.doLogin);
        $(document).on('click', '.LasGruppenOrderSchema .request_new_pincode', app.LasGruppenOrderSchema.requestNewPincode);
        $(document).on('click', '.LasGruppenOrderSchema .signin', app.LasGruppenOrderSchema.signin);
        $(document).on('keyup', '.LasGruppenOrderSchema .loginform .username', app.LasGruppenOrderSchema.doLoginKeyUp);
        $(document).on('keyup', '.LasGruppenOrderSchema .loginform .password', app.LasGruppenOrderSchema.doLoginKeyUp);
        $(document).on('keyup', '.LasGruppenOrderSchema .loginform_picode .pincode', app.LasGruppenOrderSchema.doSignupKeyUp);
        $(document).on('click', '.LasGruppenOrderSchema .logout_button', app.LasGruppenOrderSchema.logout_button);
        $(document).on('click', '.LasGruppenOrderSchema .menu_entry', app.LasGruppenOrderSchema.menuSelected);
        $(document).on('click', '.LasGruppenOrderSchema .delivery_address_to_edit', app.LasGruppenOrderSchema.editDeliveryAddress);
        $(document).on('click', '.LasGruppenOrderSchema .invoice_address_to_edit', app.LasGruppenOrderSchema.editInvoiceAddress);
        $(document).on('click', '.LasGruppenOrderSchema .deliveryoverview_button', app.LasGruppenOrderSchema.showDeliverAddresses);
        $(document).on('click', '.LasGruppenOrderSchema .invoiceaddresses_button', app.LasGruppenOrderSchema.showInvoiceAddresses);
        $(document).on('click', '.LasGruppenOrderSchema .deleteaddress', app.LasGruppenOrderSchema.deleteDelivaryAddress);
        $(document).on('click', '.LasGruppenOrderSchema .delete_invoice_address', app.LasGruppenOrderSchema.deleteInvoiceAddress);
        $(document).on('click', '.LasGruppenOrderSchema .start_order_by_address', app.LasGruppenOrderSchema.next);
        $(document).on('click', '.LasGruppenOrderSchema .show_new_address_form', app.LasGruppenOrderSchema.toggleNewAddressForm);
        
        $(document).on('click', '.LasGruppenOrderSchema .savedeliveryaddr', app.LasGruppenOrderSchema.saveDeliveryAddr);
        $(document).on('click', '.LasGruppenOrderSchema .invoice_address_to_save', app.LasGruppenOrderSchema.saveInvoiceAddr);
        
        $(document).on('change', '#invoice_company_name', app.LasGruppenOrderSchema.changeDeliveryInformation);
        $(document).on('change', '#invoice_address', app.LasGruppenOrderSchema.changeDeliveryInformation);
        $(document).on('change', '#invoice_postcode', app.LasGruppenOrderSchema.changeDeliveryInformation);
        $(document).on('change', '#invoice_cellphone', app.LasGruppenOrderSchema.changeDeliveryInformation);
        $(document).on('change', '#invoice_emailaddress', app.LasGruppenOrderSchema.changeDeliveryInformation);
    },
    
    toggleNewAddressForm: function() {
        var form = $('.LasGruppenOrderSchema #new_address_form_order');
        if (form.is(':visible')) {
            form.slideUp();
            $('.LasGruppenOrderSchema .show_new_address_form').slideDown();
        } else {
            $(this).slideUp();
            form.slideDown();
        }
    },
    
    saveInvoiceAddr: function() {
        var currentPage = $('.LasGruppenOrderSchema #edit_delivery_addr');
        var validated = app.LasGruppenOrderSchema.validatePage(currentPage);
        if (!validated) {
            alert('For å kunne gå videre må du først rette opp i de røde feltene');
            return;
        }
        
        var data = {
            vatNumber : $('.LasGruppenOrderSchema #edit_inv_birthday').val(),
            name : $('.LasGruppenOrderSchema #edit_inv_name').val(),
            addr1 : $('.LasGruppenOrderSchema #edit_inv_addr1').val(),
            addr2 : $('.LasGruppenOrderSchema #edit_inv_addr2').val(),
            postcode : $('.LasGruppenOrderSchema #edit_inv_postcode').val(),
            city : $('.LasGruppenOrderSchema #edit_inv_city').val(),
            addrid : $('.LasGruppenOrderSchema #edit_inv_addrid').val()
        }
        
        var event = thundashop.Ajax.createEvent(null, "saveInvoiceAddr", currentPage, data);
        thundashop.Ajax.postWithCallBack(event, function(result) {
            $('.LasGruppenOrderSchema .welcomepagecontent').html(result);
            $('.LasGruppenOrderSchema .invoiceaddresses_button').click();
        });
        
    },
    
    editInvoiceAddress: function() {
        $('.LasGruppenOrderSchema .invoice_addresses').hide();
        $('.LasGruppenOrderSchema .invoice_address_edit').show();
        
        $('.LasGruppenOrderSchema .invoice_address_edit #edit_inv_name').val($(this).find('.inv_name').html());
        $('.LasGruppenOrderSchema .invoice_address_edit #edit_inv_addr1').val($(this).find('.inv_addr').html());
        $('.LasGruppenOrderSchema .invoice_address_edit #edit_inv_addr2').val($(this).find('.inv_addr2').html());
        $('.LasGruppenOrderSchema .invoice_address_edit #edit_inv_postcode').val($(this).find('.inv_postcode').html());
        $('.LasGruppenOrderSchema .invoice_address_edit #edit_inv_city').val($(this).find('.inv_city').html());
        $('.LasGruppenOrderSchema .invoice_address_edit #edit_inv_birthday').val($(this).find('.inv_birthday').html());
        $('.LasGruppenOrderSchema .invoice_address_edit #edit_inv_addrid').val($(this).attr('addrid'));
        
        if ($('.LasGruppenOrderSchema .invoice_address_edit #edit_inv_addrid').val()) {
            $('#invoice_address_edit .deleteaddress').show();
        } else {
            $('#invoice_address_edit .deleteaddress').hide();
        }
    },
    
    showInvoiceAddresses: function() {
        $('.LasGruppenOrderSchema .invoice_addresses').show();
        $('.LasGruppenOrderSchema .invoice_address_edit').hide();
    },
    
    deleteDelivaryAddress: function() {
        var confirmRet = confirm("Er du sikker på at du ønsker å slette adressen?");
        if (!confirmRet)
            return;
        
        var data = {
            addrid : $('.LasGruppenOrderSchema #edit_del_addrid').val()
        }
        
        var event = thundashop.Ajax.createEvent(null, "deleteDeliveryAddr", this, data);
        thundashop.Ajax.postWithCallBack(event, function(result) {
            $('.LasGruppenOrderSchema .welcomepagecontent').html(result);
            $('.LasGruppenOrderSchema .deliveryoverview_button').click();
        });
    },
    
    deleteInvoiceAddress: function() {
        var confirmRet = confirm("Er du sikker på at du ønsker å slette adressen?");
        if (!confirmRet)
            return;
        
        var data = {
            addrid : $('.LasGruppenOrderSchema #edit_del_addrid').val()
        }
        
        var event = thundashop.Ajax.createEvent(null, "deleteDeliveryAddr", this, data);
        thundashop.Ajax.postWithCallBack(event, function(result) {
            $('.LasGruppenOrderSchema .welcomepagecontent').html(result);
            $('.LasGruppenOrderSchema .deliveryoverview_button').click();
        });
    },
    
    showDeliverAddresses: function() {
        $('.LasGruppenOrderSchema .edit_deliver_address').show();
        $('.LasGruppenOrderSchema #edit_delivery_addr').hide();
    },
    
    editDeliveryAddress: function() {
        $('.LasGruppenOrderSchema .edit_deliver_address').hide();
        $('.LasGruppenOrderSchema #edit_delivery_addr').show();
        
        $('.LasGruppenOrderSchema #edit_delivery_addr #edit_del_name').val($(this).find('.del_name').html());
        $('.LasGruppenOrderSchema #edit_delivery_addr #edit_del_addr1').val($(this).find('.del_addr').html());
        $('.LasGruppenOrderSchema #edit_delivery_addr #edit_del_addr2').val($(this).find('.del_addr2').html());
        $('.LasGruppenOrderSchema #edit_delivery_addr #edit_del_postcode').val($(this).find('.del_postcode').html());
        $('.LasGruppenOrderSchema #edit_delivery_addr #edit_del_city').val($(this).find('.del_city').html());
        $('.LasGruppenOrderSchema #edit_delivery_addr #edit_del_addrid').val($(this).attr('addrid'));
        
        if ($('.LasGruppenOrderSchema #edit_delivery_addr #edit_del_addrid').val()) {
            $('#edit_delivery_addr .deleteaddress').show();
        } else {
            $('#edit_delivery_addr .deleteaddress').hide();
        }
        
    },
    
    saveDeliveryAddr: function() {
        
        var currentPage = $('.LasGruppenOrderSchema #edit_delivery_addr');
        var validated = app.LasGruppenOrderSchema.validatePage(currentPage);
        if (!validated) {
            alert('For å kunne gå videre må du først rette opp i de røde feltene');
            return;
        }
        
        var data = {
            name : $('.LasGruppenOrderSchema #edit_del_name').val(),
            addr1 : $('.LasGruppenOrderSchema #edit_del_addr1').val(),
            addr2 : $('.LasGruppenOrderSchema #edit_del_addr2').val(),
            postcode : $('.LasGruppenOrderSchema #edit_del_postcode').val(),
            city : $('.LasGruppenOrderSchema #edit_del_city').val(),
            addrid : $('.LasGruppenOrderSchema #edit_del_addrid').val()
        }
        
        var event = thundashop.Ajax.createEvent(null, "saveDeliveryAddr", currentPage, data);
        thundashop.Ajax.postWithCallBack(event, function(result) {
            $('.LasGruppenOrderSchema .welcomepagecontent').html(result);
            $('.LasGruppenOrderSchema .deliveryoverview_button').click();
        });
        
    },
    
    menuSelected: function() {
        $('.LasGruppenOrderSchema .menu_entry').css('font-weight', 'normal');
        $('.LasGruppenOrderSchema .sub_entry_content').hide();
        $('.LasGruppenOrderSchema .sub_entry_content[content="'+$(this).attr('goto')+'"').show();
        $(this).css('font-weight', 'bold');
    },
    
    doLoginKeyUp: function(event) {
        if (event && event.keyCode === 13) {
            app.LasGruppenOrderSchema.doLogin();
        }
    },
    
    doSignupKeyUp: function(event) {
        if (event && event.keyCode === 13) {
            app.LasGruppenOrderSchema.signin();
        }
    },
    
    logout_button: function() {
        var event = thundashop.Ajax.createEvent(null, "logout", this, {});
        thundashop.Ajax.postWithCallBack(event, function(result) {
            document.location = "/";
        });
    },
    requestNewPincode: function() {
        var data = {
            username : $('.LasGruppenOrderSchema .loginform .username').val(),
            password : $('.LasGruppenOrderSchema .loginform .password').val()
        }
        
        var event = thundashop.Ajax.createEvent(null, "requestPincode", this, data);
        thundashop.Ajax.postWithCallBack(event, function(result) {
            if (result === "success") {
                alert("En ny kode er sendt til din mobil.");
            } else {
                alert("Klarte ikke å sende ny kode, ta kontakt med Certego");
            }
        });
    },
    
    signin: function() {
        var data = {
            username : $('.LasGruppenOrderSchema .loginform .username').val(),
            password : $('.LasGruppenOrderSchema .loginform .password').val(),
            pincode : $('.LasGruppenOrderSchema .loginform_picode .pincode').val()
        }
        
        var event = thundashop.Ajax.createEvent(null, "loginWithPincode", $('.LasGruppenOrderSchema'), data);
        thundashop.Ajax.postWithCallBack(event, function(result) {
            if (result === "success") {
                document.location = "/";
            } else {
                alert("Kontroler pinkoden din.");
            }
        });
    },
    
    doLogin: function() {
        var data = {
            username : $('.LasGruppenOrderSchema .loginform .username').val(),
            password : $('.LasGruppenOrderSchema .loginform .password').val()
        }
        
        var event = thundashop.Ajax.createEvent(null, "doLogin", $('.LasGruppenOrderSchema'), data);
        thundashop.Ajax.postWithCallBack(event, function(result) {
            if (result === "success") {
                app.LasGruppenOrderSchema.showPincodeRequest();
            } else {
                alert("Feil brukernavn eller passord, prøv igjen");
            }
        });
    },
    
    showPincodeRequest: function() {
        $('.LasGruppenOrderSchema .loginform').hide();
        $('.LasGruppenOrderSchema .loginform_picode').show();
    },
    
    loginClicked: function() {
        var loginForm = $('.LasGruppenOrderSchema .loginform');
        if (!loginForm.is(":visible")) {
            loginForm.slideDown();
            $('.LasGruppenOrderSchema .login_button').hide();
        } 
   },

    saveData: function(target, callback, silent) {
        if (app.LasGruppenOrderSchema.inProgress) {
            return;
        }
        
        app.LasGruppenOrderSchema.inProgress = true;
        var keys = [];
        
        if ($('#selection_key').is(':checked')) {
            $('#keys_setup_table .order_key_row_to_add').each(function() {
                var keySetting = {
                    systemNumber : $(this).find('.systemnumber').val(),
                    count : $(this).find('.keys_count').val(),
                    marking : $(this).find('.keys_marking').val()
                };

                keys.push(keySetting);
            });
        }
        
        var cylinders = [];
        
        if ($('#cylindersoption').is(':checked')) {
            $('.cylinder_setup .order_cylinder_row_to_add').each(function() {
                var cylinder = {
                    systemNumber : $(this).find('.systemnumber').val(),
                    count : $(this).find('.cylinder_count').val(),
                    cylinder_type : $(this).find('.cylinder_type').val(),
                    door_thickness : $(this).find('.door_thickness').val(),
                    keys_that_fits : $(this).find('.keys_that_fits').val(),
                    texture : $(this).find('.texture').val(),
                    cylinder_description : $(this).find('.cylinder_description').val()
                };

                cylinders.push(cylinder);
            });
        }
        
        var copyEmail = "";
        $('.send_copy_email').each(function() {
            if ($(this).val()) {
                copyEmail = $(this).val();
            }
        });
        
        var data = {
            page1 : {
                contact: {
                    systemnumber: $('#main_system_number').val(),
                    name: $('#main_name').val(),
                    email: $('#main_email').val(),
                    cellphone: $('#main_cell').val(),
                },
                invoice: {
                    vatnumber: $('#companyid').val(),
                    companyName: $('#invoice_company_name').val(),
                    address: $('#invoice_address').val(),
                    postnumer: $('#invoice_postcode').val(),
                    reference: $('#invoice_reference').val(),
                    cellphone: $('#invoice_cellphone').val(),
                    email: $('#invoice_emailaddress').val(),
                    customerNumber: $('#invoice_cusomer_number').val()
                }
            },
            
            page2: {
                keys : $('#selection_key').is(':checked'),
                cylinders : $('#cylindersoption').is(':checked'),
                keys_setup : keys,
                cylinder_setup : cylinders
            },
            
            page3: {
                shipping: $("input[name=shippingtype]:checked").val(),
                
                deliveryInfo: {
                    name: $('#delivery_company_name').val(),
                    address: $('#delivery_address').val(),
                    postnumber: $('#delivery_postcode').val(),
                    cellphone: $('#delivery_cellphone').val(),
                    emailaddress: $('#delivery_emailaddress').val(),
                },
                
                storeDeliveryInformation: {
                    name: $('#store_deliver_name').val(),
                    cellphone: $('#store_delivery_cellphone').val(),
                    store: $("input[name=selectedstore]:checked").val()
                },
                
                extrainformation : $('#shipment_extra_description').val()
            },
            
            page4: {
                securityNeeded: $('#selection_key').is(':checked'),
                emailCopy: copyEmail,
                orderExtraInfo : $("#order_extra_info").val(),
                securitytype : $("input[name=security]:checked").val(),
                pincode: $('#pincode_textfield').val()         
            }
        };
        
        data.silent = silent;

        if (!data.page4.emailCopy) {
            $('#email_copy_sent_to').hide()
        } else {
            $('#email_copy_sent_to span').html(data.page4.emailCopy);
        }
        
        var event = thundashop.Ajax.createEvent("", "downloadPdf", target, data);
        thundashop.Ajax.postWithCallBack(event, function() {
            app.LasGruppenOrderSchema.inProgress = false;
            if (callback) {
                callback();
            }
        });
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
        
        var me = this;
        
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
                app.LasGruppenOrderSchema.saveData(this, function() {
                    window.location = "/scripts/downloadPdfLasGruppen.php";
                    app.LasGruppenOrderSchema.goToNextPage(6, me);
                }, true);
                
                return;
            } else {
                if (!app.LasGruppenOrderSchema.checkPinCodeLength()) {
                    alert('Pinkoden må være minimum 6 tall/bokstaver');
                    return;
                }
                
                app.LasGruppenOrderSchema.saveData(this);
            } 
       }
        
        if (pageNumber === 6) {
            var data = {
                confirmationEmail : $($('.order_page5 input')[0]).val()
            }
            
            var event = thundashop.Ajax.createEvent("", "sendConfirmation", this, data);
            thundashop.Ajax.postWithCallBack(event, function() {
                app.LasGruppenOrderSchema.goToNextPage(pageNumber, me);
            });
                
            return;
        }
        
        app.LasGruppenOrderSchema.goToNextPage(pageNumber, this);
    },
    
    goToNextPage: function(pageNumber, target) {
        if ($('.LasGruppenOrderSchema [pageNumer="'+pageNumber+'"').length) {
            $(target).closest('.orderpage').hide();
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

        if ($('#cylindersoption').is(':checked') && app.LasGruppenOrderSchema.isCompany) {
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
        app.LasGruppenOrderSchema.addedCount++;
        
        if (app.LasGruppenOrderSchema.addedCount > 10) {
            alert('Du kan maksimalt legge inn 10 ordrelinjer');
            return;
        }
        
        
        var row = $('table tr.keys_template_row').clone();
        row.removeClass('keys_template_row');
        row.addClass('order_key_row_to_add');
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
        app.LasGruppenOrderSchema.addedCount++;
        
        if (app.LasGruppenOrderSchema.addedCount > 10) {
            alert('Du kan maksimalt legge inn 10 ordrelinjer');
            return;
        }
        
        var row = $('table tr.cylinder_template_row').clone();
        row.removeClass('cylinder_template_row');
        row.addClass('order_cylinder_row_to_add');
        row.find('.systemnumber').val($('#main_system_number').val());
        $('.cylinder_setup table').append(row);
    }
}

app.LasGruppenOrderSchema.init();