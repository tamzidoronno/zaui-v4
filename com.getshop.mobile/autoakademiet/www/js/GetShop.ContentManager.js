if (typeof(GetShop) === "undefined")  {
    GetShop = {};
};

GetShop.ContentManager = function(api, appInstance) {
    this.api = api;
    this.appInstance = appInstance;
};

GetShop.ContentManager.prototype = {
    /**
     * This event is fired when all has been
     * loaded from the GetShop core.
     */
    ready: null,
            
    load: function() {
        
        this.api.ContentManager.getContent(this.appInstance.id).done($.proxy(this.receivedContentFromApi, this));
    },
    
    resized: function() {
        
    },
            
    replaceAll: function (o,t,r,c){if(c==1){cs="g"}else{cs="gi"}var mp=new RegExp(t,cs);ns=o.replace(mp,r);return ns},
            
    receivedContentFromApi: function(content) {
        content = this.replaceAll(content, '/displayImage', "http://www.getshop.com/displayImage");
        var contentHtml = $("<div>"+content+"</div>");
        contentHtml.find('img').css('height','auto');
        contentHtml.find('img').css('width','100%');
        contentHtml.find('td span').css('font-size','8px');
        
        var dom = $('<div></div>');
        dom.attr('app', 'ContentManager');
        dom.attr('id', this.appId);
        dom.addClass('ContentManager');
        dom.html(contentHtml);
        if (typeof(this.ready) === "function") {
            this.ready(dom);
        }
    }
};