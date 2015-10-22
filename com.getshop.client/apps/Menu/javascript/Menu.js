if (typeof(getshop) === "undefined") {
    getshop = {};
}

app.Menu = {
    ignoreRemovalOfSimpleMenu: false,
    menuEntries : {},
    loadedItem : null,
    currentJsTree : null,
    
    init: function() {
        $(document).on('click', '.Menu .menueditorcontainer .save', app.Menu.saveList);
        $(document).on('keyup', '.Menu .titleinformation input', app.Menu.updateSelectedObject);
        $(document).on('change', '.Menu .titleinformation select', app.Menu.updateSelectedObject);
        $(document).on('change', '.Menu .titleinformation input', app.Menu.updateSelectedObject);
        $(document).on('mouseenter', ".Menu .menuentries.horizontal .entry", app.Menu.showSubEntries);
        $(document).on('mouseleave', ".Menu .menuentries.horizontal .entry", app.Menu.hideSubEntries);
    },
    
    open: function(app) {
        var event = thundashop.Ajax.createEvent('', 'renderSetup', app, {});
        thundashop.common.showInformationBox(event, __f('Menu Editor'));
    },
    showSubEntries : function() {
        $(this).children('.entries').show();
    },
    hideSubEntries : function() {
        $(this).children('.entries').hide();
    },
    loadMenuTree : function() {
        $.getScript("/js/jstree/jstree.min.js", function () {
            app.Menu.currentJsTree = $('#menueditorjstree').jstree({
                "core": {
                    "animation": 0,
                    "check_callback": true
                },
                "types": {
                    "#": {
                        "max_children": 1,
                        "max_depth": 4,
                        "valid_children": ["root"]
                    },
                    "root": {
                        "icon": "/static/3.2.0/assets/images/tree_icon.png",
                        "valid_children": ["default"]
                    },
                    "default": {
                        "valid_children": ["default", "file"]
                    },
                    "file": {
                        "icon": "glyphicon glyphicon-file",
                        "valid_children": []
                    }
                },
                "plugins": [
                    "contextmenu", "dnd", "search",
                    "state", "types", "wholerow"
                ]
            })
            .on('select_node.jstree', app.Menu.loadItemConfig)
            .on('create_node.jstree', app.Menu.createDataObject)
            .on('rename_node.jstree', app.Menu.renameNode)
            .on('delete_node.jstree', app.Menu.nodeRemoved);
        });
        $("<link/>", {
            rel: "stylesheet",
            type: "text/css",
            href: "/js/jstree/themes/default/style.min.css"
        }).appendTo("head");        
    },
    addMenuItem : function(menuItem) {
        app.Menu.menuEntries[menuItem.id] = menuItem;
    },
    nodeRemoved : function(event, data) {
        delete app.Menu.menuEntries[data.node.id];
    },
    
    loadItemConfig : function(e, data) {
        var selected = data.selected;
        if(selected.length == 1) {
            var id = selected[0];
            app.Menu.loadedItem = id;
            $('.iteminformation').hide();
            $('.titleinformation').show();
            app.Menu.loadDataObject(app.Menu.menuEntries[id]);
        } else {
            $('.iteminformation').show();
            $('.titleinformation').hide();
        }
    },
    
    loadDataObject : function(dataobject) {
        $('#itemname').val(dataobject.name);
        $('#itemlink').val(dataobject.hardLink);
        $('#icontext').val(dataobject.fontAwsomeIcon);
        $('#userlevel').val(dataobject.userLevel);
        $('.menu_item_language').each(function() {
            var key = $(this).val();
            if(dataobject.disabledLangues.indexOf(key) < 0) {
                $(this).attr('checked','checked');
            } else {
                $(this).attr('checked',null);
            }
        });
    },
    createDataObject : function(e, data) {
        var dataobject = {
            "id" : data.node.id,
            "name" : "New node",
            hardLink : "",
            icontext : "",
            userlevel : "",
            pageId : "",
            disabledLangues : []
        }
        app.Menu.addMenuItem(dataobject);
    },
    renameNode : function(e, data) {
        app.Menu.menuEntries[data.node.id].name = data.text;
        if(app.Menu.loadedItem === data.node.id) {
            app.Menu.loadDataObject(app.Menu.menuEntries[data.node.id]);
        } 
    },
    updateSelectedObject : function() {
        var id = app.Menu.loadedItem;
        var object = app.Menu.menuEntries[id];
        
        object.hardLink = $('#itemlink').val();
        object.fontAwsomeIcon = $('#icontext').val();
        object.userLevel = $('#userlevel').val();
        object.disabledLangues = [];
        
        $('.menu_item_language').each(function() {
            if(!$(this).is(':checked')) {
                var key = $(this).val();
                object.disabledLangues.push(key);
            }
        });
        app.Menu.menuEntries[id] = object;
    },
    
    loadSettings : function(element, application) {
         var config = {
            draggable: true,
            app : true,
            application: application,
            title: "Settings",
            items: [
                {
                    icontype: "awesome",
                    icon: "fa-edit",
                    iconsize : "30",
                    title: __f("Edit menu"),
                    click: function() {
                        app.Menu.open(application);
                    }
                }
            ]
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    },
    showMenuEditor: function() {
        getshop.MenuEditor.open(this);
    },
    
    saveList: function() {
        var editor = $('#editorroot');
        var v = $('#menueditorjstree').jstree(true).get_json();
        var result = app.Menu.generateNodeList(v[0]);
        var data = {
            "items" : result
        }

        var event = thundashop.Ajax.createEvent('','updateLists',$(this), data);
        thundashop.Ajax.post(event);
        thundashop.common.hideInformationBox();
    },
    generateNodeList : function(node) {
        var result = [];
        for(var idx in node.children) {
            var child = node.children[idx];
            var object = app.Menu.menuEntries[child.id];
            object.children = app.Menu.generateNodeList(child);
            result.push(object);
        }
        return result;
    }
}

app.Menu.init();