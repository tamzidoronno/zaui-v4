app.Products = {
    currentProductId: null,
    
    init: function() {
        $(document).on('click', '.gss_product_saveuploadimage', app.Products.uploadBoxClick);
    },
    
    productImageRemoved: function(field) {
        $(field).parent().fadeOut(100);
    },
    
    uploadBoxClick: function() {
        app.Products.currentProductId = $(this).attr('productid');
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
            app.Products.imageSelected(control.files);
        });

        selectDialogueLink.click();
        selectDialogueLink.remove();
    },
    imageSelected: function(files) {
        var file = files[0];
        var fileName = file.name;

        var reader = new FileReader();

        reader.onload = function(event) {
            var dataUri = event.target.result;

            var data = {
                productId: app.Products.currentProductId,
                fileBase64: dataUri,
                fileName: fileName
            };
            
            var field = $('<div/>');
            field.attr('gss_value', data.productId);
            field.attr('gss_view', "gss_product_thumbnails");
            field.attr('gss_fragment', "images");
            getshop.Settings.post(data, "saveImage", field);
//            debugger;
//
//            var event = thundashop.Ajax.createEvent(null, "saveImage", originalButton, data);
//            $(button).find('.progressbar').html("0" + "%");
//            $(button).find('.progressbar').show();
//            
//            thundashop.Ajax.post(
//                    event,
//                    function() {
//                        app.SedoxProductView.uploadProgressCompleted(button);
//                    },
//                    null,
//                    false,
//                    true,
//                    {
//                        "uploadcallback": function(prog) {
//                            app.SedoxProductView.uploadProgress(button, prog);
//                        }
//                    });
        };

        reader.onerror = function(event) {
            console.error("File could not be read! Code " + event.target.error.code);
        };

        reader.readAsDataURL(file);
            //We need to send dropped files to Server
//            handleFileUpload(files, $(this));
    },
}

app.Products.init();