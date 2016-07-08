app.SedoxAdmin = {
    
    init: function() {
        $(document).on('change', '.SedoxAdmin #sedox_file_upload_selector', app.SedoxAdmin.fileSelected);
        $(document).on('click', '.SedoxAdmin .closebutton', app.SedoxAdmin.closeModal);
        $(document).on('click', '.SedoxAdmin .sedox_admin_upload_file', app.SedoxAdmin.showModal);
        $(document).on('click', '.SedoxAdmin .filetype', app.SedoxAdmin.fileTypeSelected);
        $(document).on('click', '.SedoxAdmin .setChecksum', app.SedoxAdmin.setChecksum);
        $(document).on('click', '.SedoxAdmin .sedoxadmin_see_user_button', app.SedoxAdmin.showUser);
        $(document).on('click', '.SedoxAdmin .sendProductToDifferentEmail', app.SedoxAdmin.sendProductToDifferentEmail);
        
        $(document).on('dragover', '.SedoxAdmin #dragdropfilesareas', app.SedoxAdmin.handleDragOver);
        $(document).on('dragleave', '.SedoxAdmin #dragdropfilesareas', app.SedoxAdmin.handleDragOut);
        $(document).on('drop', '.SedoxAdmin #dragdropfilesareas', app.SedoxAdmin.handleFileSelect);
        getshop.WebSocketClient.addListener("com.thundashop.core.sedox.ProductStartStopToggle", app.SedoxAdmin.notificationReceived);
    },
    
    sendProductToDifferentEmail: function() {
        var email = prompt("Where do you want to send it? (email)");
        
        var data = {
            email : email,
            productId : $(this).attr('productId'),
            fileId : $(this).attr('sedox_file_id')
        };
        
        var event = thundashop.Ajax.createEvent(null, "sendProductToDifferentEmail", this, data);
        thundashop.Ajax.post(event, function() {
            alert("Sent! ;D");
        });
    },
    
    notificationReceived: function(data) {
        var currentUserId = $('[name="userid"]').val();
        
        if (currentUserId !== data.userid) {
            var dataToPost = {
                productId : data.productId
            };
            
            var event = thundashop.Ajax.createEvent(null, "renderProduct", $('.SedoxAdmin'), dataToPost);
            thundashop.Ajax.postWithCallBack(event, function(res) {
                $('.col_row_content[productid=90650]').html(res);
            })
        }  
    },
    
    showUser: function() {
        app.Users.gssinterface.showUser($(this).attr('userid'));
    },
    
    setChecksum: function() {
        var checksum = prompt("Please enter the checksum");
        if(!checksum) {
          checksum = "";
        }
        thundashop.Ajax.simplePost(this, "setChecksum", {
            checksum: checksum,
            productId : $(this).closest('.col_row_content').attr('productid'),
            fileId : $(this).attr('sedox_file_id')
        });
    },
    
    fileTypeSelected: function() {
        var data = {
            productid : app.SedoxAdmin.currentProductId,
            type : $(this).attr('type')
        }
        
        var event = thundashop.Ajax.createEvent(null, "finalizeFileUpload", this, data);
        thundashop.Ajax.post(event, null, {}, true, true);
//        thundashop.Ajax.simplePost(this, "finalizeFileUpload", data); 
        $('.sedox_internal_view[productid="'+data.productid+'"] .overlayblocker').fadeIn();
        $('.closebutton').click();
        
    },
    
    handleDragOut: function(jevt) {
        $('.sedox_dropovereffect').removeClass('sedox_dropovereffect');
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
        
        app.SedoxAdmin.showUploadProgress(files[0]);
    },
    
    showModal: function() {
        app.SedoxAdmin.currentProductId = $(this).attr('productid');
        $('.SedoxAdmin .selectfiletypearea .filetype').removeClass("origType");
        $('.SedoxAdmin .selectfiletypearea .filetype[type="' + $(this).attr('sedox_file_type') + '"]').addClass("origType");
        $('.SedoxAdmin .uploadfilemodal .progressbararea').hide();
        $('.SedoxAdmin .uploadfilemodal .selectarea').show();
        $('.SedoxAdmin .uploadfilemodal').fadeIn();
    },
    
    closeModal: function(response) {
        var content = $(response.content).find('.file_selector_area');
        $(document).find('.file_selector_area').replaceWith(content);
        $('.SedoxAdmin .uploadfilemodal').fadeOut();
    },
    
    fileSelected: function() {    
        app.SedoxAdmin.showUploadProgress(this.files[0]);
    },
    
    showUploadProgress: function(file) {
        $('.SedoxAdmin .uploadfilemodal .progressbararea').show();
        $('.SedoxAdmin .uploadfilemodal .progressbararea .meter').show();
        $('.SedoxAdmin .uploadfilemodal .progressbararea .selectarea').hide();
        $('.SedoxAdmin .uploadfilemodal .selectarea').hide();
        app.SedoxAdmin.handleDragOut();
        
        var fileName = file.name;
        var reader = new FileReader();

        reader.onload = function(event) {
            var dataUri = event.target.result;

            var data = {
                fileBase64: dataUri,
                fileName: fileName
            };

            var event = thundashop.Ajax.createEvent(null, "uploadFile", $('#sedox_file_upload_selector'), data);
            
            thundashop.Ajax.post(
                    event,
                    app.SedoxAdmin.fileUploaded,
                    null,
                    true,
                    true,
                    {
                        "uploadcallback": app.SedoxAdmin.setProgress
                    });
        };

        reader.onerror = function(event) {
            console.error("File could not be read! Code " + event.target.error.code);
        };

        reader.readAsDataURL(file);
    },
    
    setProgress: function(progess) {
        if (progess) {
            progess = progess.toFixed(2);
        }
        
        $('.SedoxAdmin .meter span').css('width', progess+"%");
        $('.SedoxAdmin .meter .progresindicator').html(progess+"%");
    },
    
    fileUploaded: function() {
        $('.SedoxAdmin .uploadfilemodal .progressbararea .meter').hide();
        $('.SedoxAdmin .uploadfilemodal .progressbararea .selectarea').show();
    }
}

app.SedoxAdmin.init();

