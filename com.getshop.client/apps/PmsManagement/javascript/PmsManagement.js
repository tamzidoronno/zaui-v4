app.PmsManagement = {
    init: function () {
        $(document).on('change', '.PmsManagement .attachedProduct', app.PmsManagement.attachProduct);
        $(document).on('click', '.PmsManagement .moreinformationaboutbooking', app.PmsManagement.showMoreInformation);
        $(document).on('click', '.PmsManagement .updateinvoicenotebutton', app.PmsManagement.updateInvoiceNote);
        $(document).on('click', '.PmsManagement .viewmode', app.PmsManagement.toggleEditMode);
        $(document).on('click', '.PmsManagement td.pricecol', app.PmsManagement.showNotPaidInfo);
        $(document).on('click', '.PmsManagement .statisticsrow', app.PmsManagement.loadStatisticsOverview);
        $(document).on('click', '.PmsManagement .editGuestToggle', app.PmsManagement.editGuestToggle);
        $(document).on('click', '.PmsManagement .checkallbookedrooms', app.PmsManagement.checkallbookedrooms);
        $(document).on('change', '.PmsManagement [gsname="numberofguests"]', app.PmsManagement.editGuestToggle);
        $(document).on('click', '.PmsManagement .showorderbutton', app.PmsManagement.showOrder);
        $(document).on('click', '.PmsManagement .doneediting', app.PmsManagement.doneediting);
        $(document).on('click', '.PmsManagement .deletebooking', app.PmsManagement.deletebooking);
        $(document).on('click', '.PmsManagement .resetnotifications', app.PmsManagement.resetnotifications);
        $(document).on('click', '.PmsManagement .editfield', app.PmsManagement.editfieldrow);
        $(document).on('click', '.PmsManagement .loadedituser', app.PmsManagement.loadedituser);
        $(document).on('click', '.PmsManagement .edituseronorderviewbutton', app.PmsManagement.loadedituseronorder);
        $(document).on('click', '.PmsManagement .createanewuserfromeditbox', app.PmsManagement.createNewUserOnBooking);
        $(document).on('click', '.PmsManagement .savenewfielddata', app.PmsManagement.savenewfielddata);
        $(document).on('click', '.PmsManagement .uploadfiletobooking', app.PmsManagement.uploadBoxClick);
        $(document).on('keyup','.PmsManagement .newroomstartdate', app.PmsManagement.updateRoomList);
        $(document).on('keyup','.PmsManagement .newroomenddate', app.PmsManagement.updateRoomList);
        $(document).on('keyup','.PmsManagement .newroomstarttime', app.PmsManagement.updateRoomList);
        $(document).on('keyup','.PmsManagement .newroomendtime', app.PmsManagement.updateRoomList);
        $(document).on('click','.PmsManagement .change_cleaning_date', app.PmsManagement.changeCleaningDate);
        $(document).on('click','.PmsManagement .setcleaningcomment', app.PmsManagement.setCleaningComment);
        $(document).on('change','.PmsManagement .newroomstartdate', app.PmsManagement.updateRoomList);
        $(document).on('change','.PmsManagement .newroomenddate', app.PmsManagement.updateRoomList);
        $(document).on('change','.PmsManagement .addroomselectiontype', app.PmsManagement.updateRoomList);
        $(document).on('click','.PmsManagement .showlog', app.PmsManagement.showlog);
        $(document).on('click','.PmsManagement .closeadduser', app.PmsManagement.closeadduser);
        $(document).on('click','.PmsManagement .sendconfirmation', app.PmsManagement.sendconfirmation);
        $(document).on('click','.PmsManagement .massupdatepricesfield .fa-close', app.PmsManagement.toggleMassUpdatePrices);
        $(document).on('click','.PmsManagement .showmassupdatepricesfield', app.PmsManagement.toggleMassUpdatePrices);
        $(document).on('change','.PmsManagement .changeuseronbooking', app.PmsManagement.changeuseronbooking);
        $(document).on('change','.PmsManagement .changecompanyonuser', app.PmsManagement.changecompanyonuser);
        $(document).on('change','.PmsManagement .changestatisticsinterval', app.PmsManagement.changeSummaryView);
        $(document).on('click','.PmsManagement .updateorderrow', app.PmsManagement.updateorderrow);
        $(document).on('click','.PmsManagement .sendinvoice, .PmsManagement .sendreciept', app.PmsManagement.showsendinvoice);
        $(document).on('click','.PmsManagement .loadExcelExportOptions', app.PmsManagement.loadExcelExportOptions);
        $(document).on('click','.PmsManagement .createorderafterstaycheckbox', app.PmsManagement.createorderafterstaycheckbox);
        $(document).on('click','.PmsManagement .removecreateafterstayfrombooking', app.PmsManagement.removeorderafterstay);
        $(document).on('click','.PmsManagement .loadorderstatistics', app.PmsManagement.loadorderstatistics);
        $(document).on('click','.PmsManagement .changepaymenttypebutton', app.PmsManagement.changepaymenttypebutton);
        $(document).on('click','.PmsManagement .markpaidbutton', app.PmsManagement.markpaidform);
        $(document).on('click','.PmsManagement .changeGuestInformation', app.PmsManagement.changeGuestInformation);
        $(document).on('click','.PmsManagement .addonincludedinroomprice', app.PmsManagement.toggleAddonIncluded);
        $(document).on('click','.PmsManagement .addproducttocart', app.PmsManagement.addProductToCart);
        $(document).on('click','.PmsManagement .changeduedates', app.PmsManagement.changeDueDates);
        $(document).on('click','.PmsManagement .removeOrderFromBooking', app.PmsManagement.removeOrderFromBooking);
        $(document).on('click','.PmsManagement .marknoshowwubook', app.PmsManagement.marknoshowwubook);
        $(document).on('click','.PmsManagement .displayordersforroom', app.PmsManagement.displayOrdersForRoom);
        $(document).on('click','.PmsManagement .loadeditcartitemonorder', app.PmsManagement.loadEditCartItemOnOrder);

        $(document).on('click','.PmsManagement .togglerepeatbox', app.PmsManagement.closeRepeatBox);
        $(document).on('click','.PmsManagement .change_cleaning_interval', app.PmsManagement.changeCleaingInterval);
        $(document).on('change','.PmsManagement .repeat_type', app.PmsManagement.changeRepeatType);
        $(document).on('click','.PmsManagement .sendpaymentlink', app.PmsManagement.sendpaymentlink);
        $(document).on('click','.PmsManagement .closesendpaymentlink', app.PmsManagement.sendpaymentlink);
        $(document).on('click','.PmsManagement .closesendreciept', app.PmsManagement.sendreciept);        
        $(document).on('change','.PmsManagement select[gsname="itemid"]', app.PmsManagement.loadTakenRoomList);
        $(document).on('change','.PmsManagement .changebookingitempanel input', app.PmsManagement.loadTakenRoomList);
        $(document).on('blur','.PmsManagement .changebookingitempanel input', app.PmsManagement.loadTakenRoomList);
        $(document).on('keyup','.PmsManagement .changebookingitempanel input', app.PmsManagement.loadTakenRoomList);
        $(document).on('change','.PmsManagement .sendlinktouser', app.PmsManagement.changePaymentLinkUser);
        $(document).on('click','.PmsManagement .tab', app.PmsManagement.selectTab);
        $(document).on('click','.PmsManagement .addAddonsButton', app.PmsManagement.addAddon);
        $(document).on('click','.PmsManagement .saveAddons', app.PmsManagement.saveAddons);
        $(document).on('click','.PmsManagement .removeAddons', app.PmsManagement.removeAddons);
        $(document).on('click','.PmsManagement .toggleColumnFilter', app.PmsManagement.toggleColumnFilter);
        $(document).on('keyup','.PmsManagement .alldayprice', app.PmsManagement.updateDayPrices);
        $(document).on('click','.PmsManagement .updatecardonroom', app.PmsManagement.updatecardonroom);
        $(document).on('click','.PmsManagement .doCreditOrder', app.PmsManagement.doCreditOrder);
        $(document).on('click','.PmsManagement .executeroomsbookedaction', app.PmsManagement.executeroomsbookedaction);
        $(document).on('keyup','.PmsManagement .matrixpricealldays', app.PmsManagement.updateRoomPriceMatrix);
        $(document).on('keyup','.PmsManagement .matrixpricealldaysextaxes', app.PmsManagement.updateRoomPriceExTaxesMatrix);
        $(document).on('keyup','.PmsManagement .matrixdaypriceextax, .PmsManagement .matrixdayprice', app.PmsManagement.calculateTax);
        $(document).on('click','.PmsManagement .addonstable', app.PmsManagement.showSaveButton);
        $(document).on('click','.PmsManagement .listaddonsaddedtoroom', app.PmsManagement.showAddonList);
        $(document).on('click','.PmsManagement .removeAddonsFromRoom', app.PmsManagement.removeAddonsFromRoom);
        $(document).on('focus','.PmsManagement .addonstable', app.PmsManagement.showSaveButton);
        $(document).on('keyup','.PmsManagement .addonstable', app.PmsManagement.showSaveButton);
        $(document).on('change','.PmsManagement .roomsbookedactionsselection', app.PmsManagement.updateRoomActionSelection);
        $(document).on('click','.PmsManagement .loadStatsForDay', app.PmsManagement.loadStatsForDay);
        $(document).on('click','.PmsManagement .selectalladdons', app.PmsManagement.selectalladdons);
        $(document).on('click','.PmsManagement .conference_addactionpoint', app.PmsManagement.addActionPoint);
        $(document).on('click','.PmsManagement .conference_addday', app.PmsManagement.addConferenceDay);
        $(document).on('click','.PmsManagement .save_conference_data', app.PmsManagement.save_conference_data);
        $(document).on('click','.PmsManagement .remove_conference_row', app.PmsManagement.removeConferenceRow);
        $(document).on('click','.PmsManagement .delete_day_row', app.PmsManagement.deleteConferenceDay);
        $(document).on('click','.PmsManagement .addPaymentMethod', app.PmsManagement.addPaymentMethod);
        $(document).on('click','.PmsManagement .removePaymentMethod', app.PmsManagement.removePaymentMethod);
        $(document).on('click','.PmsManagement .loadorderstatsentryfororder', app.PmsManagement.loadorderstatsentryfororder);
        $(document).on('click','.PmsManagement .radioinput', app.PmsManagement.updateBookingInformationRadioButton);
        $(document).on('click','.PmsManagement .setNewPasswordOnUser', app.PmsManagement.setNewPasswordOnUser);
        $(document).on('click','.PmsManagement .changeRecieptEmail', app.PmsManagement.changeRecieptEmail);
        $(document).on('click','.PmsManagement .loadadditionalinfo', app.PmsManagement.loadadditionalinfo);
        
        $(document).on('change','.PmsManagement .contactdatadropdown', app.PmsManagement.updateBookingInformationDropdown);
        $(document).on('click','.PmsManagement .loadorderinformation', app.PmsManagement.loadOrderInformation);
        $(document).on('click','.PmsManagement .createnewfilter', app.PmsManagement.createNewIncomeReportFilter);
        $(document).on('click','.PmsManagement .checkboxforbookedroom', app.PmsManagement.updateCheckedRoomUnsettledAmount);
        $(document).on('click','.PmsManagement .checkallbookedrooms', app.PmsManagement.updateCheckedRoomUnsettledAmount);
        $(document).on('click','.PmsManagement .editaddonpricematrix', app.PmsManagement.editAddonPriceMatrix);
        $(document).on('click','.PmsManagement .connectItemOnsOrderToRoom', app.PmsManagement.connectItemOnsOrderToRoom);
        $(document).on('keyup','.PmsManagement .changeorderdates', app.PmsManagement.changeOrderPeriode);
        $(document).on('click','.PmsManagement .managementviewoptions', app.PmsManagement.toggleManagementviewfilter);
        $(document).on('click','.PmsManagement .updatecartitemrowbutton', app.PmsManagement.updateCartItemRow);
        $(document).on('click','.PmsManagement .deleteitemrowfromorder', app.PmsManagement.deleteItemFromCart);
        $(document).on('click','.PmsManagement .startAddonToRoom', app.PmsManagement.startAddonToRoom);
        $(document).on('click','.PmsManagement .loadAddonsList', app.PmsManagement.loadAddonsList);
        $(document).on('click','.PmsManagement .createnewbookingbutton', app.PmsManagement.createNewBooking);
        $(document).on('click','.PmsManagement .loadAccountInformation', app.PmsManagement.loadAccountInformation);
        $(document).on('click','.PmsManagement .brregresultentry', app.PmsManagement.brregEntryClick);
        $(document).on('click','.PmsManagement .createnewcustomerbutton', app.PmsManagement.createNewCustomerButton);
        $(document).on('click','.PmsManagement .existinguserselection', app.PmsManagement.selectExistingCustomer);
        $(document).on('click','.PmsManagement .doverifonepayment', app.PmsManagement.doVerifonePayment);
        $(document).on('click','.PmsManagement .loadRoomTypes', app.PmsManagement.loadRoomTypes);
        $(document).on('click','.PmsManagement .deletecardbutton', app.PmsManagement.deleteCard);
        $(document).on('change','.informationbox .changetypeonroom', app.PmsManagement.changetypeonroom);
        $(document).on('click','.informationbox .completeRoomReservation', app.PmsManagement.completeRoomReservation);
        $(document).on('click','.PmsManagement .managementviewbanner .fa-plus-square', app.PmsManagement.loadQuickReservation);
        $(document).on('click','.PmsManagement .deleteComment', app.PmsManagement.deleteComment);
    },
    calculateTax : function() {
        var taxValue = parseInt($(this).closest('.editmode').find('.taxvalue').val());
        var val = $(this).val();
        var row = $(this).closest('tr');
        if($(this).hasClass('matrixdaypriceextax')) {
            var incTaxes = val * ((100+taxValue)/100);
            incTaxes = Math.round(incTaxes);
            row.find('.matrixdayprice').val(incTaxes);
        } else {
            var incTaxes = val / ((100+taxValue)/100);
            row.find('.matrixdaypriceextax').val(incTaxes);
        }
    },
    
    deleteComment : function() {
        var line = $(this).closest('.commentline');
        var event = thundashop.Ajax.createEvent('','toggleDeleteComment', line, {
            "bookingid" : $(this).attr('bookingid'),
            "time" : $(this).attr('time')
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            if(line.hasClass('deleted')) {
                line.removeClass('deleted');
            } else {
                line.addClass('deleted');
            }
        });
    },
    
    loadQuickReservation : function() {
        var event = thundashop.Ajax.createEvent('','loadReserveRoomInformation',$(this),{});
        event.core.appname = "PmsManagement";
        thundashop.common.showInformationBoxNew(event,'Configuration');
    },
    completeRoomReservation : function() {
        var event = thundashop.Ajax.createEvent('','completeQuickReservation',$(this),{
            "typeid" : $(this).val(),
            "roomid" : $(this).closest('tr').attr('roomid')
        });
        event.core.appname = "PmsManagement";
        thundashop.common.showInformationBoxNew(event,'Configuration');
    },
    
    changetypeonroom : function() {
        var event = thundashop.Ajax.createEvent('','changeTypeOnRoom',$(this),{
            "typeid" : $(this).val(),
            "roomid" : $(this).closest('tr').attr('roomid')
        });
        event.core.appname = "PmsManagement";
        thundashop.common.showInformationBoxNew(event,'Configuration');
    },
    deleteCard : function() {
        var confirmed = confirm("Are you sure you want to remove this card? This action can not be reverted");
        if(!confirmed) {
            return;
        }
        var event = thundashop.Ajax.createEvent('','deleteCard', $(this), {
            "userid" : $(this).attr('userid'),
            "cardid" : $(this).attr('cardid')
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.cardlist').html(res);
        });
    },
    displayVerifoneFeedBack : function(res) {
        if(res.msg === "completed") {
            app.PmsManagement.reloadtab();
        } else {
            $('.verifonefeedbackdata').show();
            $('.verifonefeedbackdata').html(res.msg);
        }
    },
    loadRoomTypes : function() {
        var event = thundashop.Ajax.createEvent('','loadBookingTypes', $(this), {
            "bookingid" : $('#openedbookingid').val(),
            "roomid" : $(this).closest('tr').attr('roomid')
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.changebookingtypepanel').html(res);
        });
    },
    doVerifonePayment : function() {
        var event = thundashop.Ajax.createEvent('','processVerifonePayment',$(this), {
            orderid : $(this).attr('orderid')
        });
        
        var btn = $(this);
        var showpayment = btn.closest('td').find('.doverifonepayment');
        
        thundashop.Ajax.postWithCallBack(event, function() {
            btn.hide();
            showpayment.show();
        });
    },
    
    brregEntryClick : function() {
         $('.nameinput').val($(this).attr('navn'));
         $('.orgidinput').val($(this).attr('orgid'));
         $('.createnewcustomerbutton').click();
    },
    createNewCustomerButton : function() {
        var event = thundashop.Ajax.createEvent('',"createNewUserOnBooking",$(this), {
            "bookingid" : $('#openedbookingid').val(),
            "name" : $('.nameinput').val(),
            "orgId" : $('.orgidinput').val()
        });
        
        var view = $('.edituserview');
        
        thundashop.Ajax.postWithCallBack(event, function(res) {
            view.html(res);
            view.find('.edituserbox').show();
        });
    },
    selectExistingCustomer : function() {
        var btn = $(this);
        btn.attr('bookingid', $('#openedbookingid').val());
        var userId = btn.attr('userid');
        var view = $('.edituserview');
        app.PmsManagement.doChangeUser(btn,view, userId);
    },
    
    loadAccountInformation : function(event) {
        if($(event.target).is(':checkbox')) {
            return;
        }
        var event = thundashop.Ajax.createEvent('','setQuickFilter',$(this),{
            "type" : "subtype_accountoverview",
            "userid" : $(this).attr('userid')
        });
        thundashop.common.hideInformationBox();
        thundashop.Ajax.post(event);  
    },
    createNewBooking : function() {
        var event = thundashop.Ajax.createEvent('','createNewBooking',$(this), {
            "userid" : $(this).attr('userid')
        });
        thundashop.common.showInformationBoxNew(event, "");
    },
    saveSuccess : function() {
        thundashop.common.Alert('Success','Account information successfully updated');
    },
    loadAddonsToBeAddedList : function() {
        var panel = $('.PmsManagement .addaddonsstep2');
        var args = thundashop.framework.createGsArgs(panel);
        var event = thundashop.Ajax.createEvent('','loadAddonsToBeAddedPreview',panel, args);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.addonpreview').html(res);
        });
    },
    loadAddonsList : function() {
        var panel = $("<span class='addaddonpanel-outer'><div style='text-align:right; padding: 5px; background-color:#efefef;'><span style='float:left;' class='addonpaneltitle'>Add addon</span><i class='fa fa-times' onclick=\"$('.addaddonpanel-outer').hide();\" style='cursor:pointer;'></i></div><span class='addaddonpanel' gstype='form' method='addAddonsToRoom' style='display:block;'><i class='fa fa-spin fa-spinner'></i></span></span>");
        $('.addaddonpanel-outer').remove();
        $(this).closest('td').append(panel);

        var data = {
            "roomId" : $(this).closest('tr').attr("roomid"),
            "bookingid" : $('#openedbookingid').val()
        }
        var event = thundashop.Ajax.createEvent('','loadAddonList', $(this), data);
        panel.show();
        thundashop.Ajax.postWithCallBack(event, function(res) {
            panel.find('.addaddonpanel').html(res);
        });
    },
    startAddonToRoom : function() {
        var data = {
            "addonId" : $(this).attr('addonid'),
            "roomId" : $(this).closest('tr[roomid]').attr("roomid"),
            "bookingid" : $('#openedbookingid').val()
        }
        var event = thundashop.Ajax.createEvent('','startAddonToRoom', $(this), data);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.addaddonpanel').html(res);
        });
    },
    deleteItemFromCart : function() {
        var confirmed = confirm("Are you sure you want to delete this row?");
        if(!confirmed) {
            return;
        }
        var args = {};
        args['cartitemid'] = $(this).attr('cartitemid');
        args['orderid'] = $(this).attr('orderid');
        var event = thundashop.Ajax.createEvent('','deleteItemFromCart', $(this), args);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            app.PmsManagement.loadOrderInformationByOrder(args['orderid']);
        });
    },
    
    updateCartItemRow : function() {
        var form = $(this).closest('[gstype="form"]');
        var args = thundashop.framework.createGsArgs(form);
        args['cartitemid'] = form.attr('cartitemid');
        args['orderid'] = form.attr('orderid');
        var event = thundashop.Ajax.createEvent('','updateCartItemRow', form, args);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            app.PmsManagement.loadOrderInformationByOrder(args['orderid']);
            $('.editcartitempanel').hide();
        });
    },
    toggleManagementviewfilter : function(){
        if($('.managementviewfilter').css('display') === 'none'){
            $('.managementviewfilter').slideDown();
            $('.managementviewfilter').css('display','inline-block');
        } else {
            $('.managementviewfilter').slideUp();
            $('.managementviewfilter').css('display','none');
        }
    },
    loadEditCartItemOnOrder : function() {
        var data = {
            "orderid" : $(this).attr('orderid'),
            "cartitemid" : $(this).attr('cartitemid')
        };
        var event = thundashop.Ajax.createEvent('','loadEditCartItemOnOrder', $(this),data);
        var panel = $('.editcartitempanel');
        var button = $(this);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            panel.html(res);
            panel.css('position','absolute');
            panel.css('top', button.position().top+10);
            panel.css('left', button.position().left - (panel.width()+10));
        });
    },
    connectItemOnsOrderToRoom : function () {
        var tr = $(this).closest('tr');
        var orderid = $(this).attr('orderid');
        var roomid = $(this).attr('roomid');
        
        var event = thundashop.Ajax.createEvent('','connectItemsToRoom',$(this), {
            "orderid" : orderid,
            "roomid" : roomid,
            "bookingid" : $('#openedbookingid').val()
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $("[fororder='"+orderid+"']").remove();
            tr.after(res);
        });
    },
    updateInvoiceNote : function() {
        var orderid = $(this).attr('orderid');
        var event = thundashop.Ajax.createEvent('','updateInvoiceNoteOnOrder',$(this), {
            "orderid" : orderid,
            "note" : $(this).closest('td').find('.invoicenotetextarea').val()
        });
        thundashop.Ajax.postWithCallBack(event, function() {
            app.PmsManagement.loadOrderInformationByOrder(orderid);
        })
    },
    
    editAddonPriceMatrix : function() {
        var event = thundashop.Ajax.createEvent('','editAddonAndPriceMatrixOnCartItem', $(this), {
            "itemid" : $(this).closest('.cartitemselectionrow').find('[gsname="itemid"]').val()
        });
        
        var loadArea = $(this).parent().find('.loadEditAddonAndPriceMatrix');
        
        thundashop.Ajax.postWithCallBack(event, function(res) {
            loadArea.html(res);
            loadArea.show();
        });
    },
    
    changeOrderPeriode : function(e) {
        var field = null;
        if(e.input) {
            field = $(e.input);
        } else {
            field = $(e.target);
        }
      
        var val = field.val();
        
        var data = {
            "newDate" : val,
            "type" : field.attr('datetype'),
            "bookingid" : $('#openedbookingid').val()
        }
        
        var event = thundashop.Ajax.createEvent('','changeOrderDatePeriode',field, data);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.warnunsettledamountouter').html(res);
        });
    },
    
    updateCheckedRoomUnsettledAmount : function() {
        var rooms = [];
        $('.checkboxforbookedroom').each(function() {
            if($(this).is(':checked')) {
                rooms.push($(this).attr('roomid'));
            }
        });
        var data = {
            "roomIdsSelected" : rooms,
            "bookingid" : $('#openedbookingid').val()
        };
       
        var event = thundashop.Ajax.createEvent('','loadUnsettledAmount', $(this), data);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.warnunsettledamountouter').html(res);
        });
    },
    displayOrdersForRoom : function() {
        $('.orderforrromview').remove();
        var event = thundashop.Ajax.createEvent('','loadBookingOrdersRoom', $(this), {
            "roomid" : $(this).closest('tr').attr('roomid'),
           "bookingid" : $('#openedbookingid').val()
        });
        var box = $(this);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            box.after(res);
        });
    },
    marknoshowwubook : function() {
        var confirmed = confirm("Are you sure you want to mark this booking as no show?");
        if(!confirmed) {
            return;
        }
        var event = thundashop.Ajax.createEvent('','markBookingAsNoShow', $(this), {
            "wubookid" : $(this).attr('wubookid')
        });
        thundashop.Ajax.postWithCallBack(event,function() {
            alert('No show set');
        });
    },
    
    createNewIncomeReportFilter : function() {
        var event = thundashop.Ajax.createEvent('','displayCreateNewIncomeReportFilter', $(this), {});
        thundashop.common.showInformationBoxNew(event, 'New income report filter');
    },
    loadOrderInformation : function() {
        var orderid = $(this).attr('orderid');
        if($("[fororder='"+orderid+"']").length > 0) {
            $("[fororder='"+orderid+"']").remove();
            return;
        }

        app.PmsManagement.loadOrderInformationByOrder(orderid);
    },
    loadOrderInformationByOrder : function(orderid) {
        var tr = $('tr[orderid="'+orderid+'"]');
        var event = thundashop.Ajax.createEvent('','loadOrderInformation', tr, {
            "orderid" : orderid,
           "bookingid" : $('#openedbookingid').val()
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $("[fororder='"+orderid+"']").remove();
            tr.after(res);
        });
    },
    
    loadadditionalinfo : function() {
        var event = thundashop.Ajax.createEvent('','loadAdditionalInformationForRoom', $(this), {
            "roomid" : $(this).attr('roomid')
        });
        var box = $('.additionalroominfobox');
        var infobutton = $(this);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.additionalroominfobox_inner').html(res);
            box.fadeIn();
            box.css('left', infobutton.position().left);
            box.css('top', infobutton.position().top+20);
            console.log(res);
        });
    },
    changeRecieptEmail : function() {
        var newEmail = prompt("New reciept email adress?");
        if(!newEmail) {
            return;
        }
        var event = thundashop.Ajax.createEvent('','updateRecieptEmailOnOrder', $(this), {
           "orderid" : $(this).attr('orderid'),
           "newEmail" : newEmail,
           "bookingid" : $('#openedbookingid').val()
        });
        thundashop.common.showInformationBoxNew(event, 'Booking information');
    },
    removeOrderFromBooking : function() {
        var confirmed = confirm("Are you sure you want to remove this booking?");
        if(!confirmed) {
            return;
        }
        var event = thundashop.Ajax.createEvent('','removeOrderFromBooking', $(this), {
           "orderid" : $(this).attr('orderid'),
           "bookingid" : $('#openedbookingid').val()
        });
        thundashop.common.showInformationBoxNew(event, 'Booking information');
    },
    
    setNewPasswordOnUser : function() {
        var newPassword = prompt("New password");
        if(!newPassword) {
            return;
        }
        var data = {
            "password" : newPassword,
            "userid" : $('.edituserbox [gsname="userid"]').val()
        }
        var event = thundashop.Ajax.createEvent('','setNewPasswordOnUser', $(this), data);
        thundashop.Ajax.postWithCallBack(event, function() {
            thundashop.common.Alert("Password updated", "The password has been changed", false);
        });
    },
    changeDueDates : function() {
        var days = $(this).attr('days');
        var newDays = prompt("Please enter the new due days", days);
        if(!newDays) {
            return;
        }
        var orderId = $(this).attr('orderid');
        var data = {
            "bookingid" : $('#openedbookingid').val(),
            "days" : newDays,
            "orderid" : orderId
        }
        var event = thundashop.Ajax.createEvent('','updateDueDaysOnOrder', $(this),data);
        thundashop.common.showInformationBoxNew(event, 'Booking information');
    },
    
    updateBookingInformationRadioButton: function(){
        var data = {
            "bookingid" : $('#openedbookingid').val(),
            "radiobutton" : $(this).val(),
            "field" : $(this).attr('field')
        }
        var event = thundashop.Ajax.createEvent('','sendBookingInformationRadioButton', $(this),data);
        thundashop.common.showInformationBoxNew(event, 'Booking information');
    },
    updateBookingInformationDropdown: function(){
        var data = {
            "bookingid" : $('#openedbookingid').val(),
            "dropdown" : $(this).val(),
            "field" : $(this).attr('field')
        }
        var event = thundashop.Ajax.createEvent('','sendupdateBookingInformationDropdown', $(this),data);
        thundashop.common.showInformationBoxNew(event, 'Booking information');
    },
    uploadBoxClick: function () {
        $('#getshop_select_files_link').remove();
        $('#your-files').remove();

        var selectDialogueLink = $('<a href="" id="getshop_select_files_link">Select files</a>');
        var fileSelector = $('<input type="file" id="your-files" multiple/>');

        selectDialogueLink.click(function () {
            fileSelector.click();
        });
        $('body').append(fileSelector);
        $('body').append(selectDialogueLink);

        var control = document.getElementById("your-files");
        var me = this;
        control.addEventListener("change", function () {
            fileSelector.remove();
            app.PmsManagement.imageSelected(control.files);
        });

        selectDialogueLink.click();
        selectDialogueLink.remove();
    },
    imageSelected: function (files) {
        var file = files[0];
        var fileName = file.name;

        var reader = new FileReader();

        reader.onload = function (event) {
            var dataUri = event.target.result;

            var data = {
                fileBase64: dataUri,
                fileName: fileName,
                userId : $('#filetouploaduserbutton').attr('userid')
            };
            
            var userid = $('[gs_model_attr="userid"]').attr('value'); 
           if($('#selecteduseridoverride').length > 0) {
                userid = $('#selecteduseridoverride').val();
            }
            var field = $('<div/>');
            field.attr('gss_view', "gs_user_workarea");
            field.attr('gss_fragment', "user");
            field.attr('gss_value', userid);
            var event = thundashop.Ajax.createEvent('','saveUploadedUserFile',$('#openedbookingid'), data);
            thundashop.Ajax.postWithCallBack(event, function(res) {
                app.PmsManagement.reloadtab();
            });
        };

        reader.onerror = function (event) {
            console.error("File could not be read! Code " + event.target.error.code);
        };

        reader.readAsDataURL(file);
    },
    
    addProductToCart : function() {
        var form = $(this).closest("[gstype='form']");
        var data = thundashop.framework.createGsArgs(form);
        
        var event = thundashop.Ajax.createEvent('','addProductToCart',$(this), data);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.ordercreationpreview').prepend(res);
        });
    },
    loadorderstatsentryfororder : function() {
        var event = thundashop.Ajax.createEvent('','loadOrderStatsForEntryCell',$(this),{
            "orderid" : $(this).attr('orderid'),
            "productid" : $(this).attr('productid')
        });
        
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('#singledayresult').html(res);
        })
    },
    calculcateCartCost : function(e) {
        var target = $(e.target);
        app.PmsManagement.calculateCartCostFromTarget(target);
    },
    calculateCartCostFromTarget : function(target) {
        var row = target.closest('.cartitemselectionrow');
        if(!target.hasClass('itemselection')) {
            row.find('.itemselection').attr('checked','checked');
        }
        var total = 0;
        $('.PmsManagement .cartitemselectionrow').each(function() {
            if($(this).find('.itemselection').is(':CHECKED')) {
                var price = $(this).find('.cartprice').val();
                var count = $(this).find('.cartcount').val();
                total += (price * count);
            }
        });
        $('.PmsManagement .totalprice').val(Math.round(total));
    },
    deleteConferenceDay: function() {
        $(this).closest('.dayform').remove();
    },
    
    removeConferenceRow: function() {
        $(this).closest('.action_point_row').remove();
    },
    
    isValidDate: function (dateString) {
        // First check for the pattern
        if(!/^\d{1,2}.\d{1,2}.\d{2}$/.test(dateString))
            return false;

        // Parse the date parts to integers
        var parts = dateString.split(".");
        var day = parseInt(parts[0], 10);
        var month = parseInt(parts[1], 10);
        var year = parseInt(parts[2], 10) + 2000;

        // Check the ranges of month and year
        if(year < 1000 || year > 3000 || month == 0 || month > 12)
            return false;

        var monthLength = [ 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 ];

        // Adjust for leap years
        if(year % 400 == 0 || (year % 100 != 0 && year % 4 == 0))
            monthLength[1] = 29;

        // Check the range of the day
        return day > 0 && day <= monthLength[month - 1];
    },

    save_conference_data: function() {
        var container = $(this).closest('.conference_data_form');
        
        var data = {};
        data.days = [];
        data.bookingid = container.find('[gsname="bookingid"]').val();
        data.note = container.find('[gsname="note"]').val();
        
        var error = false;
        
        container.find('.dayform.with_data').each(function() {
            var day = {
                dayid : $(container).attr('dayid'),
                day : $(this).find('input[gsname="day"]').val(),
                rows : []
            }
            
            if (day.day.length !== 8) {
                error = true;
            }
            
            var isValid = app.PmsManagement.isValidDate(day.day);
            if (!isValid)
                error = true;
            
            
            $(this).find('.action_point_row.with_data').each(function() {
                var row = {
                    id: $(this).attr('rowid'),
                    place : $(this).find('.col1 input').val(),
                    from : $(this).find('.col2 input').val(),
                    to : $(this).find('.col3 input').val(),
                    actionName : $(this).find('.col4 input').val(),
                    attendeesCount : $(this).find('.col5 input').val(),
                }

                day.rows.push(row);
            });
            
            data.days.push(day);
        });
        
        if (error) {
            alert("FAILED! Did not save... The day must be specified in this format dd.mm.yy");
            return;
        }
        
        console.log(data);
        
        
        var event = thundashop.Ajax.createEvent(null, "saveConferenceData", this, data);
        
        var me = $(this);
        $(this).prepend('<i class="fa fa-spin fa-spinner"></i>');
        thundashop.Ajax.post(event, function() {
            me.find('i').removeClass("fa-spin");
            me.find('i').removeClass("fa-spinner");
            me.find('i').addClass("fa-check");
            setTimeout(function() {
                me.find('i').remove();
            }, 1000);
        });
    },
    
    guid: function() {
        function s4() {
            return Math.floor((1 + Math.random()) * 0x10000)
                    .toString(16)
                    .substring(1);
        }
        ;

        
        return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4();
    },
    
    addActionPoint: function() {
        var newId = app.PmsManagement.guid();
        var dayform = $(this).closest('.dayform');
        var templateRow = dayform.find('.row_template');
        var toInsert = templateRow.clone();
        toInsert.removeClass('row_template');
        toInsert.addClass('with_data');
        toInsert.attr('rowid', newId);
        var rowsArea = dayform.find('.rows');
        rowsArea.append(toInsert);
    },
    
    addConferenceDay: function() {
        var newId = app.PmsManagement.guid();
        var dayform = $(this).closest('.conference_data_form').find('.dayform.daytemplate');
        var toInsert = dayform.clone();
        toInsert.removeClass('daytemplate');
        toInsert.addClass('with_data');
        toInsert.attr('dayid', newId);
        $(this).closest('.conference_data_form').find('.days_data').append(toInsert);
        toInsert.find('.conference_addactionpoint').click();
    },
    
    addPaymentMethod : function() {
        var data = thundashop.framework.createGsArgs($('.statsorderfilter'));
        var event = thundashop.Ajax.createEvent('','addPaymentTypeToFilter',$(this), data);
        $('.orderstatsres').html("<center style='font-size: 50px;'><i class='fa fa-spin fa-spinner'></i></center>");
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.orderstatsres').html(res);
        });
    },
    removePaymentMethod : function() {
        var data = {
            toRemove : $(this).attr('toremove')
        };
        var event = thundashop.Ajax.createEvent('','removePaymentMethod',$(this), data);
        $('.orderstatsres').html("<center style='font-size: 50px;'><i class='fa fa-spin fa-spinner'></i></center>");
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.orderstatsres').html(res);
        });
    },
    
    selectalladdons: function() {
        var checked = $(this).is(':checked');
        $(this).closest('.addonsadded').find('.addontoremove').each(function() {
            if(checked) {
                $(this).attr('checked','checked');
            } else {
                $(this).attr('checked',null);
            }
        });
    },
    toggleAddonIncluded : function() {
            $(this).attr('title','');
        if($(this).hasClass('fa-check')) {
            $(this).removeClass('fa-check');
            $(this).addClass('fa-close');
        } else {
            $(this).addClass('fa-check');
            $(this).removeClass('fa-close');
        }
    },
    showAddonList : function() {
        var event = thundashop.Ajax.createEvent('','loadEventListForRoom',$(this), {
            productId : $(this).attr('productId'),
            roomId : $(this).attr('pmsRoomId')
        });
        var addon = $(this);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            addon.closest('td').find('.addonsadded').html(res);
            addon.closest('td').find('.addonsadded').fadeIn();
        });
    },
    changeGuestInformation : function() {
        var form = $(this).closest('[gstype="form"]');
        var data = thundashop.framework.createGsArgs(form);
        var method = form.attr('method');
        var event = thundashop.Ajax.createEvent('',method, $(this), data);
        thundashop.common.showInformationBoxNew(event);
    },
    
    loadStatsForDay : function(e) {
        var target = $(e.target);
        var index = $(this).attr('index');
        var event = thundashop.Ajax.createEvent('','loadStatsForDay', $(this), {
            "index" : index,
            "rowindex" : target.attr('rowindex')
        });
        thundashop.common.showInformationBoxNew(event, "Statis for day");
    },
    changepaymenttypebutton : function() {
        var data = $(this).closest('tr').attr('orderid');
        $('.selectedorderidtochange').val(data);
        
        var form = $('.changepaymenttypeform');
        form.css('left', $(this).position().left);
        form.css('top', $(this).position().top);
        form.fadeIn();
    },
    markpaidform : function() {
        var data = $(this).closest('tr').attr('orderid');
        $('.selectedorderidtomarkpaid').val(data);
        
        var form = $('.markpaidform');
        form.css('left', $(this).position().left);
        form.css('top', $(this).position().top);
        form.fadeIn();
    },
    sendconfirmation : function() {
        var data = {
            "bookingid" : $('#openedbookingid').val(),
            "email" : $('.emailtosendconfirmationto').val(),
            "dotype" : $('.emailtosendconfirmationto').attr('dotype')
        }
        var event = thundashop.Ajax.createEvent('','resendConfirmation', $(this),data);
        thundashop.common.showInformationBoxNew(event);
    },
    
    setCleaningComment : function() {
        var comment = prompt("Comment", $(this).attr('comment'));
        var data = {
            comment : comment,
            roomid : $(this).attr('roomid'),
            bookingid : $('#openedbookingid').val()
        }
        var event = thundashop.Ajax.createEvent('','setCleaningComment', $(this),data);
        thundashop.common.showInformationBoxNew(event);
    },
    
    removeAddonsFromRoom : function() {
        var panel = $(this).closest('.addonsadded');
        var idstoremove = [];
        panel.find('.addontoremove').each(function() {
            if($(this).is(':checked')) {
                idstoremove.push($(this).attr('addonid'));
            }
        });
        
        var data = {
            roomId : panel.find('[gsname="roomId"]').val(),
            productId : panel.find('[gsname="productId"]').val(),
            idstoremove : idstoremove,
            bookingid : $('#openedbookingid').val()
        }
        var event = thundashop.Ajax.createEvent('','removeAddonsFromRoom', $(this),data);
        thundashop.common.showInformationBoxNew(event);
    },
    
    loadorderstatistics : function() {
        var data = thundashop.framework.createGsArgs($('.statsorderfilter'));
        var event = thundashop.Ajax.createEvent('','loadOrderStats',$('.PmsManagement'), data);
        $('.orderstatsres').html("<center style='font-size: 50px;'><i class='fa fa-spin fa-spinner'></i></center>");
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.orderstatsres').html(res);
        });
    },
    createorderafterstaycheckbox : function() {
        var data = {
            checked : $(this).is(':checked'),
            bookingid : $('#openedbookingid').val()
        }
        var event = thundashop.Ajax.createEvent('','setSendInvoiceAfter', $(this),data);
        thundashop.Ajax.postWithCallBack(event, function() {
            thundashop.common.Alert("Success", "Card data has been updated.");
        });
    },
    removeorderafterstay : function() {
        var data = {
            bookingid : $('#openedbookingid').val()
        }
        var event = thundashop.Ajax.createEvent('','removeCreateOrderAfterStay', $(this),data);
        thundashop.common.showInformationBoxNew(event);
    },
    executeroomsbookedaction : function() {
        var action = $('.roomsbookedactionsselection').val();
        var rooms = [];
        $('.checkboxforbookedroom').each(function() {
            if($(this).is(':checked')) {
                rooms.push($(this).attr('roomid'));
            }
        });
        
        var data = {
            "rooms" : rooms,
            "action" : action,
            "bookingid" : $('#openedbookingid').val()
        };
        
        var event = thundashop.Ajax.createEvent('','doRoomsBookedAction',$(this), data);
        thundashop.common.showInformationBoxNew(event);
    },
    updateRoomActionSelection : function() {
        var type = $(this).val();
        $('.PmsManagement .roomactionstip').hide();
        $('.PmsManagement .roomactionstip[type="'+type+'"]').show();
        
    },
    showSaveButton : function() {
        $('.saveAddons').fadeIn();
    },
    checkallbookedrooms : function() {
        if($(this).is(':checked')) {
            $('.checkboxforbookedroom').attr('checked','checked');
        } else {
            $('.checkboxforbookedroom').attr('checked',null);
        }
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
        $('.excelexportoptions').toggle();
    },
    
    loadedituser : function() {
        var event = thundashop.Ajax.createEvent('','renderEditUserView', $(this), {
            bookingid : $('#openedbookingid').val()
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('#edituserview').html(res);
            $('#edituserview').show();
            $(".PmsManagement .edituserbox").fadeIn(function() {$(".edituserbox select").chosen(); });
        })
    },
    loadedituseronorder : function() {
        var btn = $(this);
        var event = thundashop.Ajax.createEvent('','editUserOnOrder', $(this), {
            bookingid : $('#openedbookingid').val(),
            orderid : $(this).attr('orderid')
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            var view = btn.closest('td').find('.edituseronorderview');
            view.html(res);
            view.find('.edituserbox').fadeIn(function() {$(".edituserbox select").chosen(); });
        });
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
        var box = $(this).closest('.dosendbox');
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
        table.find('.matrixdayprice').each(function() {
            $(this).val(val);
            $(this).keyup();
        });
    },
    updateRoomPriceExTaxesMatrix : function() {
        var table = $(this).closest('.roompricematrixtable');
        var val = $(this).val();
        table.find('.matrixdaypriceextax').each(function() {
            $(this).val(val);
            $(this).keyup();
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
        
        var event = thundashop.Ajax.createEvent('','updateOrder', $(this), data);
        thundashop.common.showInformationBoxNew(event, '', true);
    },
    doCreditOrder : function() {
        var confirmed = confirm("Are you sure you want to credit this order?");
        if(!confirmed) {
            return;
        }
        
        var data = {
            orderid : $(this).closest('tr[orderid]').attr('orderid'),
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
            "day" : $(this).attr('day'),
            "included" : $(this).attr('included'),
            "ordersincluded" : $(this).attr('ordersincluded')
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
            toSave[addonid].includedInRoomPrice = false;
            if($(this).find('.addonincludedinroomprice').hasClass('fa-check')) {
                toSave[addonid].includedInRoomPrice = true;
            }
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
    selectTab : function(event) {
        var tab = $(this);
        var id = $('#openedbookingid').val();
        $('.tab.selected').removeClass('selected');
        tab.addClass('selected');
        $('.tabarea').hide();
        var area = $(this).attr('area');
        $('.tabarea.'+area).show();
        localStorage.setItem('selectedbookinginfotab', area);
        if(event.cancelable) {
            var event = thundashop.Ajax.createEvent('','loadBookingDataArea', $(this), {
                "bookingid" : id,
                "area" : area
            });

            $('.tabarea.'+area).html('<div style="text-align:center; padding-top: 40px; padding-bottom: 40px;font-size: 50px;"><i class="fa fa-spin fa-spinner"></i></div>');

            thundashop.Ajax.postWithCallBack(event, function(result) {
                $('.tabarea.'+area).html(result);
            });
        }
    },
    
    reloadtab : function() {
        var id = $('#openedbookingid').val();
        var area = localStorage.getItem('selectedbookinginfotab');
        var event = thundashop.Ajax.createEvent('','loadBookingDataArea', $('#openedbookingid'), {
            "bookingid" : id,
            "area" : area
        });

        $('.tabarea.'+area).html('<div style="text-align:center; padding-top: 40px; padding-bottom: 40px;font-size: 50px;"><i class="fa fa-spin fa-spinner"></i></div>');

        thundashop.Ajax.postWithCallBack(event, function(result) {
            $('.tabarea.'+area).html(result);
        });
    },
    
    sendpaymentlink : function() {
        $(this).closest('td').find('.sendpaymentlinkbox').toggle();
    },
    sendreciept : function() {
        $(this).closest('td').find('.sendinvoicebox').toggle();
    },
    
    loadTakenRoomList : function() {
        var row = $(this).closest('tr');
        var bookingid = row.attr('bookingid');
        var roomid = row.attr('roomid');
        var panel = $(this).closest('.editmode');
        var event = thundashop.Ajax.createEvent('','loadTakenRoomList', $(this), {
            "bookingid" : bookingid,
            "roomid" : roomid,
            "start" : panel.find('[gsname="start"]').val() + " " + panel.find('[gsname="starttime"]').val(),
            "end" : panel.find('[gsname="end"]').val() + " " + panel.find('[gsname="endtime"]').val(),
            "itemid" : panel.find('[gsname="itemid"]').val()
        });
        
        thundashop.Ajax.postWithCallBack(event, function(result) {
            row.find('.tiparea').hide()
            if(result) {
                row.find('.tiparea').fadeIn();
                row.find('.tiparea').html(result);
            }
        });
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
        var view = $(this).closest('.edituserview');
        var btn = $(this);
        var userId = $(this).val();
        app.PmsManagement.doChangeUser(btn, view, userId);
    },
    
    doChangeUser : function(btn, view, userId) {
        var data = {
            "userid" : userId,
            "bookingid" : btn.attr('bookingid'),
            "orderid" : btn.attr('orderid'),
            "type" : view.find('.changeusertype').val()
        };

        var event = thundashop.Ajax.createEvent('','changeBookingOnEvent', btn, data);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            view.html(res);
            view.find('.edituserbox').show();
            $(".edituserbox select").chosen();
        });        
    },
    
    createNewUserOnBooking : function() {
        var event = thundashop.Ajax.createEvent('','renderEditUserView', $(this), {
            bookingid : $('#openedbookingid').val()
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('#edituserview').html(res);
            $("#edituserview").show();
            $(".PmsManagement .edituserbox").fadeIn(function() {$(".edituserbox select").chosen(); });
            $('.newuserbox_inner').show();$('.edituserbox_inner').hide();
        });
    },
    changecompanyonuser : function() {
        var data = {
            "companyid" : $(this).val(),
            "bookingid" : $(this).attr('bookingid')
        };
        var event = thundashop.Ajax.createEvent('','changeCompanyOnUser', $(this), data);
        thundashop.common.showInformationBoxNew(event, '', true);
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
            $('.informationbox-outer').scrollTop($('.informationbox-outer').scrollTop()+100);
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
            $('.additemtypeoptions').html(result);
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
        var guests = row.find('[gsname="numberofguests"]').val();
        for(var i = 1; i <= 20; i++) {
            if(i >= guests) {
                row.find('.guestrow_'+i).hide();
            } else {
                row.find('.guestrow_'+i).show();
            }
        }
    },
    toggleEditMode : function() {
        
        if($(this).hasClass('loadRoomItem')) {
            var td = $(this).closest('td');
            var event = thundashop.Ajax.createEvent('','loadEditBookingItem',$(this),{
                "bookingId" : $(this).closest('tr').attr('bookingid'),
                "pmsRoomId" : $(this).closest('tr').attr('roomid')
            });
            thundashop.Ajax.postWithCallBack(event, function(res){
                td.find('.changebookingitempanel').html(res);
                td.find('.changebookingitempanel').show();
            });
        } else {
            var row = $(this).closest('.roomattribute');
            var edit = row.find('.editmode');
            edit.show();
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