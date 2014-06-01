app.SedoxLogin = {
    init: function() {
        $(document).on('click', '.SedoxLogin .logout', app.SedoxLogin.logout)
        $(document).on('click', '.SedoxLogin .sedox_login', app.SedoxLogin.login)
    },
    login: function() {
        var data = {
            emailAddress : $('.SedoxLogin #emailAddress').val(),
            password : $('.SedoxLogin #password').val()
        }
        
        var event = thundashop.Ajax.createEvent("", "login", this, data);
        thundashop.Ajax.post(event);
    },
    logout: function() {
        var dom = $('#getshop_logout');
        var event = thundashop.Ajax.createEvent("", "logout", dom, {});
        thundashop.Ajax.postWithCallBack(event, function() {
            document.location = "/index.php";
        });
    },
    loadSettings : function(element, application) {
         var config = {
            draggable: true,
            app : true,
            application: application,
            title: "Settings",
            items: []
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
};

app.SedoxLogin.init();