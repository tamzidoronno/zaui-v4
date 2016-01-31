app.SedoxSearchHeader = {
    init: function() {
        $(document).on('change', '.SedoxSearchHeader .sedox_file_search', app.SedoxSearchHeader.searchNow);
        $(document).on('keyup', '.SedoxSearchHeader .sedox_file_search', app.SedoxSearchHeader.keyUp);
    },
    
    keyUp: function(e) {
        var code = e.keyCode || e.which;
        if(code == 13) { //Enter keycode
            $('.SedoxSearchHeader .sedox_file_search').trigger("change");      
        }
    },
    
    searchNow: function() {
        thundashop.common.goToPage("searchresult&searchword="+$(this).val());
    }
};

app.SedoxSearchHeader.init();