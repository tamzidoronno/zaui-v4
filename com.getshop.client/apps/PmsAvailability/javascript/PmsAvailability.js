app.PmsAvailability = {
    init: function() {
        $(document).on('mouseenter', '.PmsAvailability .contains_booking', this.mouseOver);
        $(document).on('mouseleave', '.PmsAvailability .contains_booking', this.mouseOut);
        $(document).on('click', '.PmsAvailability .contains_booking', this.showMenuBox);
        
    },
    
    onDragStart : function(event) {
        app.PmsAvailability.currentlyDraggingElement = event;
    },
    
    onDragOver : function(event) {
        event.preventDefault();
        var numberOfBlocks = $(app.PmsAvailability.currentlyDraggingElement.target).find('.bookername').attr('numberofblocks');
        
        var targetToUse = event.target;
        if (!$(targetToUse).hasClass('col_outer')) {
            targetToUse = $(targetToUse).closest('.col_outer');
        }
        
        var calculatedColNumber =  parseInt($(targetToUse).attr('col')) + Math.floor(event.offsetX / $(targetToUse).outerWidth());
        
        
        var offset = Math.floor(app.PmsAvailability.currentlyDraggingElement.offsetX / $(targetToUse).outerWidth());
        var col = calculatedColNumber - offset;
        var row = parseInt($(targetToUse).attr('row'));
        var typeid = $(targetToUse).attr('typeid');
        
        var end = col + parseInt(numberOfBlocks);
        
        var cols = "Calc: " + calculatedColNumber +  ", Offset: " + offset + "Row: " + row + ", Outer width: " + $(targetToUse).outerWidth() +  " | ";
        for (var i=col; i<end; i++) {
            var selector = '.col_outer[typeid="'+typeid+'"][row="'+row+'"][col="'+i+'"]';
            cols += " "+i;
            $(selector).addClass('showdroppossible');
        }
        
        console.log(cols);
        console.log("================================");
    },
    
    onDragLeave : function(event) {
        if ($(event.target).hasClass('fullname')) {
            return;
        };
        
        console.log(event.target);
        
        $('.col_outer').removeClass('showdroppossible');
    },
    
    onDragDrop: function(event) {
        event.preventDefault();
        
        var outerTarget = event.target;
        if (!$(outerTarget).hasClass('col_outer')) {
            outerTarget = $(outerTarget).closest('.col_outer');
        }
        
        var data = {
            bookingId : $(app.PmsAvailability.currentlyDraggingElement.target).closest('.col_data').attr('bookingid'),
            roomId : $(app.PmsAvailability.currentlyDraggingElement.target).closest('.col_data').attr('roomid'),
            bookingitemid : $($('.showdroppossible')[0]).attr('bookingitemid'),
            newStartDate : app.PmsAvailability.findDragStartDate()
        }
        
        console.log(data);
        var event = thundashop.Ajax.createEvent(null, "setNewStartDateAndRoomId", event.target, data);
        thundashop.Ajax.post(event);
    },
    
     findDragStartDate: function() {
        return $($('.showdroppossible')[0]).attr('date');
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