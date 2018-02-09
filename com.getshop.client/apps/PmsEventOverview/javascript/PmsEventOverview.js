app.PmsEventOverview = {
    init : function() {
        $(document).on('click', 'PmsEventOverview .button', app.PmsEventOverview.showMessage)
    },
    showMessage : function() {
        console.log('Message logged');
    },
    loadSettings: function (element, application) {
        var config = {
            draggable: true,
            app: true,
            showSettings : true,
            application: application,
            title: "Settings",
            items: []
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
}
app.PmsEventOverview.init();