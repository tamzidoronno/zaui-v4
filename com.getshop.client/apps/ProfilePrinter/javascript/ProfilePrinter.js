app.ProfilePrinter = {
    loadSettings: function(element, application) {
        var config = { showSettings : true, draggable: true, title: "Settings", items: [] }
        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
}