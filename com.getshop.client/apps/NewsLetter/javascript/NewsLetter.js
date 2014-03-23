//Put your javascript here.
app.NewsLetter = {
    sendEmails : function() {
       var content = CKEDITOR.instances['newsletter_content'].getData();
       var recipients = [];
       $('.newsletter_user').each(function() {
           if($(this).is(':checked')) {
               recipients.push($(this).attr('userid'));
           }
       });
       if(recipients.length === 0 && $(this).attr('id') !== "send_preview") {
           alert(__f("You need to select atleast one user to send your email to."));
           return;
       }
       var data = {
           "users" : recipients,
           "mail" : content,
           "id" : $(this).attr('id'),
           "subject" : $('#news_letter_subject').val()
       }
       
       var event = thundashop.Ajax.createEvent('','sendNewsLetter',$(this),data);
       thundashop.Ajax.post(event);
    },
    showRecipients : function() {
        $('.newsletter_body').hide();
        $('.conctact_list').show();
    },
    showMailBody : function() {
        $('.newsletter_body').show();
        $('.conctact_list').hide();
    },
    saveMail : function() {
        var content = CKEDITOR.instances['newsletter_content'].getData();
          var data = {
           "mail" : content
       }
        var event = thundashop.Ajax.createEvent('','saveNewsLetter',$(this),data);
        
        thundashop.Ajax.postWithCallBack(event, function() {
            thundashop.common.Alert("Mail has been saved", "");
        })
        
    },
    init : function() {
        $(document).on('click', '.NewsLetter #send_newsletter_to_recipients', app.NewsLetter.sendEmails);
        $(document).on('click', '.NewsLetter #show_recipients', app.NewsLetter.showRecipients);
        $(document).on('click', '.NewsLetter #send_preview', app.NewsLetter.sendEmails);
        $(document).on('click', '.NewsLetter #show_mail', app.NewsLetter.showMailBody);
        $(document).on('click', '.NewsLetter #save_template', app.NewsLetter.saveMail);
    }
}

app.NewsLetter.init();