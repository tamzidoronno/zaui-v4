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
    uploadFileAttachment : function() {
        var scope = $(this);
        var options = { 
            resetForm: true,
            success : function(data) {
                data = JSON.parse(data);
                var event = thundashop.Ajax.createEvent('',"attachFile", scope, { "id" : data});
                thundashop.Ajax.postWithCallBack(event, function() {
                    $('.NewsLetter #attachments').append($('<div>' + data.name +  "<i class='fa fa-trash-o' delid='"+data.id+"'></i></div>"));
                    console.log(data);
                });
            }
        }; 
        $(this).ajaxSubmit(options);
        return false;
    },
            
    removeAttachment : function() {
        var scope = $(this);
        var id = $(this).attr('delid');
        var event = thundashop.Ajax.createEvent('','removeAttachedFile',$(this), { "id" : id });
        thundashop.Ajax.postWithCallBack(event, function() {
            scope.closest('div').remove();
        });
    },
            
    init : function() {
        $(document).on('click', '.NewsLetter #send_newsletter_to_recipients', app.NewsLetter.sendEmails);
        $(document).on('click', '.NewsLetter #show_recipients', app.NewsLetter.showRecipients);
        $(document).on('click', '.NewsLetter #send_preview', app.NewsLetter.sendEmails);
        $(document).on('click', '.NewsLetter #show_mail', app.NewsLetter.showMailBody);
        $(document).on('click', '.NewsLetter #save_template', app.NewsLetter.saveMail);
        $(document).on('click', '.NewsLetter #attachments .fa-trash-o', app.NewsLetter.removeAttachment);
        $(document).on('submit', '.NewsLetter .getshop_upload_form', app.NewsLetter.uploadFileAttachment);
    }
}

app.NewsLetter.init();