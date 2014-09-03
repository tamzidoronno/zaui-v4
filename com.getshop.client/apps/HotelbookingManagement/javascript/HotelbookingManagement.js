app.HotelbookingManagement = {
    
    refreshArxLog : function() {
        var event = thundashop.Ajax.createEvent('','loadArxLog',$('.HotelbookingManagement .arx_log'),{});
        thundashop.Ajax.postWithCallBack(event, function(result) {
            $('.HotelbookingManagement .arx_log').html(result);
        });
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
        var data = {
            "newRoom" : $('.HotelbookingManagement #moveRoomSelection').val(),
            "oldRoom" : $('.HotelbookingManagement #oldRoom').val(),
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
    initEvents : function() {
        $(document).on('click', '.HotelbookingManagement .edit_type', app.HotelbookingManagement.loadEditType);
        $(document).on('click', '.HotelbookingManagement .delete_type', app.HotelbookingManagement.deleteType);
        $(document).on('click', '.HotelbookingManagement .existingroomrow .fa-trash-o', app.HotelbookingManagement.deleteRoom);
        $(document).on('click', '.HotelbookingManagement .delete_reference', app.HotelbookingManagement.deleteReference);
        $(document).on('click', '.HotelbookingManagement .editroom', app.HotelbookingManagement.loadMoveRoom);
        $(document).on('click', '.HotelbookingManagement #doChangeRoom', app.HotelbookingManagement.updateRoom);
        $(document).on('keyup', '.HotelbookingManagement .adminfee', app.HotelbookingManagement.updateAdminFee);
    }
    
}

app.HotelbookingManagement.initEvents();