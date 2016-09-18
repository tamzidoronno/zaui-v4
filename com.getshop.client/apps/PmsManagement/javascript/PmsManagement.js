app.PmsManagement = {
    init: function () {
        $(document).on('change', '.PmsManagement .attachedProduct', app.PmsManagement.attachProduct);
        $(document).on('click', '.PmsManagement .moreinformationaboutbooking', app.PmsManagement.showMoreInformation);
        $(document).on('click', '.PmsManagement .viewmode', app.PmsManagement.toggleEditMode);
        $(document).on('click', '.PmsManagement td.pricecol', app.PmsManagement.showNotPaidInfo);
        $(document).on('click', '.PmsManagement .statisticsrow', app.PmsManagement.loadStatisticsOverview);
        $(document).on('click', '.PmsManagement .editGuestToggle', app.PmsManagement.editGuestToggle);
        $(document).on('change', '.PmsManagement [gsname="numberofguests"]', app.PmsManagement.editGuestToggle);
        $(document).on('click', '.PmsManagement .showorderbutton', app.PmsManagement.showOrder);
        $(document).on('click', '.PmsManagement .doneediting', app.PmsManagement.doneediting);
        $(document).on('click', '.PmsManagement .deletebooking', app.PmsManagement.deletebooking);
        $(document).on('click', '.PmsManagement .resetnotifications', app.PmsManagement.resetnotifications);
        $(document).on('click', '.PmsManagement .editfield', app.PmsManagement.editfieldrow);
        $(document).on('click', '.PmsManagement .loadedituser', app.PmsManagement.loadedituser);
        $(document).on('click', '.PmsManagement .savenewfielddata', app.PmsManagement.savenewfielddata);
        $(document).on('keyup','.PmsManagement .newroomstartdate', app.PmsManagement.updateRoomList);
        $(document).on('keyup','.PmsManagement .newroomenddate', app.PmsManagement.updateRoomList);
        $(document).on('keyup','.PmsManagement .newroomstarttime', app.PmsManagement.updateRoomList);
        $(document).on('keyup','.PmsManagement .newroomendtime', app.PmsManagement.updateRoomList);
        $(document).on('click','.PmsManagement .change_cleaning_date', app.PmsManagement.changeCleaningDate);
        $(document).on('change','.PmsManagement .newroomstartdate', app.PmsManagement.updateRoomList);
        $(document).on('change','.PmsManagement .newroomenddate', app.PmsManagement.updateRoomList);
        $(document).on('click','.PmsManagement .showlog', app.PmsManagement.showlog);
        $(document).on('click','.PmsManagement .closeadduser', app.PmsManagement.closeadduser);
        $(document).on('click','.PmsManagement .massupdatepricesfield .fa-close', app.PmsManagement.toggleMassUpdatePrices);
        $(document).on('click','.PmsManagement .showmassupdatepricesfield', app.PmsManagement.toggleMassUpdatePrices);
        $(document).on('change','.PmsManagement .changeuseronbooking', app.PmsManagement.changeuseronbooking);
        $(document).on('change','.PmsManagement .changecompanyonuser', app.PmsManagement.changecompanyonuser);
        $(document).on('change','.PmsManagement .changestatisticsinterval', app.PmsManagement.changeSummaryView);
        $(document).on('click','.PmsManagement .updateorderrow', app.PmsManagement.updateorderrow);
        $(document).on('click','.PmsManagement .sendinvoice, .PmsManagement .sendreciept', app.PmsManagement.showsendinvoice);
        $(document).on('click','.PmsManagement .loadExcelExportOptions', app.PmsManagement.loadExcelExportOptions);

        $(document).on('click','.PmsManagement .togglerepeatbox', app.PmsManagement.closeRepeatBox);
        $(document).on('click','.PmsManagement .change_cleaning_interval', app.PmsManagement.changeCleaingInterval);
        $(document).on('change','.PmsManagement .repeat_type', app.PmsManagement.changeRepeatType);
        $(document).on('click','.PmsManagement .changeInvoiceTo', app.PmsManagement.changeInvoiceTo);
        $(document).on('click','.PmsManagement .sendpaymentlink', app.PmsManagement.sendpaymentlink);
        $(document).on('click','.PmsManagement .closesendpaymentlink', app.PmsManagement.sendpaymentlink);
        $(document).on('change','.PmsManagement select[gsname="itemid"]', app.PmsManagement.loadTakenRoomList);
        $(document).on('change','.PmsManagement .sendlinktouser', app.PmsManagement.changePaymentLinkUser);
        $(document).on('click','.PmsManagement .tab', app.PmsManagement.selectTab);
        $(document).on('click','.PmsManagement .addAddonsButton', app.PmsManagement.addAddon);
        $(document).on('click','.PmsManagement .saveAddons', app.PmsManagement.saveAddons);
        $(document).on('click','.PmsManagement .removeAddons', app.PmsManagement.removeAddons);
        $(document).on('click','.PmsManagement .toggleColumnFilter', app.PmsManagement.toggleColumnFilter);
        $(document).on('keyup','.PmsManagement .alldayprice', app.PmsManagement.updateDayPrices);
        $(document).on('click','.PmsManagement .updatecardonroom', app.PmsManagement.updatecardonroom);
        $(document).on('click','.PmsManagement .doCreditOrder', app.PmsManagement.doCreditOrder);
        $(document).on('keyup','.PmsManagement .matrixpricealldays', app.PmsManagement.updateRoomPriceMatrix);
    },
    changeCleaningDate : function() {
        var newDate = prompt("Specify a new date", $(this).text());
        if(!newDate) {
            return;
        }
        
        var event = thundashop.Ajax.createEvent('','updateCleaningDate',$(this), {
            "date" : newDate,
            "roomid" : $(this).attr('roomid'),
            "bookingid" : $('#openedbookingid').val()
        });
        thundashop.common.showInformationBoxNew(event);
    },
    loadExcelExportOptions : function() {
        $('.excelexportoptions').show();
    },
    
    loadedituser : function() {
        var event = thundashop.Ajax.createEvent('','renderEditUserView', $(this), {
            bookingid : $('#openedbookingid').val()
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('#edituserview').html(res);
            $(".PmsManagement .edituserbox").fadeIn(function() {$(".edituserbox select").chosen(); });
        })
    },
    showNotPaidInfo : function() {
        var event = thundashop.Ajax.createEvent('','loadOrderInfoOnBooking', $(this), {
            bookingid : $(this).closest('tr').attr('bookingid')
        });
        var box = $('.orderinfobox');
        var button = $(this);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            box.html(res);
            box.css('position','absolute');
            box.css('left',button.position().left-box.width());
            box.css('top',button.position().top+60);
            box.fadeIn();
        });
    },
    
    savenewfielddata : function() {
        var saveButton = $(this);
        var data = {
            "bookingid" : $('#openedbookingid').val(),
            "newval": saveButton.closest('.editfieldrow').find('.fieldtoedit').text(),
            "field" : saveButton.attr('field')
        };
        var event = thundashop.Ajax.createEvent('','updateRegistrationData', $(this), data);
        thundashop.Ajax.postWithCallBack(event, function() {
            saveButton.closest('.editfieldrow').find('.fieldtoedit').attr('contenteditable',null);
            saveButton.closest('.editfieldrow').find('.savenewfielddata').hide();
        });
    },
    
    editfieldrow : function() {
        $(this).closest('.editfieldrow').find('.fieldtoedit').attr('contenteditable','true');
        $(this).closest('.editfieldrow').find('.fieldtoedit').focus();
        $(this).closest('.editfieldrow').find('.savenewfielddata').show();
    },
    
    toggleColumnFilter : function() {
        $('.PmsManagement .columnfilter').toggle();
    },
    changePaymentLinkUser : function() {
        var val = $(this).val();
        var splitted = val.split(":");
        var box = $(this).closest('.sendpaymentlinkbox');
        box.find('[gsname="bookerPrefix"]').val(splitted[0]);
        box.find('[gsname="bookerPhone"]').val(splitted[1]);
        box.find('[gsname="bookerEmail"]').val(splitted[2]);
    },
    showsendinvoice : function() {
        $(this).closest('tr').find('.sendinvoicebox').slideDown();
    },
    updateRoomPriceMatrix : function() {
        var table = $(this).closest('.roompricematrixtable');
        var val = $(this).val();
        console.log(val);
        table.find('.matrixdayprice').each(function() {
            $(this).val(val);
        });
    },
    
    updateDayPrices : function() {
        var val = $(this).val();
        
        $('.dayprice').val(val);
    },
    toggleMassUpdatePrices : function() {
        var field = $('.PmsManagement .massupdatepricesfield');
        if(field.is(":visible")) {
            field.slideUp();
        } else {
            field.slideDown();
        }
    },
    updateorderrow : function() {
        var row = $(this).closest('tr');
        var data = {
            bookingid : $('#openedbookingid').val(),
            "orderid" : row.attr('orderid'),
            "status" : row.find('.orderstatus').val(),
            "paymenttype" : row.find('.paymenttype').val()
        };
        
        var corScroll = $('.informationbox-outer').scrollTop();
        var event = thundashop.Ajax.createEvent('','updateOrder', $(this), data);
        thundashop.common.showInformationBoxNew(event);
        $('.informationbox-outer').scrollTop(corScroll);
    },
    doCreditOrder : function() {
        var confirmed = confirm("Are you sure you want to credit this order?");
        if(!confirmed) {
            return;
        }
        
        var data = {
            orderid : $(this).closest('tr').attr('orderid'),
            bookingid : $('#openedbookingid').val()
        };
        
        var event = thundashop.Ajax.createEvent('','creditOrder', $(this), data);
        thundashop.common.showInformationBoxNew(event);
        
        
    },
    
    changeSummaryView : function() {
        var data = {
            "view" : $(this).val()
        };
        console.log(data);
        
        thundashop.Ajax.simplePost($(this),"changeTimeView", data);
    },
    updatecardonroom : function() {
        var row = $(this).closest('tr');
        var data = {
            "roomid" : row.attr('roomid'),
            "cardtype": row.find('.cardtype').val(),
            "code" : row.find('.code').val()
        };
        
        var event = thundashop.Ajax.createEvent('','saveCardOnRoom', $(this), data);
        thundashop.Ajax.postWithCallBack(event, function() {
            thundashop.common.Alert("Success", "Card data has been updated.");
        });
    },
    
    loadStatisticsOverview : function() {
        var data = {
            "type" : $(this).attr('type'),
            "day" : $(this).attr('day')
        }
        
        var event = thundashop.Ajax.createEvent('','loadDayStatistics',$(this),data);
        
        thundashop.common.showInformationBoxNew(event, "Daily reservation");
    },
    
    removeAddons : function() {
         var data = {
            "type" : $('#addontypeselection').val(),
            "bookingid" : $('#openedbookingid').val(),
            "roomId" : $('#roomsForAddons').val(),
            "remove" : true
        };
        var event = thundashop.Ajax.createEvent('','addAddon', $(this), data);
        thundashop.common.showInformationBoxNew(event);
    },
    saveAddons : function() {
        var toSave = {};
        $('.addonrow').each(function() {
            var addonid = $(this).attr('addonid');
            if(!toSave[addonid]) {
                toSave[addonid] = {};
            }
            toSave[addonid].count = $(this).find('.addoncount').val();
            toSave[addonid].price = $(this).find('.addonprice').val();
        });
        var event = thundashop.Ajax.createEvent('','updateAddons',$(this), {
            "addons" : toSave,
            "bookingid" : $('#openedbookingid').val(),
        });
        thundashop.common.showInformationBoxNew(event);
    },
    addAddon : function() {
        var data = {
            "type" : $('#addontypeselection').val(),
            "bookingid" : $('#openedbookingid').val(),
            "roomId" : $('#roomsForAddons').val(),
            "remove" : false
        };
        var event = thundashop.Ajax.createEvent('','addAddon', $(this), data);
        thundashop.common.showInformationBoxNew(event);
    },
    selectTab : function() {
        var tab = $(this);
        $('.tab.selected').removeClass('selected');
        tab.addClass('selected');
        $('.tabarea').hide();
        var area = $(this).attr('area');
        $('.tabarea.'+area).show();
        localStorage.setItem('selectedbookinginfotab', area);
    },
    sendpaymentlink : function() {
        $(this).closest('td').find('.sendpaymentlinkbox').toggle();
    },
    
    loadTakenRoomList : function() {
        var row = $(this).closest('tr');
        var bookingid = row.attr('bookingid');
        var roomid = row.attr('roomid');
        var event = thundashop.Ajax.createEvent('','loadTakenRoomList', $(this), {
            "bookingid" : bookingid,
            "roomid" : roomid,
            "itemid" : $(this).val()
        });
        
        thundashop.Ajax.postWithCallBack(event, function(result) {
            row.find('.tiparea').hide()
            if(result) {
                row.find('.tiparea').fadeIn();
                row.find('.tiparea').html(result);
            }
        });
    },
    changeInvoiceTo : function() {
        if($(this).closest('tr').hasClass('roomdeleted')) {
            return;
        }
        var newDate = prompt("Specify a new date");
        if(!newDate) {
            return;
        }
        
        var data = {
            "newdate" : newDate,
            "roomid" : $(this).closest('tr').attr('roomid'),
            "bookingid" : $(this).closest('tr').attr('bookingid')
        }
        
        var event = thundashop.Ajax.createEvent('','changeInvoicedTo',$(this), data);
        thundashop.common.showInformationBoxNew(event);
    },
    changeCleaingInterval : function() {
        var intBox = $(this).find('.intervalset');
        var newInterval = prompt("New cleaning interval", intBox.text());
        $(this).find('.intervalset').text(newInterval);
        var event = thundashop.Ajax.createEvent('','setNewInterval',$(this), {
            "interval" : newInterval,
            "roomid" : $(this).attr('roomid')
        });
        thundashop.Ajax.postWithCallBack(event, function() {
            intBox.text(newInterval);
        });
    },
    
    changeRepeatType: function() {
        var type = $(this).val();
        $('.repeatrow').hide();
        if(type !== "0") {
            $('.repeatrow').show();
        } 
        $('.repeateachdaterow').hide();
        if(type === "1") {
            $('.repeateachdaterow').show();
        }
        
        $('.repeatoption').hide();
        $('.repeat_' + type).show();
    },
    closeRepeatBox : function() {
        var box = $('.PmsManagement .addMoredatesPanel');
        if(box.is(":visible")) {
            box.slideUp();
        } else {
            box.slideDown();
        }
    },
    
    showRepeatDates : function() {
        if(!$('.repatingroomlist').is(':visible')) {
            $('.repatingroomlist').slideDown();
        } else {
            $('.repatingroomlist').slideUp();
        }
    },
    
    changeuseronbooking : function() {
        var data = {
            "userid" : $(this).val(),
            "bookingid" : $(this).attr('bookingid')
        };
        var event = thundashop.Ajax.createEvent('','changeBookingOnEvent', $(this), data);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('#edituserview').html(res);
            $(".PmsManagement .edituserbox").show();
        })
        
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
            "start" : $('.newroomstartdate').val() + " " + $('.newroomstarttime').val(),
            "end" : $('.newroomenddate').val() + " " + $('.newroomendtime').val(),
            "selectedtype" : $('.addroomselectiontype').val()
        };
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
        edit.find('.fa-close').remove();
        var close = $("<i class='fa fa-close' style='float:right;cursor:pointer;'></i>");
        close.click(function() {
            edit.fadeOut();
            return;
        });
        edit.prepend(close);
        edit.fadeIn();
    },
    showMoreInformation : function() {
        var data = {
            "roomid" : $(this).attr('roomid'),
            "bookingid" : $(this).attr('bookingid')
        }
        
        var event = thundashop.Ajax.createEvent('','showBookingInformation',$(this), data);
        thundashop.common.showInformationBoxNew(event, 'Booking information');
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