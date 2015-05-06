app.HotelbookingCleaning = {
    markReady : function() {
        var confirmed = confirm("Please confirm that this room has been cleaned.");
        if(!confirmed) {
            return;
        }
        var data = {
            "roomId" : $(this).attr('roomId')
        }
        
        var event = thundashop.Ajax.createEvent('','markRoom', $(this), data);
        thundashop.Ajax.post(event);
    },
    
    initEvents : function() {
        $(document).on('click', '.HotelbookingCleaning .markroomasready', app.HotelbookingCleaning.markReady); 
    }
};

app.HotelbookingCleaning.initEvents();