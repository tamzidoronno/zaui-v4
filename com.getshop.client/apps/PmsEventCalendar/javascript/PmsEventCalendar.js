app.PmsEventCalendar = {
    init: function () {
        $(document).on('keyup', '.PmsEventCalendar .eventeditform input', app.PmsEventCalendar.updateFields);
        $(document).on('keyup', '.PmsEventCalendar .eventeditform select', app.PmsEventCalendar.updateFields);
        $(document).on('keyup', '.PmsEventCalendar .eventeditform textarea', app.PmsEventCalendar.updateFields);
        
        $(document).on('change', '.PmsEventCalendar .eventeditform input', app.PmsEventCalendar.updateFields);
        $(document).on('change', '.PmsEventCalendar .eventeditform select', app.PmsEventCalendar.updateFields);
        $(document).on('change', '.PmsEventCalendar .eventeditform textarea', app.PmsEventCalendar.updateFields);
    },
    updateFields : function() {
        var form = $('.PmsEventCalendar .eventeditform');

        if(typeof(saveEventBookingData) === "number") {
            clearTimeout(saveEventBookingData);
        }
        
        saveEventBookingData = setTimeout(function() {
            var data = thundashop.framework.createGsArgs(form);
            var event = thundashop.Ajax.createEvent('','savePostedForm',form, data);
            thundashop.Ajax.postWithCallBack(event, function() {});
        }, "1000");
    },
    showSettings : function() {
        var event = thundashop.Ajax.createEvent('','showSettings',$(this), {});
        thundashop.common.showInformationBoxNew(event,'Pms form settings');
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
                    click: app.PmsPricing.showSettings
                }
            ]
        };

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
};
app.PmsEventCalendar.init();