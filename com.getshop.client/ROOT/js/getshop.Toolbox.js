GetShopToolbox = function(config) {
    this.config = config;
    this.init();
};

GetShopToolbox.prototype = {
    init: function() {
        this.createContainer();
        this.addTitle();
        this.createEntries();
        this.addToPage();
        this.enableDrag();
        this.addCloseButton();
    },
            
    addCloseButton: function() {
        var me = this;
        var close = $('<div/>');
        close.addClass('close');
        close.click(function() {
            me.outerContainer.hide();
        });
        this.container.append(close);
    },
    
    enableDrag: function() {
        this.outerContainer.draggable();
    },

    addTitle: function() {
        var title = $('<div/>');
        title.addClass('title');
        title.html(this.config.title);
        this.container.append(title);
    },
            
    createContainer: function() {
        this.outerContainer = $('<div/>');
        this.outerContainer.css('position','absolute');
        this.outerContainer.css('width','100px');
        this.outerContainer.addClass('GetShopToolbox');    
        
        this.container = $('<div/>');
        this.container.css('position','relative');
        
        this.outerContainer.append(this.container);
    },
                        
    createEntries: function() {
        for (var i = 0; i < this.config.items.length; i++) {
            if (i%2 == false) {
                var row = $('<div/>');
                row.addClass('row');
            }
            
            this.container.append(row);
            var item = this.createItem(i);
            row.append(item);
        }
    },
            
    createItem: function(index) {
        var config = this.config.items[index];
        var item = $('<div/>');
        item.addClass('inline');
        item.addClass('item');
        item.attr('title', config.title);
        
        var me = this;
        item.click(function() {
            config.click(config.extraArgs);
            me.outerContainer.hide();
        });
        
        var img = $('<img/>');
        img.attr('src', config.icon);
        img.attr('alt', config.title);
        item.append(img);
        return item;
    },
            
    addToPage: function() {
        $('html').append(this.outerContainer);
    },
      
    /**
     * Ehm, move the toolbox? No problems, use this function.yo
     * 
     * @param {type} left
     * @param {type} top
     * @returns {undefined}
     */
    setPosition: function(left, top) {
        this.outerContainer.css('left', left);
        this.outerContainer.css('top', left);
    },
    
    /**
     * Can attach this to any element positions are as following
     * <br>
     * <br> <b>Note:</b> if the element is removed from dom, it will distroy this toolbox too.
     * <br>
     * <br>1 - top left corner.
     * <br>2 - top right corner.
     *  
     * @param {type} element
     * @param {type} position
     * @returns {undefined}
     */        
    attachToElement: function(element, position) {
        var offset = $(element).offset();
        var left = offset.left;
        
        if (position === 2) 
            left = offset.left + $(element).width() - this.outerContainer.width() -2;
        
        this.outerContainer.css('left', left);
        this.outerContainer.css('top', offset.top);   
        var me = this;
        
        element.on("remove", function () {
            me.destroy();
        });
    },
            
    destroy: function() {
        this.outerContainer.remove();
    },

    hide: function() {
        this.outerContainer.hide();
    },
            
    show: function() {
        this.outerContainer.fadeIn(300);
    }
};
