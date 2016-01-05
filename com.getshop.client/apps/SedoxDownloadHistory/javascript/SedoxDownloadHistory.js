app.SedoxDownloadHistory = {
    init: function() {
        $(document).on('change', '.SedoxDownloadHistory .filterTextBox', app.SedoxDownloadHistory.applyFilter)
    },
    
    applyFilter: function() {
        var data = {
            filterText : $(this).val()
        }
        
        var event = thundashop.Ajax.createEvent(null, "setFilter", this, data);
        thundashop.Ajax.post(event);
    }
}

app.SedoxDownloadHistory.init();