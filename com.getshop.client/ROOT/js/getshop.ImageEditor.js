if (typeof (getshop) === "undefined") {
    getshop = {};
}

getshop.ImageEditor = function(attachToDom, config) {
    this.attachToDom = attachToDom;
    this.config = config;
    this.init();
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
        this.createContainer();
        this.createBigStock();
        this.setDefaultConfig();
        this.createImageCanvasWorkingArea();
        this.createUploadMenu();
        this.createPreviewContainer();
        this.addMenu();
        this.refresh();
    },
    createPreviewContainer: function() {
        this.previewContainer = $('<div/>');
        this.previewContainer.addClass('gs_image_editor_preview');
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
        upload.find('.description').html(__f("Simply drag the image to this box or browse trough your computer by clicking the button below"));
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

        disableAspectRatio = this.createMenuEntry(__f("Add text"), 'fa-bold');
        menuEntry = this.addEntryToMenu(disableAspectRatio);

        disableAspectRatio = this.createMenuEntry(__f("Rotate left"), 'fa-rotate-left');
        menuEntry = this.addEntryToMenu(disableAspectRatio);
        menuEntry.click($.proxy(this.rotateLeft, this));

        disableAspectRatio = this.createMenuEntry(__f("Rotate right"), 'fa-rotate-right');
        menuEntry = this.addEntryToMenu(disableAspectRatio);
        menuEntry.click($.proxy(this.rotateRight, this));

        disableAspectRatio = this.createMenuEntry(__f("Delete"), 'fa-trash-o');
        menuEntry = this.addEntryToMenu(disableAspectRatio);
        menuEntry.click($.proxy(this.deleteImage, this));

        disableAspectRatio = this.createMenuEntry(__f("Save"), 'fa-save');
        disableAspectRatio.click($.proxy(this.saveImage, this));
        this.addEntryToMenu(disableAspectRatio);
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
    
    saveImage: function() {
        if (this.uploadStarted && typeof(this.uploadStarted) == "function") {
            this.uploadStarted();
        }
        
        var data = {
            data : this.getFullSizeImage(),
            compression: 1,
            cords: this.getCropsForFullSizeImage()
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
        console.log("Completed: " + response);
    },
    uploadProgress: function(progress) {
        console.log(progress);
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
        this.config.imageId = null;
        this.config.Image = null;
        this.refresh();
    },
    rotateRight: function() {
        this.rotation = this.rotation + 90;
        if (this.rotation > 270) {
            this.rotation = 0;
        }
        this.refresh();
    },
    rotateLeft: function() {
        this.rotation = this.rotation - 90;
        if (this.rotation < 0) {
            this.rotation = 270;
        }
        this.refresh();
    },
    toggleAspectRation: function() {
        var aspectButton = this.menu.find('.fa-lock');
        var isActive = aspectButton.hasClass('active');
        if (isActive) {
            aspectButton.removeClass('active');
        } else {
            aspectButton.addClass('active');
        }
        this.refeshCropArea();
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
        this.setBoundaries();
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
        this.refeshCropArea();
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
    refeshCropArea: function() {
        var aspectRatio = this.menu.find('.fa-lock').hasClass('active');

        this.removeCropping();
        $(this.canvasDivInner).Jcrop({
            onRelease: $.proxy(this.cropChanged, this),
            onSelect: $.proxy(this.cropChanged, this),
            onChange: $.proxy(this.cropChanged, this),
            setSelect: this.config.crops,
            aspectRatio: aspectRatio
        }, function() {
            jcrop_api = this;
        });
    },
    cropChanged: function(c) {
        this.config.crops = [c.x, c.y, c.w+c.x, c.y2];
        console.log(this.config.crops);
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

        if (this.rotation === 90 || this.rotation === 270) {
            this.canvas.height = width;
            this.canvas.width = height;
        }

        ctx.rotate(this.rotation * Math.PI / 180);

        if (this.rotation === 0) {
            ctx.drawImage(this.config.Image, 0, 0, width, height);
        }

        if (this.rotation === 90) {
            ctx.drawImage(this.config.Image, 0, -height, width, height);
        }

        if (this.rotation === 180) {
            ctx.drawImage(this.config.Image, -width, -height, width, height);
        }

        if (this.rotation === 270) {
            ctx.drawImage(this.config.Image, -width, 0, width, height);
        }

        if (this.rotation === 90 || this.rotation === 270) {
            height = this.config.Image.width * compression;
            width = this.config.Image.height * compression;
        }

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
        this.previewContainer.html(img);
        return this.previewContainer;
    }
};