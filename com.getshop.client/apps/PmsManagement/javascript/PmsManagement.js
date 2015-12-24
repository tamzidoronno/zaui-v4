app.PmsManagement = {
    init: function () {
        $(document).on('change', '.PmsManagement .attachedProduct', app.PmsManagement.attachProduct);
        $(document).on('click', '.PmsManagement .setFilter', app.PmsManagement.setFilter);
        $(document).on('click', '.PmsManagement .moreinformationaboutbooking', app.PmsManagement.showMoreInformation);
        $(document).on('click', '.PmsManagement .roomprefix .fa-edit', app.PmsManagement.toggleEditMode);
        $(document).on('click', '.PmsManagement .editGuestToggle', app.PmsManagement.editGuestToggle);
        $(document).on('change', '.PmsManagement [gsname="numberofguests"]', app.PmsManagement.editGuestToggle);
        $(document).on('click', '.PmsManagement .showorderbutton', app.PmsManagement.showOrder);
    },
    showOrder : function() {
        thundashop.common.hideInformationBox();
        app.OrderManager.gssinterface.showOrder($(this).attr('orderid'));
    },
    editGuestToggle : function() {
        var row = $(this).closest('.roomattribute');
        var guests = $('[gsname="numberofguests"]').val();
        for(var i = 1; i <= 20; i++) {
            if(i >= guests) {
                $('.guestrow_'+i).hide();
            } else {
                $('.guestrow_'+i).show();
            }
        }
    },
    toggleEditMode : function() {
        console.log('togglign');
        var row = $(this).closest('.roomattribute');
        var view = row.find('.viewmode');
        var edit = row.find('.editmode');
        console.log(view);
        if(view.is(':visible')) {
            console.log('visible');
            view.hide();
            edit.show();
        } else {
            console.log('not visible');
            view.show();
            edit.hide();
        }
    },
    showMoreInformation : function() {
        var data = {
            "roomid" : $(this).attr('roomid'),
            "bookingid" : $(this).attr('bookingid')
        }
        
        var event = thundashop.Ajax.createEvent('','showBookingInformation',$(this), data);
        thundashop.common.showInformationBoxNew(event, 'Booking information');
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