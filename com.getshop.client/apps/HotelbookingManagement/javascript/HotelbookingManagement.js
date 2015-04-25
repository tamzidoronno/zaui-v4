app.HotelbookingManagement = {
    
    refreshArxLog : function() {
//        var event = thundashop.Ajax.createEvent('','loadArxLog',$('.HotelbookingManagement .arx_log'),{});
//        thundashop.Ajax.postWithCallBack(event, function(result) {
//            $('.HotelbookingManagement .arx_log').html(result);
//        });
    },
    
    loadEditType : function() {
        var theapp = $(this).closest('.app');
        var id = theapp.find('#type').val();
        var event = thundashop.Ajax.createEvent('','reprint', $(this), { "typeId" : id });
        thundashop.Ajax.post(event);
    },
            
    deleteType : function() {
        var theapp = $(this).closest('.app');
        var id = theapp.find('#type').val();
        var event = thundashop.Ajax.createEvent('','deleteType', $(this), { "typeId" : id });
        thundashop.Ajax.post(event);
    },
            
    deleteRoom : function() {
        if(confirm('Are you sure, this will delete this room and it will not be possible to use anymore')) {
            var id = $(this).attr('roomid');
            var event = thundashop.Ajax.createEvent('','deleteRoom', $(this), { "roomid" : id });
            thundashop.Ajax.post(event);
        }
    },
    deleteReference : function() {
        if(confirm('Are you sure, this will delete this reference, the booking will then be removed from the system and available for reordering.')) {
            var id = $(this).attr('refid');
            var event = thundashop.Ajax.createEvent('','removeBookingReference', $(this), { "id" : id });
            thundashop.Ajax.post(event);
        }
    },
    updateRoom : function() {
        var rooms = {};
        $('.HotelbookingManagement .roomselection').each(function() {
            rooms[$(this).attr('oldroom')] = $(this).val();
        });
        var data = {
            "rooms" : rooms,
            "enddate" : $('.HotelbookingManagement .enddate').val(),
            "startdate" : $('.HotelbookingManagement .startdate').val(),
            "refid" : $('.HotelbookingManagement #referenceId').val()
        }
        var event = thundashop.Ajax.createEvent("", "moveRoom", $(this), data);
        thundashop.Ajax.post(event);
        thundashop.common.hideInformationBox();
    },
    loadMoveRoom : function() {
        var data = {
            "roomid" : $(this).attr('roomid'),
            "refid" : $(this).attr('refid')
        }
        var event = thundashop.Ajax.createEvent('','loadMoveRoom',$(this), data);
        thundashop.common.showInformationBox(event, "Move room");
    },
    updateAdminFee : function() {
        var val = $(this).val();
        var ref = $(this).closest('tr').attr('reservation');
        var event = thundashop.Ajax.createEvent("", "updateAdminFee", $(this), {"fee" : val, "ref" : ref});
        thundashop.Ajax.postWithCallBack(event, function() {
            
        });
    },
    doStopReference: function() {
        var data = {
            "bookingid" : $(this).attr('bookingid')
        }
        
        var event = thundashop.Ajax.createEvent("", "stopReference", $(this), data);
        thundashop.Ajax.post(event);
        
    },
    showStopReference: function() {
        var data = {
            "refid" : $(this).attr('refid')
        }
        var event = thundashop.Ajax.createEvent('','showStopReference',$(this), data);
        thundashop.common.showInformationBox(event, "Stop reference");
    },
    displayRoomBoxSettings : function() {
        var data = {
            startDate : $('input[gsname="startdate"').val(),
            endDate : $('input[gsname="enddate"').val(),
            room : $(this).attr('room')
        }
        
        var event =thundashop.Ajax.createEvent('','displayRoomBoxInfo',$(this),data);
        thundashop.common.showInformationBox(event);
    },
    tempGrantAccess : function() {
        var data = {
            refId : $(this).attr('refid'),
            room : $(this).attr('roomId')
        }
        var event = thundashop.Ajax.createEvent('','tempGrantAccess', $(this), data);
        thundashop.Ajax.postWithCallBack(event, function() {
            alert('Grant access sent, it can take up to one minute before access has been granted.');
        });
    },
    updatecontactinfo : function() {
        var row = $(this).closest('tr');
        var data = {
            "roomid" : row.attr('roomid'),
            "bookinguserinfo" : row.attr('bookinguserinfo'),
            "referenceid" : row.attr('referenceid'),
            "visitor" : {
                "name" : row.find('[name="name"]').val(),
                "phone" : row.find('[name="phone"]').val(),
                "email" : row.find('[name="email"]').val()
            }
        }
        
        var event = thundashop.Ajax.createEvent('','updateContactInfo',$(this), data);
        thundashop.Ajax.post(event, function() {
            thundashop.common.Alert('Success','User has been updated');
        }, false);
    },
    initEvents : function() {
        $(document).on('click', '.HotelbookingManagement .edit_type', app.HotelbookingManagement.loadEditType);
        $(document).on('click', '.HotelbookingManagement .delete_type', app.HotelbookingManagement.deleteType);
        $(document).on('click', '.HotelbookingManagement .existingroomrow .fa-trash-o', app.HotelbookingManagement.deleteRoom);
        $(document).on('click', '.HotelbookingManagement .delete_reference', app.HotelbookingManagement.deleteReference);
        $(document).on('click', '.HotelbookingManagement .editroom', app.HotelbookingManagement.loadMoveRoom);
        $(document).on('click', '.HotelbookingManagement #doChangeRoom', app.HotelbookingManagement.updateRoom);
        $(document).on('click', '.HotelbookingManagement .stop_reference', app.HotelbookingManagement.showStopReference);
        $(document).on('keyup', '.HotelbookingManagement .adminfee', app.HotelbookingManagement.updateAdminFee);
        $(document).on('click', '.HotelbookingManagement .stop_reference_action', app.HotelbookingManagement.doStopReference);
        $(document).on('click', '.HotelbookingManagement .go_live', app.HotelbookingManagement.confirmReservation);
        $(document).on('click', '.HotelbookingManagement .existingroomrow i', app.HotelbookingManagement.toggleOptions);
        $(document).on('click', '.HotelbookingManagement .roombox', app.HotelbookingManagement.displayRoomBoxSettings);
        $(document).on('click', '.HotelbookingManagement .tempgrantaccess', app.HotelbookingManagement.tempGrantAccess);
        $(document).on('click', '.HotelbookingManagement .fa-info-circle', app.HotelbookingManagement.showBookingInformation);
        $(document).on('click', '.HotelbookingManagement .updatebookingprice', app.HotelbookingManagement.updateBookingPrice);
        $(document).on('click', '.HotelbookingManagement .markaspayedfor', app.HotelbookingManagement.markAsPayedFor);
        $(document).on('click', '.HotelbookingManagement .updatecontactinfo', app.HotelbookingManagement.updatecontactinfo);
        $(document).on('click', '.HotelbookingManagement .changeroombutton', app.HotelbookingManagement.changeRoom);
    },
    
    changeRoom : function() {
        var row = $(this).closest('tr');
        var data = {
            "oldroom" : row.attr('roomid'),
            "bookinguserinfo" : row.attr('bookinguserinfo'),
            "referenceid" : row.attr('referenceid'),
            "roomId" : row.find('.roomSelected').val()
        }
        
        var event = thundashop.Ajax.createEvent('','changeRoom',$(this), data);
        thundashop.Ajax.post(event, function() {
            thundashop.common.Alert('Success','User has been updated');
        }, false);
    },
    
    markAsPayedFor : function() {
        var id = $(this).attr('bdata-id');
        var data = {
            "id" : id
        };
        var event = thundashop.Ajax.createEvent('','markAsPayed',$(this), data);
        thundashop.Ajax.post(event);
        thundashop.common.hideInformationBox();
    },
    
    updateBookingPrice : function() {
        var price = $('.bookingprice').val();
        var bookingid = $('.bookingprice').attr('bookingid');
        var event = thundashop.Ajax.createEvent('','updateBookingPrice', $(this), {
            "price" : price,
            "bookingid" : bookingid
        });
        thundashop.Ajax.postWithCallBack(event, function() {
            alert('Price is now updated');
        });
    },
    
    showBookingInformation : function() {
        
        var id = $(this).attr('booking-id');
        var event = thundashop.Ajax.createEvent('','showBookingInformation',$(this), {
            "bookingid" : id
        });
        
        thundashop.common.showInformationBox(event, 'Booking information');
    },
    toggleOptions : function() {
        if($(this).hasClass('fa-eraser')) {
            return;
        }
        if($(this).hasClass('active')) {
            $(this).removeClass('active');
        } else {
            $(this).addClass('active');
        }
    },
    
    confirmReservation: function() {
        var data = {
            bookingid : $(this).attr('bookingid')
        };
        
        var event = thundashop.Ajax.createEvent(null, "activateBooking", this, data);
        thundashop.Ajax.post(event);
    }
}

app.HotelbookingManagement.initEvents();