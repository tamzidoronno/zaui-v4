thundashop.app.login = {};
thundashop.app.login.resetPassword = function(target) {
    var app = target.closest('.Login');
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
    
    var event = thundashop.Ajax.createEvent("Event", "sendResetPassword", target, data);
    var result = thundashop.Ajax.postSynchron(event);
    result = jQuery.parseJSON(result);
    if(result.error == 0) {
        thundashop.common.Alert(__w("Password has been updated"), __w("Password has been successfully updated"));
        thundashop.common.hideInformationBox(null);
    } else {
        thundashop.Ajax.showErrorMessage("<div class='errorform '>" + result.error + "</div>");
    }
}

$('.header .loginform .entry').live('click', function() {
    $('.header .loginform .form').slideToggle('slow');
});

$('.header .loginform .tstextfield[name="password"]').live('keyup', function(e) {
    if(e.keyCode == 13) {
        $(this).closest('form').find('#loginbutton').click();
    }
});

$('.loginform #loginbutton').live('click', function(e) {
    $(this).closest("form").submit();
})

$('.middle .loginform .tstextfield[name="password"]').live('keyup', function(e) {
    if(e.keyCode == 13) {
        $(this).closest('form').find('#loginbutton').click();
    }
});

$('.Login #recoverinputbutton').live('click', function(e) {
    var email = $(e.target).closest('.Login').find('#recoverpasswordemail').val();
    var event = thundashop.Ajax.createEvent("AppName", "sendConfirmation", $('.Login'), {"email" : email});
    var result = thundashop.Ajax.postSynchron(event);
    result = jQuery.parseJSON(result);
    if(result.error === "0") {
        thundashop.common.Alert(__w("Email sent"), __w("An email has been sent to you with the confirmation code."));
    } else {
        thundashop.Ajax.showErrorMessage("<div class='errorform '>" + result.error + "</div>");
    }
});

$('.Login .loginbutton').live('click', function(e) {
    $(this).closest('form').submit();
});
$('.Login .recoverpassword').live('click', function(e) {
    var event = thundashop.Ajax.createEvent("", "recoverPassword", $(this), null);
    thundashop.common.showInformationBox(event, '');
});
$('.Login #resetpasswordbutton').live('click', function(e) {
    thundashop.app.login.resetPassword($(e.target));
});