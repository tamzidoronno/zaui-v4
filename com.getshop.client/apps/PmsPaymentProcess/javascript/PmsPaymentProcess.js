useNewGroupBookingView = false;
app.PmsPaymentProcess = {
    init: function() {
        $(document).on('click', '.PmsPaymentProcess .setrooms', this.setRoomsSelected);
        $(document).on('click', '.PmsPaymentProcess .createorder', this.createOrder);
        $(document).on('change', '.PmsPaymentProcess .item_count', this.updateTotalValue);
        $(document).on('change', '.PmsPaymentProcess .item_price', this.updateTotalValue);  
        $(document).on('change', '.PmsPaymentProcess .row_checkbox', this.updateTotalValue);  
        $(document).on('click', '.PmsPaymentProcess .toggleshowdetailedroomview', this.toggleShowDetailedItemLines);  
        $(document).on('click', '.PmsPaymentProcess .toggledatefilter', this.togleDateFilter);  
        $(document).on('click', '.PmsPaymentProcess .toggleselector', this.toggleSelector);  
        $(document).on('click', '.PmsPaymentProcess .showcalc', this.toggleCalc);  
        $(document).on('click', '.PmsPaymentProcess .updateorder', this.updateOrder);  
        $(document).on('keyup', '.PmsPaymentProcess .percentcalculator', this.recalcByPercent);  
        $(document).on('change', '.PmsPaymentProcess .totalvalcalculator', this.recalcByTotalVal);  
        $(document).on('change', '.PmsPaymentProcess .searchfortabs', this.searchForTabs);  
        $(document).on('click', '.PmsPaymentProcess .add_pos_tab', this.addPosTab);  
        $(document).on('click', '.PmsPaymentProcess .removeconference', this.removeConference);  
        $(document).on('change', '.PmsPaymentProcess .sendtobookerdropdown', this.changeSendToRecipient);  
        $(document).on('click', '.PmsPaymentProcess .sendrequestbutton', this.sendRequest);  
        $(document).on('keyup', '.PmsPaymentProcess #messagetosend', this.checkIfPaymentLinkVariableIsFound);  
        $(document).on('change', '.PmsPaymentProcess .searchforaddonvalue', this.searchForAddons);  
        $(document).on('click', '.PmsPaymentProcess .searchforaddonsbutton', this.searchForAddons);  
        $(document).on('click', '.PmsPaymentProcess .addextraaddons', this.addExtraAddons);  
    },
    
    addExtraAddons: function() {
        var productid = $(this).closest('.cartitemline').attr('createorderonproductid');
        var count = $(this).closest('.cartitemline').find('.item_count').val();
        var name = $(this).closest('.cartitemline').find('.textOnOrder').val();
        var price =  $(this).closest('.cartitemline').find('.item_price').val();
        
        var today = new Date();
        var dd = String(today.getDate()).padStart(2, '0');
        var mm = String(today.getMonth() + 1).padStart(2, '0'); //January is 0!
        var yyyy = today.getFullYear();

        today = dd + '-' + mm + '-' + yyyy;

        var roomDiv = $('.PmsPaymentProcess .extra_summary [roomid="virtual"]');
        
        var cartItemRow = $("<div/>");
        cartItemRow.addClass('row');
        cartItemRow.addClass('cartitemline');
        cartItemRow.attr('date', today);
        cartItemRow.attr('createorderonproductid', productid);
        cartItemRow.attr('isaccomocation', 'false');
        cartItemRow.attr('includedinroomprice', 'false');
        
        cartItemRow.append("<div class='col' style='margin-right: 10px; width: 20px; padding-top: 5px;'><input class='row_checkbox' style='font-size: 20px; height: 20px; width: 20px;' type='checkbox' checked='true'/></div>");
        cartItemRow.append("<div class='col date' style='vertical-align: top;'>"+today+"</div>");
        cartItemRow.append("<div class='col count' style='vertical-align: top;'><input class='gsniceinput1 item_count' orgcount='"+count+"' value='"+count+"'/> x </div>");
        cartItemRow.append("<div class='col price' style='vertical-align: top;'><input class='gsniceinput1 item_price' orgvalue='"+price+"' value='"+price+"'/></div>");
                                
        
        var productName = $("<div class='col productname' style='vertical-align: top;line-height: 36px;'></div>");
        productName.append("<input class='gsniceinput1 textOnOrder' type='hidden' orgvalue='"+name+"' value='"+name+"'/>");
        productName.append("<input class='gsniceinput1 addonId' type='hidden' orgvalue='' value=''/>");
        productName.append(name);
        
        cartItemRow.append(productName);
        
        roomDiv.append(cartItemRow);

        $('.extra_div_outer').show();
        $('.PmsPaymentProcess .searchforaddonvalue').val("");
        $('.PmsPaymentProcess .searchforaddonsarea').hide();
        
        app.PmsPaymentProcess.updateTotalValue();
    },
    
    searchForAddons: function() {
        var event = thundashop.Ajax.createEvent(null, "searchForAddons", this, {
            searchvalue : $('.PmsPaymentProcess .searchforaddonvalue').val()
        });
        
        event['synchron'] = true;
        
        $('.PmsPaymentProcess .searchforaddonsarea').show();
        $('.PmsPaymentProcess .searchforaddonsarea').html('fa fa-spin fa-spinner');
        
        thundashop.Ajax.post(event, function(res) {
            $('.PmsPaymentProcess .searchforaddonsarea').html(res);
        });
    },
    
    updateOrder: function() {
        
        var data = {
            bookingid : $(this).attr('bookingid'),
            orderid : $(this).attr('orderid')
        };
        
        var event = thundashop.Ajax.createEvent(null, "updateOrder", this, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(res) {
            $('.gsoverlay3.active').click();
        });
    },
    
    removeConference: function() {
        $(this).closest('.cart_room_summary').remove();
    },
    
    checkIfPaymentLinkVariableIsFound : function() {
        var msg = $('#messagetosend').val();
        if(msg.indexOf("{paymentlink}") !== -1) {
            $('.haspaymentlink').show();
            $('.nopaymentlink').hide();
        } else {
            $('.nopaymentlink').show();
            $('.haspaymentlink').hide();
        }
    },
    addPosTab: function() {
        var me = $(this);
        var event = thundashop.Ajax.createEvent(null, "getPosTabContent", this, {
            conferenceid : $(this).attr('conferenceid')
        });
        
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(res) {
            var addedConferenceId = $("<div>"+res+"</div>").find('.room').attr('conferenceid');
            
            me.closest('.app').find('.postabs').append(res);
            app.PmsPaymentProcess.updateTotalValue();
            $('.conferenceuserhint[conferenceid="'+addedConferenceId+'"]').remove();
        });
        
        $(me).closest('.app').find('.search_tab_conference_result').hide();
    },
    
    searchForTabs: function() {
        var searchWord = $(this).val();
        var me = this;
        
        var event = thundashop.Ajax.createEvent(null, "searchForConferences", this, {
            searchWord : searchWord
        });
        
        event['synchron'] = true;
        
        $(me).closest('.app').find('.search_tab_conference_result').show();
        
        thundashop.Ajax.post(event, function(res) {
            $(me).closest('.app').find('.search_tab_conference_result').html(res);
        });
    },
    
    sendRequest : function() {
         var form = $(this).closest('.sendrequestbox');
        var result = thundashop.framework.createGsArgs(form);
        result.message = $('#messagetosend').val();
        var event = thundashop.Ajax.createEvent('','sendPaymentLinkRequest',$(this), result);
        var btn = $(this);
        btn.append('<i class="fa fa-spinner fa-spin"></i>');
        thundashop.Ajax.postWithCallBack(event, function(res) {
            setTimeout(function() {
                btn.fadeOut(function() {
                    form.find('.sentmessage').show();
                });
            }, "500");
        });
    },
    changeSendToRecipient : function() {
        var id = $(this).val();
        
        var objects = $('#customersobject').val();
        objects = JSON.parse(objects);
        for(var k in objects) {
            var obj = objects[k];
            var body = $(this).closest('.sendpaymentrequestsbody');
            if(obj.id === id) {
                body.find("[gsname='email']").val(obj.email);
                body.find("[gsname='prefix']").val(obj.prefix);
                body.find("[gsname='phone']").val(obj.phone);
            }
        }
    },
    toggleCalc: function() {
        var filter = $(this).closest('.app').find('.percentfilter');
        if ($(filter).is(':visible')) {
            $(filter).slideUp();
        } else {
            $(filter).slideDown();
        }
    },
    
    recalcByPercent: function() {
        var lines = $(this).closest('.app').find('.cartitemline');
        var factor = $(this).val() / 100;
        
        $(lines).each(function() {
            var active = $(this).find('.row_checkbox').prop('checked');
            
            if (!active) {
                return;
            }
        
            var itemLine = $(this).find('.item_price');
            var orgPrice = itemLine.attr('orgvalue');
            var newPrice = orgPrice * factor;
            newPrice = Math.round(newPrice * 100) / 100;
            itemLine.val(newPrice);
        });
        
        app.PmsPaymentProcess.updateTotalValue();
    },
    
    recalcByTotalVal: function() {
        var lines = $(this).closest('.app').find('.cartitemline');
        var newTotal = $(this).val();
        var total = 0;
        var toCorrectOn = null;
        
        $(lines).each(function() {
            var active = $(this).find('.row_checkbox').prop('checked');
            
            if (!active) {
                return;
            }
            
            if (toCorrectOn == null)
                toCorrectOn = this;
        
            var itemLine = $(this).find('.item_price');
            var orgPrice = itemLine.attr('orgvalue');
            var newPrice = orgPrice * $(this).find('.item_count').val();
            total += newPrice;
        });
        
        if (total != 0) {
            var percent = (newTotal / total) * 100;
            $(this).closest('.app').find('.percentcalculator').val(percent);
            $('.PmsPaymentProcess .percentcalculator').trigger('keyup');
        }
        
        
        
        var corrections = parseFloat(newTotal) - parseFloat($(this).val());
        var toCorrectOn = $(toCorrectOn).find('.item_price');
        var newCorrectedValue = parseFloat(toCorrectOn.val()) + parseFloat(corrections);
        newCorrectedValue = Math.round(newCorrectedValue * 100) / 100;
        toCorrectOn.val(newCorrectedValue);
        console.log(corrections, toCorrectOn, newCorrectedValue);
        app.PmsPaymentProcess.updateTotalValue();
    },
    
    toggleSelector: function() {
        if ($(this).hasClass('active')) {
            $(this).removeClass('active');
        } else {
            $(this).addClass('active');
        }
        
        var activeButtons = $(this).closest('.app').find('.shop_button.active');
        
        if (activeButtons.length == 0) {
            $('.cartitemline').find('.row_checkbox').prop('checked', true);
            $('.PmsPaymentProcess .detaileditemlines').slideUp();
        } else {
            $('.cartitemline').find('.row_checkbox').prop('checked', false);
            $(activeButtons).each(function() {
                if ($(this).hasClass('toggleaccomodation')) {
                    $('.cartitemline.accomocation').find('.row_checkbox').prop('checked', true);
                }
                if ($(this).hasClass('toggleaddoninc')) {
                    $('.cartitemline.included').find('.row_checkbox').prop('checked', true);
                }
                if ($(this).hasClass('toggleaddonsex')) {
                    $('.cartitemline.not_included').find('.row_checkbox').prop('checked', true);
                }
            });
            
            $('.PmsPaymentProcess .detaileditemlines').slideDown();
        }
        
        app.PmsPaymentProcess.updateTotalValue();
    },
    
    refresh: function() {
        var me = $('.PmsPaymentProcess.app');
        
        var event = thundashop.Ajax.createEvent(null, "render", me, {});
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(res) {
            $(me).html(res);
        })
    },
    
    togleDateFilter: function() {
        var div = $(this).closest('.app').find('.datefilter');
        if (div.is(':visible')) {
            div.slideUp();
        } else {
            div.slideDown();
        }
    },
    
    toggleShowDetailedItemLines: function() {
        var div = $(this).closest('.room').find('.detaileditemlines');
        if (div.is(':visible')) {
            div.slideUp();
        } else {
            div.slideDown();
        }
    },
    
    updateTotalValue: function() {
        $('.PmsPaymentProcess .cart_room_summary').each(function() {
            var overallTotal = 0;
            var org_overallTotal = 0;
            
            $(this).find('.room').each(function() {
                var totalForRoom = 0;
                var org_totalForRoom = 0;
                $(this).find('.row.cartitemline').each(function() {
                    var active = $(this).find('.row_checkbox').is(':checked');
                    if (!active)
                        return;
                    
                    var price = $(this).find('.item_price').val();
                    var count = $(this).find('.item_count').val();    
                   
                    var org_price = $(this).find('.item_price').attr('orgvalue');
                    var org_count = $(this).find('.item_count').attr('orgcount');    

                    if (price && count) {
                        overallTotal += (price * count);
                        totalForRoom += (price * count);
                    }
                    
                    if (org_price && org_count) {
                        org_overallTotal += (org_price * org_count);
                        org_totalForRoom += (org_price * org_count);
                    }
                });
            
                totalForRoom = Math.round(totalForRoom * 100) / 100;
                $(this).find('.totalforroom').html(totalForRoom);
//                $('.PmsPaymentProcess').find('input.totalval').val(totalForRoom);
            });
            
            
            overallTotal = Math.round(overallTotal * 100) / 100;
            org_overallTotal = Math.round(org_overallTotal * 100) / 100;
            
            $(this).find('.totalval').html(overallTotal);
            $(this).find('.totalvalforselect').html(org_overallTotal);
        });
    },
    
    overlayClosed: function() {
        if(useNewGroupBookingView && app && app.PmsBookingRoomView) {
            thundashop.framework.reloadOverLayType2();
        } else if (app && app.PmsBookingRoomView) {
            app.PmsBookingRoomView.refresh();
        }
        
        if (app && app.PmsInvoicing) {
            app.PmsInvoicing.refresh();
        }
    },
    
    setRoomsSelected: function() {
        var checkboxes = $(this).closest('.app').find('.roomcheckbox');
        console.log(checkboxes);
        var roomIds = [];
        for (var i in checkboxes) {
            var checkbox = checkboxes[i];
            
            if (typeof checkbox == "object" && $(checkbox).is(':checked')) {
                roomIds.push($(checkbox).val());
            }
        }
        
        var data = {
            setRoomIds : roomIds
        };
        
        var event = thundashop.Ajax.createEvent(null, 'setRoomIds', $(this), data);
        thundashop.Ajax.post(event);
    },
    
    createOrder: function() {
        var app = $(this).closest('.app');
        var conferenceOfBooking = null;
        var rooms = [];

        $(app).find('.room').each(function() {
            var roomId = $(this).attr('roomid');
            var conferenceId = $(this).attr('conferenceId');
            if (conferenceId != null){
                conferenceOfBooking = conferenceId;
            }
            var items = [];
            
            $(this).find('.cartitemline').each(function() {
                var active = $(this).find('.row_checkbox').is(':checked');
                
                if (!active) {
                    return;
                }
                
                var item = {
                    createOrderOnProductId : $(this).attr('createorderonproductid'),
                    isAccomocation : $(this).attr('isaccomocation') == "1" ? true : false,
                    includedInRoomPrice : $(this).attr('includedinroomprice') == "1" ? true : false,
                    count : $(this).find('.item_count').val(),
                    price : $(this).find('.item_price').val(),
                    textOnOrder : $(this).find('.textOnOrder').val(),
                    addonId : $(this).find('.addonId').val(),
                    date : $(this).attr('date'),
                    cartItemId : $(this).attr('cartitemid')
                }
                
                items.push(item);
            });
            
            var roomData = {
                roomId : roomId,
                conferenceId : conferenceId,
                items: items
            }
            
            rooms.push(roomData);
        });
        
        var data = {
            rooms : rooms,
            conference : conferenceOfBooking
        };
        
        var event = thundashop.Ajax.createEvent(null, "createOrder", this, data);
        thundashop.Ajax.post(event);
    }
};

app.PmsPaymentProcess.init();