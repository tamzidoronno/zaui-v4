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
    updateNeedHandicap : function() {
        var need = $(this).is(':checked');

        var event = thundashop.Ajax.createEvent("", "updateNeedHandicap", $(this), {
            "need": need
        });

        thundashop.Ajax.post(event);
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
            thundashop.common.goToPage(nextpage);
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
            "type": cal.closest('.calendar').find('.cal_header').attr('type'),
            "time": $(this).attr('time')
        });

        thundashop.Ajax.post(event);
    },
    goToPage: function (pagenumber) {
        window.history.pushState({url: "", ajaxLink: "pagenumber=" + pagenumber}, "Title", "pagenumber=" + pagenumber);
        thundashop.Ajax.doJavascriptNavigation("pagenumber=" + pagenumber, null, true);
    },
    saveCurrentData: function () {
        if($(this).hasClass('checkNeedFlex')) {
            return;
        }
        var data = {};
        $('.bookingsummary').find('[gsname]').each(function () {
            if($(this).is(':radio') && !$(this).is(':checked')) {
                return;
            }
            
            var name = $(this).attr('gsname');
            data[name] = $(this).val();
        });

        data['customer_type'] = $('.booking_contact_data [gsname="userData][customer_type]"]:checked').val();
        data['userData][checklicenagreement]'] = $('.checklicenagreement').is(':checked');

        var event = thundashop.Ajax.createEvent('', 'setBookingData', $(this), data);
        var gsname = $(this).attr('gsname');
        if (gsname === "mvaregistered" || gsname === "userData][customer_type]" || gsname === "partner_type") {
            thundashop.Ajax.post(event);
        } else {
            thundashop.Ajax.postWithCallBack(event, function () {
            });
        }
    },
    
    updateNeedFlex : function() {
        var need = $(this).is(':checked');
        var event = thundashop.Ajax.createEvent("", "updateNeedFlex", $(this), {
            "need": need
        });
        thundashop.Ajax.post(event);
    },
    
    changeToPartnership: function () {
        if (!$('[gsname="partnershipdeal"]').is(":checked")) {
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
        console.log(val);
        if(val.length !== 4) {
            return;
        }
        $.ajax({
            "dataType" : "jsonp",
            "url": "https://api.bring.com/shippingguide/api/postalCode.json?clientUrl=insertYourClientUrlHere&country=no&pnr=" + val,
            "success": function (data) {
                if(data.valid) {
                    $('.Hotelbooking input[gsname="userData][city]"]').val(data.result);
                }
            }
        }
        );
    },
    updateBrreg : function() {
        var val = $(this).val();
        if(val.indexOf(".") >= 0) {
            return;
        } 
        if(val.length === 9) {
            $.ajax({
                "url" : "http://hotell.difi.no/api/json/brreg/enhetsregisteret?query=" + val,
                "success" : function(data) {
                    var data = data.entries[0];
                    $('.Hotelbooking input[gsname="userData][birthday]"]').val(data.orgnr);
                    $('.Hotelbooking input[gsname="userData][address]"]').val(data.forretningsadr);
                    $('.Hotelbooking input[gsname="userData][postal_code]"]').val(data.forradrpostnr);
                    $('.Hotelbooking input[gsname="userData][city]"]').val(data.forradrpoststed);
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
    
    animateNext : function() {
        if($(this).find('.fa').length > 0) {
            return;
        }
        $(this).html('<i class="fa fa-spin fa-spinner">');
    },
    hideToolTip : function() {
        var productId = $(this).attr('data-productid');
        var tooltip = $('.Hotelbooking .tooltip[data-productid="'+productId+'"]');
        tooltip.hide();
    },
    
    showToolTip : function() {
        var productId = $(this).attr('data-productid');
        var tooltip = $('.Hotelbooking .tooltip[data-productid="'+productId+'"]');
        tooltip.show();
    },
    
    selectMonth : function() {
        var checked = $(this).is(':checked');
        var cal = $(this).closest('.company_calendar');
        cal.find('.weekcheckbox').each(function() {
            if(checked) {
                $(this).attr('checked','checked');
            } else {
                $(this).attr('checked',null);
            }
            $(this).change();
        });
    },
    
    weekCheckBox : function() {
        var checked = $(this).is(':checked');
        $(this).closest('.calweek').find('.caldate').each(function() {
            if($(this).hasClass('emptypadding')) {
                return;
            }
        if($(this).find('input[type="checkbox"]').length > 0) {
            return;
        }
            if(checked) {
                $(this).addClass('selected');
            } else {
                $(this).removeClass('selected');
            }
        });
    },
    
    selectDay : function() {
        if($(this).find('input[type="checkbox"]').length > 0) {
            return;
        }
        if($(this).hasClass('emptypadding')) {
            return;
        }
        if($(this).hasClass('datelabel')) {
            return;
        }
        if($(this).hasClass('selected')) {
            $(this).removeClass('selected');
        } else {
            $(this).addClass('selected');
        }
    },
    
    continueToSummary : function() {
        var days = [];
        
        $('.caldate').each(function() {
            if($(this).hasClass('selected')) {
                var day = $(this).attr('day');
                var year = $(this).attr('year');
                days.push(day + "-" + year);
            }
        });
        var pageId = $(this).attr('summarypage');
        
        var data = {
            "days" : days,
            "product" : $('.company_room_selection').val()
        }
        
        var event = thundashop.Ajax.createEvent('','doPartnerBooking',$(this), data);
        
        thundashop.Ajax.postWithCallBack(event, function(data) {
            if(data === "ok") {
                
                thundashop.common.goToPage(pageId);
            } else {
                alert('Can not check in');
            }
        });
    },
    complete_partner_checkout: function() {
        var event = thundashop.Ajax.createEvent('','completePartnerCheckout',$(this),{});
        thundashop.Ajax.post(event);
    },
    
    initEvents: function () {
        $(document).on('click', '.Hotelbooking .check_available_button', app.Hotelbooking.checkAvailability);
        $(document).on('change', '.Hotelbooking #ordertype', app.Hotelbooking.changeOrderType);
        $(document).on('change', '.Hotelbooking .number_of_rooms', app.Hotelbooking.updateNumberOfRooms);
        $(document).on('click', '.Hotelbooking .cal_field', app.Hotelbooking.changeBookingDate);
        $(document).on('change', '.Hotelbooking .number_of_rooms', app.Hotelbooking.updateRoomCount);
        $(document).on('click', '.Hotelbooking .need_handicat', app.Hotelbooking.updateNeedHandicap);
        $(document).on('change', '.Hotelbooking .cleaning_option', app.Hotelbooking.updateCleaningCount);
        $(document).on('click', '.Hotelbooking .parking_option', app.Hotelbooking.updateParking);
        $(document).on('change', '.Hotelbooking #numberofpersons', app.Hotelbooking.setNumberOfPersons);
        $(document).on('blur', '.Hotelbooking #numberofpersons', app.Hotelbooking.setNumberOfPersons);
        $(document).on('blur', '.Hotelbooking .bookingsummary input', app.Hotelbooking.saveCurrentData);
        $(document).on('click', '.Hotelbooking .checklicenagreement', app.Hotelbooking.saveCurrentData);
        $(document).on('click', '.Hotelbooking .checkNeedFlex', app.Hotelbooking.updateNeedFlex);
        $(document).on('click', '.Hotelbooking input[gsname="partnershipdeal"]', app.Hotelbooking.changeToPartnership);
        $(document).on('click', '.Hotelbooking .fa.calnav', app.Hotelbooking.navigateMonth);
        $(document).on('click', '.Hotelbooking .searchexisting', app.Hotelbooking.searchExistingButton);
        $(document).on('click', '.Hotelbooking input[gsname="mvaregistered"]', app.Hotelbooking.saveCurrentData);
        $(document).on('click', '.Hotelbooking input[gsname="userData][customer_type]"]', app.Hotelbooking.saveCurrentData);
        $(document).on('click', '.Hotelbooking input[gsname="partner_type"]', app.Hotelbooking.saveCurrentData);
        $(document).on('keyup', '.Hotelbooking input[gsname="userData][postal_code]"]', app.Hotelbooking.updatePostalPlace);
        $(document).on('keyup', '.Hotelbooking input[gsname="userData][birthday]"]', app.Hotelbooking.updateBrreg);
        $(document).on('keyup', '.Hotelbooking .searchcustomerinput', app.Hotelbooking.searchCustomer);
        $(document).on('click', '.Hotelbooking .searchcustomerbutton', app.Hotelbooking.searchCustomer);
        $(document).on('click', '.Hotelbooking .selectcustomer', app.Hotelbooking.selectCustomer);
        $(document).on('click', '.Hotelbooking .monthcheckbox', app.Hotelbooking.selectMonth);
        $(document).on('click', '.Hotelbooking .caldate', app.Hotelbooking.selectDay);
        $(document).on('click', '.Hotelbooking .continue_to_summary', app.Hotelbooking.continueToSummary);
        $(document).on('click', '.Hotelbooking .complete_partner_checkout', app.Hotelbooking.complete_partner_checkout);
        $(document).on('change', '.Hotelbooking .weekcheckbox', app.Hotelbooking.weekCheckBox);
        $(document).on('click', '.Hotelbooking .continue_to_cart_button', app.Hotelbooking.animateNext);
        $(document).on('mouseover', '.Hotelbooking .room_selection_2 .fa-info-circle', this.showToolTip);
        $(document).on('mouseout', '.Hotelbooking .room_selection_2 .fa-info-circle', this.hideToolTip);
    }
};

app.Hotelbooking.initEvents();
