ProMeisterLogin = {
    init: function() {
        $(document).on('click', '.login_create_new_account', ProMeisterLogin.showStep1)
    },
    
    showStep1: function() {
        $('.signupform_content_signin').hide();
        $('.signup_step1').fadeIn();
    }
}

ProMeisterLogin.init();