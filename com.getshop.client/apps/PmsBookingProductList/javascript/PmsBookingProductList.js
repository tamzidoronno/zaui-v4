app.PmsBookingProductList = {
    init : function() {
        $(document).on('click', '.PmsBookingProductList .select_button', app.PmsBookingProductList.selectRoom);
        $(document).on('click', '.PmsBookingProductList .continue_button', app.PmsBookingProductList.continueToPage);
    },
    continueToPage : function() {
        var attr = $(this).attr('next_page');
        window.location.href=attr;
    },
    selectRoom : function() {
        var data = {
            "typeid" : $(this).attr('typeid')
        }
        var event = thundashop.Ajax.createEvent('','selectRoom',$(this), data);
        thundashop.Ajax.post(event);
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
                    click: app.PmsBookingProductList.showSettings
                }
            ]
        };

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
};

app.PmsBookingProductList.init();