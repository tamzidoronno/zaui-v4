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

        function draw(element) {
            var element = $(element.target);
            var htmlCanvas = container.parent().find('#canvas')[0];
            var ctx = htmlCanvas.getContext('2d'),
                    img = new Image(),
                    f = element[0].files[0],
                    url = window.URL || window.webkitURL,
                    src = url.createObjectURL(f);
            var canvasCopy = document.createElement("canvas");
            var copyContext = canvasCopy.getContext("2d");

            img.src = src;
            img.onload = function() {
                var ratio = 1;

                if (img.width > maxWidth)
                    ratio = maxWidth / img.width;
                else if (img.height > maxHeight)
                    ratio = maxHeight / img.height;

                canvasCopy.width = img.width;
                canvasCopy.height = img.height;
                copyContext.drawImage(img, 0, 0);

                canvas.width = img.width * ratio;
                canvas.height = img.height * ratio;
                console.log(maxHeight);
                if(canvas.height > maxHeight) {
                    ratio = maxHeight / canvas.height;
                    canvas.width = canvas.width * ratio;
                    canvas.height = canvas.height * ratio;
                }
                if(canvas.width > maxWidth) {
                    ratio = maxWidth / canvas.width;
                    canvas.width = canvas.width * ratio;
                    canvas.height = canvas.height * ratio;
                }
                
                htmlCanvas.width = canvas.width;
                htmlCanvas.height = canvas.height;
                
                canvas.hide();
                ctx.clearRect(0, 0, maxWidth, maxWidth);

                ctx.drawImage(canvasCopy, 0, 0, canvasCopy.width, canvasCopy.height, 0, 0, canvas.width, canvas.height);
                url.revokeObjectURL(src);
                container.hide();
                canvas.fadeIn(function() {
                    if(!keepScalingOnUpload) {
                        doUpload(htmlCanvas.toDataURL('image/png'));
                    } else {
                        doUpload(canvasCopy.toDataURL('image/png'));
                    }
                });
            };
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
                "data" : data
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