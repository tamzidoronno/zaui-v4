app.NewsElements = { 
    
    init: function() {
        PubSub.subscribe("SIMPELFILEUPLOAD_COMPLETED", function(data, test) {
            $('#imgtodisplay').attr('src', 'displayImage.php?id='+test);
        });
    },
    loadSettings: function(element, application) {
        var config = { showSettings : true, draggable: true, title: "Settings", items: [] }
        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
};

app.NewsElements.init();