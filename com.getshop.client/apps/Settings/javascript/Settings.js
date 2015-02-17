app.Settings = {
    init: function() {
        $(document).on('click', '.gss_button_area .addLanguage', app.Settings.addLanguage)
        
    },
    
    addLanguage: function() {
        var ans = prompt(__f("Enter the name of the lanaguge, example (se,no,en)"));
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