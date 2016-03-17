app.Settings = {
    init: function() {
        $(document).on('click', '.addLanguage', app.Settings.addLanguage)
        
    },
    
    addLanguage: function() {
        var ans = $('[gs_model_attr="language"]').val();
        var data = {
            value : ans
        }
        getshop.Settings.post(data, "addLanguage");
    },
    
    storeDeleted : function() {
        window.location = "https://www.getshop.com";
    }
};

app.Settings.init();