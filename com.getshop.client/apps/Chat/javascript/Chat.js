com.getshop.app.Chat = {
    adminLastDialog : "",
    lastDialog : "",
    hasBeenNotified: false,
    windowHasFocus: true,
    initDone: false,
    notifyTimer: null,
    numberOfMessageCount : -1,
    
    init: function() { 
        var scope = this;
        $('#bottom-chat-window #chat-header').live('click', function() { scope.openChat() });
        $('.Chat .chatinput').live('keypress', function(e) { if (e.which == 13) scope.sendMessage(); });
        $('.Chat .adminchatinput').live('keypress', function(e) { if (e.which == 13) scope.replyMessage(); });
        $('.chatters .chat').live('click', scope.selectChatter);
        $('.chatters .chat .close').live('click', scope.closeChatter);
        com.getshop.app.Chat.checkForMessages();
        $(window).focus(function() { com.getshop.app.Chat.windowHasFocus = true; com.getshop.app.Chat.unNotify(); });
        $(window).blur(function() { com.getshop.app.Chat.windowHasFocus = false; });
    },
    
    closeChatter: function() {
        com.getshop.app.Chat.unNotify();
        var chatter = $(this).closest('.chat');
        chatter.fadeOut(200);
        var chatterid = chatter.attr('chatterid');
        com.getshop.app.Chat.clearAdminChat();
        var event = thundashop.Ajax.createEvent("Chat", "closeChat", chatter, { chattrid : chatterid } );
        thundashop.Ajax.post(event);
        chatter.remove();
    },
    
    pollAdmin: function() {
        com.getshop.app.Chat.updateAdminChat();
        setTimeout(com.getshop.app.Chat.pollAdmin, 5000);
    },
    
    replyMessage: function() {
        var selected = $('.chatters .chat.selected');
        if (selected.length == 0) {
            alert(__w('You need to select at least one to send back message to'));
            return;
        }
        var chatterid = $(selected).attr('chatterid');
        var textField = $('.Chat .adminchatinput');
        com.getshop.app.Chat.sendChatMessage(textField, chatterid, com.getshop.app.Chat.printMessages);
    },
    
    doMobileUpdate : function(data) {
        $('#texttosendbutton').val('');
        com.getshop.app.Chat.startMobileChat(1);
    },
    
    sendChatMessage : function(textField, chatterid, callback) {
        data = { 'text' : textField.val(), 'chatterid' : chatterid };
        var event = thundashop.Ajax.createEvent("Chat", "replyMessage", textField, data);
        thundashop.Ajax.postWithCallBack(event, callback);
        textField.val("");
        com.getshop.app.Chat.updateAdminChat();
        com.getshop.app.Chat.unNotify(); 
    },
    
    selectChatter: function(e) {
        if ($(e.target).hasClass('close') || $(e.target).hasClass("closeimage"))
            return;
        
        $('.chatters .chat').each(function() {
            $(this).removeClass('selected');
        });
        
        $(this).addClass('selected');
        com.getshop.app.Chat.updateAdminChat();
        com.getshop.app.Chat.unNotify();
    },
    
    updateAdminChat: function() {
        var selected = $('.Chat .chat.selected');
        var chatterid = (selected.length > 0) ? $(selected).attr('chatterid') : "";
        var event = thundashop.Ajax.createEvent("Chat", "showChatter", $('.Chat .chat'), {chatterid: chatterid});
        event['synchron'] = true;
        thundashop.Ajax.post(event, com.getshop.app.Chat.updateChatterBox, null, true, true, { file : 'Chat.php' });
    },
    
    clearAdminChat: function() {
        $('.skeleton5 .maindialog .inner').html("<center>"+__w("Please select a user in the left menu")+"</center>");  
    },
    
    updateChatterBox: function(response) {
        com.getshop.app.Chat.updateChatters(response.chatters);
        if (response != com.getshop.app.Chat.adminLastDialog && $('.Chat .chat.selected').length > 0) {
            com.getshop.app.Chat.clearAdminChat();
            $('.skeleton5 .maindialog .inner').html(response.chatdata);
            com.getshop.app.Chat.adminLastDialog = response;    
            com.getshop.app.Chat.scrollBottom();
        }
    },
    
    unNotify: function() {
        $('.Chat .chat.selected').each(function() {
            $(this).css('font-weight', 'normal');
        });
        
        com.getshop.app.Chat.hasBeenNotified = false;
        if (com.getshop.app.Chat.notifyTimer) {
            clearInterval(com.getshop.app.Chat.notifyTimer);
            document.title = com.getshop.app.Chat.oldTitle;
            com.getshop.app.Chat.notifyTimer = null;
        }
    },
    
    notify: function(originalChatter) {
        if (!com.getshop.app.Chat.hasBeenNotified) {
            com.getshop.app.Chat.hasBeenNotified = true
            document.getElementById("sound").play();    
            
            var user = $("<div>"+$(originalChatter).html()+"</div>");
            user.find('img').remove();
            user.find('.close').remove();
            com.getshop.app.Chat.blinkTitle(user.html());
        }
    },
    
    blinkTitle: function(user) {
        com.getshop.app.Chat.oldTitle = document.title;
        com.getshop.app.Chat.notifyTimer = setInterval(function(){
            var title = document.title;
            document.title = (title == __w("New message") ? user : __w("New message"));
        }, 1000);
        com.getshop.app.Chat.hasBeenNotified = true;
    },
    
    updateChatters : function(chatters) {
        var knownChatters = [];
        $(chatters).each(function() {
            var selectedChatter = $('.Chat .chatters .chat.selected');
            if (selectedChatter.attr('chatterid') == this.id && com.getshop.app.Chat.windowHasFocus) {
                selectedChatter.attr('messagecount', this.messages);
            }
                
            knownChatters.push(this.id);
            var originalChatter = $('.Chat .chatters .chat[chatterid="'+this.id+'"]');
            if (originalChatter.length > 0) {
                if ($(originalChatter).attr('messagecount') != this.messages) {
                    com.getshop.app.Chat.notify(originalChatter);
                    $(originalChatter).css('font-weight', 'bold');
                    $(originalChatter).attr('messagecount', this.messages);
                }   
            } else {
                var closeButton = $('<div/>');
                closeButton.addClass('close');
                closeButton.html('<img class="closeimage" width="20" height="20" src="/skin/default/elements/closebutton.png">');
                
                var chatter = $('<div/>');
                chatter.addClass('chat');
                chatter.attr('chatterid', this.id);
                chatter.attr('messagecount', this.messages);
                chatter.html(this.username);
                chatter.append(closeButton);
                
                $('.Chat .chatters').append(chatter);
            }
        });
        
        $('.Chat .chatters .chat').each(function() {
            var chattrid = $(this).attr('chatterid');
            
            for (var i = 0; i < knownChatters.length; i++) {
                if (knownChatters[i] == chattrid)
                    return;
            }
        
            $(this).fadeOut(200, function() {
                $(this).remove();
            });
        });
    },
    
    checkForMessages: function() {
        var app = $('#bottom-chat-window .chatinput');
        if (typeof(app[0]) != "undefined") {
            var event = thundashop.Ajax.createEvent("Chat", "renderContent", app[0], {});
            thundashop.Ajax.postWithCallBack(event, com.getshop.app.Chat.printMessages, true, { file : 'Chat.php' });
        }
        setTimeout(com.getshop.app.Chat.checkForMessages, 5000);
    },
    
    sendMessage: function() {
        var textField = $('.Chat .chatinput');
        data = { 'text' : textField.val() };
        var event = thundashop.Ajax.createEvent("Chat", "sendMessage", textField, data);
        thundashop.Ajax.postWithCallBack(event, com.getshop.app.Chat.printMessages);
        textField.val("");
    },
    
    startMobileChat : function(response) {
        var target = $('.Chat #mobilechat_area');
        target.html(response);
        var data = {
            "chatterid" : target.attr('chatterid')
        }
        
        if(!com.getshop.app.Chat.windowHasFocus) {
            setTimeout("com.getshop.app.Chat.startMobileChat(0)","1000");
            return;
        }
        
        if(typeof(response) === "string") {
            var count = response.split("chatmessage").length;
            if(count !== com.getshop.app.Chat.numberOfMessageCount) {
                com.getshop.app.Chat.numberOfMessageCount = count;
                $(window).scrollTop($(document).height());
            }
            if(count === 2) {
                var event = thundashop.Ajax.createEvent("Chat", "sendGreeting", target,data);
                thundashop.Ajax.postWithCallBack(event, com.getshop.app.Chat.startMobileChat);
                return;
            }
        }

        var event = thundashop.Ajax.createEvent("Chat", "printMobileChat", target, data);
        var timeout = 2000;
        if(response === 0) {
            timeout = 0;
        }
        if(response === 1) {
            thundashop.Ajax.postWithCallBack(event, com.getshop.app.Chat.startMobileChat);
            return;
        }
        
        setTimeout(function() {
            thundashop.Ajax.postWithCallBack(event, com.getshop.app.Chat.startMobileChat);
        }, timeout);
    },
    
    printMessages: function(response) {
        var dialogInner = $('#bottom-chat-window .maindialog .inner');
        
        if (response != com.getshop.app.Chat.lastDialog) {
            com.getshop.app.Chat.lastDialog = response;
            dialogInner.html(response);
            if (com.getshop.app.Chat.initDone && !com.getshop.app.Chat.hasBeenNotified) {
                com.getshop.app.Chat.hasBeenNotified = true;
                
                if (!com.getshop.app.Chat.windowHasFocus) 
                    com.getshop.app.Chat.blinkTitle("You got");
                
                if (!$('#bottom-chat-dialog').is(':visible'))
                    $('#bottom-chat-window .newchatmessage').fadeIn(200)
            } 
            
            if ($('#bottom-chat-dialog').is(':visible'))
                com.getshop.app.Chat.scrollBottom();
        }
        
        com.getshop.app.Chat.initDone = true;
    },
    
    scrollBottom: function() {
        var scrollHeight = $('.Chat .maindialog .inner').find('.chatevent').length * 61;
        $('.Chat .maindialog').scrollTop(scrollHeight)
        document.getElementById('chatinput').focus();
    },
    
    openChat: function() {
        if (!$('#bottom-chat-dialog').is(':visible')) {
            $('#bottom-chat-dialog').slideDown(300, function() {
                $('#bottom-chat-window .newchatmessage').fadeOut(200);
            });
        } else {
            $('#bottom-chat-dialog').slideUp(300);
        }
        com.getshop.app.Chat.scrollBottom();
    }
}

com.getshop.app.Chat.init();