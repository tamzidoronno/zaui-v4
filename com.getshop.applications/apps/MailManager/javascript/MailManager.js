app.MailManager = {
    init : function() {
        $(document).on('click', '.setuporderemail', this.showOrderMailSettings);
        $(document).on('focusout', '#ordermailsubject', this.saveOrderMailSubject);
    },
            
    saveOrderMailSubject: function() {
        var subject = $(this).val();
        var event = thundashop.Ajax.createEvent('MailManager', 'saveOrderMailSubject', this, { subject: subject });
        thundashop.Ajax.post(event);
    },
            
    showOrderMailSettings: function() {
        var event = thundashop.Ajax.createEvent('MailManager', 'showOrderMailSettings', this);
        thundashop.common.showInformationBox(event);
    }
};

app.MailManager.init();