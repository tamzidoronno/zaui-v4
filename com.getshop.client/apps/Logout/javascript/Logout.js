app.Logout = {
    initEvents : function() {
        $(document).on('click', '.Logout .logoutclick', app.Logout.logout);
    },
    logout : function() {
        var event = thundashop.Ajax.createEvent('','logout',$(this), {});
        thundashop.Ajax.postWithCallBack(event, function() {
            window.location.href = window.location.href;
        });
    },
    loadSettings : function(element, application) {
         var config = {
            draggable: true,
            app : true,
            showSettings: true,
            application: application,
            title: "Settings",
            items: []
        };

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
};
app.Logout.initEvents();