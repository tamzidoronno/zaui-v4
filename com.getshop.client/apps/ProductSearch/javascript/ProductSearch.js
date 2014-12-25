app.ProductSearch = {
    init: function() {
        $(document).on('change', '.ProductSearch .searchField input', app.ProductSearch.search);
    },
    
    search: function() {
        var data = {
            searchWord : $('.ProductSearch .searchField input').val()
        };
        
        var event = thundashop.Ajax.createEvent(null, "search", this, data);
        var ajaxLink = "/index.php?page=productsearch&searchWord="+data.searchWord;
        window.history.pushState({url: ajaxLink, ajaxLink: ajaxLink}, "Title", ajaxLink);
        thundashop.Ajax.post(event);
    }
}

app.ProductSearch.init();