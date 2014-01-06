if (typeof(GetShop) === "undefined")  {
    GetShop = {};
};

GetShop.ImageDisplayer = function(api, appInstance) {
    this.api = api;
    this.appInstance = appInstance;
};

GetShop.ImageDisplayer.prototype = {
    /**
     * Fires when image displayer is ready.
     */
    ready: null,

    load: function() {
        var imageId = this.appInstance.settings.original_image.value;
        var image = "http://"+this.api.address + "/displayImage.php?id=" + imageId;
        var imageDom = $('<img src="'+image+'"/>');
        var imageDisplayer = $('<div>');
        imageDisplayer.addClass('ImageDisplayer');
        imageDisplayer.append(imageDom);
        if (typeof(this.ready) === "function") {
            this.ready(imageDisplayer);
        }
    }
};
 