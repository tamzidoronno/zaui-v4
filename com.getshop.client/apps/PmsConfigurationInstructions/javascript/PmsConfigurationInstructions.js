app.PmsConfigurationInstructions = {
    init : function() {
        $(document).on('click', '.PmsConfigurationInstructions #otherinstructionsfiled', function() {
            thundashop.common.activateCKEditor('otherinstructionsfiled', {
                autogrow : false
            });
        });
        $(document).on('click', '.PmsConfigurationInstructions #fireinstructions', function() {
            thundashop.common.activateCKEditor('fireinstructions', {
                autogrow : false
            });
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
app.PmsConfigurationInstructions.init();
