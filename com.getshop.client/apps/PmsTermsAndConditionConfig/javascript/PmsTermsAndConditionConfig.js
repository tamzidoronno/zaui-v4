app.PmsTermsAndConditionConfig = {
    init : function() {
        $(document).on('click', '.PmsTermsAndConditionConfig .savetermsandconditions',app.PmsTermsAndConditionConfig.saveTermsAndConditions);
        $(document).on('click', '.PmsTermsAndConditionConfig #contractfield', function() {
            thundashop.common.activateCKEditor('contractfield', {
                autogrow : false,
                removesave : true
            });
        });
        $(document).on('click', '.PmsTermsAndConditionConfig .changeLanguage', app.PmsTermsAndConditionConfig.changeLanguage);
    },
    saveTermsAndConditions : function() {
        var data = {
            content : CKEDITOR.instances.contractfield.getData()
        };
        var event = thundashop.Ajax.createEvent('','saveTermsAndConditions',$(this),data);
        thundashop.Ajax.postWithCallBack(event, function() {
            window.location.reload();
        });
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
