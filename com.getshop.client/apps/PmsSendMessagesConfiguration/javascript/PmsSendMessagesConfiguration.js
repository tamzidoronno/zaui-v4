app.PmsSendMessagesConfiguration = {
    init : function() {
        $(document).on('click', '.PmsSendMessagesConfiguration .changeLanguage', app.PmsSendMessagesConfiguration.changeLanguage);
        $(document).on('click', '.PmsSendMessagesConfiguration [loadeditmeesage]', app.PmsSendMessagesConfiguration.createNewAutomatedMessage);
        $(document).on('mousedown', '.PmsSendMessagesConfiguration .loadeditevent', app.PmsSendMessagesConfiguration.hideEditEvent);
        $(document).on('click', '.PmsSendMessagesConfiguration .languageselectionbox', app.PmsSendMessagesConfiguration.updateLangaugeSelection);
        $(document).on('click', '.PmsSendMessagesConfiguration .allroomtypes', app.PmsSendMessagesConfiguration.updateAllRoomTypesSelection);
        $(document).on('click', '.PmsSendMessagesConfiguration .roomtypeselectionbox', app.PmsSendMessagesConfiguration.updateRoomSelection);
        $(document).on('click', '.PmsSendMessagesConfiguration .alllangaugeselectionbox', app.PmsSendMessagesConfiguration.updateAllLangaugeSelection);
        $(document).on('click', '.PmsSendMessagesConfiguration .prefixselectionbox', app.PmsSendMessagesConfiguration.updatPrefixSelection);
        $(document).on('click', '.PmsSendMessagesConfiguration .allprefixesbox', app.PmsSendMessagesConfiguration.updateAllPrefixSelection);
        $(document).on('click', '.PmsSendMessagesConfiguration .updatemessage', app.PmsSendMessagesConfiguration.doUpdateMessage);
        $(document).on('click', '.PmsSendMessagesConfiguration .fa-trash-o', app.PmsSendMessagesConfiguration.removeMessage);
        $(document).on('change', '.PmsSendMessagesConfiguration select[name="typeofmessage"]', app.PmsSendMessagesConfiguration.updateRoomSpecificCodes);
        $(document).on('change', '.PmsSendMessagesConfiguration .filterbykeytype', app.PmsSendMessagesConfiguration.filterByKey);
        $(document).on('click', "input[name='deliverytype']", app.PmsSendMessagesConfiguration.updateBySelection);
    },
    filterByKey : function() {
        var key = $(this).val();
        app.PmsSendMessagesConfiguration.filterBySpecifiedKey(key);
    },
    
    filterBySpecifiedKey : function(key) {
        $('.messageboxnew').show();
        if(!key) {
            return;
        }
        if(history.pushState) {
            history.pushState(null, null, '#filter='+key);
        } else {
            location.hash = '#filter='+key;
        }

        $('.messageboxnew').each(function() {
            if($(this).attr('key') !== key) {
                $(this).hide();
            }
        });
    },
    
    removeMessage : function() {
        var btn = $(this);
        var msgid = btn.attr('msgid');
        var confirmed = confirm("Are you sure you want to delete this message?");
        if(confirmed) {
            var event = thundashop.Ajax.createEvent('','removeMessage',$(this), {
                "id" : msgid
            });
            thundashop.Ajax.postWithCallBack(event, function(res) {
                btn.closest('.messageboxnew').remove();
            });
        }
    },
    updateRoomSelection : function() {
        $('.allroomtypes').attr('checked',null);
    },
    
    updateAllRoomTypesSelection : function() {
        if($(this).is(':checked')) {
             $('.roomtypeselectionbox').attr('checked',null);
         }
    },
    doUpdateMessage : function() {
        var data = {
            "id" : $(this).attr('msgid'),
            "title" : $('input[name="msgtitle"]').val(),
            "content" : $('textarea[name="msgcontent"]').val(),
            "type" : $("input[name='deliverytype']:checked").val(),
            "key" : $("select[name='typeofmessage']").val(),
            "ismanual" : $("input[name='ismanual']").is(':checked'),
        };
        var languages = [];
        $('.languageselectionbox').each(function() {
            if($(this).is(':checked')) {
                languages.push($(this).val());
            }
        });
        var prefixes = [];
        $('.prefixselectionbox').each(function() {
            if($(this).is(':checked')) {
                prefixes.push($(this).val());
            }
        });
        
        var roomtypes = [];
        $('.roomtypeselectionbox').each(function() {
            if($(this).is(':checked')) {
                roomtypes.push($(this).val());
            }
        });
        
        data.languages = languages;
        data.prefixes = prefixes;
        data.roomtypes = roomtypes;
        
        var event = thundashop.Ajax.createEvent('','updateCreateMessage', $(this), data);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            window.location.reload();
        });
    },
    updateRoomSpecificCodes : function() {
        var typeofmsg = $('select[name="typeofmessage"]').val();
        if(typeofmsg.startsWith('room')) {
            $('.roomspecificvariables').show();
        } else {
            $('.roomspecificvariables').hide();
        }
    },
    updatPrefixSelection : function() {
        $('.allprefixesbox').attr('checked',false);
        var found = false;
        $('.prefixselectionbox').each(function() {
            if($(this).is(':checked')) {
                found = true;
            }
        });
        if(!found) {
            $('.allprefixesbox').prop('checked','checked');
        }

    },
    updateAllPrefixSelection : function() {
        $('.prefixselectionbox').attr('checked',null);
    },
    
    updateAllLangaugeSelection : function() {
        $('.languageselectionbox').attr('checked',null);
    },
    
    updateLangaugeSelection : function() {
        $('.alllangaugeselectionbox').attr('checked',false);
        var found = false;
        $('.languageselectionbox').each(function() {
            if($(this).is(':checked')) {
                found = true;
            }
        });
        if(!found) {
            $('.alllangaugeselectionbox').prop('checked','checked');
        }
    },
    updateBySelection : function() {
        var radioValue = $("input[name='deliverytype']:checked"). val();
        $('.messagetitle').hide();
        $('.languageselectionoptions').hide();
        $('.phoneprefixoptions').hide();
        if(radioValue === "sms") {
            $('.languageselectionoptions').show();
            $('.phoneprefixoptions').show();
        }
        if(radioValue === "email") {
            $('.messagetitle').show();
            $('.languageselectionoptions').show();
        }
    },
    
    hideEditEvent : function(e) {
        if(!$(e.target).hasClass('loadeditevent') && !$(e.target).hasClass('fa-close')) {
            return;
        }
        $('.PmsSendMessagesConfiguration .loadeditevent').hide();
        $('.PmsSendMessagesConfiguration .loadeditevent').html('');
            $("body").css("overflow", "auto");
    },
    createNewAutomatedMessage : function() {
        var id = $(this).attr('loadeditmeesage');
        var event = thundashop.Ajax.createEvent('','loadCreatedEditEvent', $(this), {
            "id" : id
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.PmsSendMessagesConfiguration .loadeditevent').show();
            $('.PmsSendMessagesConfiguration .loadeditevent').html(res);
            $('.loadeditevent').scrollTop(0);
            $("body").css("overflow", "hidden");
        });
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