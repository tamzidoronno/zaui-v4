if (typeof(getshop) === "undefined") {
    getshop = {};
}

getshop.Tree = function(id, config) {
    this.outerContainer = $('#'+id);
    this.container = $('<div/>');
    this.outerContainer.addClass('getshoptreemenuouter');
    this.outerContainer.html('<div class="title"></div>');
    this.outerContainer.append(this.container);
    this.config = config;
};

getshop.Tree.prototype = {
    
    refresh: function() {
        this.outerContainer.find(".title").html(this.config.menuname);
        this.container.html("");
        this.container.addClass('getshopmenu');
        var menu = this.renderItems(this.config.items, false);
        this.container.append(menu);
    },
            
    renderItems: function(items, lastEntry) {
        var menu = $("<ul></ul>");
        for (var i in items) {
            var item = items[i];
            var totalItems = parseInt(i)+1;
            var element = this.renderMenuItem(item, items.length === totalItems);
            menu.append(element);
        }
        if (lastEntry) {
            menu.addClass('lastEntry');
        }
        return menu;
    },
            
    s4: function() {
        return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
    },

    guid: function () {
        return this.s4() + this.s4() + '-' + this.s4() + '-' + this.s4() + '-' + this.s4() + '-' + this.s4() + this.s4() + this.s4();
    },

    createNewItem: function() {
        var item = {
            name : __f("New entry"),
            id: this.guid(),
            items: []
        };
        return item;
    },
            
    renderMenuItem: function(item, lastItem) {
        var element = $('<li/>')
        var me = this;
        var name = $('<div/>');
        name.html(item.name);
        name.addClass('text');
        
        name.click(function() {
            getshop.MenuEditor.menuEntryClicked(item);
        });
        
        element.addClass('dropaccept');
        element.attr('entryId', item.id);
        element.html("<div class='outerentry'><div class='top drophere'></div><div class='middle drophere'><table><tr><td><div class='lefticon'></div></td><td class='tabletdtitle'></td></tr></table></div><div class='bottom drophere'></div>");
        element.find('.tabletdtitle').html(name);
        if (lastItem) {
            element.find('.lefticon').addClass('lastEntry');
        }
        element.draggable({
            revert: 'invalid'
        });
        
        element.find('.drophere').droppable({
            accept: '.dropaccept',
            drop: function(event, ui) {
                var to = $(this).closest('li');
                var from = ui.draggable;
                if ($(this).hasClass('middle')) {
                    me.moveElement(from, to);
                }
                if ($(this).hasClass('top')) {
                    me.moveElementTopOf(from, to);
                }
                if ($(this).hasClass('bottom')) {
                    me.moveElementBottomOf(from, to);
                }
                me.refresh();
            },
            over: function( event, ui ) {
                $(this).addClass('active');
            },
            out: function( event, ui ) {
                $(this).removeClass('active');
            },
            tolerance: 'pointer'
        });
        
        if (item.items) {
            var subMenu = this.renderItems(item.items, lastItem);
            element.append(subMenu);
        } else {
            element.addClass('singleEntry');
        }
        return element;
    },
    
    getAndRemoveConfig: function(id, items) {
        if (!id) {
            return null;
        }
        if (!items) {
            items = this.config.items;
        }
            
        for (var i in items) {
            var item = items[i];
            if (item.items) {
                var retItem = this.getAndRemoveConfig(id, item.items);
                if (retItem) {
                    return retItem;
                }
            }
            if (item.id === id) {
                retItem = items[i];
                items.splice(i, 1);
                return retItem;
            }
        }
        
        return null;
    },
    
    addConfig: function(id, config, items) {
        if (!config) {
            config = this.createNewItem();
        }
        for (var i in items) {
            var item = items[i];
            if (item.items) {
                var added = this.addConfig(id,config,item.items);
                if (added) {
                    return true;
                }
            }
            
            if (item.id === id) {
                if (!item.items) {
                    item.items = [];
                };
                item.items.unshift(config);
                return true;
            }
        }
        
        return false;  
    },
    
    addConfigPosition: function(id, config, items, parents, bottom) {
        if (!config) {
            config = this.createNewItem();
        }
        for (var i in items) {
            var item = items[i];
            
            if (item.items) {
                this.addConfigPosition(id,config,item.items, items, bottom);
            }
            
            if (item.id === id) {
                if (bottom) {
                    items.splice(i+1, 0, config);
                } else {
                    items.splice(i, 0, config);
                }
                return;
            }
        }
        
    },
    
    moveElement: function(from, to) {
        var fromId = from.attr('entryId');
        var toId = to.attr('entryId');
        var fromConfig = this.getAndRemoveConfig(fromId, this.config.items);
        this.addConfig(toId, fromConfig, this.config.items);
    },
            
    moveElementTopOf: function(from, to) {
        var fromId = from.attr('entryId');
        var toId = to.attr('entryId');
        var fromConfig = this.getAndRemoveConfig(fromId, this.config.items);
        this.addConfigPosition(toId, fromConfig, this.config.items, this.config.items, false);
    },
            
    moveElementBottomOf: function(from, to) {
        var fromId = from.attr('entryId');
        var toId = to.attr('entryId');
        var fromConfig = this.getAndRemoveConfig(fromId, this.config.items);
        this.addConfigPosition(toId, fromConfig, this.config.items, this.config.items, true);
    },
            
    getConfig: function() {
        return this.config;
    }
};