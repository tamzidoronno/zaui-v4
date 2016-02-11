app.PmsManagement = {
    init: function () {
        $(document).on('change', '.PmsManagement .attachedProduct', app.PmsManagement.attachProduct);
        $(document).on('click', '.PmsManagement .setFilter', app.PmsManagement.setFilter);
        $(document).on('click', '.PmsManagement .moreinformationaboutbooking', app.PmsManagement.showMoreInformation);
        $(document).on('click', '.PmsManagement .roompostfix', app.PmsManagement.toggleEditMode);
        $(document).on('click', '.PmsManagement .editGuestToggle', app.PmsManagement.editGuestToggle);
        $(document).on('change', '.PmsManagement [gsname="numberofguests"]', app.PmsManagement.editGuestToggle);
        $(document).on('click', '.PmsManagement .showorderbutton', app.PmsManagement.showOrder);
        $(document).on('click', '.PmsManagement .doneediting', app.PmsManagement.doneediting);
        $(document).on('click', '.PmsManagement .deletebooking', app.PmsManagement.deletebooking);
        $(document).on('click', '.PmsManagement .resetnotifications', app.PmsManagement.resetnotifications);
        $(document).on('keyup','.PmsManagement .newroomstart', app.PmsManagement.updateRoomList);
        $(document).on('keyup','.PmsManagement .newroomend', app.PmsManagement.updateRoomList);
        $(document).on('click','.PmsManagement .showlog', app.PmsManagement.showlog);
        $(document).on('click','.PmsManagement .closeadduser', app.PmsManagement.closeadduser);
        $(document).on('change','.PmsManagement .changeuseronbooking', app.PmsManagement.changeuseronbooking);
        $(document).on('change','.PmsManagement .changecompanyonuser', app.PmsManagement.changecompanyonuser);
    },
    changeuseronbooking : function() {
        var data = {
            "userid" : $(this).val(),
            "bookingid" : $(this).attr('bookingid')
        };
        var corScroll = $('.informationbox-outer').scrollTop();
        var event = thundashop.Ajax.createEvent('','changeBookingOnEvent', $(this), data);
        thundashop.common.showInformationBoxNew(event);
        $('.informationbox-outer').scrollTop(corScroll);
        
    },
    changecompanyonuser : function() {
        var data = {
            "companyid" : $(this).val(),
            "bookingid" : $(this).attr('bookingid')
        };
        var corScroll = $('.informationbox-outer').scrollTop();
        var event = thundashop.Ajax.createEvent('','changeCompanyOnUser', $(this), data);
        thundashop.common.showInformationBoxNew(event);
        $('.informationbox-outer').scrollTop(corScroll);
    },
    closeadduser : function() {
        $('.PmsManagement .edituserbox').fadeOut();
        $('.PmsManagement .editcompanybox').fadeOut();
    },
    showlog: function () {
        var data = {
            "bookingid" : $(this).attr('bookingid')
        }
        
        var event = thundashop.Ajax.createEvent('','showLog',$(this), data);
        thundashop.Ajax.postWithCallBack(event, function(result) {
            $('#logarea').html(result);
        });
    },
    resetnotifications : function() {
        var event = thundashop.Ajax.createEvent('','resetnotifications',$(this), {
            "roomid" : $(this).attr('roomid'),
            "bookingid" : $(this).attr('bookingid')
        });
        
        thundashop.Ajax.post(event);
    },
    updateRoomList : function() {
        var data = {
            "start" : $('.newroomstart').val(),
            "end" : $('.newroomend').val()
        };
        console.log(data);
        var event = thundashop.Ajax.createEvent('','updateItemList', $(this), data);
        thundashop.Ajax.postWithCallBack(event, function(result) {
            $('.addroomselectiontype').replaceWith(result);
        });
    },
    
    deletebooking : function() {
        var confirmed = confirm("Are you sure you want to delete this booking?");
        if(confirmed) {
            var event = thundashop.Ajax.createEvent('','deleteBooking', $(this), {
                "bookingid" : $(this).attr('bookingid')
            });
            thundashop.Ajax.postWithCallBack(event, function() {
                thundashop.common.hideInformationBox();
                thundashop.framework.reprintPage();
            });
        }
    },
    doneediting : function() {
        thundashop.framework.reprintPage();
        thundashop.common.hideInformationBox();
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
        var row = $(this).closest('.roomattribute');
        var view = row.find('.viewmode');
        var edit = row.find('.editmode');
        view.hide();
        edit.show();
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