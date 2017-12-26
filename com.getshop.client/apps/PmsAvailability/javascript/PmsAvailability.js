app.PmsAvailability = {
    init: function() {
        $(document).on('mouseenter', '.PmsAvailability .contains_booking', this.mouseOver);
        $(document).on('mouseleave', '.PmsAvailability .contains_booking', this.mouseOut);
        $(document).on('click', '.PmsAvailability .contains_booking', this.showMenuBox);
        $(document).on('click', '.PmsAvailability .canUse', this.addSelectedClass);
        $(document).on('change', '.PmsAvailability .unitprice_changed', this.unitPriceChanged);
    },
    
    unitPriceChanged: function() {
        $(this).attr('manuallyset', 'true');
        app.PmsSearchBooking.formChanged();
        app.PmsAvailability.calculateTotal();
    },
    
    calculateTotal: function() {
        var total = 0;
        
        $('.PmsAvailability .item_prices input').each(function() {
            total += parseInt($(this).val());
        });
        
        $('.PmsAvailability .item_prices .totalrow span').html(total);
    },
    
    updateAvailability: function(onlyPrices) {
        $('.PmsAvailability .itemview').addClass('update_in_progress');
        
        var typeid = $('.datarow.active .PmsAvailability .gs_selected').closest('.movetotype').attr('typeid');
        
        var data = {
            bookingid: $('.PmsAvailability .itemview').attr('bookingid'),
            start : $('.PmsAvailability .startdate').val(),
            end : $('.PmsAvailability .enddate').val(),
            roomId: $('.PmsAvailability .itemview').attr('roomid')
        };
        
        if (onlyPrices) {
            data.typeId = typeid;
        }
        
        var event = thundashop.Ajax.createEvent(null, "loaditemview", $('.PmsAvailability'), data);
        
        event['synchron'] = true;
        thundashop.Ajax.post(event, function(res) {
            var changedPrices = [];
            
            $('.PmsAvailability .itemview .pricesview input[manuallyset="true"]').each(function() {
                var data = {
                    date : $(this).closest('.pricerow').attr('date'),
                    price : $(this).val()
                };
                changedPrices.push(data);
            });

            if (onlyPrices === true) {
                var div = $('<div></div>');
                div.append(res);
                $('.PmsAvailability .itemview .pricesview').replaceWith(div.find('.pricesview'));
            } else {
                $('.PmsAvailability .itemview').html(res);
            }
            
            for (var i in changedPrices) {
                var data = changedPrices[i];
                var row = $('.PmsAvailability .itemview .pricerow[date="'+data.date+'"]');
                row.find('input').attr('manuallyset', 'true');
                row.find('input').val(data.price);
            }

            app.PmsAvailability.calculateTotal();

            $('.PmsAvailability .itemview').removeClass('update_in_progress');
        });
    },
    
    addSelectedClass: function() {
        $('.PmsAvailability .gs_selected').removeClass('gs_selected');
        $(this).addClass('gs_selected');
        app.PmsSearchBooking.formChanged();
        app.PmsAvailability.updateAvailability(true);
    },
    
    showMenuBox: function(event) {
        var bookingId = $(this).attr('bookingId');
        $('.PmsAvailability .bookingoverview').css('left', event.pageX + "px");
        $('.PmsAvailability .bookingoverview').css('top', (event.pageY-300) + "px");
        $('.PmsAvailability .bookingoverview').fadeIn();
    
        var data = {
            bookingid : bookingId,
            tab: ""
        };
        
        var event = thundashop.Ajax.createEvent(null, "showBookingInformation", this, data);
        event['synchron'] = true;
        thundashop.Ajax.post(event, function(res) {
            $('.PmsAvailability .bookingoverview .tab_content').html(res);
        });
    },
    
    mouseOver: function() {
        var bookingId = $(this).attr('bookingId');
        $('.PmsAvailability .contains_booking[bookingid="'+bookingId+'"]').addClass('mouseover');
    },
    
    mouseOut: function() {
        var bookingId = $(this).attr('bookingId');
        $('.PmsAvailability .contains_booking[bookingid="'+bookingId+'"]').removeClass('mouseover');
    }
    
}

app.PmsAvailability.init();