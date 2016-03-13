app.Login = {
    
    sendResetCode : function() {
        if($('.recover_section_outer[step="1"]').hasClass('disabled')) {
            return;
        }

        var email = $('.Login').find('#recoverpasswordemail').val();
        var event = thundashop.Ajax.createEvent("AppName", "sendConfirmation", $('.Login'), {"email" : email});
        var result = thundashop.Ajax.postWithCallBack(event, function(result) {
            result = JSON.parse(result);
            if(result.code === 0) {
                $('.recover_section_outer[step="1"]').addClass('disabled');
                $('.recover_section_outer[step="2"]').removeClass('disabled');
            }
            thundashop.Ajax.showErrorMessage("<div class='errorform '>" + result.msg + "</div>");


        });
    },
    resetPassword : function() {
        var app = $(this).closest('.Login');
        var email = app.find('#recoverpasswordemail').val();
        var confirmCode = app.find('#recoverpasswordcode').val();
        var newPass = app.find('#newpassword').val();
        var repeatPass = app.find('#repeat_newpassword').val();
        if(newPass.trim().length == 0) {
            thundashop.Ajax.showErrorMessage("<div class='errorform '>"+__w("Password cannot be empty")+"</div>");
            return;
        }
        if(newPass != repeatPass) {
            thundashop.Ajax.showErrorMessage("<div class='errorform '>"+__w("Password does not match, please re enter password")+"</div>");
            return;
        }

        var data = {
            "confirmCode" : confirmCode,
            "newPassword" : newPass,
            "email" : email
        }

        var event = thundashop.Ajax.createEvent("Event", "sendResetPassword", $(this), data);
        var result = thundashop.Ajax.postWithCallBack(event, function(result) {
            if(!result) {
                location.reload();
                return;
            }
            result = jQuery.parseJSON(result);
            thundashop.Ajax.showErrorMessage("<div class='errorform '>" + result.msg + "</div>");
        });
    },
   
    checkEnter : function(e) {
        if(e.keyCode == 13) {
            $(this).closest('form').find('#loginbutton').click();
        }
    },
   
    doLogin : function(event) {
        if(event.type === "keyup" && event.keyCode !== 13) {
            return;
        }
        $(this).closest("form").submit();
    },
   
   showRecoverPassword : function() {
       $('.Login .loginuserpasswordform').hide();
       $('.Login .recoverpasswordholder').show();
       
   },
   
    initEvents : function() {
        $(document).on('click','.Login #recoverinputbutton',app.Login.sendResetCode);
        $(document).on('click','.Login #resetpasswordbutton',app.Login.resetPassword);
        $(document).on('click','.Login .loginform .tstextfield[name="password"]',app.Login.checkEnter);
        $(document).on('click','.Login .loginbutton',app.Login.doLogin);
        $(document).on('click','.Login input[name="username"]',app.Login.doLogin);
        $(document).on('click','.Login input[name="password"]',app.Login.doLogin);
        $(document).on('click','.Login .recoverpassword',app.Login.showRecoverPassword);
    }

}

app.Login.initEvents();
