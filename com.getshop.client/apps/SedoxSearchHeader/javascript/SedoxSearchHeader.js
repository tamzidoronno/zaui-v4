app.SedoxSearchHeader = {
    init: function() {
        $(document).on('change', '.SedoxSearchHeader .sedox_file_search', app.SedoxSearchHeader.searchNow);
    },
    
    searchNow: function() {
        thundashop.common.goToPage("searchresult&searchword="+$(this).val());
    }
};

app.SedoxSearchHeader.init();