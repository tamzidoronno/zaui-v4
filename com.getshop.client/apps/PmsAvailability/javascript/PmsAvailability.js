app.PmsAvailability = {
    gstoppbbooked : false,
    isMarking : 0,
    startMarkingElement : null,
    init: function() {
        $(document).on('mousedown', '.PmsAvailability .gsdayevent', this.navigateToBooking);
        $(document).on('mouseover', '.PmsAvailability .gsdayevent', this.mouseOverConferenceRoom);
        $(document).on('mouseleave', '.PmsAvailability .gsdayevent', this.mouseOutConferenceRoom);
        $(document).on('mouseenter', '.PmsAvailability .contains_booking', this.mouseOver);
        $(document).on('mouseleave', '.PmsAvailability .contains_booking', this.mouseOut);
        $(document).on('click', '.PmsAvailability .markRoomClean', this.markRoomClean);
        $(document).on('click', '.PmsAvailability .displaywatinglist', this.showWaitinglistList);
        $(document).on('click', '.PmsAvailability .otherevents', this.loadEvent);
//        $(document).on('click', '.PmsAvailability .contains_booking', this.showMenuBox);
    },
    
    loadEvent : function() {
        var sourceid = $(this).attr('sourceid');
        var event = thundashop.Ajax.createEvent('','loadEventData', $(this), {
            "id" : sourceid
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.eventeditarea').html(res);
            $('.eventeditarea').show();
        });
    },
    showWaitinglistList : function() {
        $('.PmsAvailability .pmsavailabilitywaitinglist').slideDown();
    },
    markRoomClean : function() {
        var confirmed = confirm("Are you sure you want to mark this room as cleaned?");
        if(!confirmed) {
            return;
        }
        
        var cell = $(this);
        var event = thundashop.Ajax.createEvent('','markRoomCleaned',$(this), {
            "itemid" : cell.closest('.col_outer').attr('bookingitemid')
        });
        thundashop.Ajax.post(event);
        
    },
    navigateToBooking : function() {
        var area = $(this);
        if(area.attr('title')) {
            return;
        }
        var event = JSON.parse($(this).attr('event'));
        event = thundashop.Ajax.createEvent('','getIdForBooking', $(this), {
            "bookingengineid" : event.id
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            thundashop.common.goToPageLink('/?page=groupbooking&bookingId='+res);
        });
    },
    mouseOutConferenceRoom : function() {
        $('.titlearea').hide();
    },
    mouseOverConferenceRoom : function() {
        var area = $(this);
        if(area.attr('title')) {
            return;
        }
        var event = JSON.parse($(this).attr('event'));
        event = thundashop.Ajax.createEvent('','loadTitleOnConferenceRoom', $(this), {
            "bookingengineid" : event.id
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            area.find('.titlearea').html(res);
            area.find('.titlearea').fadeIn();
        });
    },
    
    onDragStart : function(event) {
        app.PmsAvailability.currentlyDraggingElement = event;
    },
    startMarking : function(event) {
        app.PmsAvailability.isMarking = true;
        app.PmsAvailability.startMarkingElement = $(event.target);
        app.PmsAvailability.markArea(event);
    },
    closeCloseRoomDialog : function() {
        $('.hatched').removeClass('hatched');
        $('.closemodalarea').hide();
    },
    endMarking : function(event) {
        if(!app.PmsAvailability.isMarking) {
            return;
        }
        
        app.PmsAvailability.isMarking = false;
        var modal = $('.closemodalarea');
        modal.css('left', event.clientX-100);
        modal.css('top', event.clientY+20);
        var bookingItemId = "";
        var start = null;
        var end = null;
        var roomId = app.PmsAvailability.startMarkingElement.closest('.col_data').attr('roomid');
        var bookingid = app.PmsAvailability.startMarkingElement.closest('.col_data').attr('bookingid');
        var isclosed = app.PmsAvailability.startMarkingElement.closest('.col_data').hasClass('closed');
        var cleaning = app.PmsAvailability.startMarkingElement.closest('.col_data').hasClass('cleaning');
        if(cleaning) {
            return;
        }
        
        $('.hatched').each(function() {
            if(!start) {
                start = $(this).attr('date');
            }
            end = $(this).attr('date');
            bookingItemId = $(this).attr('bookingitemid');
        });
        var event = thundashop.Ajax.createEvent('','loadMarkedAreaBox', $(event.target), {
            "start" : start,
            "end" : end,
            "bookingitemid" : bookingItemId,
            "roomid" : roomId,
            "bookingid" : bookingid,
            "isclosed" : isclosed
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            modal.html(res);
            modal.show();
            modal.draggable();
        });
    },
    
    markArea : function(event) {
        if(!app.PmsAvailability.isMarking) {
            return;
        }
        $('.curhatchedmarked').removeClass('curhatchedmarked');
        var current = $(event.target);
        current.addClass('curhatchedmarked');
        
        var nextElement = app.PmsAvailability.startMarkingElement.closest('.col_outer');
        var found = false;
        for(var i = 0; i < 100; i++) {
            if(nextElement.find('.curhatchedmarked').length > 0) {
                found = true;
            }
            var nextElement = nextElement.next();
        }
        if(!found) {
            return;
        }
        
        $('.hatched').removeClass('hatched');
        var nextElement = app.PmsAvailability.startMarkingElement.closest('.col_outer');
        var found = false;
        for(var i = 0; i < 100; i++) {
            nextElement.addClass('hatched');
            if(nextElement.find('.curhatchedmarked').length > 0) {
                break;
            }
            nextElement = nextElement.next();
        }
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
            if($(selector).find('.contains_booking.normal').length > 0) {
                $(selector).addClass('showdropnotpossible');
            } else {
                $(selector).addClass('showdroppossible');
            }
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
    
//    showMenuBox: function(event) {
//        var bookingId = $(this).attr('bookingId');
//        $('.PmsAvailability .bookingoverview').css('left', event.pageX + "px");
//        $('.PmsAvailability .bookingoverview').css('top', (event.pageY-300) + "px");
//        $('.PmsAvailability .bookingoverview').fadeIn();
//    
//        var data = {
//            bookingid : bookingId,
//            tab: ""
//        };
//        
//        var event = thundashop.Ajax.createEvent(null, "showBookingInformation", this, data);
//        event['synchron'] = true;
//        thundashop.Ajax.post(event, function(res) {
//            $('.PmsAvailability .bookingoverview .tab_content').html(res);
//        });
//    },
    
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