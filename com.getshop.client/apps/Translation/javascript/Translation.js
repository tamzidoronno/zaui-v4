if (typeof(GetShop) == "undefined") {
    GetShop = {};
}

GetShop.Translation = {
    init : function() {
        $('.Translation .save_button').live('click', GetShop.Translation.saveTranslation);
    },
       
    saveTranslation : function() {
        var data = {};
        $('.all_translation_rows input').each(function(e) {
            var val = $(this).val();
            var key = $(this).attr('key');
            val = thundashop.base64.encode(val);
            data[key] = val;
        });
    
        var event = thundashop.Ajax.createEvent("", "saveTranslation", $(this), data);
        thundashop.Ajax.post(event, GetShop.Translation.saved);
    },
    
    saved : function(data) {
        thundashop.common.Alert(__f("Saved"), __f("The translation has been saved"));
    }
}

GetShop.Translation.init();