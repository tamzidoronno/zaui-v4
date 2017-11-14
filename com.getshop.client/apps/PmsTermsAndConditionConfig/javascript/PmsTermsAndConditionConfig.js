app.PmsTermsAndConditionConfig = {
    init : function() {
        $(document).on('click', '.PmsTermsAndConditionConfig #contractfield', function() {
            thundashop.common.activateCKEditor('contractfield', {
                autogrow : false
            });
        });
        $(document).on('click', '.PmsTermsAndConditionConfig .changeLanguage', app.PmsTermsAndConditionConfig.changeLanguage);
    },
    changeLanguage : function() {
        var lang = $(this).val();
        var event = thundashop.Ajax.createEvent('','changeLanguage',$(this), {
            "lang" : lang
        });
        thundashop.Ajax.post(event);
    }

};

app.PmsTermsAndConditionConfig.init();
