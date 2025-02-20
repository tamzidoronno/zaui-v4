app.SedoxLogin = {
    init: function() {
        $(document).on('keyup', '.SedoxLogin input[gsname="username"]', app.SedoxLogin.keyUp);
        $(document).on('keyup', '.SedoxLogin input[gsname="password"]', app.SedoxLogin.keyUp);
        $(document).on('click', '.SedoxLogin .dologin', app.SedoxLogin.doLogin);
        $(document).on('click', '.SedoxLogin .register_user', app.SedoxLogin.registerUser);
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
        };
        
        var event = thundashop.Ajax.createEvent(null, "login", this, data);
    
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(result) {
            app.SedoxLogin.loginResult(result, data.username, data.password);
        }, true);
    },
    
    registerUser: function() {
        if ($('.SedoxLogin input[gsname="create_password"]').val() !== $('.SedoxLogin input[gsname="create_password2"]').val()) {
            alert("Password does not match, please check");
            return;
        }
        
        $('.SedoxLogin .login_check_modal .content').html("<i class='fa fa-spin fa-spinner'></i>&nbsp;&nbsp; Creating account, please wait..");
        $('.SedoxLogin .login_check_modal').fadeIn();
        
        var user = {
            firstname: $('.SedoxLogin input[gsname="create_firstname"]').val(),
            lastname: $('.SedoxLogin input[gsname="create_lastname"]').val(),
            email: $('.SedoxLogin input[gsname="create_email"]').val(),
            tax: $('.SedoxLogin input[gsname="create_tax"]').val(),
            password: $('.SedoxLogin input[gsname="create_password"]').val(),
        };
        
        var event = thundashop.Ajax.createEvent(null, "createAccount", this, user);
        
        event['synchron'] = true;
        thundashop.Ajax.postWithCallBack(event, app.SedoxLogin.createResult, null, true, true);
    },
    
    createResult: function(result) {
        if (result) {
            $('.SedoxLogin .login_check_modal .content').html(result);
            setTimeout(function() {
                $('.SedoxLogin .login_check_modal').fadeOut(); 
            }, 5000);
        } else {
            $('.SedoxLogin .login_check_modal .content').html("Account created, please check your email account and verify your account before you can login");
            $('*[gsname]').val("");
            setTimeout(function() {
                $('.SedoxLogin .login_check_modal').fadeOut(); 
            }, 2000);
        }
    },
    
    loginResult: function(result, username, password) {
//        debugger;
        if (result === "success") {
            
            var form = document.createElement("form");
            var element1 = document.createElement("input"); 
            var element2 = document.createElement("input");  

            form.method = "POST";
            form.action = "/?page=ab359dd6-062d-4c3f-a5d2-d3361f78d22b";   

            element1.value=username;
            element1.name="username";
            element1.type="text";
            form.appendChild(element1);  

            element2.value=password;
            element2.name="password";
            element2.type="password";
            form.appendChild(element2);

            document.body.appendChild(form);

            form.submit();
        } else {
            $('.SedoxLogin .login_check_modal .content').html("Wrong username or password!");
            setTimeout(function() {
                $('.SedoxLogin .login_check_modal').fadeOut(); 
            }, 2000);
        }
    },
    
    config: function() {
        var key = prompt("Please enter key");
        if (!key) {
            return;"marte eline"
        }
        
        var address = prompt("Please enter address");
        if (!address) {
            return;
        }
        
        thundashop.Ajax.simplePost(this, "saveApiKey", { key : key });
        thundashop.Ajax.simplePost(this, "saveAddress", { address : address });
    },
    
    loadSettings : function(element, application) {
         var config = {
            draggable: true,
            app : true,
            application: application,
            title: "Settings",
            items: [
                {
                    icontype: "awesome",
                    icon: "fa-edit",
                    iconsize : "30",
                    title: __f("Config"),
                    click: app.SedoxLogin.config
                }
            ]
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
};

app.SedoxLogin.init();