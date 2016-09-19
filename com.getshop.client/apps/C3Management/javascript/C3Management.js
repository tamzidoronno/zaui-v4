app.C3Management = {
    init: function() {
        $(document).on('click', '.addTimeRate', app.C3Management.addTimeRate)
        
    },
    
    addTimeRate: function() {
     
//        var ans = $('[gs_model_attr="language"]').val();
//        var data = {
//            value : ans
//        }
//        getshop.Settings.post(data, "addLanguage");
    }
};

app.C3Management.init();