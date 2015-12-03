app.PmsBooking = {
    init: function () {
        $(document).on('click', '.PmsBooking .check_available_button', app.PmsBooking.next);
    },
    next : function() {
        var next = $(this).attr('next_page');
        var app = $(this).closest('.app');
        var event = thundashop.Ajax.createEvent('','initBooking',$(this), {
            start : app.find('.start_date').val(),
            end : app.find('.end_date').val(),
            product : app.find('.selected_product').val()
        });
        thundashop.Ajax.postWithCallBack(event, function() {
            window.location.href =next;
        });
    },
    showSettings : function() {
        var event = thundashop.Ajax.createEvent('','showSettings',$(this), {});
        thundashop.common.showInformationBox(event,'Pms form settings');
    },
    loadSettings : function(element, application) {
         var config = {
            draggable: true,
            app : true,
            application: application,
            title: "Settings",
            items: [
                {
                    icontype: "awesome",
                    icon: "fa-edit",
                    iconsize : "30",
                    title: __f("Add / Remove products to this list"),
                    click: app.PmsBooking.showSettings
                }
            ]
        };

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
};
app.PmsBooking.init();