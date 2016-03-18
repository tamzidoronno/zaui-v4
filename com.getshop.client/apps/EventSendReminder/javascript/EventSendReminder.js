app.EventSendReminder = {
    init: function () {
        $(document).on('click', '.EventSendReminder .savetemplatepage', app.EventSendReminder.saveTemplate);
        $(document).on('change', '.EventSendReminder .templateselector', app.EventSendReminder.changedTemplate);
        $(document).on('click', '.EventSendReminder .sendmail', app.EventSendReminder.sendMail);
        $(document).on('click', '.EventSendReminder .sendsms', app.EventSendReminder.sendSms);
   },
    changedTemplate: function () {
        var content = $(this).find(':selected').attr('content');
        if (content) {
            content = thundashop.base64.decode(content)
        } else {
            content = "";
        }
        
        $('.EventSendReminder textarea').val(content);
    },
    saveTemplate: function () {

        var id = $('.EventSendReminder .templateselector').val();
        var name = "";
        if (!id) {
            name = prompt("Please enter name of template");
        }

        thundashop.Ajax.simplePost(this, "saveTemplate", {
            id: id,
            name: name,
            content: $('.EventSendReminder textarea').val()
        });
    },
    
    sendMail: function() {
        var userids = $('.EventSendReminder input:checkbox:checked.userid').map(function () {
            return this.value;
        }).get();
        
        userids = JSON.stringify(userids);

        thundashop.Ajax.simplePost(this, "sendReminder", {
            userids: userids,
            type: "mail",
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