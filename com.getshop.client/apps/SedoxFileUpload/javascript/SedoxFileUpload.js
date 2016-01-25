app.SedoxFileUpload = {
    init: function() {
        $(document).on('change', '#sedox_file_upload_selector', app.SedoxFileUpload.fileSelected);
        $(document).on('click', '.SedoxFileUpload .select_new_file', app.SedoxFileUpload.showModal);
        $(document).on('click', '.SedoxFileUpload .closebutton', app.SedoxFileUpload.closeModal);
        
        $(document).on('dragover', '.SedoxFileUpload #dragdropfilesareas', app.SedoxFileUpload.handleDragOver);
        $(document).on('dragleave', '.SedoxFileUpload #dragdropfilesareas', app.SedoxFileUpload.handleDragOut);
        $(document).on('drop', '.SedoxFileUpload #dragdropfilesareas', app.SedoxFileUpload.handleFileSelect);
        
        $(document).on('click', '.SedoxFileUpload .go_to_next_step_upload', app.SedoxFileUpload.goToNextStep);
    },
    
    checkRquiredFields: function() {
        var allOk = true;
        
        $('.SedoxFileUpload input[required="true"]').each(function() {
            if (!$(this).val()) {
                $(this).addClass('field_mandatory');
                allOk = false;
            } else {
                $(this).removeClass('field_mandatory');
            }
        });
        
        return allOk;
    },
    
    goToNextStep: function() {
        if (!app.SedoxFileUpload.checkRquiredFields()) {
            alert("Check fields in red");
            return;
        }
        
        if ($('.SedoxFileUpload .file_selector_area').attr('isselected') !== "true") {
            alert("Please select a file first");
            return;
        }
        
        var data = {
            upload_brand : $('#upload_brand').val(),
            upload_model : $('#upload_model').val(),
            upload_enginesize : $('#upload_enginesize').val(),
            upload_power : $('#upload_power').val(),
            upload_year : $('#upload_year').val(),
            upload_tool : $('#upload_tool').val(),
            upload_comment : $('#upload_comment').val(),
            upload_reference : $('#upload_reference').val(),
            upload_withdraw : $('#upload_withdraw').val(),
            upload_automatic : $('input[name="sedox_upload_automatic"]').is(':checked'),
            upload_widthdraw : $('input[name="sedox_upload_withdraw"]').is(':checked'),
            
            // New
            upload_dpf : $('input[name="sedox_checkbox_dpf"]').is(':checked'),
            upload_egr : $('input[name="sedox_checkbox_egr"]').is(':checked'),
            upload_decat : $('input[name="sedox_checkbox_decat"]').is(':checked'),
            upload_vmax : $('input[name="sedox_checkbox_vmax"]').is(':checked'),
            upload_adblue : $('input[name="sedox_checkbox_adblue"]').is(':checked'),
            upload_dtc : $('input[name="sedox_checkbox_dtc"]').is(':checked'),
            
        }
        
        if ($('#partnerselect').length > 0) {
            data['selected_parther'] = $('#partnerselect').val()
        }
        
        var event = thundashop.Ajax.createEvent(null, "completeUpload", this, data);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            thundashop.common.goToPage("upload_successful");
        });
        
    },
    
    handleDragOut: function(jevt) {
        $(this).removeClass('sedox_dropovereffect');
    },
    
    handleDragOver: function(jevt) {
        var evt = jevt.originalEvent;
        evt.stopPropagation();
        evt.preventDefault();
        evt.dataTransfer.dropEffect = 'copy'; // Explicitly show this is a copy.
        $(this).addClass('sedox_dropovereffect');
    },
    
    handleFileSelect: function(jevt) {
        var files = jevt.originalEvent.dataTransfer.files;
        jevt.preventDefault();
        jevt.stopPropagation();
        
        app.SedoxFileUpload.uploadFile(files[0]);
    },
    
    setProgress: function(progess) {
        if (progess) {
            progess = progess.toFixed(2);
        }
        
        $('.SedoxFileUpload .meter span').css('width', progess+"%");
        $('.SedoxFileUpload .meter .progresindicator').html(progess+"%");
    },
    
    fileSelected: function() {
        
        app.SedoxFileUpload.uploadFile(this.files[0]);
    },
    
    uploadFile: function(file) {
        $('.SedoxFileUpload .uploadfilemodal .progressbararea').show();
        $('.SedoxFileUpload .uploadfilemodal .selectarea').hide();
        
        var blobSlice = File.prototype.slice || File.prototype.mozSlice || File.prototype.webkitSlice,
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
                app.SedoxFileUpload.checkForFileMatch(spark.end(), file);
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
    
    closeModal: function(response) {
        var content = $(response.content).find('.file_selector_area');
        $(document).find('.file_selector_area').replaceWith(content);
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
    
    showUploadProgress: function(file) {
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
                    true,
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
    
    checkForFileMatch: function(md5, file) {
        var data = {
            md5 : md5
        }
        
        var event = thundashop.Ajax.createEvent(null, "doesProductExists", $('.SedoxFileUpload'), data);
        event['synchron'] = true;
        
        thundashop.Ajax.postWithCallBack(event, function(res) {
            if (res !== "false") {
                app.SedoxFileUpload.selectBasedOnMd5(md5);
            } else {
                app.SedoxFileUpload.showUploadProgress(file);
            }
        });
    }
    
};

app.SedoxFileUpload.init();