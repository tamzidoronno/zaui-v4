(function($) {
    $.fn.imageupload = function(config) {
        var maxWidth = config.maxWidth;
        var maxHeight = config.maxHeight;
        var uploadText = config.uploadText;
        var container = $(this);
        var callback = config.callback;
        var target = config.target;
        var loaded = false;
        var keepScalingOnUpload = config.keepScalingOnUpload;


        function getRatio(oldWidth, oldHeight, maxWidth, maxHeight) {
            var useWidth = true;
            var ratio = 1;
            if(oldWidth > maxWidth && oldHeight > maxHeight) {
                //The largest size has to be the ratio
                if(oldHeight > oldWidth) {
                    useWidth = false;
                }
            }
            
            if(oldWidth > maxWidth && useWidth) {
                ratio = maxWidth / oldWidth; 
            } else if(oldHeight > maxHeight) {
                ratio = maxHeight / oldHeight; 
            }
            
            if((oldHeight * ratio) > maxHeight) {
                ratio = maxHeight / oldHeight; 
            }
            if((oldWidth * ratio) > maxWidth) {
                ratio = maxWidth / oldWidth; 
            }
            
            return ratio;
        }

        function draw(element) {
            var element = $(element.target);
            var canvasCopy = document.createElement("canvas");
            var copyctx = canvasCopy.getContext('2d');
            var htmlCanvas = container.parent().find('#canvas')[0];
            var ctx = htmlCanvas.getContext('2d'),
                    img = new Image(),
                    f = element[0].files[0],
                    url = window.URL || window.webkitURL,
                    src = url.createObjectURL(f);

            img.onload = function() {
                var ratio = getRatio(img.width, img.height, maxWidth, maxHeight);
                console.log(img.width);
                console.log(img.height);
                canvas.width = img.width * ratio;
                canvas.height = img.height * ratio;
                htmlCanvas.width = img.width * ratio;
                htmlCanvas.height = img.height * ratio;
                canvas.hide();
                ctx.clearRect(0, 0, maxWidth, maxHeight);
                ctx.drawImage(img, 0, 0, img.width, img.height, 0, 0, canvas.width, canvas.height);
                url.revokeObjectURL(src);
                container.hide();
                canvas.fadeIn(function() {
                    if(!keepScalingOnUpload) {
                        doUpload(htmlCanvas.toDataURL('image/png'));
                    } else {
                        var ratio = getRatio(img.width, img.height, 1000, 1000); 
                        canvasCopy.width = img.width * ratio;
                        canvasCopy.height = img.height * ratio;
                        copyctx.drawImage(img, 0, 0, img.width, img.height, 0, 0, canvasCopy.width, canvasCopy.height);
                        var data = canvasCopy.toDataURL('image/png');
                        doUpload(data);
                    }
                });
            };
            img.src = src;
        }

        function doUpload(data) {
            function s4() {
                return Math.floor((1 + Math.random()) * 0x10000)
                        .toString(16)
                        .substring(1);
            }

            var id = s4() + s4() + '-' + s4() + '-' + s4() + '-' +s4() + '-' + s4() + s4() + s4();
            thundashop.common.addNotificationProgress(id, uploadText);
            
            var postData = {
                "data" : data,
                config : config
            };
            
            var event = thundashop.Ajax.createEvent('', 'uploadImage', container, postData);
            var result = thundashop.Ajax.postSynchron(event);
            if(typeof(callback) === "function") {
                callback(result, config);
            }
            setTimeout("thundashop.common.removeNotificationProgress('"+id+"')", 500);
        }

        var uploader = $('<input type="file" value="" id="uploader">');
        var canvas = $('<canvas id="canvas" width="' + maxWidth + '" height="' + maxHeight + '"></canvas>');
        canvas.hide();
        uploader.hide();

        container.bind('mouseup', function() {
            uploader.click();
        })
        container.append(uploader);
        container.find('#uploader').bind('change', draw);
        container.after(canvas);
    };
})(jQuery);