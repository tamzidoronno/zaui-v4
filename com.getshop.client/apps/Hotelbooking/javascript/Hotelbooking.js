app.Hotelbooking = {
    updateRoomCountTimer: null,
    setSize: function () {
        var newHeight = $('.Hotelbooking .booking_page').outerHeight() + 30;
        var top = (500 - newHeight) / 2;
        $('.Hotelbooking .bookingbox').css('top', top + 'px');
        $('.Hotelbooking .bookingbox').css('height', newHeight);
    },
    updateNumberOfRooms: function () {
        var price = parseInt($('.Hotelbooking .price').attr('price'));
        var price = price * parseInt($(this).val());
        $('.Hotelbooking .price').html(price);
    },
    changeOrderType: function () {
        var type = parseInt($(this).val());
        if (type === 1) {
            $('.Hotelbooking .orderfields.private').hide();
            $('.Hotelbooking .orderfields.company').show();
        } else {
            $('.Hotelbooking .orderfields.private').show();
            $('.Hotelbooking .orderfields.company').hide();
        }
        app.Hotelbooking.setSize();
    },
    updateRoomCount: function () {
        var count = $(this).val();
        clearTimeout(app.Hotelbooking.updateRoomCountTimer);
        var container = $(this);
        app.Hotelbooking.updateRoomCountTimer = setTimeout(function () {
            container.blur();
            clearTimeout(app.Hotelbooking.updateRoomCountTimer);
            var event = thundashop.Ajax.createEvent("", "updateRoomCount", container, {"count": count});
            thundashop.Ajax.post(event, function () {
            });
        }, "500");
    },
    setNumberOfPersons: function () {
        var count = $(this).val();
        var event = thundashop.Ajax.createEvent("", "updatePersonCount", $(this), {"count": count});
        thundashop.Ajax.postWithCallBack(event, function () {

        });
    },
    updateCleaningCount: function () {
        var count = $(this).val();
        var event = thundashop.Ajax.createEvent("", "setCleaningOption", $(this), {"product": count});
        thundashop.Ajax.postWithCallBack(event, function () {
        });
    },
    updateParking: function () {
        var count = $(this).val();
        var event = thundashop.Ajax.createEvent("", "setParkingOption", $(this), {"parking": $(this).is(':checked')});
        thundashop.Ajax.postWithCallBack(event, function () {
        });
    },
    checkAvailability: function () {
        var nextpage = $(this).attr('nextpage');
        var apparea = $(this).closest('.app');
        var data = {
            start: apparea.find('#start_date').val(),
            stop: apparea.find('#end_date').val(),
            roomProduct: apparea.find('.room_selection').val()
        };
        var event = thundashop.Ajax.createEvent('', 'checkavailability', $(this), data);
        thundashop.Ajax.postWithCallBack(event, function (result) {
            result = parseInt(result);
            if (result <= 0) {
                $('.Hotelbooking .room_available').css('padding-top', '20px');
                $('.Hotelbooking .error_on_check').hide();
                if (result === -1) {
                    $('.Hotelbooking .date_before_start').show();
                }
                if (result === -2) {
                    $('.Hotelbooking .end_before_start').show();
                }
                app.Hotelbooking.setSize();
            } else {
                thundashop.common.goToPage(nextpage);
            }
        });
    },
    changeBookingDate: function () {
        if ($(this).hasClass('disabled')) {
            return;
        }
        var cal = $(this).closest('.booking_table');
        cal.find('.selected').removeClass('selected');
        $(this).addClass('selected');

        var event = thundashop.Ajax.createEvent("", "updateCalendarDate", $(this), {
            "type": cal.prev().attr('type'),
            "time": $(this).attr('time')
        });

        thundashop.Ajax.post(event);
    },
    goToPage: function (pagenumber) {
        window.history.pushState({url: "", ajaxLink: "pagenumber=" + pagenumber}, "Title", "pagenumber=" + pagenumber);
        thundashop.Ajax.doJavascriptNavigation("pagenumber=" + pagenumber, null, true);
    },
    saveCurrentData: function () {
        var data = {};
        $('.booking_contact_data').find('[gsname]').each(function () {
            var name = $(this).attr('gsname');
            data[name] = $(this).val();
        });

        data['mvaregistered'] = $('.booking_contact_data [gsname="mvaregistered"]').is(':checked');
        data['partnershipdeal'] = $('.booking_contact_data [gsname="partnershipdeal"]').is(':checked');
        data['customer_type'] = $('.booking_contact_data [gsname="customer_type"]:checked').val();
        var event = thundashop.Ajax.createEvent('', 'setBookingData', $(this), data);
        if ($(this).attr('gsname') === "mvaregistered" || $(this).attr('gsname') === "customer_type") {
            thundashop.Ajax.post(event);
        } else {
            thundashop.Ajax.postWithCallBack(event, function () {
            });
        }
    },
    changeToPartnership: function () {
        if ($('.partnership').is(":visible")) {
            $('input[gsname="referencenumber"]').val('');
            $('.common_input').show();
            $('.partnership').hide();
        } else {
            $('.common_input').hide();
            $('.partnership').show();
        }
    },
    loadSettingPanel: function () {
        var event = thundashop.Ajax.createEvent('', 'loadSettings', $(this), null);
        thundashop.common.showInformationBox(event, "Booking settings");
    },
    navigateMonth: function () {
        var type = $(this).attr('navigation');

        var month = parseInt($(this).parent().attr('month'));
        var year = parseInt($(this).parent().attr('year'));

        if (type === "prev") {
            if (month === 1) {
                month = 12;
                year--;
            } else {
                month--;
            }
        } else {
            if (month === 12) {
                month = 1;
                year++;
            } else {
                month++;
            }
        }

        var data = {
            "year": year,
            "month": month,
            "type": $(this).parent().attr('type')
        }
        var container = $(this).closest('.calendar');
        var event = thundashop.Ajax.createEvent('', 'navigateMonthCalendar', $(this), data);
        thundashop.Ajax.postWithCallBack(event, function (data) {
            container.html(data);
        });

    },
    displayMvaRow: function () {
        $('.common_text').hide();
        if ($(this).is(':checked')) {
            $('.common_text.company').show();
        } else {
            $('.common_text.private').show();
        }
    },
    loadSettings: function (element, application) {
        var config = {
            application: application,
            draggable: true,
            title: "Settings",
            items: [
                {
                    icontype: "awesome",
                    icon: "fa-cog",
                    iconsize: "30",
                    title: __f("Configure your booking application"),
                    click: app.Hotelbooking.loadSettingPanel
                }
            ]
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    },
    updatePostalPlace: function () {
        var val = $(this).val();
        $.ajax({
            "dataType" : "jsonp",
            "url": "https://api.bring.com/shippingguide/api/postalCode.json?clientUrl=insertYourClientUrlHere&country=no&pnr=" + val,
            "success": function (data) {
                if(data.valid) {
                    $('.Hotelbooking input[gsname="city"]').val(data.result);
                }
            }
        }
        );
    },
    updateBrreg : function() {
        var val = $(this).val();
        if(val.length === 9) {
            $.ajax({
                "url" : "http://hotell.difi.no/api/json/brreg/enhetsregisteret?query=" + val,
                "success" : function(data) {
                    var data = data.entries[0];
                    console.log(data);
                    $('.Hotelbooking input[gsname="birthday"]').val(data.orgnr);
                    $('.Hotelbooking input[gsname="address"]').val(data.forretningsadr);
                    $('.Hotelbooking input[gsname="postal_code"]').val(data.forradrpostnr);
                    $('.Hotelbooking input[gsname="city"]').val(data.forradrpoststed);
                }
            });
        }
    },
    searchExistingButton : function() {
        var event = thundashop.Ajax.createEvent('','searchCustomerBox',$(this),'');
        thundashop.common.showInformationBox(event);
    },
    searchCustomer : function(event) {
        if(event.type === "keyup" && event.keyCode !== 13) {
            return;
        }
        var value = $('.Hotelbooking .searchcustomerinput').val();
        var event = thundashop.Ajax.createEvent('','searchingCustomer',$(this), {
            "value" : value
        });
        thundashop.Ajax.postWithCallBack(event, function(data) {
            $('.Hotelbooking .searchresultarea').html(data);
        });
    },
    
    selectCustomer : function() {
        var phone = $(this).attr('phone');
        var name = $(this).attr('name');
        var referenceid = $(this).attr('referenceid');
        $('.Hotelbooking input[gsname="name_1"]').val(name);
        $('.Hotelbooking input[gsname="phone_1"]').val(phone);
        $('.Hotelbooking input[gsname="referencenumber"]').val(referenceid);
        thundashop.common.hideInformationBox();
    },
    
    initEvents: function () {
        $(document).on('click', '.Hotelbooking .check_available_button', app.Hotelbooking.checkAvailability);
        $(document).on('change', '.Hotelbooking #ordertype', app.Hotelbooking.changeOrderType);
        $(document).on('change', '.Hotelbooking .number_of_rooms', app.Hotelbooking.updateNumberOfRooms);
        $(document).on('click', '.Hotelbooking .cal_field', app.Hotelbooking.changeBookingDate);
        $(document).on('change', '.Hotelbooking .number_of_rooms', app.Hotelbooking.updateRoomCount);
        $(document).on('blur', '.Hotelbooking .number_of_rooms', app.Hotelbooking.updateRoomCount);
        $(document).on('change', '.Hotelbooking .cleaning_option', app.Hotelbooking.updateCleaningCount);
        $(document).on('click', '.Hotelbooking .parking_option', app.Hotelbooking.updateParking);
        $(document).on('change', '.Hotelbooking #numberofpersons', app.Hotelbooking.setNumberOfPersons);
        $(document).on('blur', '.Hotelbooking #numberofpersons', app.Hotelbooking.setNumberOfPersons);
        $(document).on('blur', '.Hotelbooking .booking_contact_data input', app.Hotelbooking.saveCurrentData);
        $(document).on('click', '.Hotelbooking input[gsname="partnershipdeal"]', app.Hotelbooking.changeToPartnership);
        $(document).on('click', '.Hotelbooking .fa.calnav', app.Hotelbooking.navigateMonth);
        $(document).on('click', '.Hotelbooking .searchexisting', app.Hotelbooking.searchExistingButton);
        $(document).on('click', '.Hotelbooking input[gsname="mvaregistered"]', app.Hotelbooking.saveCurrentData);
        $(document).on('click', '.Hotelbooking input[gsname="customer_type"]', app.Hotelbooking.saveCurrentData);
        $(document).on('keyup', '.Hotelbooking input[gsname="postal_code"]', app.Hotelbooking.updatePostalPlace);
        $(document).on('keyup', '.Hotelbooking input[gsname="birthday"]', app.Hotelbooking.updateBrreg);
        $(document).on('keyup', '.Hotelbooking .searchcustomerinput', app.Hotelbooking.searchCustomer);
        $(document).on('click', '.Hotelbooking .searchcustomerbutton', app.Hotelbooking.searchCustomer);
        $(document).on('click', '.Hotelbooking .selectcustomer', app.Hotelbooking.selectCustomer);
    }
};

app.Hotelbooking.initEvents();
