app.HotelbookingManagement = {
    
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
    
    initEvents : function() {
        $(document).on('click', '.HotelbookingManagement .edit_type', app.HotelbookingManagement.loadEditType);
        $(document).on('click', '.HotelbookingManagement .delete_type', app.HotelbookingManagement.deleteType);
        $(document).on('click', '.HotelbookingManagement .existingroomrow .fa-trash-o', app.HotelbookingManagement.deleteRoom);
        $(document).on('click', '.HotelbookingManagement .delete_reference', app.HotelbookingManagement.deleteReference);
    }
    
}

app.HotelbookingManagement.initEvents();