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
        $(document).on('keyup', '.PmsPaymentProcess .percentcalculator', this.recalcByPercent);  
        $(document).on('change', '.PmsPaymentProcess .totalvalcalculator', this.recalcByTotalVal);  
        $(document).on('change', '.PmsPaymentProcess .searchfortabs', this.searchForTabs);  
        $(document).on('click', '.PmsPaymentProcess .add_pos_tab', this.addPosTab);  
        $(document).on('click', '.PmsPaymentProcess .removeconference', this.removeConference);  
        $(document).on('change', '.PmsPaymentProcess .sendtobookerdropdown', this.changeSendToRecipient);  
        $(document).on('click', '.PmsPaymentProcess .sendrequestbutton', this.sendRequest);  
    },
    
    removeConference: function() {
        $(this).closest('.cart_room_summary').remove();
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
                $('.PmsPaymentProcess').find('input.totalval').val(totalForRoom);
            });
            
            
            overallTotal = Math.round(overallTotal * 100) / 100;
            org_overallTotal = Math.round(org_overallTotal * 100) / 100;
            
            $(this).find('.totalval').html(overallTotal);
            $(this).find('.totalvalforselect').html(org_overallTotal);
        });
    },
    
    overlayClosed: function() {
        if (app && app.PmsBookingRoomView) {
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
        
        var rooms = [];
        
        $(app).find('.room').each(function() {
            var roomId = $(this).attr('roomid');
            var conferenceId = $(this).attr('conferenceId');
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
            rooms : rooms
        };
                
        var event = thundashop.Ajax.createEvent(null, "createOrder", this, data);
        thundashop.Ajax.post(event);
    }
};

app.PmsPaymentProcess.init();