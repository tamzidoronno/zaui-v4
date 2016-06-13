app.C3EventRegistration = {
    init: function() {
    },
    
    loadSettings: function(element, application) {
        var config = { 
            showSettings : true,
            application: application,
            draggable: true, 
            title: "Settings", 
            items: [
                
            ]
        }
        
        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
}