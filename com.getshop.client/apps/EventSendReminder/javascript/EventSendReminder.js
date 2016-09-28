app.EventSendReminder = {
    init: function () {
        $(document).on('click', '.EventSendReminder .savetemplatepage', app.EventSendReminder.saveTemplate);
        $(document).on('change', '.EventSendReminder .templateselector', app.EventSendReminder.changedTemplate);
        $(document).on('click', '.EventSendReminder .sendmail', app.EventSendReminder.sendMail);
        $(document).on('click', '.EventSendReminder .sendsms', app.EventSendReminder.sendSms);
        $(document).on('click', '.EventSendReminder .deleteTemplate', app.EventSendReminder.deleteTemplate);
   },
   
   deleteTemplate: function() {
       var yes = confirm("Are you sure you want to delete this template?");
       if (yes) {
           thundashop.Ajax.simplePost(this, "deleteTemplate", {
               templateId: $('.EventSendReminder .templateselector').val()
           });
       }
   },
   
   changedTemplate: function () {
       var content = $(this).find(':selected').attr('content');
       var subject = $(this).find(':selected').attr('subject');
        
       if (content) {
           content = thundashop.base64.decode(content)
       } else {
           content = "";
       }
       
       if (subject) {
           subject = thundashop.base64.decode(subject)
       } else {
           subject = "";
       }
        
       $('.EventSendReminder textarea').val(content);
       $('.EventSendReminder .subject').val(subject);
    },
    saveTemplate: function () {

        var id = $('.EventSendReminder .templateselector').val();
        var name = "";
        if (!id) {
            name = prompt("Please enter name of template");
        }
        
        var data = {
            id: id,
            name: name,
            subject: $('.EventSendReminder .subject').val(),
            content: $('.EventSendReminder textarea').val()
        };
        
        $(this).prepend("<span class='gs_loader_icon'><i class='fa fa-spinner fa-spin'/> </span>");
        var event = thundashop.Ajax.createEvent(null, "saveTemplate", this, data);
        var me = this;
        
        thundashop.Ajax.postWithCallBack(event, function(res) {
            var load = $(me).find('.fa-spinner');
            load.removeClass("fa-spinner");
            load.removeClass("fa-spin");
            load.addClass("fa-check");
            setTimeout(function() {
                load.remove();
            }, 2000);
        })
    },
    
    sendMail: function() {
        var userids = $('.EventSendReminder input:checkbox:checked.userid').map(function () {
            return this.value;
        }).get();
        
        userids = JSON.stringify(userids);

        thundashop.Ajax.simplePost(this, "sendReminder", {
            userids: userids,
            type: "mail",
            subject: $('.EventSendReminder .subject').val(),
            content: $('.EventSendReminder textarea').val()
        });
    },
    
    sendSms: function() {
        var userids = $('.EventSendReminder input:checkbox:checked.userid').map(function () {
            return this.value;
        }).get();
        
        userids = JSON.stringify(userids);

        thundashop.Ajax.simplePost(this, "sendReminder", {
            userids: userids,
            type: "sms",
            content: $('.EventSendReminder textarea').val()
        });
    },
    
    checkForReminderUpdates: function() {
        if ($('.EventSendReminder').is(':visible')) {
            setTimeout(app.EventSendReminder.checkForReminderUpdates, 1000);
        }
        
        $('.EventSendReminder .indicators').each(function() {
            if ($(this).find('.fa-spinner').length == 0) {
                return;
            }
            
            var data = {
                userid : $(this).attr('userid'),
                reminderId : $(this).attr('reminderid')
            }
            var event = thundashop.Ajax.createEvent(null, "updateIndicator", this, data);
            event['synchron'] = true;
            
            var me = $(this);
            thundashop.Ajax.post(event, function(res) {
                me.html(res);
            }, null, true, true);
            
        });
    }
};

app.EventSendReminder.init();