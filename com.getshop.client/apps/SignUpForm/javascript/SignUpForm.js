app.SignUpForm = {
    loadSettings: function(element, application) {
        var config = {
            application: application,
            draggable: true,
            title: "Settings",
            items: []
        }
        
        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
}