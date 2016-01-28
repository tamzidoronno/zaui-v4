app.SedoxLogin = {
    init: function() {
        $(document).on('keyup', '.SedoxLogin input[gsname="username"]', app.SedoxLogin.keyUp);
        $(document).on('keyup', '.SedoxLogin input[gsname="password"]', app.SedoxLogin.keyUp);
        $(document).on('click', '.SedoxLogin .dologin', app.SedoxLogin.doLogin);
    },
    
    keyUp: function(e) {
        if(e.keyCode == 13)
        {
            $('.SedoxLogin .dologin').click();
        }
    },
    
    doLogin: function() {
        $('.SedoxLogin .login_check_modal .content').html("<i class='fa fa-spin fa-spinner'></i>&nbsp;&nbsp; Checking username and password, please wait..");
        $('.SedoxLogin .login_check_modal').fadeIn();
        
        var data = {
            sedoxlogin: $('.SedoxLogin input[gsname="sedoxlogin"]').val(),
            username: $('.SedoxLogin input[gsname="username"]').val(),
            password: $('.SedoxLogin input[gsname="password"]').val(),
        }
        
        var event = thundashop.Ajax.createEvent(null, "login", this, data);
        
        event['synchron'] = true;
        thundashop.Ajax.postWithCallBack(event, app.SedoxLogin.loginResult, null, true, true);
    },
    
    loginResult: function(result) {
        if (result === "success") {
            document.location = "/?page=ab359dd6-062d-4c3f-a5d2-d3361f78d22b";
        } else {
            $('.SedoxLogin .login_check_modal .content').html("Wrong username or password!");
            setTimeout(function() {
                $('.SedoxLogin .login_check_modal').fadeOut(); 
            }, 2000);
        }
    }
};

app.SedoxLogin.init();