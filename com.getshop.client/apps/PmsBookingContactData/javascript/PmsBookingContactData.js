app.PmsBookingContactData = {
    init : function() {
        $(document).on('click','.PmsBookingContactData .typeSelection', app.PmsBookingContactData.selectTypeView);
        $(document).on('click','.PmsBookingContactData .complete_button', app.PmsBookingContactData.completeRegistration);
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
            console.log(offset);
            if(!roomdata[offset]) {
                roomdata[offset]=[];
            }
            roomdata[offset].push(guest);
        });
        
        var data = {
            "billingdata" : billingdata,
            "roomdata" : roomdata
        }
        
        console.log(data);
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
        thundashop.common.showInformationBox(event, 'Settings');
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