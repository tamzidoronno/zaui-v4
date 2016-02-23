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
            content = app.EventSendReminder.decode_base64(content)
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
    
    decode_base64: function (s)
    {
        var e = {}, i, k, v = [], r = '', w = String.fromCharCode;
        var n = [[65, 91], [97, 123], [48, 58], [43, 44], [47, 48]];

        for (z in n)
        {
            for (i = n[z][0]; i < n[z][1]; i++)
            {
                v.push(w(i));
            }
        }
        for (i = 0; i < 64; i++)
        {
            e[v[i]] = i;
        }

        for (i = 0; i < s.length; i += 72)
        {
            var b = 0, c, x, l = 0, o = s.substring(i, i + 72);
            for (x = 0; x < o.length; x++)
            {
                c = e[o.charAt(x)];
                b = (b << 6) + c;
                l += 6;
                while (l >= 8)
                {
                    r += w((b >>> (l -= 8)) % 256);
                }
            }
        }
        return r;
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