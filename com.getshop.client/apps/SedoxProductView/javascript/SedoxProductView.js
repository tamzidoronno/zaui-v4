app.SedoxProductView = {
    uploadBoxClick: function() {
        var originalButton = $(this);
        $('#getshop_select_files_link').remove();
        $('#your-files').remove();
        
        var selectDialogueLink = $('<a href="" id="getshop_select_files_link">Select files</a>');
        var fileSelector = $('<input type="file" id="your-files" multiple/>');

        selectDialogueLink.click(function() {
            fileSelector.click();
        });
        $('body').append(fileSelector);
        $('body').append(selectDialogueLink);

        var control = document.getElementById("your-files");
        var me = this;

        control.addEventListener("change", function() {
            fileSelector.remove();
            app.SedoxProductView.imageSelected(control.files, originalButton);
        });

        selectDialogueLink.click();
        selectDialogueLink.remove();
    },
          
    imageSelected: function(files, originalButton) {
        var file = files[0];
        var fileName = file.name;
        var fileType = originalButton.attr('fileType');

        var reader = new FileReader();
        var button = originalButton;

        reader.onload = function(event) {
            var dataUri = event.target.result;

            var data = {
                fileBase64: dataUri,
                fileName: fileName,
                fileType: fileType
            };

            var event = thundashop.Ajax.createEvent(null, "saveModifiedFile", originalButton, data);
            $(button).find('.progressbar').html("0" + "%");
            $(button).find('.progressbar').show();
            
            thundashop.Ajax.post(
                    event,
                    function() {
                        app.SedoxProductView.uploadProgressCompleted(button);
                    },
                    null,
                    false,
                    true,
                    {
                        "uploadcallback": function(prog) {
                            app.SedoxProductView.uploadProgress(button, prog);
                        }
                    });
        };

        reader.onerror = function(event) {
            console.error("File could not be read! Code " + event.target.error.code);
        };

        reader.readAsDataURL(file);
            //We need to send dropped files to Server
//            handleFileUpload(files, $(this));
    },
            
    init: function() {
        var me = this;
        $(document).on('click', '.SedoxProductView .purchase', app.SedoxProductView.purchaseFile);
        $(document).on('click', '.SedoxProductView .purchaseorderonly', app.SedoxProductView.purchaseorderonly);
        $(document).on('click', '.SedoxProductView .notifybyemailandsms', app.SedoxProductView.notifybyemailandsms);
        $(document).on('click', '.SedoxProductView .sendproductbyemail', app.SedoxProductView.sendproductbyemail);
        $(document).on('click', '.SedoxProductView .deletebinfile', app.SedoxProductView.deleteBinFile);
        $(document).on('click', '.SedoxProductView .changeinfo', app.SedoxProductView.changeInfo);
        $(document).on('click', '.SedoxProductView .saveextrainfo', app.SedoxProductView.saveExtraInfo);
        $(document).on('click', '.SedoxProductView .uploadtuningfilebox', app.SedoxProductView.uploadBoxClick);
        
        $(document).on('dragenter', '.SedoxProductView .uploadtuningfilebox', function(e)
        {
            e.stopPropagation();
            e.preventDefault();
        });
        $(document).on('dragover', '.SedoxProductView .uploadtuningfilebox', function(e)
        {
            e.stopPropagation();
            e.preventDefault();
            $(this).css('border', '2px dotted #0B85A1');
        });
        $(document).on('dragleave', '.SedoxProductView .uploadtuningfilebox', function(e)
        {
            e.stopPropagation();
            e.preventDefault();
            $(this).css('border', '2px solid #DDD');
        });

        $(document).on('drop', '.SedoxProductView .uploadtuningfilebox', function(e) {
            var originalButton = $(this);
            e.preventDefault();
            var files = e.originalEvent.dataTransfer.files;
            me.imageSelected(files, originalButton);
            
        });
    },
    
    changeInfo: function()  {
        var data = {
            fileid: $(this).attr('fileid'),
            productid: $(this).attr('productid'),
        };
        
        var event = thundashop.Ajax.createEvent(null, "showAddInformation", this, data);
        thundashop.common.showInformationBox(event, "Change file extra information");
    },
    
    deleteBinFile: function() {
        var binFileId = $(this).closest('.binaryfilerow').attr('binfileid');
        var productId = $(this).attr('productid');
        
        var data = {
            binFileId : binFileId,
            productId : productId
        };
        
        var event = thundashop.Ajax.createEvent("", "deleteBinaryFile", this, data);
        thundashop.Ajax.post(event);
    },
    getData : function(me) {
        var files = [];
        
        $('.SedoxProductView input:checked').each(function() { 
            files.push($(this).attr('fileid'));
        });
        
        var data = {
            productId : $(me).attr('productId'),
            extraInfo : $('.SedoxProductView .infotextareatocustomer').val(),
            checksum : $('.SedoxProductView .original_check_input_box').val(),
            files: files
        };
        
        return data;
    },
            
    purchaseorderonly: function() {
        var data = app.SedoxProductView.getData(this);
        var event = thundashop.Ajax.createEvent("", "purchaseProductOnly", this, data);
        thundashop.Ajax.post(event, function() { thundashop.common.Alert("Message sent", "purchase completed")} );
    },
    notifybyemailandsms: function() {
        var data = app.SedoxProductView.getData(this);
        var event = thundashop.Ajax.createEvent("", "notifyByEmailAndSms", this, data);
        thundashop.Ajax.post(event, function() { thundashop.common.Alert("Message sent", "a mail is on its way to the customer")} );
    },
    sendproductbyemail: function() {
        var data = app.SedoxProductView.getData(this);
        var event = thundashop.Ajax.createEvent("", "sendProductByEmail", this, data);
        thundashop.Ajax.post(event, function() { thundashop.common.Alert("Message sent", "product is now under its way to the customer")} );
    },
    uploadProgressCompleted: function(button) {
        $(button).find('.progressbar').hide();
    },
    uploadProgress: function(button, progress) {
        progress = Math.round(progress * 100) / 100;
        $(button).find('.progressbar').html(progress+"%");
    },
            
    purchaseFile: function() {
        var files = [];
        $('.SedoxProductView input:checked').each(function() { 
            files.push($(this).attr('fileid'));
        });
        
        var data = {
            files : files
        };
        
        var me = this;
        
        var event = thundashop.Ajax.createEvent(null, "purchaseProduct", this, data);
        thundashop.Ajax.postWithCallBack(event, function(response) {
            
            var event = thundashop.Ajax.createEvent("", "dummy", me, {});
            thundashop.Ajax.post(event, function() {
                window.location = response;
            });
            
            
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
    },
            
    saveExtraInfo: function() {
         var data = {
            fileid: $(this).attr('fileid'),
            productid: $(this).attr('productid'),
            text: $('#extrafileinformationbox').val()
        };
        
        var event = thundashop.Ajax.createEvent(null, "setAddInformation", this, data);
        thundashop.Ajax.post(event);
        thundashop.common.hideInformationBox();
    }
};

app.SedoxProductView.init();