app.PmsBookingContactData = {
    init : function() {
        $(document).on('click', '.PmsBookingContactData .chooseregform input', app.PmsBookingContactData.choseRegType);
        $(document).on('keyup', '.PmsBookingContactData input[gsname="user_address_postCode"]', app.PmsBookingContactData.updatePostalPlace);
        $(document).on('keyup', '.PmsBookingContactData input[gsname="company_name"]', app.PmsBookingContactData.updateBrreg);
        
        $(document).on('keyup', '.PmsBookingContactData .bookingregistrationform input', app.PmsBookingContactData.saveForm);
        $(document).on('keyup', '.PmsBookingContactData .bookingregistrationform select', app.PmsBookingContactData.saveForm);
        $(document).on('keyup', '.PmsBookingContactData .bookingregistrationform textarea', app.PmsBookingContactData.saveForm);
        $(document).on('blur', '.PmsBookingContactData .bookingregistrationform input', app.PmsBookingContactData.saveForm);
        $(document).on('blur', '.PmsBookingContactData .bookingregistrationform select', app.PmsBookingContactData.saveForm);
        $(document).on('blur', '.PmsBookingContactData .bookingregistrationform textarea', app.PmsBookingContactData.saveForm);
        $(document).on('change', '.PmsBookingContactData .bookingregistrationform input', app.PmsBookingContactData.saveForm);
        $(document).on('change', '.PmsBookingContactData .bookingregistrationform select', app.PmsBookingContactData.saveForm);
        $(document).on('change', '.PmsBookingContactData .bookingregistrationform textarea', app.PmsBookingContactData.saveForm);
        $(document).on('change', '.PmsBookingContactData .selectregisterduser', app.PmsBookingContactData.changeUserToRegisterOn);
        $(document).on('click', '.PmsBookingContactData .complete_button', app.PmsBookingContactData.completeRegistration);
        
        $(document).on('keyup', '.PmsBookingContactData [gsname="visitor_name_1"]', function() { $('[gsname="user_fullName"]').val($(this).val());  });
        $(document).on('keyup', '.PmsBookingContactData [gsname="visitor_phone_1"]', function() { $('[gsname="user_cellPhone"]').val($(this).val());  });
        $(document).on('keyup', '.PmsBookingContactData [gsname="visitor_email_1"]', function() { $('[gsname="user_emailAddress"]').val($(this).val());  });
    },
    completeRegistration : function() {
        $(this).html('<i class="fa fa-spin fa-spinner"></i>');
        var form = $(this).closest('.bookingregistrationform');
        var args = thundashop.framework.createGsArgs(form);
        args['submit'] = $(this).attr('gsvalue');
        var event = thundashop.Ajax.createEvent('','submitForm', $(this), args);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.registrationdatafieldarea').html(res);
        });

    },
    changeUserToRegisterOn : function() {
        var data = {
            userid : $(this).val()
        }
        thundashop.Ajax.simplePost($(this), 'changeUserToSubmitOn', data);
    },
    
    saveForm : function() {
        var form = $('.bookingregistrationform');

        if(typeof(savePmsBookingFormContactDataTimeout) === "number") {
            clearTimeout(savePmsBookingFormContactDataTimeout);
        }
        
        savePmsBookingFormContactDataTimeout = setTimeout(function() {
            var data = thundashop.framework.createGsArgs(form);
            var event = thundashop.Ajax.createEvent('','savePostedForm',form, data);
            thundashop.Ajax.postWithCallBack(event, function() {
                $('.PmsBookingSummary .summarizedbooking').each(function() {
                    var view = $(this);
                    var event = thundashop.Ajax.createEvent('','reRenderSummary', view, {});
                    thundashop.Ajax.postWithCallBack(event, function(res) {
                        view.html(res);
                    });
                    
                });
                
            });
        }, "1000");
    },
    
    updatePostalPlace: function () {
        var val = $(this).val();
        if(val.length !== 4) {
            return;
        }
        $.ajax({
            "dataType" : "jsonp",
            "url": "https://api.bring.com/shippingguide/api/postalCode.json?clientUrl=insertYourClientUrlHere&country=no&pnr=" + val,
            "success": function (data) {
                if(data.valid) {
                    $('input[gsname="user_address_city"]').val(data.result);
                }
            }
        }
        );
    },
    updateBrreg : function() {
        var val = $(this).val();
        
        $.ajax({
            "url" : "https://hotell.difi.no/api/json/brreg/enhetsregisteret?query=" + val,
            "success" : function(data) {
                var availableTags = [];
                for(var key in data.entries) {
                    availableTags.push(data.entries[key].navn);
                }
                
                $('.PmsBookingContactData input[gsname="company_name"]').autocomplete({
                    source: availableTags,
                    select: function( event, ui ) {
                        console.log(event);
                        console.log(ui);
                        console.log(data);
                        var res = null;
                        for(var key in data.entries) {
                            if(data.entries[key].navn == ui.item.value) {
                                res = data.entries[key];
                            }
                        }
                        $('input[gsname="company_address_address"]').val(res.forretningsadr);
                        $('input[gsname="company_address_postCode"]').val(res.forradrpostnr);
                        $('input[gsname="company_address_city"]').val(res.forradrpoststed);
                        $('input[gsname="company_vatNumber"]').val(res.orgnr);
                    }
                  });
            }
        });
    },    
    
    searchPostalCode : function() {
    },
    choseRegType : function() {
        var type = $(this).val();
        $('.regforminput').hide();
        $('.whenTypeIsSelected').show();
        $('.'+type).fadeIn();
        if(type === "registered_user") {
            app.PmsBookingContactData.saveForm();
            $('.selectregisterduser').chosen();
        }
    },
    showSettings : function() {
        var event = thundashop.Ajax.createEvent('','showSettings',$(this), {});
        thundashop.common.showInformationBoxNew(event, 'Settings');
    },
    loadSettings: function (element, application) {
        var config = {
            draggable: true,
            app: true,
            application: application,
            title: "Settings",
            items: [
                {
                    icontype: "awesome",
                    icon: "fa-edit",
                    iconsize: "30",
                    title: __f("Show settings"),
                    click: app.PmsBookingCalendar.showSettings
                }
            ]
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
};

app.PmsBookingContactData.init();