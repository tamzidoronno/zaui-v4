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
        var type = $('[name="userid_in_body"]').attr('usertype');
        if (type && ( type === "50" || type === "100")) {
            thundashop.common.goToPage("4f35e47e-9905-4f60-82d2-ea63e25e3316&searchword="+$(this).val());
        } else {
            thundashop.common.goToPage("searchresult&searchword="+$(this).val());
        }
    }
};

app.SedoxSearchHeader.init();