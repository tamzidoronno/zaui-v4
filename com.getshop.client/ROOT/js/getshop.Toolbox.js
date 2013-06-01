GetShopToolbox = function(config) {
    this.config = config;
    this.init();
};

GetShopToolbox.prototype = {
    init: function() {
        this.createContainer();
        this.addTitle();
        var entryGroup = this.createEntries(this.config.items);
        this.container.append(entryGroup);
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
                        
    createEntries: function(items, sub) {
        var entryGroup = $('<div/>');
        var j = 0;
        for (var i = 0; i < items.length; i++) {
            var config = items[i];
            if (config.type && config.type === "seperator") {
                j = 1;
            }
            
            if (j%2 === 0) {
                var row = $('<div/>');
                row.addClass('row');
                entryGroup.append(row);
            }
            
            var item = this.createItem(config);
            row.append(item);
            
            j++;
        }
        
        entryGroup.addClass('toolboxgroup');
        return entryGroup;
    },

    createItem: function(config) {
        var item = null;
        
        if (config.type && config.type === "seperator") {
            item = this.createSeperator(config);
        } else {
            item = this.createButton(config);
        }
        
        if (config.items) {
            var group = this.createEntries(config.items);
            item.append(group);
            item.hover(this.buttonWithChildHover, this.buttonWithChildHoverOut);
        }
        
        return item;
    },
            
    buttonWithChildHover : function() {
        $(this).children('.toolboxgroup').fadeIn(300);
    },
            
    buttonWithChildHoverOut: function() {
        $(this).children('.toolboxgroup').hide();
    },

    createSeperator: function(config) {
        var seperator = $('<div/>');
        seperator.html(config.title);
        seperator.addClass('seperator');
        return seperator;
    },
            
    createButton: function(config) {
        var item = $('<div/>');
        item.addClass('inline');
        item.addClass('item');
        item.attr('title', config.title);

        var me = this;
        item.click(function() {
            if (me.config.closeOnClick !== false) 
                me.outerContainer.hide();
            config.click(config.extraArgs);
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
