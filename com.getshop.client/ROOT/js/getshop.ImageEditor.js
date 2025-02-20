if (typeof (getshop) === "undefined") {
    getshop = {};
}

getshop.ImageEditorApi = {
    editors: [],
    add: function(id, editor) {
        getshop.ImageEditorApi.editors[id] = editor;
    },
    get: function(id) {
        var editor = getshop.ImageEditorApi.editors[id];

        if (editor && !editor.isValid()) {
            getshop.ImageEditorApi.remove(id);
            return null;
        }

        return editor;
    },
    remove: function(id) {
        delete getshop.ImageEditorApi.editors[id];
    }
};

getshop.ImageEditor = function(attachToDom, config, id) {
    this.attachToDom = attachToDom;
    this.config = config;
    this.init();
    this.id = id;

    getshop.ImageEditorApi.add(id, this);
};

getshop.ImageEditor.prototype = {
    /**
     * This is the canvas area
     * that the imageeditor uses.
     * 
     * @type type
     */
    canvas: null,
    
    init: function() {
        this.textFields = [];
        this.createUuid();
        this.setDefaultCrops();
        this.createContainer();
        this.createBigStock();
        this.setDefaultConfig();
        this.createImageCanvasWorkingArea();
        this.createUploadMenu();
        this.createPreviewContainer();
        this.addMenu();
        this.setBoundaries();
        this.refresh();
    },
    
    createUuid: function() {
        function s4() {
            return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
        };

        function guid() {
            return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4();
        }

        this.uuid = guid();
    },
    
    show: function() {
        this.attachToDom.show();
    },
    hide: function() {
        this.attachToDom.hide();
    },
    hideSaveButton: function() {
        this.menu.find('.fa-save').closest('.entry').hide();
    },
    hideChangeButton: function() {
        this.menu.find('.fa-trash-o').closest('.entry').hide();
    },
    hideAspectRatioButton: function() {
        this.menu.find('.fa-lock').closest('.entry').hide();
    },
    disableCropping: function() {
        this.menu.find('.fa-lock').closest('.entry').hide();
        this.config.cropDisabled=true;
    },
    enableAspectRatio: function() {
        this.menu.find('.fa-lock').addClass('active');
    },
    setDefaultCrops: function() {
        if (!this.config.crops && this.config.app) {
            var innerApp = this.config.app.closest('.applicationarea');
            this.config.crops = [0, 0, innerApp.width(), innerApp.height()];
        }

        this.originalAspectRatio = 1;
        if (this.config.app) {
            var innerApp = this.config.app.closest('.applicationarea');
            if (innerApp) {
                this.originalAspectRatio = innerApp.width() / innerApp.height();
            }
        }
    },
    isValid: function() {
        if (this.config && this.config.imageId) {
            return true;
        }

        if (this.config && this.config.Image) {
            return true;
        }

        return false;
    },
    createPreviewContainer: function() {
        this.previewContainer = $('<div/>');
        this.previewContainer.addClass('gs_image_editor_preview');

        this.progressDiv = $('<div/>');
        this.progressDiv.addClass('gs_image_editor_progress_inner');

        var progressBarHolder = $('<div/>');
        progressBarHolder.addClass('gs_image_editor_progress_bar_holder')

        var progressBar = $('<div/>');
        progressBar.addClass('gs_image_editor_progress_bar')

        progressBarHolder.append(progressBar);
        this.progressDiv.append(progressBarHolder);
    },
    createBigStock: function() {
        this.bigStockDom = $('<div/>');
        this.outerDom.append(this.bigStockDom);
        this.bigStock = new getshop.BigStock(this.bigStockDom, this);
        this.bigStockDom.hide();
    },
    createUploadMenu: function() {
        this.uploadMenu = $('<div/>');
        this.uploadMenu.addClass('uploadmenu');

        var option = $('<div/>');
        option.addClass('uploadoption');
        option.html('<div class="icon"/></i>' + "<div class='headline'></div><div class='description'></div><br><br><center><div class='gs_button'></div></center>");

        var upload = option.clone();
        upload.find('.icon').html('<i class="fa fa-picture-o">');
        upload.find('.headline').html(__f("Upload image from your computer"));
        upload.find('.description').html(__f("Select a picture from your local hard-drive, upload it and display it to your page viewers"));
        upload.find('.gs_button').html('<i class="fa fa-upload"></i>' + __f("Upload"));
        upload.find('.gs_button').click($.proxy(this.showFileDialog, this));
        this.uploadMenu.append(upload);

        var bigstock = option.clone();
        bigstock.find('.icon').html('<i class="bigstockicon">');
        bigstock.find('.headline').html(__f("Over 16 million pictures at your fingertip"));
        bigstock.find('.description').html(__f("Dont you have any quality pictures? Dont worry, we offer pictures from BigStock. Checkout the library, over 16 000 000 images ready to use."));
        bigstock.find('.gs_button').html('<i class="fa fa-cloud-upload"></i>' + __f("BigStock"));
        bigstock.find('.gs_button').click($.proxy(this.showBigStock, this));
        this.uploadMenu.append(bigstock);

        this.imageWorkArea.append(this.uploadMenu);
    },
    showBigStock: function() {
        this.innerDom.hide();
        this.bigStockDom.fadeIn();
        this.bigStock.showSearchForm();
    },
    bigstockCanceled: function() {
        this.bigStockDom.hide();
        this.innerDom.fadeIn();
    },
    imageSelected: function(control) {
        var i = 0, files = control.files, len = files.length;
        var me = this;

        this.showImageLoader();

        for (; i < len; i++) {
            var file = files[i];
            var reader = new FileReader();
            reader.onload = function(event) {
                var dataUri = event.target.result;
                me.config.Image = new Image();
                me.config.Image.onload = $.proxy(me.compressImage, me);
                me.config.Image.src = dataUri;
            };

            reader.onerror = function(event) {
                console.error("File could not be read! Code " + event.target.error.code);
            };

            reader.readAsDataURL(file);
        }
    },
            
    compressImage: function() {
        var oldImage = this.config.Image;
        
        var canvas = document.createElement("canvas");
        
        var ratio = 1;
        var maxSize = 1500;
        
        if (oldImage.width === oldImage.height && oldImage.width > maxSize) {
            ratio = maxSize / this.config.Image.height;
        }
        
        if (oldImage.width < oldImage.height && oldImage.height > maxSize) {
            ratio = maxSize / this.config.Image.height;
        }
        
        if (oldImage.width > oldImage.height && oldImage.width > maxSize) {
            ratio = maxSize / this.config.Image.width;
        }
        
        var ctx = canvas.getContext("2d");
        ctx.height = oldImage.height*ratio;
        ctx.width = oldImage.width*ratio;
        canvas.width = ctx.width;
        canvas.height = ctx.height;


        ctx.drawImage(oldImage, 0, 0, oldImage.width*ratio, oldImage.height*ratio);
        
        this.config.Image = new Image();
        this.config.Image.onload = $.proxy(this.onImageLoaded, this);
        this.config.Image.src = canvas.toDataURL();
    },
            
    showFileDialog: function() {
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
            me.imageSelected(control);
        });

        selectDialogueLink.click();
        selectDialogueLink.remove();
    },
    createImageCanvasWorkingArea: function() {
        this.imageWorkArea = $('<div>');
        this.imageWorkArea.addClass('imageworkarea');

        this.canvasDiv = $('<div/>');
        this.canvasDivInner = $('<div/>');
        this.canvasDiv.append(this.canvasDivInner);

        this.canvasDiv.addClass('canvasDiv');
        this.canvasDivInner.addClass('innerCanvas');

        this.canvas = document.createElement("canvas");
        this.canvasDivInner.append(this.canvas);
        this.imageWorkArea.append(this.canvasDiv);

        this.loader = $('<center><i style="color:#FFF; font-size: 20px; text-align: center; margin-top: 20px;" class="fa fa-spinner fa-spin"></i><span style="margin-left: 10px; color: #FFF;">' + __f("Loading image, please wait") + '</span></center>');
        this.loader.hide();
        this.imageWorkArea.append(this.loader);

        this.innerDom.append(this.imageWorkArea);

        this.canvasDiv.click($.proxy(this.imageWorkAreaClicked, this));
    },
    imageWorkAreaClicked: function() {
        for (var i in this.textFields) {
            var textField = this.textFields[i];
            textField.imageAreaClicked();
        }
    },
    createContainer: function() {
        this.outerDom = $('<div/>');
        this.outerDom.addClass("gs_image_editor");
        this.outerDom.attr('id', this.uuid);
        
        this.innerDom = $('<div/>');
        this.outerDom.append(this.innerDom);
        this.attachToDom.html(this.outerDom);
    },
    setDefaultConfig: function() {
        if (!this.config)
            this.config = {};

        if (this.config.rotation == null) {
            this.config.rotation = 0;
        }
    },
    setOriginalSize : function() {
        var button = this.menu.find('.fa-arrows');
        this.config.originalSize = "true";
        if(button.hasClass('active')) {
            this.config.originalSize = "false";
            this.refeshCropArea(true);
        } else {
            this.removeCropping();
        }
        this.setActive(button);
        
    },
    adjustLeft : function() {
        this.config.adjust = "left";
        var buttons = this.menu.find('.fa-align-left,.fa-align-right,.fa-align-center');
        buttons.removeClass('active');
        var button = this.menu.find('.fa-align-left');
        this.setActive(button);
    },
    adjustCenter : function() {
        this.config.adjust = "center";
        var buttons = this.menu.find('.fa-align-left,.fa-align-right,.fa-align-center');
        buttons.removeClass('active');
        var button = this.menu.find('.fa-align-center');
        this.setActive(button);
    },
    adjustRight : function() {
        this.config.adjust = "right";
        var buttons = this.menu.find('.fa-align-left,.fa-align-right,.fa-align-center');
        buttons.removeClass('active');
        var button = this.menu.find('.fa-align-right');
        this.setActive(button);
    },
    setActive : function(button) {
        var isActive = button.hasClass('active');
        if (isActive) {
            button.removeClass('active');
        } else {
            button.addClass('active');
        }        
    },
    setLink : function() {
        var button = this.menu.find('.fa-link');
        if(button.hasClass('active')) {
            this.config.link = "";
            this.setActive(button);
            return;
        }
        var link = window.prompt("Add your link", "");
        this.config.link = link;
        button.removeClass('active');
        this.setActive(button);
    },
    addMenu: function() {
        this.menu = $('<div/>');
        this.menu.addClass('gs_image_editor_menu');

        this.innerDom.append(this.menu);
        
        var me = this;
        this.addMenuEntry("Rotate left", 'fa-rotate-left', $.proxy(this.rotateLeft, this));
        this.addMenuEntry("Rotate right", 'fa-rotate-right', $.proxy(this.rotateRight, this));
        this.addMenuEntry("Aspect ratio", 'fa-lock', $.proxy(this.toggleAspectRation, this));
        this.addMenuEntry("Change", 'fa-trash-o', $.proxy(this.deleteImage, this));
        this.addMenuEntry("Link", 'fa-link', $.proxy(this.setLink, this));
        this.addMenuEntry("Original size", 'fa-arrows', $.proxy(this.setOriginalSize, this));
        this.addMenuEntry("", 'fa-align-left', $.proxy(this.adjustLeft, this), 'small');        
        this.addMenuEntry("", 'fa-align-center', $.proxy(this.adjustCenter, this), 'small');        
        this.addMenuEntry("", 'fa-align-right', $.proxy(this.adjustRight, this), 'small last');        
        this.addMenuEntry("Save", 'fa-save', $.proxy(this.saveImage, this));
    },
    
    addMenuEntry: function(text, classes, func, size) {
        var disableAspectRatio = this.createMenuEntry(__f(text), classes);
        var menuEntry = this.addEntryToMenu(disableAspectRatio, size);
        menuEntry.click(func);

    },
    addTextField: function(config, silent) {
        var textField = new getshop.ImageEditorTextField(config, this);
        this.imageWorkArea.append(textField.getContainer());
        if (!silent)
            textField.cropChanged(this.config.crops);
        this.textFields.push(textField);
    },
    getFullSizeOriginalImageData: function() {
        var image = this.config.OriginalImage;
        var canvas = document.createElement("canvas");
        canvas.width = image.width;
        canvas.height = image.height;

        var ctx = canvas.getContext("2d");
        ctx.drawImage(image, 0, 0, image.width, image.height);
        return canvas.toDataURL();
    },
    getCroppedImage: function() {
        var crops = this.getCropsForFullSizeImage();
        var width = crops[2] - crops[0];
        var height = crops[3] - crops[1];

        var canvas = document.createElement("canvas");
        canvas.width = this.config.Image.width;
        canvas.height = this.config.Image.height;

        var ctx = canvas.getContext("2d");
        ctx.height = this.config.Image.height;
        ctx.width = this.config.Image.width;

        ctx.height = height;
        ctx.width = height;
        canvas.width = width;
        canvas.height = height;

        ctx.drawImage(this.config.OriginalImage, crops[0], crops[1], width, height, 0, 0, width, height);

        return canvas.toDataURL();
    },
    getCropsForFullSizeImage: function() {
        var compressionRate = this.generateCompressionRate();
        var retConfig = [];
        retConfig[0] = Math.floor(this.config.crops[0] / compressionRate);
        retConfig[1] = Math.floor(this.config.crops[1] / compressionRate);
        retConfig[2] = Math.floor(this.config.crops[2] / compressionRate);
        retConfig[3] = Math.floor(this.config.crops[3] / compressionRate);
        return retConfig;
    },
    onUploadCompleted: function(callback) {
        this._uploadCompleted = callback;
    },
    onUploadStarted: function(callback) {
        this.uploadStarted = callback;
    },
    _uploadStarted: function() {
        this.progressDiv.show();
        if (this.uploadStarted && typeof (this.uploadStarted) == "function") {
            this.uploadStarted(this);
        }
    },
    getCurrentPageId: function() {
        return $('.skelholder').find('#pageid').attr('value');
    },
    saveImage: function(silent) {
        PubSub.publish("LAYOUT_UPDATED", "image");

        this._uploadStarted();

        var currentPageId = this.getCurrentPageId();

        var data = {
            compression: 1,
            cords: this.getCropsForFullSizeImage(),
            rotation: this.config.rotation,
            'getShopPageId': currentPageId,
            'adjustment' : this.config.adjust,
            'originalSize' : this.config.originalSize,
            'link' : this.config.link,
            textFields : []
        };

        for (var i in this.textFields) {
            var textField = this.textFields[i];
            var config = textField.getConfig();
            data.textFields.push(config);
        }
        
        if (this.config.imageId) {
            data.imageId = this.config.imageId;
        }
        
        var dontUpdate = (silent === true);

        if (this.config.imageId) {
            var event = thundashop.Ajax.createEvent('', 'updateCordinates', this.config.app, data);
            thundashop.Ajax.post(event, $.proxy(this.uploadCompleted, this), null, dontUpdate, dontUpdate);
            return;
        }

        data.data = this.getFullSizeOriginalImageData();

        var event = thundashop.Ajax.createEvent('', 'saveOriginalImage', this.config.app, data);
        event.synchron = true;

        thundashop.Ajax.post(
                event,
                $.proxy(this.uploadCompleted, this),
                null,
                true,
                true,
                {
                    "uploadcallback": $.proxy(this.uploadProgress, this)
                }
        );
    },
    uploadCompleted: function(response) {
        this.config.imageId = response;
        getshop.ImageEditorApi.remove(this.id);
        this.progressDiv.hide();
        if (this._uploadCompleted && typeof (this._uploadCompleted) == "function") {
            this._uploadCompleted(this);
        }
    },
    uploadProgress: function(progress) {
        this.progressDiv.find('.gs_image_editor_progress_bar').css('width', progress + "%");
    },
    addEntryToMenu: function(entry, size) {
        var outer = $('<div/>');
        outer.append(entry);
        if(size) {
            outer.addClass(size);
        }
        outer.addClass('entry');

        if(outer.find('.fa-arrows').length > 0 && this.config.originalSize === "true") {
            entry.find('i').addClass('active');
        }
        if(outer.find('.fa-align-left').length > 0 && this.config.adjust === "left") {
            entry.find('i').addClass('active');
        }
        if(outer.find('.fa-align-center').length > 0 && this.config.adjust === "center") {
            entry.find('i').addClass('active');
        }
        if(outer.find('.fa-align-right').length > 0 && this.config.adjust === "right") {
            entry.find('i').addClass('active');
        }
        if(outer.find('.fa-link').length > 0 && this.config.link && this.config.link !== "") {
            entry.find('i').addClass('active');
        }

        var disabled = $('<div/>');
        disabled.addClass('disabled');
        outer.append(disabled);

        this.menu.append(outer);
        return outer;
    },
    deleteImage: function() {
        if (this.imageDeleted && typeof (this.imageDeleted) === "function") {
            this.imageDeleted(this);
        }
        this.config.imageId = null;
        this.config.Image = null;
        this.config.OriginalImage = null;
        this.config.rotation = 0;
        var toBeDeleted = [];
        
        for (var key in this.textFields) {
            toBeDeleted.push(this.textFields[key]);
        }
        
        for (var key in toBeDeleted) {
            toBeDeleted[key].destroy();
        }
        this.config.textFields = [];
        
        this.canvas.getContext("2d").clearRect(0, 0, this.canvas.width, this.canvas.height);
        this.refresh();
    },
    rotateRight: function(silent) {
        if (silent !== true) {
            this.imageJustRotated = true;
            this.config.rotation = this.config.rotation + 90;
            if (this.config.rotation >= 360) {
                this.config.rotation = 0;
            }
        }

        var height = this.config.Image.width;
        var width = this.config.Image.height;

        var canvasLoc = document.createElement("canvas");
        canvasLoc.height = height;
        canvasLoc.width = width;

        var ctx = canvasLoc.getContext("2d");
        ctx.rotate(90 * Math.PI / 180);
        ctx.drawImage(this.config.Image, 0, -width, height, width);
        this.config.Image.src = canvasLoc.toDataURL("image/png");
    },
    rotateLeft: function(silent) {
        if (silent !== true) {
            this.imageJustRotated = true;
            this.config.rotation = this.config.rotation - 90;
            if (this.config.rotation < 0) {
                this.config.rotation = 270;
            }
        }

        var height = this.config.Image.width;
        var width = this.config.Image.height;

        var canvasLoc = document.createElement("canvas");
        canvasLoc.height = height;
        canvasLoc.width = width;

        var ctx = canvasLoc.getContext("2d");
        ctx.rotate(-90 * Math.PI / 180);
        ctx.drawImage(this.config.Image, -height, 0, height, width);
        this.config.Image.src = canvasLoc.toDataURL("image/png");
    },
    flipImage: function() {
        var width = this.config.Image.width;
        var height = this.config.Image.height;

        var canvasLoc = document.createElement("canvas");
        canvasLoc.height = this.config.Image.height;
        canvasLoc.width = this.config.Image.width;

        var ctx = canvasLoc.getContext("2d");
        ctx.rotate(180 * Math.PI / 180);
        ctx.drawImage(this.config.Image, -width, -height, width, height);
        ctx.restore();

        this.config.Image.src = canvasLoc.toDataURL("image/png");
    },
    toggleAspectRation: function() {
        var aspectButton = this.menu.find('.fa-lock');
        this.setActive(aspectButton);
        this.refeshCropArea(false);
    },
    createMenuEntry: function(text, iconClass) {
        var entry = $('<div/>');

        var icon = $('<i/>');
        icon.addClass("fa");
        icon.addClass("icon");
        icon.addClass(iconClass);

        var textDiv = $('<div/>');
        textDiv.addClass('menu_text_image_editor');
        textDiv.html(text);
        
        entry.append(icon);
        entry.append(textDiv);
        
        return entry;
    },
    showImageLoader: function() {
        $(this.canvas).fadeIn();
        this.uploadMenu.hide();
        this.loader.show();
    },
    loadImage: function() {
        if (!this.config.imageId) {
            return;
        }

        this.showImageLoader();
        this.config.Image = new Image();
        this.config.Image.onload = $.proxy(this.onImageLoaded, this);
        this.config.Image.src = "displayImage.php?id=" + this.config.imageId;
    },
    setBoundaries: function() {        
        this.outerDom.width(this.attachToDom.width());
        this.outerDom.height(this.attachToDom.height());

        this.imageWorkArea.width(this.attachToDom.width() - 140);
        this.imageWorkArea.height(this.attachToDom.height() - 40);

        this.bigStock.setSize();
    },
    /**
     * Refresh the editor to a new 
     * state based on the config set.
     * 
     * @returns {undefined}
     */
    refresh: function() {
        this.loader.hide();
        if (!this.config.Image && !this.config.imageId) {
            this.showUploadForm();
        } else if (!this.config.Image) {
            this.loadImage();
        } else {
            this.onImageLoaded();
        }
    },
    showUploadForm: function() {
        $(this.menu).find('.disabled').show();
        this.removeCropping();
        $(this.canvas).hide();
        this.uploadMenu.fadeIn();
    },
    onImageLoaded: function() {
        this.loader.hide();

        var firstTimeLoaded = !this.config.OriginalImage;
       
        if (firstTimeLoaded) {
            this.setOriginalImage();
        }

        $(this.menu).find('.disabled').hide();
        $(this.canvas).fadeIn();
        this.uploadMenu.hide();
        this.appendImageToCanvas();

        var addText = this.initialRotating && !firstTimeLoaded;
        
        if (firstTimeLoaded && this.config.rotation) {
            this.initialRotating = true;
            this.rotateImageInitially();
        } else {
            this.imageJustRotated = null;
        }
        
        var doCompression = this.imageJustRotated === true ? false : true;
        if(!this.config.cropDisabled) {
            this.refeshCropArea(doCompression);
        }
        
        if (firstTimeLoaded && !this.initialRotating || addText) {
            this.initialRotating = null;
            this.addTexts();
        }
    },
    
    /**
     * Listen for this event if you want to
     * be notified if a picture has been changed
     */
    onImageChanged:function(func) {
        this._onImageLoaded = func;
    },
    
    addTexts: function() {
        for (var key in this.config.textFields) {
            var config = this.config.textFields[key];
            this.addTextField(config, true);
        }
    },
    rotateImageInitially: function() {
        if (this.config.rotation === 90) {
            this.rotateRight(true);
        }

        if (this.config.rotation === 180) {
            this.flipImage();
        }

        if (this.config.rotation === 270) {
            this.rotateLeft(true);
        }
    },
    setOriginalImage: function() {
        var canvas = document.createElement("canvas");
        
        var ratio = 1;
//        var maxSize = 1500;
//        
//        if (this.config.Image.width === this.config.Image.height && this.config.Image.width > maxSize) {
//            ratio = maxSize / this.config.Image.height;
//        }
//        
//        if (this.config.Image.width < this.config.Image.height && this.config.Image.height > maxSize) {
//            ratio = maxSize / this.config.Image.height;
//        }
//        
//        if (this.config.Image.width > this.config.Image.height && this.config.Image.width > maxSize) {
//            ratio = maxSize / this.config.Image.width;
//        }
        
        var ctx = canvas.getContext("2d");
        ctx.height = this.config.Image.height*ratio;
        ctx.width = this.config.Image.width*ratio;
        canvas.width = ctx.width;
        canvas.height = ctx.height;


        ctx.drawImage(this.config.Image, 0, 0, this.config.Image.width*ratio, this.config.Image.height*ratio);
        
        this.config.OriginalImage = new Image();
        this.config.OriginalImage.src = canvas.toDataURL();
        this.config.OriginalImage.onload = $.proxy(this.originalImageLoaded, this);
    },
    
    originalImageLoaded: function() {
        if (typeof(this._onImageLoaded) === "function") {
            this._onImageLoaded(this);
        }  
    },
    removeCropping: function() {
        var jCropApi = $(this.canvasDivInner).data('Jcrop');
        if (jCropApi) {
            this.canvasDiv.html(this.canvasDivInner);
            jCropApi.destroy();
        }
    },
    setImageId: function(imageId) {
        this.config.imageId = imageId;
        this.bigstockCanceled();
        this.refresh();
    },
    refeshCropArea: function(doCompression) {
        var aspectRatio = false;

        this.removeCropping();
        
        if (this.config.originalSize === "true")
            return;
        
        var crops = this.config.crops;

        if (doCompression) {
            var compression = this.generateCompressionRate();
            crops[0] = crops[0] * compression;
            crops[1] = crops[1] * compression;
            crops[2] = crops[2] * compression;
            crops[3] = crops[3] * compression;
        }

        if (this.imageJustRotated) {
            crops[0] = 0;
            crops[1] = 0;
        }

        if (this.menu.find('.fa-lock').hasClass('active')) {
            aspectRatio = this.originalAspectRatio;
        }

        $(this.canvasDivInner).Jcrop({
            onRelease: $.proxy(this.cropChanged, this),
            onSelect: $.proxy(this.cropChanged, this),
            onChange: $.proxy(this.cropChanged, this),
            setSelect: crops,
            aspectRatio: aspectRatio
        }, function() {
            jcrop_api = this;
        });
    },
    cropChanged: function(c) {
        if (!c) {
            return;
        }

        this.config.crops = [c.x, c.y, c.w + c.x, c.y2];

        for (var key in this.textFields) {
            var textField = this.textFields[key];
            textField.cropChanged(this.config.crops);
        }
    },
    generateCompressionRate: function() {
        var img = this.config.Image;

        var maxWidth = this.imageWorkArea.width();
        var maxHeight = this.imageWorkArea.height();
        var ratio = 0;
        var width = img.width;
        var height = img.height;

        ratio = maxWidth / width;
        var newHeight = height * ratio;
        if (newHeight > maxHeight) {
            ratio = maxHeight / height;
        }

        return ratio;
    },
    appendImageToCanvas: function() {
        var ctx = this.canvas.getContext("2d");
        var compression = this.generateCompressionRate();
        var width = this.config.Image.width * compression;
        var height = this.config.Image.height * compression;

        this.canvas.height = height;
        this.canvas.width = width;

        ctx.drawImage(this.config.Image, 0, 0, width, height);

        var top = (this.imageWorkArea.height() / 2) - (height / 2);
        var left = (this.imageWorkArea.width() / 2) - (width / 2);

        this.canvasDiv.height(height);
        this.canvasDiv.width(width);

        this.canvasDivInner.height(height);
        this.canvasDivInner.width(width);

        $(this.canvasDiv).css('position', 'absolute');
        $(this.canvasDiv).css('top', top + "px");
        $(this.canvasDiv).css('left', left + "px");
    },
    /**
     * Returns the image with applied current configurations.
     * 
     * @returns Image
     */
    getImage: function(loadFunction) {
        var img = new Image();
        if (loadFunction) 
            img.onload = loadFunction;
        
        img.src = this.getCroppedImage();

        this.previewContainer.html("");
        this.previewContainer.append(img);
        this.previewContainer.append(this.progressDiv);
        return this.previewContainer;
    },
    
    /**
     * Returns the fullsize image uncropped.
     * 
     * @returns {getshop.ImageEditor.prototype.getFullSizeImage.img|Image}
     */
    getFullSizeImage: function() {
        var img = new Image();
        img.src = this.getFullSizeOriginalImageData();
        return img;
    },
    
    removeTextField: function(textField) {
        var newArray = [];
        for (var key in this.textFields) {
            var savedTextField = this.textFields[key];
            if (savedTextField !== textField) {
                newArray.push(savedTextField);
            }
        }
        this.textFields = newArray;
    },
    
    heightChanged: function(newHeight) {
        var innerApp = this.config.app.closest('.applicationarea');
        this.originalAspectRatio = innerApp.width() / newHeight;
        this.refeshCropArea(false);
    }
};

getshop.ImageEditorTextField = function(config, parent) {
    this.config = config;
    this.parent = parent;
    this.init();
};

getshop.ImageEditorTextField.prototype = {
    init: function() {
        this.createDefaultConfig();
        this.createContainer();
        this.createTextField();
        this.createToolBar();
        this.refreshByConfig(true);
    },
    createDefaultConfig: function() {
        var compressionRate = this.parent.generateCompressionRate();
        
        if (!this.config) {
            this.config = {};
        }
        
        if (!this.config.text) {
            this.config.text = __f("New text");
        }
        
        if (!this.config.color) {
            this.config.color = "000000";
        }
        
        if (!this.config.fontSize) {
            this.config.fontSize = 14/compressionRate;
        }
        
        this.config.x = this.config.x ? this.config.x : 0;
        this.config.y = this.config.y ? this.config.y : 0;
        
        this.config.x = this.config.x * compressionRate;
        this.config.y = this.config.y * compressionRate;
        this.config.fontSize = this.config.fontSize * compressionRate;
        
        this.config.fontSize = Math.floor(this.config.fontSize);
    },
    getContainMent: function() {
        return this.parent.imageWorkArea.find('.jcrop-holder div .jcrop-tracker');
    },
    createToolBar: function() {
        this.toolbar = $('<div/>');
        this.toolbar.hide();
        this.toolbar.addClass("toolbar");
        this.container.append(this.toolbar);
        this.createDragMenu();
        this.createRemoveOption();
        this.createColorPicker();
        this.createSizeSelection();
    },
    
    createRemoveOption: function() {
        var input = $('<span class="remove"></span>');
        input.click($.proxy(this.destroy, this));
        this.toolbar.append(input);
    },
    
    destroy: function() {
        this.parent.removeTextField(this);
        this.container.remove();
    },
    
    createColorPicker: function() {
        var input = $('<span class="text_color_selection"></span>');

        this.toolbar.append(input);
        var me = this;
        input.ColorPicker({
            color: "#" + this.textField.css('color'),
            onShow: function(colpkr) { $(colpkr).fadeIn(500); return false; },
            onSubmit: function(colpkr) { $(colpkr).fadeOut(); return false; },
            onHide: function(colpkr) { $(colpkr).fadeOut(); return false; },
            onChange: function(hsb, hex, rgb) {
                me.config.color = hex;
                me.refreshByConfig();
            }
        });
    },
    createDragMenu: function() {
        this.dragMenu = $('<span/>');
        this.dragMenu.addClass('drag');
        this.toolbar.append(this.dragMenu);
    },
    createSizeSelection: function() {
        var sizetoselect = [8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 36, 48, 76];

        this.sizeselector = $("<select></select>");
        for (var key in sizetoselect) {
            var size = sizetoselect[key];
            if (this.config.fontSize === size) {
                this.sizeselector.append("<option selected value='" + size + "'>" + size + "px</option>");
            } else {
                this.sizeselector.append("<option value='" + size + "'>" + size + "px</option>");
            }
        }

        this.sizeselector.change($.proxy(this.textSizeChanged, this));
        this.toolbar.append(this.sizeselector);
    },
    textSizeChanged: function() {
        this.config.fontSize = parseInt(this.sizeselector.val());
        this.refreshByConfig();
    },
    refreshByConfig: function(updatePosition) {
        this.textField.css('font-size', this.config.fontSize + "px");
        this.textField.css('color', "#"+this.config.color);
        if (updatePosition) {
            var outerPadding = parseInt(this.getContainMent().parent().parent().parent().parent().css('left'));
            var innerPadding = parseInt(this.getContainMent().parent().parent().css('left'));
            var offsetX = this.config.x + outerPadding + innerPadding;

            var outerPaddingTop = parseInt(this.getContainMent().parent().parent().parent().parent().css('top'));
            var innerPaddingTop = parseInt(this.getContainMent().parent().parent().css('top'));
            var offsetY = this.config.y + outerPaddingTop + innerPaddingTop;
            
            this.container.css('left', offsetX+"px");
            this.container.css('top', offsetY+"px");
        }
    },
    createTextField: function() {
        this.textField = $('<span contenteditable="true"/>');
        this.textField.css("border", "solid 1px #333;");
        this.textField.html(this.config.text);
        this.textField.focus($.proxy(this.editing, this));
        this.textField.on('blur keyup paste input', $.proxy(this.textChanged, this));
        this.container.append(this.textField);
    },
    textChanged: function() {
        this.config.text = this.textField.html();
    },
    createContainer: function() {
        var containment = "#"+this.parent.uuid + " .jcrop-tracker";
        this.container = $('<span/>');
        this.container.css('z-index', 10000);
        this.container.css('position', "absolute");        
        this.container.draggable({
            handle: ".drag",
            containment: containment,
            stop: $.proxy(this.updatePositions, this)
        });
    },
    updatePositions: function() {
        var outerPadding = this.getContainMent().parent().parent().parent().parent().position().left;
        var innerPadding = this.getContainMent().parent().parent().position().left;
        var offsetX = this.container.position().left - outerPadding - innerPadding;

        var outerPaddingTop = this.getContainMent().parent().parent().parent().parent().position().top;
        var innerPaddingTop = this.getContainMent().parent().parent().position().top;
        var offsetY = this.container.position().top - outerPaddingTop - innerPaddingTop;

        this.config.x = offsetX;
        this.config.y = offsetY;
    },
    cropChanged: function(crops) {
        var outerPaddingTop = this.getContainMent().parent().parent().parent().parent().position().top;
        var outerPadding = this.getContainMent().parent().parent().parent().parent().position().left;

        var top = parseInt(this.container.css('top'));
        var left = parseInt(this.container.css('left'));
        var right = left + this.container.width();
        var bottom = top + this.container.height();

        if (left < (crops[0] + outerPadding)) {
            this.container.css('left', (crops[0] + outerPadding) + 'px');
        }

        if (top < (crops[1] + outerPaddingTop)) {
            this.container.css('top', (crops[1] + outerPaddingTop) + 'px');
        }

        var outerLimit = crops[0] + this.getContainMent().width() + outerPadding;
        var outerLimitBottom = crops[1] + this.getContainMent().height() + outerPaddingTop;

        if (right > outerLimit) {
            this.container.css('left', (outerLimit - this.container.outerWidth()) + 'px');
        }

        if (bottom > outerLimitBottom) {
            this.container.css('top', (outerLimitBottom - this.container.outerHeight()) + 'px');
        }

        this.editingDone();
    },
    editing: function() {
        this.textField.css("background-color", "rgba(256,256,256,0.5)");
        this.toolbar.show();
    },
    editingDone: function() {
        this.textField.blur();
        this.textField.css("background-color", "inherit");
        this.toolbar.hide();
        this.updatePositions();
    },
    getContainer: function() {
        return this.container;
    },
    imageAreaClicked: function() {
        this.editingDone();
    },
    getConfig: function() {
        var compressionRate = this.parent.generateCompressionRate();
        this.config.x = this.config.x/compressionRate;
        this.config.y = this.config.y/compressionRate;
        this.config.fontSize = this.config.fontSize/compressionRate;
        return this.config;
    }
}
