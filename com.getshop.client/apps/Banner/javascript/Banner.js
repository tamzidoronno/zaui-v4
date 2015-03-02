app.Banner = {
    editors : [],
    currentApp: null,
    saveInProgress: false,
    settingValidBanners: false,
    
    init: function() {
        $(document).on('click', '.Banner .addnewbanner', app.Banner.addNewBanner);
        $(document).on('click', '.Banner .saveset', app.Banner.saveSet);
        $(document).on('click', '.Banner .banner_settings_images .imageholder', app.Banner.imageClicked);
        $(document).on('change', '.Banner #height', $.proxy(app.Banner.heightChanged, app.Banner));
        $(document).on('mouseenter', '.Banner .gallerythumbnailswap', app.Banner.swapImage);
        $(document).on('click', '.Banner .gallerythumbnailswap', app.Banner.swapImage);
    },
    
    swapImage : function() {
        var mainimgdiv =  $(this).closest('.gsgalleryroot').find('.gallerymainimgdiv');
        mainimgdiv.css('text-align','center');
        mainimgdiv.css('background-color','#333');
        var mainimg = $(this).closest('.gsgalleryroot').find('.gallerymainimg');
        var currentwidth = mainimg.css('max-height');
        var height = mainimg.height();
        var width = mainimg.width();
        if(currentwidth === "none" || !currentwidth) {
            mainimg.css('max-height',height);
            mainimgdiv.css('height',height);
            mainimg.css('width',"auto");
            mainimg.css('max-width',width);
        } else {
            height = mainimgdiv.height();
            width = mainimgdiv.width();
        }
        var newimage = $(this).closest('.galimg').attr('img');
        mainimg.attr('src',newimage + "&width=" + width + "&height=" + height);
    },
    
    setHeight: function() {
        var informationbox = this.getInformationBoxApp(); 
        var height = parseInt(informationbox.find('#height').val());
        if (!height) {
            height = $(this.currentApp).height();
        }
        
        informationbox.find('#height').val(height);
    },
    
    start: function() {
        app.Banner.setHeight();
        app.Banner.createEditors();
    },
    
    createEditors: function() {
        $(this.getInformationBoxApp().find('.imageholder')).each(function () {
            app.Banner.createEditor(this, true);
        });
    },
    
    getHeight: function() {
        var informationbox = this.getInformationBoxApp(); 
        return parseInt(informationbox.find('#height').val());
    },
    
    getInterval: function() {
        var informationbox = this.getInformationBoxApp(); 
        return parseInt(informationbox.find('#interval').val());
    },
    
    heightChanged: function() {
        var height = app.Banner.getHeight();
        for (var i in app.Banner.editors) {
            var editor = app.Banner.editors[i];
            editor.heightChanged(height);
        }
    },
    
    getInformationBoxApp: function() {
        return $($(document).find('[appid='+this.currentApp.attr('appid')+'].informationbox')[0]);
    },
    
    saveSet: function() {
        app.Banner.saveInProgress = true;
        
        var data = {
            images : []
        };
        
        app.Banner.getContainer(this).find('.imageholder').each(function() {
            if ($(this).attr('imageId')) {
                data.images.push($(this).attr('imageId'));
            }
        });
        
        app.Banner.settingValidBanners = true;
        data.interval = app.Banner.getInterval();
        data.height = app.Banner.getHeight();

        var event = thundashop.Ajax.createEvent(null, "setValidBanners", this, data);
        thundashop.Ajax.postWithCallBack(event, function() {
            app.Banner.settingValidBanners = false;
            app.Banner.onUploadCompleted(); 
        });
        
        $(app.Banner.currentApp).find('.bannerslider').hide();
        $(app.Banner.currentApp).find('.uploadprogress_bannerslider').show();
        thundashop.common.hideInformationBox();
        
        $(app.Banner.editors).each(function() {
            this.saveImage(true);
        });
    },
    
    loadEdit : function(element, invokingApp) {
        if (!app.Banner.saveInProgress) {
            app.Banner.editors = [];
        }
        
        if($(app.Banner.editors).length > 0) {
            thundashop.common.Alert(__f("Please wait"), __f("You need to wait until all uploads has completed before you can do more modifications."), true);
            return;
        }
        
        app.Banner.currentApp = invokingApp;
        var event = thundashop.Ajax.createEvent('','showAddBanner',invokingApp, {});
        thundashop.common.showInformationBox(event, __f("Image carousel"));
    },

    imageClicked: function() {
        app.Banner.hideAllEditors(this);
        var imageEditor = app.Banner.getExistingEditor(this);
        
        if (!imageEditor) {
            imageEditor = app.Banner.createEditor(this);
        } 
        
        app.Banner.getContainer(this).find('.image_editor_got_no_image').hide();
        imageEditor.show();
    },
    
    getContainer: function(me) {
        return $(me).closest('.informationbox');
    },
    
    createEditor: function(me, silent) {
        var dom = $('<div/>');
        dom.css('width','676px');
        dom.css('height','460px');
        if (silent) {
            dom.hide();
        }
        
        app.Banner.getContainer(me).find('.banner_image_editor').append(dom);
        
        var config = {
            app : app.Banner.currentApp
        };
        
        if ($(me).attr('crops')) {
            config.crops = $(me).attr('crops').split(":"); 
        }
        
        if ($(me).attr('rotation')) {
            config.rotation = parseInt($(me).attr('rotation')); 
        }
        
        if ($(me).attr('texts')) {
            var texts = JSON.parse($(me).attr('texts'));
            
            var textNewConfigs = Array();
            for (var i in texts) {
                var text = texts[i];
                
                var textConfig = {
                    color : text.colour,
                    fontSize : text.size,
                    text : text.text,
                    x : text.x,
                    y : text.y
                }
            

                textNewConfigs[i] = textConfig;
            }
            
            config.textFields = textNewConfigs;
        }
        
        if ($(me).attr('imageId')) {
            config.imageId = $(me).attr('imageId');
        }
        
        
        var imageEditor = new getshop.ImageEditor(dom, config, 1);
        imageEditor.invokedByButton = me;
        imageEditor.hideSaveButton();
        imageEditor.hideAspectRatioButton();
        imageEditor.hideChangeButton();
        imageEditor.disableCropping();
//        imageEditor.enableAspectRatio();
        imageEditor.onImageChanged(app.Banner.imageChanged);
        imageEditor.onUploadStarted(app.Banner.uploadStarted);
        imageEditor.addMenuEntry("Delete", 'fa-trash-o', $.proxy(app.Banner.deleteImage, imageEditor));
        imageEditor.onUploadCompleted(app.Banner.onUploadCompleted);
        app.Banner.editors.unshift(imageEditor);
        
        return imageEditor;
    },
    
    uploadStarted: function() {
        var image = this.getImage();
        var uploadContainer = $('<div/>');
        uploadContainer.addClass('banner_slider_upload_container');
        uploadContainer.html(image);
        app.Banner.currentApp.find(".bannerslider_uploads").append(uploadContainer);
    },
    
    onUploadCompleted: function() {
        var editor = app.Banner.removeEditor(this);
        
        if (editor) {
            var image = $(editor.getImage()).find('img');
            $(app.Banner.currentApp).find('.banner_slider_upload_container').each(function() {
                if ($(this).find('img')[0] === image[0]) {
                    $(this).remove();
                }
            });
        }
        
        if ($(app.Banner.editors).length === 0 && !app.Banner.settingValidBanners) {
            app.Banner.saveInProgress = false;
            app.Banner.reloadBanner();
        }
    },
    
    reloadBanner: function() {
        var event = thundashop.Ajax.createEvent(null, "render", app.Banner.currentApp, {});
        event['synchron'] = true;
        thundashop.Ajax.postWithCallBack(event, function(response) {
            app.Banner.currentApp.find('.bannerview').remove();
            app.Banner.currentApp.append(response);
        }, true);
    },
    
    removeEditor: function(editor) {
        for (var i in app.Banner.editors) {
            var editor2 = app.Banner.editors[i];
            if (editor2 === editor) {
                app.Banner.editors.splice(i, 1);
                break;
            }
        }
        return editor2;
    },
    
    deleteImage: function(me) {
        app.Banner.hideAllEditors(this.invokedByButton);
        
        var editor = app.Banner.getExistingEditor(this.invokedByButton);
        app.Banner.removeEditor(editor);
        
        var container = app.Banner.getContainer(this.invokedByButton);
        this.invokedByButton.remove();
        
        if (app.Banner.isImageListIsEmpty(container)) {
            container.find('.no_image_text_information').show();
        } else {
            container.find('.no_image_text_information').hide();
        }
    },
    
    imageChanged: function(editor) {
        var image = editor.getFullSizeImage();
        $(editor.invokedByButton).find('.innerholder').html(image);
    },
    
    getExistingEditor: function(me) {
        var editor = false;
        $(app.Banner.editors).each(function() {
            if (this.invokedByButton === me) {
                editor = this;
            }
        });
        
        return editor;
    },
    
    hideAllEditors: function(me) {
        $(app.Banner.editors).each(function() {
            this.hide();
        });
        app.Banner.getContainer(me).find('.image_editor_got_no_image').show();
    },
    
    isImageListIsEmpty: function(container) {
        var holders = container.find('.imageholder');
        if (holders.length === 0) {
            return true;
        } else {
            return false;
        }
    },
    
    addNewBanner: function() {
        var empty = $("<div class='imageholder'><div class='innerholder'><i style='margin-top: 24px; font-size: 80px; color: #333;' class='fa pictureicon fa-picture-o'></i></div></div>");
        var previewHolder = $(this).closest('.informationbox').find('.banner_settings_preview .banner_settings_images');
        $(this).closest('.informationbox').find('.no_image_text_information').hide();
        previewHolder.prepend(empty);
        empty.click();
    },
            
    toggleDots: function(event, invokingApp) {
        var event = thundashop.Ajax.createEvent('','toggleDots',invokingApp, {});
        thundashop.Ajax.post(event);
    },
    toggleMouseOver : function(event, invokingApp) {
        var event = thundashop.Ajax.createEvent('','toggleMouseOver',invokingApp, {});
        thundashop.Ajax.post(event);
    },
    loadSettings : function(element, application) {
         var config = {
            draggable: true,
            app : true,
            application: application,
            title: "Settings",
            items: [
                {
                    icontype: "awesome",
                    icon: "fa-gears",
                    iconsize : "30",
                    title: __f("Settings"),
                    click: app.Banner.loadEdit
                },
                {
                    icontype: "awesome",
                    icon: "fa-dot-circle-o",
                    iconsize : "30",
                    title: __f("Show dots"),
                    click: app.Banner.toggleDots
                },
                {
                    icontype: "awesome",
                    icon: "fa-toggle-on",
                    iconsize : "30",
                    title: __f("Active mouseover effect"),
                    click: app.Banner.toggleMouseOver
                }
            ]
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
};

app.Banner.Slider = {
    banners: {},
    
    init: function() {
    },
            
    
    startTimeout: function(id) {
        var timeout = function() {
            app.Banner.Slider.runner(id);
        }

        if (typeof(app.Banner.Slider.banners[id].timeOutVar) != "undefined") {
            clearInterval(app.Banner.Slider.banners[id].timeOutVar);
        }

        app.Banner.Slider.banners[id].timeOutVar = setTimeout(timeout, app.Banner.Slider.banners[id].interval);
    },
    
    goToImage: function(id, counter) {
        app.Banner.Slider.banners[id].nextImageCounter = counter;
        app.Banner.Slider.runner(id);
    },
  
    start: function(id) {
        app.Banner.Slider.banners[id] = {};
        app.Banner.Slider.banners[id].id = id;
        app.Banner.Slider.banners[id].nextImageCounter = 0;
        app.Banner.Slider.banners[id].currentImageCounter = 0;
        app.Banner.Slider.banners[id].banners = $("#" + id).find('.banner');
        app.Banner.Slider.banners[id].interval = $("#" + id).attr("interval");
        app.Banner.Slider.runner(id);
    },

    runner: function(id) {
        if (!$('#' + id).length) {
            return;
        }

        if (!app.Banner.Slider.banners[id]) {
            return;
        }

        var nextImageCounter = app.Banner.Slider.banners[id].nextImageCounter;
        var banners = app.Banner.Slider.banners[id].banners;
        var current = banners[app.Banner.Slider.banners[id].currentImageCounter];

        var next = banners[nextImageCounter];
        var imageId = $(next).attr('imageid');
        $('.Banner .banner_text').hide();

        $(current).attr('visible', "0");
        $(current).parent().find('.text_over_image').hide();
        $(current).hide();
        
        $(next).attr('visible', "1");
        
        $(next).show();
        
        setTimeout(function() {
            $(next).parent().find('.text_over_image').fadeIn(500);
        }, 300);
        
        $('.dots .dot').each(function() {
            $(this).removeClass('selected');
        });

        $('.dots .dot[imageId=' + imageId + ']').addClass('selected');

        app.Banner.Slider.banners[id].currentImageCounter = nextImageCounter;
        nextImageCounter++;

        if (nextImageCounter >= banners.length) {
            app.Banner.Slider.banners[id].nextImageCounter = 0;
        } else {
            app.Banner.Slider.banners[id].nextImageCounter = nextImageCounter;
        }

//        this.startTimeout(id);
    },
          
    addTextToImage: function(imageContainer, config) {
        var textHolder = $("<span>");
        textHolder.addClass('text_over_image');
        textHolder.css('position', 'absolute');
        textHolder.css('display', 'none');
        textHolder.css('left', config.x);
        textHolder.css('font-size', config.fontSize);
        textHolder.css('z-index', 1);
        textHolder.css('color', "#"+config.color);
        textHolder.css('top', config.y);
        textHolder.html(config.text);
        imageContainer.append(textHolder);
        textHolder.fadeIn(400);
    },
            
    showText: function(appId, texts) {
        var bannerSlider = $('.bannerslider#'+appId);
        for (var imageId in texts) {
            
            var textEntires = texts[imageId];
            
            var newImage = $("<img/>");
            var imageUrl = bannerSlider.find('.banner[imageid='+imageId+']').find('img').attr('src');
            newImage.attr("src", imageUrl);
            newImage.attr('imageId', imageId);
            newImage.attr('texts', JSON.stringify(textEntires));
            newImage.load(function() {
                var originalWidth = this.width;
                
                var texts = JSON.parse($(this).attr('texts'));
                var imageId = $(this).attr('imageId');
                var imageContainer = $(bannerSlider.find('.banner[imageid='+imageId+"]")[0])
                var compressionRate = bannerSlider.width() / originalWidth;

                for (var key in texts) {
                    var config = texts[key];
                    config.x = config.x * compressionRate;
                    config.y = config.y * compressionRate;
                    config.fontSize = config.size * compressionRate;
                    config.color = config.colour;
                    app.Banner.Slider.addTextToImage(imageContainer, config);
                }
            });
        }
        
    }
};

app.Banner.init();