app.MailSpreader = {
    addRow : function() {
        var row = $($('.MailSpreader .emailspreader tbody').find('tr')[0]);
        row = row.clone();
        row.find('input').val('');
        $('.MailSpreader .emailspreader tbody').append(row);
    },
    saveEmail : function() {
        var data = {
            "email" : $('.MailSpreader .emailtext').val(),
            "emailtitle" : $('.MailSpreader .emailtitle').val()
        }
        var event = thundashop.Ajax.createEvent('','saveEmail',$(this),data);
        thundashop.Ajax.post(event);
    },
    sendContacts: function() {
        var data = {
            "name" : $('.MailSpreader .inputname').val(),
        }
        
        var entries = [];
        $('.MailSpreader .emailspreader tbody tr').each(function() {
            var entry = {
                "name" : $(this).find('.name').val(),
                "email" : $(this).find('.email').val()
            }
            entries.push(entry);
        });
        
        data['entries'] = entries;
        
        var event = thundashop.Ajax.createEvent('','sendEmails',$(this),data);
        thundashop.Ajax.post(event);
    },
    initEvents : function() {
        $(document).on('click', '.MailSpreader .addcontacts', app.MailSpreader.addRow);
        $(document).on('click', '.MailSpreader .sendcontacts', app.MailSpreader.sendContacts);
        $(document).on('click', '.MailSpreader .saveEmail', app.MailSpreader.saveEmail);
    }
};
app.MailSpreader.initEvents();