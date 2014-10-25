app.SedoxFileUpload = {
    init: function() {
        $(document).on('click', '.SedoxFileUpload .savecardetails', app.SedoxFileUpload.saveCarDetails);
        $(document).on('click', '.SedoxFileUpload .specialfile', app.SedoxFileUpload.specialfilerequest);
        $(document).on('click', '.SedoxFileUpload .sendspecialrequest', app.SedoxFileUpload.sendSpecialRequest);
        $(document).on('click', '.SedoxFileUpload .saveupload', app.SedoxFileUpload.saveUploadFile);
    },
    
    addListeningForFileChangeToCalculateMd5: function() {
        var fileApiSupported = window.File && window.FileReader && window.FileList && window.Blob;
        if (!fileApiSupported) {
            return;
        }

        document.getElementById("originalfile").addEventListener("change", function() {
            $('.SedoxFileUpload .md5sumsearchresult').html("Calculating checksum of your file...");
            var blobSlice = File.prototype.slice || File.prototype.mozSlice || File.prototype.webkitSlice,
            file = this.files[0],
            chunkSize = 2097152,
            chunks = Math.ceil(file.size / chunkSize),
            currentChunk = 0,
            spark = new SparkMD5.ArrayBuffer(),
            frOnload = function(e) {
                spark.append(e.target.result);
                currentChunk++;

                if (currentChunk < chunks) {
                loadNext();
                } else {

                var data = {
                    md5 : spark.end()
                }
                $('.SedoxFileUpload .md5sumsearchresult').html("Please wait while searching for a product that matches your file...");
                var event = thundashop.Ajax.createEvent(null, "doesProductExists", $('.SedoxFileUpload'), data);
                event['synchron'] = true;
                thundashop.Ajax.postWithCallBack(event, function(res) {
                    if (res !== "false") {
                    delete event['synchron'];
                    thundashop.Ajax.post(event);
                    $('.SedoxFileUpload .md5sumsearchresult').html("Found product, please wait.");
                    } else {
                    $('.SedoxFileUpload .md5sumsearchresult').html("We do not have a matching product<br/>Please continue by enter the information in the boxes below.");
                    }
                })
                }
            },
            frOnerror = function () {
                $('.SedoxFileUpload .md5sumsearchresult').html("");
            };

            function loadNext() {
            var fileReader = new FileReader();
            fileReader.onload = frOnload;
            fileReader.onerror = frOnerror;

            var start = currentChunk * chunkSize,
                end = ((start + chunkSize) >= file.size) ? file.size : start + chunkSize;

            fileReader.readAsArrayBuffer(blobSlice.call(file, start, end));
            };

            loadNext();
        });
    },
            
    saveUploadFile: function() {
		if ($(this).attr('sending') === "true") {
			return;
		}
	
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
	
        $(this).attr('sending', 'true');
		$(this).find('span').html('Sending..');
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