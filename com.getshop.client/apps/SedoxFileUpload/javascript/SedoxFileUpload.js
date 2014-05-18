app.SedoxFileUpload = {
    init: function() {
        $(document).on('click', '.SedoxFileUpload .savecardetails', app.SedoxFileUpload.saveCarDetails);
    },
            
    saveCarDetails: function() {
        data = {
            brand : $(".SedoxFileUpload #brand").val(),
            model : $(".SedoxFileUpload #model").val(),
            enginesize : $(".SedoxFileUpload #enginesize").val(),
            power : $(".SedoxFileUpload #power").val(),
            year : $(".SedoxFileUpload #year").val(),
            tool : $(".SedoxFileUpload #tool").val(),
            comments : $(".SedoxFileUpload #comments").val(),
            automaticgear : $(".SedoxFileUpload #automaticgear").is(':checked'),
            usecredit : $(".SedoxFileUpload #usecredit").is(':checked'),
        };
        var event = thundashop.Ajax.createEvent(null, "saveCarDetails", this, data);
        thundashop.Ajax.post(event);
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

app.SedoxFileUpload.init();