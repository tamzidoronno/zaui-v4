if (typeof(GetShop) === "undefined")  {
    GetShop = {};
};

GetShop.YouTube = function(api, appInstance, page) {
    this.api = api;
    this.appInstance = appInstance;
    this.page = page;
    this.init();
};

GetShop.YouTube.prototype = {
    /**
     * Fires when image displayer is ready.
     */
    ready: null,
            
    init: function() {
        $(window).resize($.proxy(this.resized, this));
    },

    resized: function() {
        if (this.element) {
            var height = Math.floor($(this.element).width()*0.75);
            $(this.element).find('iframe').height(height+"px");
        }
    },
            
    load: function() {
        if (this.appInstance && this.appInstance.settings && this.appInstance.settings.youtubeid) {
            var youtubeid = this.appInstance.settings.youtubeid.value;
            var height = Math.floor($(document).width()*0.75);
            var video = $('<iframe title="YouTube video player" class="youtube-player" width="100%" height="'+height+'px" type="text/html" src="http://www.youtube.com/embed/'+youtubeid+'" frameborder="0" allowFullScreen></iframe>');
            
            var dom = $('<div/>');
            this.element = dom;
            dom.addClass('YouTube');
            dom.html(video);
            
            if (typeof(this.ready) === "function") {
                this.ready(dom);
            }
            
        }
    }
};
 