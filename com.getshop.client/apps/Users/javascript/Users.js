app.Users = {
    init: function() {
        $(document).on('click','#autogeneratepassword', app.Users.autoGenPassword);
    },
    autoGenPassword: function() {
        var randomstring = Math.random().toString(36).slice(-8);
        $('#gss_new_user_password1').val(randomstring);
        $('#gss_new_user_password2').val(randomstring);
        $('#gss_new_user_password1').change();
        $('#gss_new_user_password2').change();
        
        $('#gss_new_user_password1').attr('type', 'textfield');
        $('#gss_new_user_password2').attr('type', 'textfield');
    },
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
    },
    
    refresh: function(field, response) {
        var userId = $(field).closest('.gss_overrideapp').find('input[gs_model_attr="userid"]').val()
        
        var data = {
            gss_fragment: 'user',
            gss_view: 'gs_user_workarea',
            gss_value: userId
        }

        getshop.Settings.post({}, "gs_show_fragment", data);
        getshop.Settings.showSuccessMessage("Successfully updated");
    }
}

app.Users.gssinterface =  {
    showUser: function(userId) {
        getshop.Settings.showSettings();
        getshop.Settings.setApplicationId('ba6f5e74-87c7-4825-9606-f2d3c93d292f', function () {
            var data = {
                gss_fragment: 'user',
                gss_view: 'gs_user_workarea',
                gss_value: userId
            }

            getshop.Settings.post({}, "gs_show_fragment", data);
        });
    }
};

app.Users.init();