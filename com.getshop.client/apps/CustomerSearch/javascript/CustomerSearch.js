app.CustomerSearch = {
    initEvents : function() {
        $(document).on('click','.CustomerSearch .search_button', app.CustomerSearch.search);
        $(document).on('keyup','.CustomerSearch .searchfield', app.CustomerSearch.search);
    },
    
    search : function(event) {
        if(event !== undefined) {
            if(event.type === "keyup" && event.keyCode !== 13) {
                return;
            }
        }
        var keyword = $('.CustomerSearch .searchfield').val();
        
        var event = thundashop.Ajax.createEvent('','search',$(this),{ "keyword" : keyword});
        thundashop.Ajax.postWithCallBack(event, function(data) {
            $('.CustomerSearch .search_result').html(data);
        });
    }
};

app.CustomerSearch.initEvents();