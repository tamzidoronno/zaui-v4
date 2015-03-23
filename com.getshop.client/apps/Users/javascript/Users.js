app.Users = {
    userCreated: function(res) {
        alert(res);
        return true;
    },
    
    validatePassword: function() {
        if (!$('#gss_new_user_password2').val() || !$('#gss_new_user_password1').val()) {
            alert(__f('Password can not be empty'));
            return false;
        }
        
        if ($('#gss_new_user_password2').val() != $('#gss_new_user_password1').val()) {
            alert(__f('Please check your password'));
            return false;
        }
        
        return true;
    }
}