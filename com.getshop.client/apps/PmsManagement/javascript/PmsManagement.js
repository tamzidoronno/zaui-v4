app.PmsManagement = {
    init: function () {
        $(document).on('change', '.PmsManagement .attachedProduct', app.PmsManagement.attachProduct);
        $(document).on('click', '.PmsManagement .setFilter', app.PmsManagement.setFilter);
        $(document).on('click', '.PmsManagement .moreinformationaboutbooking', app.PmsManagement.showMoreInformation);
    },
    showMoreInformation : function() {
        var data = {
            "roomid" : $(this).attr('roomid'),
            "bookingid" : $(this).attr('bookingid')
        }
        
        var event = thundashop.Ajax.createEvent('','showBookingInformation',$(this), data);
        thundashop.common.showInformationBox(event, 'Booking information');
    },
    setFilter : function() {
        var app = $(this).closest('.app');
        var data = {
            "start" : app.find('.pmsinput.start').val(),
            "end" : app.find('.pmsinput.end').val(),
            "filterType" : app.find('.filterType').val(),
            "searchWord" : app.find('.pmsinput.searchword').val()
        };
        var event = thundashop.Ajax.createEvent('','setFilter',$(this), data);
        thundashop.Ajax.post(event);
    },
    attachProduct : function() {
        var event = thundashop.Ajax.createEvent('','attachProduct',$(this), {
            "typeid" : $(this).attr('typeid'),
            "productid" : $(this).val()
        });
        thundashop.Ajax.post(event);
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
                    click: app.PmsManagement.showSettings
                }
            ]
        };

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
};
app.PmsManagement.init();