ProductManagerVariation = {};

ProductManagerVariation.demoConfig = [
    {
        "title": "Color",
        "children": [
            {
                "title": "Red",
                "children": [
                    {
                        "title": "Large",
                        "children": []
                    },
                    {
                        "title": "Medium",
                        "children": []
                    },
                    {
                        "title": "Small",
                        "children": []
                    }
                ]
            }
        ]
    }
];

ProductManagerVariation.Selector = function(holder, config, simple) {
    this.config = config;
    this.simple = simple;
    this.holder = holder;
    this.init();
};

ProductManagerVariation.Selector.prototype = {
    isValid : function() {
        if (this.config.length === 0) {
            return true;
        }
        
        var children = this.innerContainer.children('.selectiongroup');
        return this.checkValid(children);
    },
            
    checkValid: function(children) {
        var retval = true;
        var me = this;
        
        $(children).each(function() {
            $(this).find('select').removeClass('error');
            var level = $(this).children('select').find(':selected').attr('level');
            var parent_index = $(this).children('select').find(':selected').attr('parent_index');
            
            if (typeof(level) === "undefined") {
                $(this).find('select').addClass('error');
                retval = false;
            };
            
            level++;
            
            var activateChild = $(this).closest('div').children('div[idi='+level+'_'+parent_index+']');
            if (retval && activateChild.length > 0) {
                retval = me.checkValid(activateChild);
            }
        });
        
        return retval;
    },
    
    init : function() {
        this.createContainer();
        this.refresh();
    },
            
    setConfig: function(config) {
        this.config = config;
    },
            
    refresh: function() {
        this.innerContainer.html("");
        var options = this.config.length;
        
        if (options === 0 && !this.simple) {
            this.innerContainer.html(__f("Please click on 'Add new top level variation' above"));
            return;
        } 
        
        for (var i= 0; i<options; i++) {
            this.showSelectionBox(this.config[i], 0);
        }
    },

    onSelectionChange: function(scope) {
        var level = scope.find(':selected').attr('level');
        var parent_index = scope.find(':selected').attr('parent_index');
        var price = scope.find(':selected').attr('price');
        
        level++;
        
        scope.closest('div').children('div').each(function() {
            $(this).hide();
        });
        
        var activateChild = scope.closest('div').children('div[idi='+level+'_'+parent_index+']');
        if (activateChild) {
            activateChild.show();
        }
        
        this.updatePrice();
    },
        
    getUUIds: function() {
        var retvals = [];
        if (this.isValid()) {
            this.innerContainer.find('select :selected').each(function(i) { 
                retvals[i] = $(this).attr('uuid');
            });
        } 
        return retvals;
    },
            
    updatePrice: function() {
        var uuids = this.getUUIds();
        var data = {};
        
        if (uuids.length > 0) {
            data.uuids = this.getUUIds();
        }
        
        var event = thundashop.Ajax.createEvent("", "getPrice", this.container, data);
        
        var me = this;
        thundashop.Ajax.post(event);
    },
            
    showSelectionBox: function(option, level, parent, index) {
        var optionGroup = $('<div/>');
        optionGroup.addClass('inline');
        optionGroup.addClass('selectiongroup');
        
        var selection = $("<select/>");
        var me = this;
        selection.change(function() {
            var scope = $(this);
            me.onSelectionChange(scope);
        });
        
        if (level === 0) {
            selection.append("<option>"+__f('Please select')+" "+option.title+"</option>");
        } else {
            selection.addClass('subselection');
            selection.append("<option>"+__f('Please select')+"</option>");
        }
        
        level++;
        
        for (var i=0;i<option.children.length;i++) {
            var child = option.children[i];
            var selected = "";
            if(typeof(selectedUuids) !== "undefined" && selectedUuids.indexOf(child.id) > -1) {
                selected = "selected";
            }
            selection.append("<option "+selected+" price='"+child.priceDifference+"' uuid='"+child.id+"' level='"+level+"' parent_index='"+i+"' value='"+i+"'>"+child.title+"</option>");
        }
        
        for (var i=0;i<option.children.length;i++) {
            var child = option.children[i];
            if (child.children && child.children.length > 0) {
                this.showSelectionBox(child, level, optionGroup, i);
            }
        }
        
        optionGroup.prepend(selection);
        
        if (parent) {
            optionGroup.attr('idi', level+"_"+index);
            optionGroup.addClass('hidden');
            parent.append(optionGroup);
        } else {
            this.innerContainer.append(optionGroup);
        }
    },

    createContainer : function() {
        this.container = $('<div/>');
        this.innerContainer = $('<div/>');
        this.container.append(this.innerContainer);
        this.holder.append(this.container);
        this.container.addClass('variationselection');
        if (!this.simple)
            this.container.prepend(__f("This area displays how the variation will look like for this product view")+"<br><br>");
        this.container.append("<br>");
    }
};

ProductManagerVariation.MainPanel = function(domelement, config) { 
    this.config = config;
    this.groupPanels = new Array();
    this.conatiner = null;
    this.innerContainer = null;
    this.domelement = domelement;
    this.init(); 
};

ProductManagerVariation.MainPanel.prototype = {
    init : function() {
        this.createContainer();
        this.createTopLevelAddButton();
        this.groupPanels = this.createGroupPanels(this.config, null);
        this.appendGroupPanels();
        this.createDemo();
        this.bindSave(); 
    },
            
    bindSave: function() {
        var me = this;
        this.domelement.closest('.informationbox').find('.save_variations').click(function() {
            me.save();
        });
    },
            
    refreshDemo: function() {
        if (this.demo) {
            var config = this.getConfig();
            this.demo.setConfig(config);
            this.demo.refresh();
        }
    },
            
    createDemo: function() {
        var demo = $('<div/>');
        this.demo = new ProductManagerVariation.Selector(demo, this.getConfig());
        this.demo.refresh();
        this.container.append(demo);
    },
            
    createTopLevelAddButton: function() {
        var button = $('<div/>');
        var me = this;
        button.html(__f("Add new top level variation"));
        button.addClass("add_top_variation");
        button.addClass("gs_button");
        button.click(function() {
            me.addTopLevel();
        });
        this.innerContainer.append(button);
    },
            
    addTopLevel: function() {
        var topLevel = [{
            title: __f("Click to change"),
            children: []
        }];
        this.groupPanels = this.createGroupPanels(topLevel, null);
        this.appendGroupPanels();
        this.refreshDemo();
    },
            
    createGroupPanels: function(childrens, parent) {
        var groupPanels = new Array();
        
        if (typeof(childrens) === "undefined") {
            return groupPanels;
        }
        
        for(var i=0; i<childrens.length; i++) {
            var group = childrens[i];
            groupPanels[i] = this.createGroupPanel(group, parent);
        }
        
        return groupPanels;
    },
            
    deleteSubEntry: function(groupPanel) {
        groupPanel.remove();
        this.refreshDemo();
    },

    addSubEntry: function(groupPanel) {
        var config = this.getConfigInternal(groupPanel);
        var nextIterator = config.children.length;
        config.children[nextIterator] = {
            title: __f("Click to change")
        };
        var newGroupPanel = this.createGroupPanel(config);
        groupPanel.replaceWith(newGroupPanel);
        this.refreshDemo();
    },

    getConfig: function() {
        var groupPanels = this.container.find('div:first').children('.grouppanel');
        var ret = [];
        for (var i=0; i<groupPanels.length; i++) {
            ret[i] = this.getConfigInternal($(groupPanels[i]));
        }
        return ret;
    },
            
    /**
     * Returns the config for a specified groupPanel,
     * <br> if no group panel is specified it will return the
     * <br> complete variation panel
     * @param {type} groupPanel
     * @returns {ProductManagerVariation.MainPanel.prototype.getConfigInternal.config}
     */
    getConfigInternal : function(groupPanel) {
        var config = {};
        var title = groupPanel.children('.title');
        if (title && $(title).text() !== "")
            config.title = $(title).find('span').text();
                
        var desc = groupPanel.children('.description');
        if (desc && desc.text() !== "") 
            config.description = desc.text();
        
        var children = [];
        var me = this;
        groupPanel.children('.grouppanel').each(function(i) {
            var child = me.getConfigInternal($(this));
            children[i] = child;
        });
        
        if (title) {
            config.price = groupPanel.children('.title').find('.pricediff').val();
        }
        
        config.children = children;
        
        return config;
    },
            
    createGroupPanel: function(config, parent) {
        var me = this;
        
        var groupPanel = $('<div/>');
        groupPanel.addClass("grouppanel");
        groupPanel.parent = parent;
        var padding = "<font style='color: #838383;'>--- </font>";
        
        var spanTitle = $('<span/>');
        spanTitle.html(config.title);
        spanTitle.attr('contenteditable', 'true');
        spanTitle.keydown(function(event) {
            if (event.which === 13) {
                var text = $(this).html();
                if (text === "<br>") {
                    $(this).html(__f("Click to change"));
                }
                $(this).blur();
                me.refreshDemo();
            }
        });
        
        spanTitle.blur(function() {
            me.refreshDemo();
        });
        
        var title = $('<div/>');
        title.append(padding);
        title.append(spanTitle);
        title.addClass('title');
        
        var priceField = $('<input/>');
        priceField.attr('type', 'textfield');
        priceField.addClass('pricediff');
        priceField.val(config.priceDifference);
        if (parent) {
            title.append(priceField);
        } else {
            var head = $('<div/>');
            head.addClass('head');
            head.html(__f("Price diff") + " ( +/- )");
            title.append(head);
        }
        
        var deleteButton = $('<div/>');
        deleteButton.addClass('delete');
        deleteButton.addClass('fa');
        deleteButton.addClass('fa-trash-o');

        deleteButton.hover(function() {
            groupPanel.addClass('variationhovered');
        }, function() {
            groupPanel.removeClass('variationhovered');
        });
        deleteButton.click(function() {
            me.deleteSubEntry(groupPanel);
        });
        title.append(deleteButton);
        
        var addSubentryButton = $('<div/>');
        addSubentryButton.addClass('addsubentry');
        addSubentryButton.addClass('fa');
        addSubentryButton.addClass('fa-plus');
        addSubentryButton.click(function() {
            me.addSubEntry(groupPanel);
        });
        addSubentryButton.hover(function() {
            groupPanel.addClass('variationhovered');
        }, function() {
            groupPanel.removeClass('variationhovered');
        });
        title.append(addSubentryButton);
        
        groupPanel.append(title);
        groupPanel.append(this.createGroupPanels(config.children, groupPanel));
        
        return groupPanel;
    },
            
    createContainer : function() {
        this.container = $('<div/>');
        this.container.addClass("productvariationspanel");
        this.innerContainer = $('<div/>');
        this.container.append(this.innerContainer);
        this.domelement.append(this.container);
    },
            
    appendGroupPanels: function() {
        for (var i=0; i<this.groupPanels.length; i++) {
            var group = this.groupPanels[i];
            this.innerContainer.append(group);
        }
    },
            
    save: function() {
        data = {
            config : this.getConfig()
        };
        
        var event= thundashop.Ajax.createEvent('ProductManager', 'saveVariations', this.domelement, data);
        thundashop.Ajax.post(event, function() {
            thundashop.common.hideInformationBox();
        });
    }
};

ProductManagerVariation.GroupPanel = function(title) {
    this.parent = null;
    this.children = null;
    this.title = title;
    this.init(); 
};

ProductManagerVariation.GroupPanel.prototype = {
    init: function() {
    },
            
    showTitle: function() {
        alert(this.title);
    }
};
