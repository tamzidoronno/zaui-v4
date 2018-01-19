app.PmsBookingRoomView = {
    init: function() {
        $(document).on('click', '.PmsBookingRoomView .orderpreview .close', this.closePreview);
        $(document).on('click', '.PmsBookingRoomView .orderpreview .closebutton', this.closePreview);
        $(document).on('click', '.PmsBookingRoomView .orderpreview .continue', this.continueToBooking);
        $(document).on('click', '.PmsBookingRoomView .menuarea .menuentry', this.menuClicked);
        $(document).on('click', '.PmsBookingRoomView .bookinginformation .remove_guest', this.removeGuest);
        $(document).on('click', '.PmsBookingRoomView .bookinginformation .add_more_guests', this.addGuest);
        $(document).on('click', '.PmsBookingRoomView .payment_option_choice', this.paymentSelected);
        $(document).on('click', '.PmsBookingRoomView .pagenumber', this.changeLogIndex);
        $(document).on('click', '.PmsBookingRoomView .canUse', this.addSelectedClass);
        $(document).on('change', '.PmsBookingRoomView [gsname]', this.formChanged);
        $(document).on('change', '.PmsBookingRoomView .unitprice_changed', this.unitPriceChanged);
    },
    
    unitPriceChanged: function() {
        $(this).attr('manuallyset', 'true');
        app.PmsBookingRoomView.formChanged();
        app.PmsBookingRoomView.calculateTotal();
    },
    
    addSelectedClass: function() {
        $('.PmsBookingRoomView .gs_selected').removeClass('gs_selected');
        $(this).addClass('gs_selected');
        app.PmsBookingRoomView.formChanged();
        app.PmsBookingRoomView.updateAvailability(true);
    },
    
    changeLogIndex: function() {
        var scope = $('.PmsBookingRoomView .datarow.active');
        var data = {
            selectedTab : "log",
            id: scope.find('[gsname="id"]').val(),
            roomId: scope.find('[gsname="roomId"]').val(),
            index : $(this).attr('index')
        }
        
        var event = thundashop.Ajax.createEvent(null, "renderTabContent", this, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, app.PmsBookingRoomView.tabChanged, data);
    },
    
    calculateTotal: function() {
        var total = 0;
        
        $('.PmsBookingRoomView .item_prices input').each(function() {
            total += parseInt($(this).val());
        });
        
        $('.PmsBookingRoomView .item_prices .totalrow span').html(total);
    },
    
    formChanged: function() {
        $('.PmsBookingRoomView .datarow.active [gstype="submit"]').removeClass('disabled');
    },
    
    paymentSelected: function() {
        var appId = $(this).attr('payment');
        $('.payment_option_choice').removeClass('active');
        $('.payment_content').hide();
        $('.payment_content[payment="'+appId+'"]').show();
        $(this).closest('.payment_option_choice').addClass('active');
    },
    
    ordersTabChanged: function(res) {
        $('.PmsBookingRoomView .guestinformation[tab="orders"]').html(res);
        app.PmsSearchPaymentProcess.updatePaymentProcess();
        return false;
    },
        
    addGuest: function() {
        var cloned = $(this).closest('.bookinginformation').find('.guest_row.row_template').clone();
        
        var highest = 0;
        $('.guest_row').each(function() {
            var counter = parseInt($(this).attr('counter'));
            if (counter > highest) {
                highest = counter;
            }
        });
        
        highest++;
        cloned.removeClass('row_template');
        cloned.attr('counter', highest);
        cloned.css('display', 'block');
        
        var found = cloned.find('[temp_gsname="guestinfo_name"]'); found.removeAttr('temp_gsname'); found.attr('gsname', 'guestinfo_'+highest+'_name');
        var found = cloned.find('[temp_gsname="guestinfo_email"]'); found.removeAttr('temp_gsname'); found.attr('gsname', 'guestinfo_'+highest+'_email');
        var found = cloned.find('[temp_gsname="guestinfo_prefix"]'); found.removeAttr('temp_gsname'); found.attr('gsname', 'guestinfo_'+highest+'_prefix');
        var found = cloned.find('[temp_gsname="guestinfo_phone"]'); found.removeAttr('temp_gsname'); found.attr('gsname', 'guestinfo_'+highest+'_phone');
        
        $('.guestinfo').append(cloned);
        app.PmsBookingRoomView.formChanged();
    },
    
    removeGuest: function(e) {
        $(this).closest('.guest_row').remove();
        app.PmsBookingRoomView.formChanged();
    },
   
    updateAvailability: function(onlyPrices) {
        $('.PmsBookingRoomView .itemview').addClass('update_in_progress');
        
        var typeid = $('.PmsBookingRoomView .gs_selected').closest('.movetotype').attr('typeid');
        
        var data = {
            bookingid: $('.PmsBookingRoomView .itemview').attr('bookingid'),
            start : $('.PmsBookingRoomView .startdate').val(),
            end : $('.PmsBookingRoomView .enddate').val(),
            roomId: $('.PmsBookingRoomView .itemview').attr('roomid')
        };
        
        if (onlyPrices) {
            data.typeId = typeid;
        }
        
        var event = thundashop.Ajax.createEvent(null, "loaditemview", $('.PmsBookingRoomView'), data);
        
        event['synchron'] = true;
        thundashop.Ajax.post(event, function(res) {
            var changedPrices = [];
            
            $('.PmsBookingRoomView .itemview .pricesview input[manuallyset="true"]').each(function() {
                var data = {
                    date : $(this).closest('.pricerow').attr('date'),
                    price : $(this).val()
                };
                changedPrices.push(data);
            });

            if (onlyPrices === true) {
                var div = $('<div></div>');
                div.append(res);
                $('.PmsBookingRoomView .itemview .pricesview').replaceWith(div.find('.pricesview'));
            } else {
                $('.PmsBookingRoomView .itemview').html(res);
            }
            
            for (var i in changedPrices) {
                var data = changedPrices[i];
                var row = $('.PmsBookingRoomView .itemview .pricerow[date="'+data.date+'"]');
                row.find('input').attr('manuallyset', 'true');
                row.find('input').val(data.price);
            }

            app.PmsBookingRoomView.calculateTotal();

            $('.PmsBookingRoomView .itemview').removeClass('update_in_progress');
        });
    },
    
    contentSaved: function(res, target) {
        var scope = $(target).closest('.datarow_extended_content');
        scope.find('.bookingoverview_content_row').replaceWith(res);
        app.PmsBookingRoomView.checkForUnsettledAmount(scope);
        
        var event = thundashop.Ajax.createEvent(null, "refresh", target, {});
        thundashop.Ajax.post(event, function() {
            thundashop.common.closeModal();
        });
    },

    checkForUnsettledAmount: function(scope) {
        var data = {
            id: scope.find('[gsname="id"]').val(),
            roomId: scope.find('[gsname="roomId"]').val()
        }
        
        var event = thundashop.Ajax.createEvent(null, "hasUnsettledAmount", scope, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(res) {
            if (res === "YES") {
                $('.menuentry[tab="orders"]').addClass('unsettledamount'); 
            } else {
                $('.menuentry[tab="orders"]').removeClass('unsettledamount'); 
            }
        });
    },
   
    menuClicked: function() {
    
        var tab = $(this).attr('tab');
        var needAllSaved = $(this).attr('needAllSaved');
        
        if (needAllSaved === "true" && !$('.PmsBookingRoomView .datarow.active [gstype="submit"]').hasClass('disabled')) {
            alert(__f("Please save your changes before you go to payment"));
            return;
        }
    
        $('.PmsBookingRoomView .menuarea .menuentry').removeClass('active');
        $(this).addClass('active');
        
        $('.PmsBookingRoomView .workarea div[tab]').removeClass('active');
        $('.PmsBookingRoomView .workarea div[tab="'+tab+'"]').addClass('active');
        
        if ($(this).attr('clearTabContent') == "true") {
            var text = __f("Loading");
            $('.PmsBookingRoomView .workarea div[tab="'+tab+'"]').html("<div class='loaderspinner'><i class='fa fa-spin fa-spinner'></i><br/>"+text+"</div>");
        }
        
        var data = {
            selectedTab : tab,
            roomId : $(this).closest('.menuarea').attr('roomId'),
            id :  $(this).closest('.menuarea').attr('bookingEngineId'),    
        }
        
        var event = thundashop.Ajax.createEvent(null, "subMenuChanged", this, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, app.PmsBookingRoomView.tabChanged, data, true, true);
    },
    
    tabChanged: function(res, args) {
        if (res) {
            $('.PmsBookingRoomView .workarea div[tab="'+args.selectedTab+'"]').html(res);
        }
    },
    
    continueToBooking: function() {
        thundashop.common.goToPage("checkout&changeGetShopModule=salespoint");
    },
    
    closePreview: function() {
        $('.PmsBookingRoomView .orderpreview').fadeOut();
    }
}

app.PmsBookingRoomView.init();