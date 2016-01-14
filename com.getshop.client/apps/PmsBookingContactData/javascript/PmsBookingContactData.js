app.PmsBookingContactData = {
    init : function() {
        $(document).on('click', '.PmsBookingContactData .chooseregform input', app.PmsBookingContactData.choseRegType);
        $(document).on('keyup', '.PmsBookingContactData input[gsname="user_address_postCode"]', app.PmsBookingContactData.updatePostalPlace);
        $(document).on('keyup', '.PmsBookingContactData input[gsname="company_vatNumber"]', app.PmsBookingContactData.updateBrreg);
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
                "url" : "http://hotell.difi.no/api/json/brreg/enhetsregisteret?query=" + val,
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
       alert('test'); 
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