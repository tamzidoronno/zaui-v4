app.PmsBookingContactData = {
    init : function() {
        $(document).on('click','.PmsBookingContactData .typeSelection', app.PmsBookingContactData.selectTypeView);
        $(document).on('click','.PmsBookingContactData .complete_button', app.PmsBookingContactData.completeRegistration);
        $(document).on('keyup','.PmsBookingContactData .roomdata input', app.PmsBookingContactData.updateBillingInformation);
    },
    
    updateBillingInformation : function() {
        var name = $(this).attr('name');
        var row = $(this).closest('.roomdata');
        if($('.roomdata').first().attr('pmsbookingroomid') === row.attr('pmsbookingroomid')) {
            $('.private_person_form input[name="'+name+'"]').val($(this).val());
            $('.company_form input[name="'+name+'"]').val($(this).val());
        }
    },
    
    completeRegistration : function() {
        var billingdataform = $('.billingform:visible');
        var billingdata = {};
        billingdataform.find('input').each(function() {
            billingdata[$(this).attr('name')] = $(this).val();
        });
        
        var roomdata = {};
        $('.roomdata').each(function() {
            var guest = {};
            $(this).find('input').each(function() {
                guest[$(this).attr('name')] = $(this).val();
            });
            var offset = $(this).attr('pmsBookingRoomId');

            if(!roomdata[offset]) {
                roomdata[offset]=[];
            }
            roomdata[offset].push(guest);
        });
        
        var agreeonterms = "false";
        if($('input[gsname="agreeonterms"]').is(':checked')) {
            agreeonterms = "true";
        }
        
        var data = {
            "billingdata" : billingdata,
            "roomdata" : roomdata,
            "agreetoterms" : agreeonterms
        }
        
        var event = thundashop.Ajax.createEvent('','setContactData',$(this), data);
        thundashop.Ajax.post(event);
    },
    selectTypeView : function() {
        var view = $(this).attr('view');
        $('.billingform').hide();
        $('.'+view).show();
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