app.SedoxProductSearcher = {
    init: function() {
        $(document).on('change', '.SedoxProductSearcher input', app.SedoxProductSearcher.search);
    },
            
    search: function() {
        data = {
            searchKey : $(this).val()
        };
        console.log(data);
        var event = thundashop.Ajax.createEvent(null, "searchProduct", this, data);
        thundashop.Ajax.post(event);
    }
};

app.SedoxProductSearcher.init();