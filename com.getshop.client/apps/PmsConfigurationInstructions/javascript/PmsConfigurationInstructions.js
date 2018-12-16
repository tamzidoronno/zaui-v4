app.PmsConfigurationInstructions = {
    init : function() {
        $(document).on('click', '.PmsConfigurationInstructions .saveinstructions', app.PmsConfigurationInstructions.saveOtherInstructions);
        $(document).on('click', '.PmsConfigurationInstructions #otherinstructionsfiled', function() {
            thundashop.common.activateCKEditor('otherinstructionsfiled', {
                notinline : true,
                autogrow : false,
                removesave : true
            });
        });
        $(document).on('click', '.PmsConfigurationInstructions #fireinstructions', function() {
            thundashop.common.activateCKEditor('fireinstructions', {
                notinline : true,
                autogrow : false,
                removesave : true
            });
        });
        $(document).on('click', '.PmsConfigurationInstructions #cleaninginstruction', function() {
            thundashop.common.activateCKEditor('cleaninginstruction', {
                notinline : true,
                autogrow : false,
                removesave : true
            });
        });
    },
    saveOtherInstructions: function() {
        var id = $(this).attr('fromid');
        var data = {
            content : CKEDITOR.instances[id].getData(),
            fromid : id
        };
        var event = thundashop.Ajax.createEvent('','saveContent',$(this),data);
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
app.PmsConfigurationInstructions.init();
