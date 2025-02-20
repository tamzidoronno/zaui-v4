var featherEditor;

app.Aviary = {
    
    init: function() {
        PubSub.subscribe('NAVIGATION_COMPLETED', app.Aviary.initAviaryForImages);
        $(document).on('click', '.aviary_edit_button', app.Aviary.editImage);
        $(document).on('click', '.aviary_edit_background_button', app.Aviary.editBackgroundImage);
        $(document).on('click', '.aviary_revert_button', app.Aviary.revertImage);
        $(document).on('hover', '.aviary_edit_button', app.Aviary.showAviaryRevertButton);
        $(document).on('hover', '.aviary_edit_background_button', app.Aviary.showAviaryRevertButton);
        $(document).on('hover', '.aviary_revert_button', app.Aviary.showAviaryRevertButton);
        $(document).on('hover', '.aviary_wrap', app.Aviary.showAviaryButtons);
    },
    
    initAviaryForImages: function() {
        if(isAdministrator) {
            $("img").each(function() {
                var imageId = $(this).attr("src").split("/displayImage.php?id=")[1];
                if(imageId != undefined) {
                    imageId = imageId.split("&")[0];
                    $(this).wrap("<div class='aviary_wrap'></div>");
                    $(this).before( "<div class='aviary_button aviary_edit_button' imageid='" + imageId + "' imagesrc='" + this.src + "'><i class='fa fa-instagram'></i></div>" );
                    $(this).before( "<div class='aviary_button aviary_revert_button' imageid='" + imageId + "'><i class='fa fa-undo'></i></div>" );
                    $(this).attr("id", imageId);
                }
            });
            
            $("*").each(function() {
               if($(this).css("background-image").indexOf("/displayImage.php?id=") >= 0) {
                   var url = $(this).css("background-image");
                   var imageId = url.split("/displayImage.php?id=")[1].slice(0, -2);
                   imageId = imageId.split("&")[0];
                   $(this).addClass("aviary_wrap");
                   $(this).prepend("<div class='aviary_button aviary_revert_button' imageid='" + imageId + "'><i class='fa fa-undo'></i></div>");
                   $(this).prepend("<div class='aviary_button aviary_edit_background_button' imageid='" + imageId + "' imagesrc='" + url.slice(4, -1) + "'><i class='fa fa-instagram'></i></div>");
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
    },
    
    editBackgroundImage: function() {
        var imageId = $(this).attr("imageid");
        
        app.Aviary.urlToData("/displayImage.php?id=" + $(this).attr("imageid"), function(base64) {
            featherEditor.launch({
                image: imageId,
                url: base64
            }); 
        });
        
        
    },
    
    saveImage: function(imageId, url) {
        var data = {
            url: url,
            imageId: imageId
        };
               
        var event = thundashop.Ajax.createEvent(null, "saveImage", "ns_3405a32a_c82d_4587_825a_36f10890be8e\\Aviary", data);
        
        thundashop.Ajax.postWithCallBack(event, function(res) {
            var image = $("img[id='" + imageId + "']");
            image.attr("src", image.attr("src") + "?timestamp=" + new Date().getTime());
            featherEditor.close();
        });
    },
    
    revertImage: function() {
        var data = {
            imageId: $(this).attr("imageid")
        };
               
        var event = thundashop.Ajax.createEvent(null, "revertImage", "ns_3405a32a_c82d_4587_825a_36f10890be8e\\Aviary", data);
        
        thundashop.Ajax.postWithCallBack(event, function(res) {
            thundashop.framework.reprintPage();
        });
    },
    
    showAviaryButtons: function() {
        var id = $(this).children(".aviary_button").attr("imageid");
        $(".aviary_edit_button[imageid='" + id + "']").toggle();
        $(".aviary_edit_background_button[imageid='" + id + "']").toggle();
    },
    
    showAviaryRevertButton: function() {
        var id = $(this).attr("imageid");
        $(".aviary_revert_button[imageid='" + id + "']").toggle();
    },
    
    urlToData: function(url, callback) {
        console.log(url);
        
        var xhr = new XMLHttpRequest();
        xhr.responseType = 'blob';
        xhr.onload = function() {
          var reader = new FileReader();
          reader.onloadend = function() {
            callback(reader.result);
          }
          reader.readAsDataURL(xhr.response);
        };
        xhr.open('GET', url);
        xhr.send(); 
    },
};

app.Aviary.init();