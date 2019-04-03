app.PmsPaymentProcess = {
    init: function() {
        $(document).on('click', '.PmsPaymentProcess .setrooms', this.setRoomsSelected);
        $(document).on('click', '.PmsPaymentProcess .createorder', this.createOrder);
        $(document).on('change', '.PmsPaymentProcess .item_count', this.updateTotalValue);
        $(document).on('change', '.PmsPaymentProcess .item_price', this.updateTotalValue);  
        $(document).on('click', '.PmsPaymentProcess .toggleshowdetailedroomview', this.toggleShowDetailedItemLines);  
        $(document).on('click', '.PmsPaymentProcess .toggledatefilter', this.togleDateFilter);  
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
            
            $(this).find('.room').each(function() {
                var totalForRoom = 0;
                $(this).find('.row.cartitemline').each(function() {
                    var price = $(this).find('.item_price').val();
                    var count = $(this).find('.item_count').val();    

                    if (price && count) {
                        overallTotal += (price * count);
                        totalForRoom += (price * count);
                    }
                });
                
                $(this).find('.totalforroom').html(totalForRoom);
            });
            
            
            $(this).find('.totalval').html(overallTotal);
        });
    },
    
    overlayClosed: function() {
        if (app && app.PmsBookingRoomView) {
            app.PmsBookingRoomView.refresh();
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
            var items = [];
            
            $(this).find('.cartitemline').each(function() {
                
                var item = {
                    createOrderOnProductId : $(this).attr('createorderonproductid'),
                    isAccomocation : $(this).attr('isaccomocation') == "1" ? true : false,
                    includedInRoomPrice : $(this).attr('includedinroomprice') == "1" ? true : false,
                    count : $(this).find('.item_count').val(),
                    price : $(this).find('.item_price').val(),
                    date : $(this).attr('date')
                }
                
                items.push(item);
            });
            
            var roomData = {
                roomId : roomId,
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