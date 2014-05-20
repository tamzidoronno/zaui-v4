app.SedoxProductView = {
    init: function() {
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
            
            var file = files[0];
            var fileName = file.name;
            var fileType = $(this).attr('fileType');
            
            var reader = new FileReader();
            var button = this;
            
            reader.onload = function(event) {
                var dataUri = event.target.result;
                
                var data = {
                    fileBase64: dataUri,
                    fileName: fileName,
                    fileType: fileType
                };

                var event = thundashop.Ajax.createEvent(null, "saveModifiedFile", originalButton, data);
                var me = this;
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
                                app.SedoxProductView.uploadProgress(me, button);
                            }
                        });
            };

            reader.onerror = function(event) {
                console.error("File could not be read! Code " + event.target.error.code);
            };

            reader.readAsDataURL(file);
            //We need to send dropped files to Server
//            handleFileUpload(files, $(this));
        });
    },
    uploadProgressCompleted: function(button) {
        $(button).find('.progressbar').hide();
    },
    uploadProgress: function(button, progress) {
        $(button).find('.progressbar').html(progress+"%");
    }
};

app.SedoxProductView.init();