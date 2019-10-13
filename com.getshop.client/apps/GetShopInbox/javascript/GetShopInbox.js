app.GetShopInbox = {
    init: function() {
        $(document).on('click', '.GetShopInbox .menu .taboperation', app.GetShopInbox.changeMenu);
        $(document).on('change', '.GetShopInbox .row.email.header input', app.GetShopInbox.toggleChecked);
        $(document).on('click', '.GetShopInbox .connecttocompany', app.GetShopInbox.connectToCompany);
        $(document).on('click', '.GetShopInbox .closeConnectToCompany', app.GetShopInbox.closeConnectToCompany);
        $(document).on('change', '.GetShopInbox .filtercompanylist', app.GetShopInbox.filterList);
        $(document).on('click', '.GetShopInbox .selected_checkbox_options .checkboxaction', app.GetShopInbox.toggleMenu);
        $(document).on('click', '.GetShopInbox .assignaction', app.GetShopInbox.toggleMailMenu);
        $(document).on('click', '.GetShopInbox .selected_checkbox_options .doassignment', app.GetShopInbox.doAssignment);
        $(document).on('click', '.GetShopInbox .selected_checkbox_options .ignore', app.GetShopInbox.markAsIgnored);
        $(document).on('click', '.GetShopInbox .showemail', app.GetShopInbox.showMail);
        $(document).on('click', '.GetShopInbox .typeselection', app.GetShopInbox.changeTypeOnMessage);
        $(document).on('click', '.GetShopInbox .mailview .closebox', app.GetShopInbox.closeMailView);
        $(document).on('click', '.GetShopInbox .timespentupdate', app.GetShopInbox.promtTimeSpent);
        $(document).on('click', '.GetShopInbox .mailview .sendreply', app.GetShopInbox.sendReply);
        $(document).on('click', '.GetShopInbox .mailview .markCompleted', app.GetShopInbox.markCompleted);
        $(document).on('click', '.GetShopInbox .startticket', app.GetShopInbox.startTicket);
        $(document).on('keydown', app.GetShopInbox.keyDownPressed);
    },
    
    startTicket : function() {
        var addr = $(this).attr('address');
        var systemid = $(this).attr('systemid');
        $.get("https://" + addr + "/scripts/createTicket.php", function(res) {
            window.open("https://" + addr + "/totp.php", "fdasfasdfs");
            window.location.href='/getshop.php?page=ticketview&ticketId=' + res;
         });

    },
    promtTimeSpent : function() {
        var time = prompt("Time spent");
        var msgid = $(this).attr('msgid');
        var field = $(this);
        if(!time) {
            return;
        }
        var event = thundashop.Ajax.createEvent('','updateTimeSpent',$(this), {
            "time" : time,
            "msgid" : msgid
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            field.html(time);
        });
    },
    changeTypeOnMessage : function() {
        var msgId = $(this).attr('msgid');
        var type = $(this).attr('type');
        var btn = $(this);
        btn.closest('.menu').find('.typeselection').removeClass('selected');
        var event = thundashop.Ajax.createEvent('','changeTypeOnMessage',$(this),{
            "msgid" : msgId,
            "type" : type
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            btn.addClass('selected');
        });
    },
    
    keyDownPressed : function(e) {
        if(e.keyCode === 27) {
            app.GetShopInbox.closeMailView();
        }
    },
    sendReply: function() {
        var data = {
            content : myEditor.getData(),
            msgid : $(this).attr('msgid'),
            "timespent" : $('.timespentcounter').val()
        };
        
        var event = thundashop.Ajax.createEvent(null, "sendReply", this, data);
        thundashop.Ajax.post(event);
    },
    markCompleted: function() {
        var data = {
            msgid : $(this).attr('msgid')
        };
        
        var event = thundashop.Ajax.createEvent(null, "markEmailAsCompleted", this, data);
        thundashop.Ajax.post(event);
    },
    
    closeMailView: function() {
        $('.mailview').hide();
        $('.mailview .inner_mail_view').html("");
    },
    
    showMail: function(e) {
        if ($(e.target).hasClass('fa'))
            return;
        if ($(e.target).hasClass('timespentupdate'))
            return;
        if ($(e.target).closest('.menu').length > 0)
            return;
        
        var data = {
            msgid : $(this).closest('.row').attr('msgid')
        };
        
        var event = thundashop.Ajax.createEvent(null, "createMailView", this, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(res) {
            $('.mailview .inner_mail_view').html(res);
            $('.mailview').show();
        });
        
    },
    
    singleCheck : function(me) {
        if ($(me).closest('.row.email').length) {
            $('input').prop('checked', false);
            $(me).closest('.row.email').find('input').prop('checked', true);
        }
    },
    
    getCheckboxes: function() {
        var checkboxes = [];
        
        var found = false;
        $('.GetShopInbox .rowcheckbox').each(function() {
            if ($(this).is(':checked')) {
                checkboxes.push($(this).val());
                found = true;
            }
        });
        
        var data = {
            msgs : checkboxes
        };
        if(!found) {
            data.msgs = [];
            var id = $('.mailview .menu').attr('msgid');
            data.msgs.push(id);
        }
        
        return data;
    },
    
    doAssignment: function() {
        app.GetShopInbox.singleCheck(this);
        
        var data = app.GetShopInbox.getCheckboxes();
        data.userid = $(this).attr('userid');
        
        var event = thundashop.Ajax.createEvent(null, "assignTo", $(this), data);
        
        thundashop.Ajax.post(event);
    },
    
    markAsIgnored: function() {
        app.GetShopInbox.singleCheck(this);
        
        var data = app.GetShopInbox.getCheckboxes();
        
        var event = thundashop.Ajax.createEvent(null, "markAsIgnored", this, data);
        
        thundashop.Ajax.post(event);
    },
    
    toggleMenu: function() {
        var menu = $(this).closest('.selected_checkbox_options').find('.menu');
        if ($(menu).is(':visible')) {
            $(menu).slideUp();
        } else {
            $('.selected_checkbox_options').find('.menu').hide();
            $(menu).slideDown();
        }
    },
    toggleMailMenu: function() {
        var menu = $(this).closest('.subject').find('.menu');
        if ($(menu).is(':visible')) {
            $(menu).slideUp();
        } else {
            $('.selected_checkbox_options').find('.menu').hide();
            $(menu).slideDown();
        }
    },

    filterList: function() {
        var search = $(this).val().toLowerCase();
        
        $('.companyrow').each(function() {
            var text = $(this).text();
            if (!search) {
                $(this).show();
            } else if (text.toLowerCase().indexOf(search) === -1) { 
                $(this).hide();
            } else {
                $(this).show();
            }
        });
    },

    connectToCompany: function() {
        $('.GetShopInbox .connectMailToCompany .startconnection').attr('msgid', $(this).attr('msgid'));
        $('.GetShopInbox .connectMailToCompany').show();
    },

    closeConnectToCompany: function() {
        $('.GetShopInbox .connectMailToCompany').hide();
    },

    toggleChecked: function() {
        var val = $(this).is(':checked');
        if (val) {
            $('input').prop('checked', true);
        } else {
            $('input').prop('checked', false);
        }
    },

    changeMenu: function() {
        var data = {
            tab : $(this).attr('tab')
        };
        
        thundashop.Ajax.simplePost(this, 'changeMenu', data);
    }
};

app.GetShopInbox.init();