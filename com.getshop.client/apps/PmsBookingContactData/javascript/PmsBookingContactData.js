app.PmsBookingContactData = {
    init : function() {
        $(document).on('click', '.PmsBookingContactData .chooseregform input', app.PmsBookingContactData.choseRegType);
        $(document).on('keyup', '.PmsBookingContactData input[gsname="user_address_postCode"]', app.PmsBookingContactData.updatePostalPlace);
        $(document).on('keyup', '.PmsBookingContactData input[gsname="company_vatNumber"]', app.PmsBookingContactData.updateBrreg);
        
        $(document).on('keyup', '.PmsBookingContactData .bookingregistrationform input', app.PmsBookingContactData.saveForm);
        $(document).on('keyup', '.PmsBookingContactData .bookingregistrationform select', app.PmsBookingContactData.saveForm);
        $(document).on('keyup', '.PmsBookingContactData .bookingregistrationform textarea', app.PmsBookingContactData.saveForm);
        $(document).on('blur', '.PmsBookingContactData .bookingregistrationform input', app.PmsBookingContactData.saveForm);
        $(document).on('blur', '.PmsBookingContactData .bookingregistrationform select', app.PmsBookingContactData.saveForm);
        $(document).on('blur', '.PmsBookingContactData .bookingregistrationform textarea', app.PmsBookingContactData.saveForm);
        $(document).on('change', '.PmsBookingContactData .bookingregistrationform input', app.PmsBookingContactData.saveForm);
        $(document).on('change', '.PmsBookingContactData .bookingregistrationform select', app.PmsBookingContactData.saveForm);
        $(document).on('change', '.PmsBookingContactData .bookingregistrationform textarea', app.PmsBookingContactData.saveForm);
        
        $(document).on('keyup', '.PmsBookingContactData [gsname="visitor_name_1"]', function() { $('[gsname="user_fullName"]').val($(this).val());  });
        $(document).on('keyup', '.PmsBookingContactData [gsname="visitor_phone_1"]', function() { $('[gsname="user_cellPhone"]').val($(this).val());  });
        $(document).on('keyup', '.PmsBookingContactData [gsname="visitor_email_1"]', function() { $('[gsname="user_emailAddress"]').val($(this).val());  });
    },
    saveForm : function() {
        var form = $('.bookingregistrationform');

        if(typeof(savePmsBookingFormContactDataTimeout) === "number") {
            clearTimeout(savePmsBookingFormContactDataTimeout);
        }
        
        savePmsBookingFormContactDataTimeout = setTimeout(function() {
            var data = thundashop.framework.createGsArgs(form);
            var event = thundashop.Ajax.createEvent('','savePostedForm',form, data);
            thundashop.Ajax.postWithCallBack(event, function() {});
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
        if(val.indexOf(".") >= 0) {
            return;
        } 
        if(val.length === 9) {
            $.ajax({
                "url" : "https://hotell.difi.no/api/json/brreg/enhetsregisteret?query=" + val,
                "success" : function(data) {
                    var data = data.entries[0];
                    $('input[gsname="company_vatNumber"]').val(data.orgnr);
                    $('input[gsname="company_address_address"]').val(data.forretningsadr);
                    $('input[gsname="company_address_postCode"]').val(data.forradrpostnr);
                    $('input[gsname="company_address_city"]').val(data.forradrpoststed);
                    $('input[gsname="company_name"]').val(data.navn);
                }
            });
        }
    },    
    
    searchPostalCode : function() {
    },
    choseRegType : function() {
        var type = $(this).val();
        $('.regforminput').hide();
        $('.whenTypeIsSelected').show();
        $('.'+type).fadeIn();
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