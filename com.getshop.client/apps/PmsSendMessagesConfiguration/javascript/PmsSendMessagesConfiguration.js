app.PmsSendMessagesConfiguration = {
    init : function() {
        $(document).on('click', '.PmsSendMessagesConfiguration .changeLanguage', app.PmsSendMessagesConfiguration.changeLanguage);
    },
    changeLanguage : function() {
        var lang = $(this).val();
        var event = thundashop.Ajax.createEvent('','changeLanguage',$(this), {
            "lang" : lang
        });
        thundashop.Ajax.post(event);
    }
};

app.PmsSendMessagesConfiguration.init();