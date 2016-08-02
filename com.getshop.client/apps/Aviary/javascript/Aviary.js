var featherEditor;

app.Aviary = {
    
    init: function() {
        PubSub.subscribe('NAVIGATION_COMPLETED', app.Aviary.initAviaryForImages);
        $(document).on('click', '.aviary_edit_button', app.Aviary.editImage);
        $(document).on('hover', '.aviary_edit_button', app.Aviary.showAviaryRevertButton);
        $(document).on('click', '.aviary_revert_button', app.Aviary.revertImage);
        $(document).on('hover', '.aviary_revert_button', app.Aviary.showAviaryRevertButton);
        $(document).on('hover', '.aviary_wrap', app.Aviary.showAviaryButtons);
    },
    
    initAviaryForImages: function() {
        if(isAdministrator) {
            $("img").each(function() {
                var imageId = $(this).attr("src").split("/displayImage.php?id=")[1];
                if(imageId != undefined) {
                    imageId = imageId.split("&")[0];
                    $(this).wrap("<div class='aviary_wrap'></div><>");
                    $(this).before( "<div class='aviary_button aviary_edit_button' imageid='" + imageId + "'><i class='fa fa-instagram'></i></div>" );
                    $(this).before( "<div class='aviary_button aviary_revert_button' imageid='" + imageId + "'><i class='fa fa-undo'></i></div>" );
                    $(this).attr("id", imageId);
                }
            });
            
            featherEditor = new Aviary.Feather({
                apiKey: 'your-key-here',
                apiVersion: 3,
                theme: 'dark',
                tools: 'all',
                appendTo: '',
                onSave: function(imageID, newURL) {
                    app.Aviary.saveImage(imageID, newURL);
                },
                onError: function(errorObj) {
                    alert(errorObj.message);
                }
            });
        }
        
    },
    
    editImage: function() {
       var image = $("img[id='" + $(this).attr("imageid") + "']");
       featherEditor.launch({
            image: $(image).attr("id"),
            url: $(image).src
        });
        return false;
    },
    
    saveImage: function(imageId, url) {
        var data = {
            url: url,
            imageId: imageId
        }
               
        var event = thundashop.Ajax.createEvent(null, "saveImage", "ns_3405a32a_c82d_4587_825a_36f10890be8e\\Aviary", data);
        
        thundashop.Ajax.postWithCallBack(event, function(res) {
            location.reload();
        });
    },
    
    revertImage: function() {
        var data = {
            imageId: $("img[id='" + $(this).attr("imageid") + "']")
        }
               
        var event = thundashop.Ajax.createEvent(null, "revertImage", "ns_3405a32a_c82d_4587_825a_36f10890be8e\\Aviary", data);
        
        thundashop.Ajax.postWithCallBack(event, function(res) {
            location.reload();
        });
    },
    
    showAviaryButtons: function() {
        var id = $(this).children("img").attr("id");
        $(".aviary_edit_button[imageid='" + id + "']").toggle();
    },
    
    showAviaryRevertButton: function() {
        var id = $(this).attr("imageid");
        $(".aviary_revert_button[imageid='" + id + "']").toggle();
    }
}

app.Aviary.init();