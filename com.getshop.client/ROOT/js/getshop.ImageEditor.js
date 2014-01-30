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
    rotation: 0,
    init: function() {
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
    setDefaultCrops: function() {
        if (!this.config.crops) {
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
        var i = 0,files = control.files,len = files.length;
        var me = this;
        
        for (; i < len; i++) {
            var file = files[i];
            var reader = new FileReader();
            reader.onload = function(event) {
                var dataUri = event.target.result;
                me.config.Image = new Image();
                me.config.Image.onload = $.proxy(me.onImageLoaded, me);
                me.config.Image.src = dataUri;
            };

            reader.onerror = function(event) {
                console.error("File could not be read! Code " + event.target.error.code);
            };

            reader.readAsDataURL(file);
        }
    },
    showFileDialog: function() {
        var selectDialogueLink = $('<a href="">Select files</a>');
        var fileSelector = $('<input type="file" id="your-files" multiple/>');

        selectDialogueLink.click(function() {
            fileSelector.click();
        });
        $('body').append(fileSelector);
        $('body').append(selectDialogueLink);

        var control = document.getElementById("your-files");
        var me = this;
        
        control.addEventListener("change", function() {
            me.imageSelected(control);
        });
        
        selectDialogueLink.click();
        fileSelector.remove();
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
        this.innerDom.append(this.imageWorkArea);
    },
    createContainer: function() {
        this.outerDom = $('<div/>');
        this.outerDom.addClass("gs_image_editor");
        
        this.innerDom = $('<div/>');
        this.outerDom.append(this.innerDom);
        this.attachToDom.html(this.outerDom);
    },
    setDefaultConfig: function() {
        if (!this.config)
            this.config = {};
    },
    addMenu: function() {
        this.menu = $('<div/>');
        this.menu.addClass('gs_image_editor_menu');
        
        this.innerDom.append(this.menu);

        var disableAspectRatio = this.createMenuEntry(__f("Aspect ratio"), 'fa-lock');
        var menuEntry = this.addEntryToMenu(disableAspectRatio);
        menuEntry.click($.proxy(this.toggleAspectRation, this));

        disableAspectRatio = this.createMenuEntry(__f("Delete"), 'fa-trash-o');
        menuEntry = this.addEntryToMenu(disableAspectRatio);
        menuEntry.click($.proxy(this.deleteImage, this));

//        disableAspectRatio = this.createMenuEntry(__f("Add text"), 'fa-bold');
//        menuEntry = this.addEntryToMenu(disableAspectRatio);

        disableAspectRatio = this.createMenuEntry(__f("Rotate left"), 'fa-rotate-left');
        menuEntry = this.addEntryToMenu(disableAspectRatio);
        menuEntry.click($.proxy(this.rotateLeft, this));

        disableAspectRatio = this.createMenuEntry(__f("Rotate right"), 'fa-rotate-right');
        menuEntry = this.addEntryToMenu(disableAspectRatio);
        menuEntry.click($.proxy(this.rotateRight, this));

        disableAspectRatio = this.createMenuEntry(__f("Save"), 'fa-save');
        menuEntry = this.addEntryToMenu(disableAspectRatio);
        menuEntry.click($.proxy(this.saveImage, this));
    },
    
    getFullSizeImage: function() {
        var canvas = document.createElement("canvas");
        canvas.width = this.config.Image.width;
        canvas.height = this.config.Image.height;
        
        var ctx = canvas.getContext("2d");
        ctx.drawImage(this.config.Image, 0, 0, this.config.Image.width, this.config.Image.height);
        return canvas.toDataURL();
    },
    
    getCroppedImage: function() {
        var crops = this.getCropsForFullSizeImage();
        var width = crops[2]-crops[0];
        var height = crops[3]-crops[1];
        
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
        
        ctx.drawImage(this.config.Image, crops[0], crops[1], width, height, 0, 0, width, height);
         
        return canvas.toDataURL();
    },
    
    getCropsForFullSizeImage: function() {
        var compressionRate = this.generateCompressionRate();
        var retConfig = [];
        retConfig[0] = Math.floor(this.config.crops[0]/compressionRate);
        retConfig[1] = Math.floor(this.config.crops[1]/compressionRate);
        retConfig[2] = Math.floor(this.config.crops[2]/compressionRate);
        retConfig[3] = Math.floor(this.config.crops[3]/compressionRate);
        return retConfig;
    },
    
    onUploadStarted: function(callback) {
        this.uploadStarted = callback;
    },
    
    _uploadStarted: function() {
        this.progressDiv.show();
        if (this.uploadStarted && typeof(this.uploadStarted) == "function") {
            this.uploadStarted(this);
        }
    },
    
    getCurrentPageId: function() {
        return $('.skelholder').find('#pageid').attr('value');
    },
    
    saveImage: function() {
        PubSub.publish("LAYOUT_UPDATED", "image");
        
        this._uploadStarted();
        
        var currentPageId = this.getCurrentPageId();
        
        if (this.config.imageId) {
            var event = thundashop.Ajax.createEvent('', 'updateCordinates', this.config.app, { cords: this.getCropsForFullSizeImage(), 'getShopPageId' : currentPageId });
            thundashop.Ajax.post(event, $.proxy(this.uploadCompleted, this));
            return;
        }
        
        var data = {
            data : this.getFullSizeImage(),
            compression: 1,
            cords: this.getCropsForFullSizeImage(), 
            'getShopPageId' : currentPageId
        };
        
        var event = thundashop.Ajax.createEvent('', 'saveOriginalImage', this.config.app, data);
        event.synchron = true;
        
        var dontUpdate = true;
        var dontShowLoaderBox = true;
        
        thundashop.Ajax.post(
            event, 
            $.proxy(this.uploadCompleted, this), 
            null, 
            dontUpdate, 
            dontShowLoaderBox, 
            {
                "uploadcallback": $.proxy(this.uploadProgress, this)
            }
        );
    },
    uploadCompleted: function(response) {
        getshop.ImageEditorApi.remove(this.id);
        this.progressDiv.hide();
        if (this.uploadFinished && typeof(this.uploadFinished) == "function") {
            this.uploadFinished(this);
        }
    },
    uploadProgress: function(progress) {
        this.progressDiv.find('.gs_image_editor_progress_bar').css('width', progress+"%");
    },
    addEntryToMenu: function(entry) {
        var outer = $('<div/>');
        outer.append(entry);
        outer.addClass('entry');
        
        var disabled = $('<div/>');
        disabled.addClass('disabled');
        outer.append(disabled);
        
        this.menu.append(outer); 
        return outer;
    },
    deleteImage: function() {
        if (this.imageDeleted && typeof(this.imageDeleted) === "function") {
            this.imageDeleted(this);
        }
        this.config.imageId = null;
        this.config.Image = null;
        this.refresh();
    },
    rotateRight: function() {
        var height = this.config.Image.width;
        var width = this.config.Image.height;
        
        var canvasLoc = document.createElement("canvas");
        canvasLoc.height = height;
        canvasLoc.width = width;
        
        var ctx = canvasLoc.getContext("2d");
        ctx.rotate(90*Math.PI/180);
        ctx.drawImage(this.config.Image, 0, -width, height , width);
        this.config.Image.src = canvasLoc.toDataURL("image/png");
    },
    rotateLeft: function() {
        var height = this.config.Image.width;
        var width = this.config.Image.height;
        
        var canvasLoc = document.createElement("canvas");
        canvasLoc.height = height;
        canvasLoc.width = width;
        
        var ctx = canvasLoc.getContext("2d");
        ctx.rotate(-90*Math.PI/180);
        ctx.drawImage(this.config.Image, -height, 0, height , width);
        this.config.Image.src = canvasLoc.toDataURL("image/png");
    },
    toggleAspectRation: function() {
        var aspectButton = this.menu.find('.fa-lock');
        var isActive = aspectButton.hasClass('active');
        if (isActive) {
            aspectButton.removeClass('active');
        } else {
            aspectButton.addClass('active');
        }
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
    loadImage: function() {
        if (!this.config.imageId) {
            return;
        }

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
        $(this.menu).find('.disabled').hide();
        $(this.canvas).fadeIn();
        this.uploadMenu.hide();
        this.appendImageToCanvas();
        this.refeshCropArea(true);
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
        var crops = this.config.crops;
        
        if (doCompression) {
            var compression = this.generateCompressionRate();
            crops[0] = crops[0]*compression;
            crops[1] = crops[1]*compression;
            crops[2] = crops[2]*compression;
            crops[3] = crops[3]*compression;
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
        
        this.config.crops = [c.x, c.y, c.w+c.x, c.y2];
    },
    generateCompressionRate: function() {
        var img = this.config.Image;

        var maxWidth = this.imageWorkArea.width();
        var maxHeight = this.imageWorkArea.height();
        var ratio = 0;
        var width = img.width;
        var height = img.height;

        if (this.rotation === 90 || this.rotation === 270) {
            width = img.height;
            height = img.width;
        }

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
    getImage: function() {
        var img = new Image();
        img.src = this.getCroppedImage();
        
        this.previewContainer.html("");
        this.previewContainer.append(img);
        this.previewContainer.append(this.progressDiv);
        return this.previewContainer;
    }
};