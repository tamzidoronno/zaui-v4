GetShopToolbox = function(config, app) {
    this.config = config;

    if (this.config.application !== undefined) {
        this.config.items.push({
            icontype: "awesome",
            icon: "fa-trash-o",
            iconsize: "30",
            title: __f("Remove application"),
            click: thundashop.Skeleton.settingsRemoveApplication
        });
    }

    this.application = app;
    this.init();
};

GetShopToolbox.prototype = {
    hidden: false,
    init: function() {
        this.createContainer();
        this.refresh();
        this.addToPage();
        this.enableDrag();
    },
    refresh: function() {
        this.container.html("");
        this.addTitle();
        var entryGroup = this.createEntries(this.config.items);
        if (this.hidden) {
            entryGroup.hide();
        }
        this.container.append(entryGroup);
        this.addCloseButton();
    },
    addCloseButton: function() {
        var me = this;
        var close = $('<div/>');
        this.closeButton = close;
        if (me.hidden) {
            this.closeButton.hide();
        }
        close.addClass('close');
        close.click(function() {
            if (me.config.closeOnClick === false) {
                me.hidden = true;

                me.outerContainer.find('.toolboxgroup:first').slideUp();
                me.closeButton.hide();
            } else {
                me.outerContainer.hide();
            }
        });


        this.container.append(close);
    },
    enableDrag: function() {
        this.outerContainer.draggable({distance: 5});
    },
    addTitle: function() {
        var me = this;
        var title = $('<div/>');
        title.addClass('title');
        title.html(this.config.title);
        title.click(function() {
            me.hidden = false;
            me.outerContainer.find('.toolboxgroup:first').slideDown();
            me.closeButton.show();
        });
        this.container.append(title);
    },
    createContainer: function() {
        this.outerContainer = $('<div/>');
        this.outerContainer.css('position', 'absolute');
        this.outerContainer.css('width', '100px');
        this.outerContainer.addClass('GetShopToolbox');

        this.container = $('<div/>');
        this.container.css('position', 'relative');

        this.outerContainer.append(this.container);
    },
    createEntries: function(items, parent) {
        var entryGroup = $('<div/>');
        var j = 0;
        for (var i = 0; i < items.length; i++) {
            var config = items[i];
            if (config.type && config.type === "seperator") {
                j = 1;
            }

            if (j % 2 === 0) {
                var row = $('<div/>');
                row.addClass('row');
                entryGroup.append(row);
            }
            var item = this.createItem(config, parent, this);
            row.append(item);

            j++;
        }

        entryGroup.addClass('toolboxgroup');
        return entryGroup;
    },
    createItem: function(config, selfContainer, scope) {
        var item = null;

        if (config.type && config.type === "seperator") {
            item = this.createSeperator(config);
        } else {
            item = this.createButton(config, selfContainer, scope);
        }

        if (config.items) {
            var group = this.createEntries(config.items, item);
            item.append(group);
            item.hover(this.buttonWithChildHover, this.buttonWithChildHoverOut);
        }

        if (config.disableOnSystemPages) {
            item.addClass('disableOnSystemPages');
        }

        return item;
    },
    buttonWithChildHover: function() {
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
    createButton: function(config, parent, scope) {
        var item = $('<div/>');
        item.addClass('inline');
        item.addClass('item');
        item.attr('title', config.title);
        if (typeof(config.extraArgs) !== "undefined")
            item.attr('extraarg', config.extraArgs);

        if (config.clazz)
            item.addClass(config.clazz);

        var me = this;
        item.click(function() {
            if (me.config.closeOnClick !== false)
                me.outerContainer.hide();

            if (parent) {
                parent.find('.toolboxgroup').hide();
            }

            if (config.click) {
                config.click(config.extraArgs, scope.application);
            }
        });

        if (config.icon) {
            if (config.icontype !== "undefined" && config.icontype === "awesome") {
                var img = $('<i></i>');
                img.addClass("fa");
                img.addClass(config.icon);
                img.addClass("gs_icon");
                img.css('font-size', config.iconsize + "px");
            } else {
                var img = $('<img/>');
                img.attr('src', config.icon);
            }
            img.attr('alt', config.title);
            item.append(img);
        }

        if (config.text) {
            var div = $('<div/>');
            div.html(config.text);
            item.append(div);
        }

        if (config.appid) {
            item.attr('appid', config.appid);
        }

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
            left = offset.left + $(element).width() - this.outerContainer.width() - 2;

        if (position === 3)
            left = offset.left + $(element).outerWidth() + 30;

        this.outerContainer.css('left', left);
        this.outerContainer.css('top', offset.top);
        var me = this;
        this.outerContainer.attr('attached_to_app', 'true');
        element.on("remove", function() {
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
    },
    getConfig: function() {
        return this.config;
    },
    setConfig: function(config) {
        this.config = config;
        this.refresh();
    }
};
