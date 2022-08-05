app.PmsAvailability = {
    gstoppbbooked : false,
    timeoutonguestadjustment : null,
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
        $(document).on('click', '.PmsAvailability .prioritizeRoom', this.prioritizeRoom);
        $(document).on('click', '.PmsAvailability .startbookingprocess', this.startbookingprocess);
        $(document).on('click', '.PmsAvailability .closeselectedforbookingwindow', this.stopbookingprocess);
        $(document).on('click', '.PmsAvailability .removeselectedroom', this.removeselectedroom);
        $(document).on('click', '.PmsAvailability .highlightspecificroom', this.highlightspecificroom);
        $(document).on('click', '.PmsAvailability .adjustguestcount', this.adjustguestcount);
//        $(document).on('click', '.PmsAvailability .contains_booking', this.showMenuBox);
    },
    adjustguestcount : function() {
        var type = $(this).attr('type');
        var btn = $(this);
        var count = parseInt($(this).closest('tr').find('.guestcount').text());
        if(type == "increase") {
            count++;
        } else {
            count--;
        }
        if(count <= 0) {
            count = 1;
        }
        $(this).closest('tr').find('.guestcount').text(count);
        
        if(app.PmsAvailability.timeoutonguestadjustment != null) {
            clearTimeout(app.PmsAvailability.timeoutonguestadjustment);
        }
        
        app.PmsAvailability.timeoutonguestadjustment = setTimeout(function() {
            var guestcounter = {};
            $('.selectedroom').each(function() {
                guestcounter[$(this).attr('roomid')] = $(this).find('.guestcount').text()
            });
             var event = thundashop.Ajax.createEvent('','updateGuestCounter',btn, {
                 "guestcounter" : guestcounter
             });
            thundashop.Ajax.postWithCallBack(event, function(res) {
                $('.selectedforbookingarea').html(res);
            });
        }, "1000");
        
    },
    highlightspecificroom : function() {
        var roomid = $(this).closest('tr').attr('roomid');
        var btn = $(this);
        var event = thundashop.Ajax.createEvent('','highlightRoom',$(this), {
            "roomid" : roomid
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            res = JSON.parse(res);
            btn.removeClass('ispinned');
            btn.removeClass('isfloating');
            
            if(res.success === "true") {
                btn.addClass('ispinned');
            } else {
                btn.addClass('isfloating');
            }
        });
    },
    removeselectedroom : function() {
        var roomid = $(this).closest('tr').attr('roomid');
        var event = thundashop.Ajax.createEvent('','removeSelectedRoom',$(this), {
            "roomid" : roomid
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.selectedforbookingarea').html(res);
            if($('.selectedroom').length == 0) {
                app.PmsAvailability.stopbookingprocess();
            }
        });
        $('.selectedforbooking[selectedpmsbookingroomid="'+roomid+'"]').removeClass('selectedforbooking');
    },
    stopbookingprocess : function() {
        $('.selectedforbookingarea').html("");
        $('.selectedforbookingarea').fadeOut();
        $('.selectedforbooking').removeClass('selectedforbooking');
    },
    startbookingprocess : function() {
        var btn = $(this);
        
        var first = $('.selectedforbooking').length == 0;
        $('.PmsAvailability .hatched').addClass('selectedforbooking');
        $('.PmsAvailability .hatched').addClass('tmpselectedforbooking');
        $('.PmsAvailability .hatched').removeClass('hatched');
        app.PmsAvailability.closeCloseRoomDialog();
        var event = thundashop.Ajax.createEvent('','addToMarkedForBookingArea',$(this), {
            "start" : btn.attr('start'),
            "end" : btn.attr('end'),
            "itemid" : btn.attr('itemid'),
            "first" : first
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            res = JSON.parse(res);
            $('.selectedforbookingarea').html(res.content);
            $('.PmsAvailability .selectedforbookingarea').fadeIn();
            $('.tmpselectedforbooking').attr('selectedpmsbookingroomid',res.roomid);
            $('.tmpselectedforbooking').removeClass('tmpselectedforbooking');
        });
    },
    prioritizeRoom : function() {
        var row = $(this).closest('tr');
        var event = thundashop.Ajax.createEvent('','prioritizeroom',$(this),{
            "roomid" : $(this).attr('roomid')
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            if(row.hasClass('prioritized')) {
                row.removeClass('prioritized');
                row.find('.progressState').html('waiting');
            } else {
                row.addClass('prioritized');
                row.find('.progressState').html('prioritized');
            }
        });
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
    needAdjustmentTop : function(element) {
        var elementTop = element.offset().top;
        var elementBottom = elementTop + element.outerHeight() - $(window).scrollTop();
        var viewportBottom = $(window).height();
        return parseInt(elementBottom) - parseInt(viewportBottom)
    },
    needAdjustmentRight : function(element) {
        var elementLeft = element.offset().left;
        var elementRight = elementLeft + element.outerWidth();
        var viewportWidth = $(window).width();
        return parseInt(elementRight) - parseInt(viewportWidth)
    },
    
    endMarking : function(event) {
        if(!app.PmsAvailability.isMarking) {
            return;
        }
        
        app.PmsAvailability.isMarking = false;
        var target = $(event.target);
        if(target.closest('.col_outer').hasClass('selectedforbooking')) {
            var roomId = target.closest('.col_outer').attr('selectedpmsbookingroomid');
            $('.removeselectedroom[roomid="'+roomId+'"]').click();
            $('.hatched').removeClass('hatched');
            return;
        }
        
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
            if($('.selectedforbooking').length > 0) {
                $('.PmsAvailability .startbookingprocess').click();
            }
            var moveUp = app.PmsAvailability.needAdjustmentTop(modal);
            if(moveUp > 0) {
                var newTop = modal.offset().top - moveUp - $(window).scrollTop();
                modal.css('top',newTop + "px");
            }
            var moveRight = app.PmsAvailability.needAdjustmentRight(modal);
            if(moveRight > 0) {
                var newLeft = modal.offset().left - moveRight;
                modal.css('left',newLeft + "px");
            }
            
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
        
    },
    onDragLeave : function(event) {
        if ($(event.target).hasClass('fullname')) {
            return;
        };
        
        $('.col_outer').removeClass('showdroppossible');
    },
    
    onDragDrop: function(event) {
        event.preventDefault();

        var newStartDate = app.PmsAvailability.findDragStartDate();
        if(newStartDate===undefined){
            return;
        }
        var outerTarget = event.target;
        if (!$(outerTarget).hasClass('col_outer')) {
            outerTarget = $(outerTarget).closest('.col_outer');
        }

        var data = {
            bookingId : $(app.PmsAvailability.currentlyDraggingElement.target).closest('.col_data').attr('bookingid'),
            roomId : $(app.PmsAvailability.currentlyDraggingElement.target).closest('.col_data').attr('roomid'),
            bookingitemid : $($('.showdroppossible')[0]).attr('bookingitemid'),
            newStartDate : newStartDate
        }

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