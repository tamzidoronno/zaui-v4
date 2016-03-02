app.ImageDisplayer = {
    
    loadSettings: function(element, application) {
        var config = {
            draggable: true,
            application: application,
            title: __f("Settings"),
            items: [
                {
                    icontype: "awesome",
                    icon: "fa-arrows",
                    iconsize : "30",
                    title: __f("Edit the image"),
                    click: function() {
                        app.ImageDisplayer.showEditImage(application);
                    }
                },
                {
                    icontype: "awesome",
                    icon: "fa-angle-double-down",
                    iconsize: "30",
                    title: __f("Toggle on/off bottom area"),
                    click: thundashop.Skeleton.activateBottomArea
                },
                {
                    icontype: "awesome",
                    icon: "fa-search",
                    iconsize: "30",
                    title: __f("Toggle able to zoom"),
                    click: app.ImageDisplayer.toggleZoom
                }
            ]
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    },
    
    toggleZoom: function(application) {
        var event = thundashop.Ajax.createEvent("", "toggleZoomImage", this);
        thundashop.Ajax.post(event);
    },
    
    showEditImage: function(application) {
        var app = $(application).hasClass('app') ? application : this;
        var event = thundashop.Ajax.createEvent("", "showImageEditor", app);
        thundashop.common.showInformationBoxNew(event, __f('Image Editor'));
    },
            
    showImageDisplayer : function() {
        
        if ($(this).attr('zoomable') === "false") {
            return;
        }
        
        if($(document).width() < 1000) {
            return;
        }
        thundashop.common.showInformationBoxNew();

        var src = $(this).closest('.displayimagecontainer').attr('zoomSrc');
        $('#informationbox').html('<div style="background-color:#FFF;padding-top:10px;"><center><img src="'+src+'" style="max-width:90%"></div>');
    },
            
    initEvents: function() {
        $(document).on('click', '.ImageDisplayer .showimageeditor', app.ImageDisplayer.showEditImage);
        $(document).on('click', '.ImageDisplayer .imagecontainer img', app.ImageDisplayer.showImageDisplayer);
    },
    
    addTextToImage: function(imageContainer, config) {
        var textHolder = $("<span>");
        textHolder.addClass('text_over_image');
        textHolder.css('position', 'absolute');
        textHolder.css('displa', 'none');
        textHolder.css('left', config.x);
        textHolder.css('font-size', config.fontSize);
        textHolder.css('color', "#"+config.color);
        textHolder.css('top', config.y);
        textHolder.html(config.text);
        imageContainer.append(textHolder);
        textHolder.fadeIn(400);
    },
    
    showText: function(appId, texts, originalSizeWidth) {
        var imageContainer = $('.app[appid='+appId+']:not(".informationbox") .displayimagecontainer');
        
        imageContainer.find('.text_over_image').remove();
        
        var imageUrl = imageContainer.find('img').attr('src');
        var me = this;
        
        
        if (originalSizeWidth) {
            var compressionRate3 = imageContainer.find('img').width() / originalSizeWidth;
            for (var key in texts) {
                var config = texts[key];
                config.x = config.x * compressionRate3;
                config.y = config.y * compressionRate3;
                config.fontSize = config.fontSize * compressionRate3;
                me.addTextToImage(imageContainer, config);
            }
            
        } else {
            imageContainer.find('.text_over_image').remove();

            var newImage = $("<img/>");
            newImage.attr("src", imageUrl);
            newImage.attr('texts', JSON.stringify(texts));
            newImage.load(function() {
                var originalWidth = this.width;
                var compressionRate = imageContainer.find('img').width() / originalWidth;
                var texts = JSON.parse($(this).attr('texts'));
                for (var key in texts) {
                    var config = texts[key];
                    config.x = config.x * compressionRate;
                    config.y = config.y * compressionRate;
                    config.fontSize = config.fontSize * compressionRate;
                    me.addTextToImage(imageContainer, config);
                }
            });
        }
    },
    
    loadImage: function(domId, imgSrc) {
        if (imgSrc) {
            var container = $('.displayimagecontainer[containerId="'+domId+'"] span');
            var width = $('.displayimagecontainer[containerId="'+domId+'"]').width();
            if(container.closest('.gsflipcard').length > 0) {
                width = container.closest('.gsflipcard').width();
            }
            var image = $("<img src='"+imgSrc+"&width="+width+"'>");
            var zoomAble = container.find('.loadshower').attr('zoomable');
            image.attr('zoomable', zoomAble);
            container.html(image);
        }
    }
};

app.ImageDisplayer.initEvents();