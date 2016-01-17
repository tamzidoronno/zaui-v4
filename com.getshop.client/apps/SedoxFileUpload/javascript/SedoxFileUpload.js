app.SedoxFileUpload = {
    init: function() {
        $(document).on('change', '#sedox_file_upload_selector', app.SedoxFileUpload.fileSelected);
        $(document).on('click', '.SedoxFileUpload .select_new_file', app.SedoxFileUpload.showModal);
        $(document).on('click', '.SedoxFileUpload .closebutton', app.SedoxFileUpload.closeModal);
    },
    
    
    setProgress: function(progess) {
        if (progess) {
            progess = progess.toFixed(2);
        }
        
        $('.SedoxFileUpload .meter span').css('width', progess+"%");
        $('.SedoxFileUpload .meter .progresindicator').html(progess+"%");
    },
    
    fileSelected: function() {
        $('.SedoxFileUpload .uploadfilemodal .progressbararea').show();
        $('.SedoxFileUpload .uploadfilemodal .selectarea').hide();
        
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
                app.SedoxFileUpload.checkForFileMatch(spark.end(), this);
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
    },
    
    closeModal: function() {
        $('.SedoxFileUpload .uploadfilemodal').fadeOut();
    },
    
    showModal: function() {
        $('.SedoxFileUpload .uploadfilemodal .progressbararea').hide();
        $('.SedoxFileUpload .uploadfilemodal .selectarea').show();
        $('.SedoxFileUpload .uploadfilemodal').fadeIn();
    },
    
    selectBasedOnMd5: function() {
        alert("Found a match.");
    },
    
    showUploadProgress: function(files) {
        var file = files[0];
        var fileName = file.name;

        var reader = new FileReader();

        $('.SedoxFileUpload .sedox_upload_filename').html(fileName);
        reader.onload = function(event) {
            var dataUri = event.target.result;

            var data = {
                fileBase64: dataUri,
                fileName: fileName
            };

            var event = thundashop.Ajax.createEvent(null, "uploadFile", $('#sedox_file_upload_selector'), data);
            
            thundashop.Ajax.post(
                    event,
                    app.SedoxFileUpload.closeModal,
                    null,
                    false,
                    true,
                    {
                        "uploadcallback": app.SedoxFileUpload.setProgress
                    });
        };

        reader.onerror = function(event) {
            console.error("File could not be read! Code " + event.target.error.code);
        };

        reader.readAsDataURL(file);
    },
    
    checkForFileMatch: function(md5, fileReader) {
        var files = document.getElementById("sedox_file_upload_selector").files;
        var data = {
            md5 : md5
        }
        
        var event = thundashop.Ajax.createEvent(null, "doesProductExists", $('.SedoxFileUpload'), data);
        event['synchron'] = true;
        
        thundashop.Ajax.postWithCallBack(event, function(res) {
            if (res !== "false") {
                app.SedoxFileUpload.selectBasedOnMd5(md5);
            } else {
                app.SedoxFileUpload.showUploadProgress(files);
            }
        });
    }
    
};

app.SedoxFileUpload.init();