app.HotelbookingCleaning = {
    markReady : function() {
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