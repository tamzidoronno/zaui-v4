
(function($) {
    $.fn.doImageUpload = function(event, imagebox, config) {
        if (config === undefined) {
            config = {};
        }
        var ajaxTarget = $(this);
        var inputevent = event;
        var previewWidth = 400;
        var previewHeight = 400;
        var saveOriginal = true;
        var saveCropped = true;
        var callback = false;
        var source = false;
        var autohideinfobox = true;
        var autosave = false;
        var selectedCropArea = [0, 0, previewWidth, previewHeight];
        var keepAspect = true;
        var extra = {};

        if (config.keepAspect !== undefined)
            keepAspect = config.keepAspect;
        if (config.previewHeight !== undefined)
            previewHeight = config.previewHeight;
        if (config.previewWidth !== undefined)
            previewWidth = config.previewWidth;
        if (config.saveOriginal !== undefined)
            saveOriginal = config.saveOriginal;
        if (config.saveCropped !== undefined)
            saveCropped = config.saveCropped;
        if (config.callback !== undefined)
            callback = config.callback;
        if (config.source !== undefined)
            source = config.source;
        if (config.extra !== undefined)
            extra = config.extra;
        if (config.autohideinfobox !== undefined)
            autohideinfobox = config.autohideinfobox;
        if (config.selectedCropArea !== undefined)
            selectedCropArea = config.selectedCropArea;
        if (config.autosave !== undefined)
            autosave = config.autosave;
        var compression = null;
        var cords = null;
        var originalCanvas = null;

        var event = thundashop.Ajax.createEvent('', 'loadImageUpload', $(this), {});
        var open = true;
        if (autosave) {
            open = false;
        }
        
        var infobox = thundashop.common.createInformationBox($(this).closest('.app').attr('appid'), 'Crop image', open);
        infobox.html('<center id="uploadedimagerow"></center>');
        infobox.find('#uploadedimagerow').html('<img id="uploadedimage">');
        infobox.find('#uploadedimagerow').append('<div class="button add_image_button"><div class="rightglare"></div><div class="filler"></div><ins>Save image</ins></div>');

        function generateCompressionRate(img, width, height) {
            if (img.width >= img.height) {
                compression = width / img.width;
                if (img.width <= width) {
                    compression = 1;
                }
            } else {
                compression = height / img.height;
                if (img.height <= height) {
                    compression = 1;
                }
            }
            return compression;
        }

        function compressImage(maxWidth, maxHeight, setOriginal) {
            var canvas = document.createElement("canvas");
            var ctx = canvas.getContext("2d");

            var imgwidth = originalCanvas.width;
            var imgheight = originalCanvas.height;

            var compression = generateCompressionRate(originalCanvas, maxWidth, maxHeight);
            var width = imgwidth * compression;
            var height = imgheight * compression;

            ctx.width = width;
            ctx.height = height;
            canvas.width = width;
            canvas.height = height;
            ctx.clearRect(0, 0, width, height);
            ctx.drawImage(originalCanvas, 0, 0, imgwidth, imgheight, 0, 0, width, height);
            if (setOriginal) {
                originalCanvas = canvas;
            }
            var data = canvas.toDataURL('image/png');
            return data;
        }

        function cropImage(x1, x2, y1, y2) {
            if(x1 < 0) {
                x1 = 0;
            }
            if(x2 < 0) {
                x2 = 0;
            }
            if(y1 < 0) {
                y1 = 0;
            }
            if(y2 < 0) {
                y2 = 0;
            }
            
            var imgwidth = imagebox.width();
            var imgheight = imagebox.height();            
            var compression = generateCompressionRate(originalCanvas, previewWidth, previewHeight);
            
            if(!keepAspect) {
                imgheight = (y2 - y1) / compression;
                console.log(imgheight);
            }
            
            var canvas = document.createElement("canvas");
            var ctx = canvas.getContext("2d");

            x1 /= compression;
            x2 /= compression;
            y1 /= compression;
            y2 /= compression;

            ctx.width = imgwidth;
            ctx.height = imgheight;
            canvas.width = imgwidth;
            canvas.height = imgheight;
            ctx.clearRect(0, 0, imgwidth, imgheight);
            ctx.drawImage(originalCanvas, x1, y1, (x2 - x1), (y2 - y1), 0, 0, imgwidth, imgheight);
            var data = canvas.toDataURL('image/png');
            return data;
        }

        function insertImage() {
            var data = cropImage(cords.x, cords.x2, cords.y, cords.y2);
            var origdata = originalCanvas.toDataURL('image/png');
            if (autosave) {
                imagebox.find('.displayed_image').attr('imageheight', imagebox.height());
                imagebox.find('.displayed_image').attr('src', data);
            } else {
                imagebox.find('.imagecontainer').html('<img src="' + data + '" class="displayed_image">');
            }
            if(autohideinfobox) {
                thundashop.common.hideInformationBox();
            }

            //Saving image.
            var id = "saveorgimg_" + parseInt(Math.random() * 100);
            if (saveCropped) {
                thundashop.common.addNotificationProgress(id + "_1", "Saving cropped image");
                var event = thundashop.Ajax.createEvent('', 'saveCroppedImage', ajaxTarget, {data: data, extra: extra, cords: cords, imageheight: imagebox.height()});
                thundashop.Ajax.post(event, function() {
                    thundashop.common.removeNotificationProgress(id + "_1");
                }, null, autosave);
            }
            if (saveOriginal) {
                thundashop.common.addNotificationProgress(id + "_2", "Saving original image");
                var event = thundashop.Ajax.createEvent('', 'saveOriginalImage', ajaxTarget, {data: origdata, extra: extra});
                thundashop.Ajax.post(event, function() {
                    thundashop.common.removeNotificationProgress(id + "_2");
                });
            }

            if (typeof(callback) === "function") {
                callback(data, cords, origdata, extra);
            }
        }

        function loadImage() {
            var data = compressImage(previewWidth, previewHeight, false);
            infobox.find('#uploadedimage').attr('src', data);
            infobox.find('.add_image_button').on('click', insertImage);

            var ratio = imagebox.width() / imagebox.height();
            if(!keepAspect) {
                ratio = false;
            }

            infobox.find('#uploadedimage').Jcrop({
                onRelease: function(c) {
                    cords = c;
                },
                onSelect: function() {
                    if (autosave) {
                        insertImage();
                    }
                },
                onChange: function(c) {
                    cords = c;
                },
                setSelect: selectedCropArea,
                aspectRatio: ratio
            });

        }

        function loadFromElement() {
            var element = $(inputevent.target);
            originalCanvas = document.createElement("canvas");
            var ctx = originalCanvas.getContext('2d'),
                    img = new Image(),
                    f = element[0].files[0],
                    url = window.URL || window.webkitURL,
                    src = url.createObjectURL(f);

            img.onload = function() {
                //First scale the image to a size possible to handle-
                infobox.find('.jcrop-holder').remove();
                var width = img.width;
                var height = img.height;

                var compression = generateCompressionRate(img, 1500, 1500);
                width *= compression;
                height *= compression;

                originalCanvas.width = width;
                originalCanvas.height = height;
                ctx.clearRect(0, 0, img.width, img.height);
                ctx.drawImage(img, 0, 0, img.width, img.height, 0, 0, width, height);
                url.revokeObjectURL(src);
                compressImage(1500, 1500, true);
                loadImage();
            };
            img.src = src;
        }

        function loadFromSource() {
            originalCanvas = document.createElement("canvas");
            var ctx = originalCanvas.getContext('2d');
            var img = new Image();
            img.onload = function() {
                originalCanvas.width = img.width;
                originalCanvas.height = img.height;
                ctx.drawImage(img, 0, 0, img.width, img.height);
                loadImage();
            };
            img.src = source;
        }

        if (!source) {
            loadFromElement();
        } else {
            loadFromSource();
        }

    };
})(jQuery);
