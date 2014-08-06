app.SedoxFileUpload = {
    init: function() {
        $(document).on('click', '.SedoxFileUpload .savecardetails', app.SedoxFileUpload.saveCarDetails);
        $(document).on('click', '.SedoxFileUpload .specialfile', app.SedoxFileUpload.specialfilerequest);
        $(document).on('click', '.SedoxFileUpload .sendspecialrequest', app.SedoxFileUpload.sendSpecialRequest);
        $(document).on('click', '.SedoxFileUpload .saveupload', app.SedoxFileUpload.saveUploadFile);
    },
            
    saveUploadFile: function() {
        if ($('#originalfile').val() == "") {
            thundashop.common.Alert("File not selected", "You have not selected a file, please add a file before you continue click next.", true);
            return;
        }
        if ($('#brand').val() == "") {
            thundashop.common.Alert("Cant be empty", "Field Brand is mandatatory, it can not be empty.", true);
            return;
        }
        if ($('#model').val() == "") {
            thundashop.common.Alert("Cant be empty", "Field Model is mandatatory, it can not be empty.", true);
            return;
        }
        if ($('#enginesize').val() == "") {
            thundashop.common.Alert("Cant be empty", "Field EngineSize is mandatatory, it can not be empty.", true);
            return;
        }
        if ($('#power').val() == "") {
            thundashop.common.Alert("Cant be empty", "Field Power is mandatatory, it can not be empty.", true);
            return;
        }
        if ($('#year').val() == "") {
            thundashop.common.Alert("Cant be empty", "Field Year is mandatatory, it can not be empty.", true);
            return;
        }
        if ($('#tool').val() == "") {
            thundashop.common.Alert("Cant be empty", "Field Tool is mandatatory, it can not be empty.", true);
            return;
        }
        
        $('#uploadfileform').submit();
    },
            
    specialfilerequest: function() {
        $('.SedoxFileUpload .specialform').slideDown();
    },
            
    sendSpecialRequest: function() {
        var data = {
            desc : $('.SedoxFileUpload .specialform textarea').val(),
            fileId: $(this).attr('fileid')
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