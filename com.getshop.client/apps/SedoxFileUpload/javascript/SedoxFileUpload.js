app.SedoxFileUpload = {
    init: function() {
        $(document).on('click', '.SedoxFileUpload .savecardetails', app.SedoxFileUpload.saveCarDetails);
        $(document).on('click', '.SedoxFileUpload .specialfile', app.SedoxFileUpload.specialfilerequest);
        $(document).on('click', '.SedoxFileUpload .sendspecialrequest', app.SedoxFileUpload.sendSpecialRequest);
    },
            
    specialfilerequest: function() {
        $('.SedoxFileUpload .specialform').slideDown();
    },
            
    sendSpecialRequest: function() {
        var data = {
            desc : $('.SedoxFileUpload .specialform textarea').val()
        };
        
        var event = thundashop.Ajax.createEvent("", "sendSpecialRequest", this, data);
        thundashop.Ajax.post(event);
    },
            
    saveCarDetails: function() {
        var selectbox = $('#partnerselect');
        var slaveAccount = null;
        
        if (selectbox) {
            slaveAccount = selectbox.val();
        }
        
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
            slaveAccount : slaveAccount
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