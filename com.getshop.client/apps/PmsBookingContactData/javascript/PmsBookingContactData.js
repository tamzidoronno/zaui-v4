app.PmsBookingContactData = {
    init : function() {
        $(document).on('click', '.PmsBookingContactData .chooseregform input', app.PmsBookingContactData.choseRegType);
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