if (typeof(getshop) === "undefined") {
    getshop = {};
}

getshop.MenuEditor = {
    init: function() {
        $(document).on('click', ".Menu .menulist .menu .menuitem", getshop.MenuEditor.menuClicked);
        $(document).on('keyup', ".Menu .titleinformation #itemname", getshop.MenuEditor.nameKeyUp);
        $(document).on('keyup', ".Menu .titleinformation #icontext", getshop.MenuEditor.iconKeyUp);
        $(document).on('keyup', ".Menu .titleinformation #itemlink", getshop.MenuEditor.itemLinkChanged);
        $(document).on('mouseenter', ".Menu .menuentries.horizontal .entry", getshop.MenuEditor.showSubEntries);
        $(document).on('mouseleave', ".Menu .menuentries.horizontal .entry", getshop.MenuEditor.hideSubEntries);
        $(document).on('click', ".Menu .save", getshop.MenuEditor.saveMenuEditor);
        $(document).on('click', ".Menu .mobilenavigatemenu .fa-navicon", getshop.MenuEditor.showMenu);
        $(document).on('click', ".Menu .cancel", getshop.MenuEditor.closeMenuEditor);
        $(document).on('change', ".Menu #userlevel", getshop.MenuEditor.userLevelChanged);
    },
    
    showSubEntries : function() {
        $(this).children('.entries').show();
    },
    hideSubEntries : function() {
        $(this).children('.entries').hide();
    },
    showMenu : function() {
        if($('.Menu .menuentries').is(':visible')) {
            $('.Menu .menuentries').slideUp();
        } else {
            $('.Menu .menuentries').slideDown();
        }
    },
    closeMenuEditor : function() {
        thundashop.common.hideInformationBox();
    },
    userLevelChanged: function() {
        var val = $(this).val();
        if (val === "customers") {
            getshop.MenuEditor.activeItem.userLevel = 10;
        } else if (val === "editor") {
            getshop.MenuEditor.activeItem.userLevel = 50;
        } else if (val === "admin") {
            getshop.MenuEditor.activeItem.userLevel = 90;
        } else {
            getshop.MenuEditor.activeItem.userLevel = 0;
        }
        console.log(getshop.MenuEditor.activeItem.userLevel);
    },
    saveMenuEditor : function() {
        var data = {};
        $(this).closest('.MenuEditor').find('.menuitem').each(function() {
            var show = $(this).attr('list');
            var id = show.replace("container_","");
            var list = window["tree_"+id];
            
            data[$(this).attr('appid')] = {
                "name" : list.config.menuname,
                "items" : list.config.items
            }
            
        });
        
        console.log(data);
        var event = thundashop.Ajax.createEvent('','updateLists', $('.MenuEditor'),data);
        thundashop.Ajax.post(event);
        thundashop.common.hideInformationBox();
    },

    editorStarted: function(appId) {
        $('.menueditorcontainer .menu.delete').droppable({
            accept: '.dropaccept',
            drop: function(event, ui) {
                var droppedId = $(ui.draggable).attr('entryid');
                getshop.MenuEditor.deleteEntry(droppedId);
                $(this).removeClass('active');
            },
            over: function( event, ui ) {
                $(this).addClass('active');
            },
            out: function( event, ui ) {
                $(this).removeClass('active');
            },
            tolerance: 'pointer'
        });
        
        $('.menueditorcontainer .menu.new').draggable({
            revert: true
        });
        $('[appid="'+appId+'"]').find('.menulist').find('.menuitem[appid="'+appId+'"]').click();
    },
    
    deleteEntry: function(entryId) {
        if (getshop.MenuEditor.list) {
            getshop.MenuEditor.list.getAndRemoveConfig(entryId);
            getshop.MenuEditor.list.refresh();
        }
    },
            
    nameKeyUp: function() {
        getshop.MenuEditor.activeItem.name = $(this).val();
        getshop.MenuEditor.list.refresh();
    },
            
    iconKeyUp: function() {
        getshop.MenuEditor.activeItem.icon = $(this).val();
        getshop.MenuEditor.list.refresh();
    },

    itemLinkChanged: function() {
        getshop.MenuEditor.activeItem.link = $(this).val();
        getshop.MenuEditor.activeItem.pageId = $(this).attr('pageId');
    },
            
    menuClicked: function() {
        var show = $(this).attr('list');
        getshop.MenuEditor.list = window[show.replace("container_","tree_")];
        $('.menutree').slideUp();
        $('#'+show).fadeIn(200);
        $('.disabled').fadeOut(300);
        getshop.MenuEditor.activeItem = null;
        getshop.MenuEditor.refreshItemDetails();
        $('.menulist .active').removeClass('active');
        $(this).addClass('active');
    },

    open: function(app) {
        var event = thundashop.Ajax.createEvent('', 'renderSetup', app, {});
        thundashop.common.showInformationBox(event, __f('Menu Editor'));
    },
            
    menuEntryClicked: function(item) {
        $('.menutree .active').removeClass('active');
        $('.menutree [entryid='+item.id+"]").addClass('active');
        getshop.MenuEditor.activeItem = item;
        this.refreshItemDetails();
    },
            
    refreshItemDetails: function() {
        if (!getshop.MenuEditor.activeItem) {
            
            $('.iteminformation').show();
            $('.titleinformation').slideUp();
        } else {
            $('.iteminformation').slideUp();
            $('.titleinformation').show();
            $('.titleinformation #itemname').val(getshop.MenuEditor.activeItem.name);    
            $('.titleinformation #itemlink').attr('pageId', getshop.MenuEditor.activeItem.pageId);    
            $('.titleinformation #itemlink').val(getshop.MenuEditor.activeItem.link);    
            $('.titleinformation #icontext').val(getshop.MenuEditor.activeItem.icon);    
            var userLevel = this.activeItem.userLevel;
            if (!userLevel) {
                $('#userlevel').val("public");
            } else if (userLevel === 10) {
                $('#userlevel').val("customers");
            } else if (userLevel === 50) {
                $('#userlevel').val("editor");
            } else if (userLevel === 90) {
                $('#userlevel').val("admin");
            }
        }
        
    }
};

getshop.MenuEditor.init();

app.Menu = {
    ignoreRemovalOfSimpleMenu: false,
    
    init: function() {
        $(document).on('click', '.Menu .showedit', this.showMenuEditor);
        $(document).on('click', '.Menu .simpleadd', this.simpleAdd);
        $(document).on('click', '.Menu .saveit', this.saveEntry);
        $(document).on('change', '.Menu .addtext', this.saveEntry);
        $(document).on('mouseenter', '.app.Menu', this.showSimpleAdd);
        $(document).on('mouseleave', '.app.Menu', this.removeSimpleAdd);
    },
    
    showSimpleAdd: function() {
        var menu = $(this).find('.menu_simple_menu');
        if (!menu.hasClass('hidden_simple_menu')) {
            app.Menu.ignoreRemovalOfSimpleMenu = true;
            return;
        } else {
            app.Menu.ignoreRemovalOfSimpleMenu = false;
            menu.removeClass('hidden_simple_menu');
        }
        
    },
    
    removeSimpleAdd: function() {
        
        if (app.Menu.ignoreRemovalOfSimpleMenu) {
            return;
        }
        
        var app2 = $(this).closest('.app');
        if (app2.find('.simepladd_dialog').is(":visible")) {
            return;
        }
        
        var menu = $(this).find('.menu_simple_menu');
        menu.addClass('hidden_simple_menu');
    },
    
    showMenuEditor: function() {
        getshop.MenuEditor.open(this);
    },
    
    simpleAdd: function() {
        var app = $(this).closest('.app');
        app.find('.simepladd_dialog').slideDown(300);
    },
    
    saveEntry: function() {
        var app = $(this).closest('.app')
        var data = {
            text : app.find('.addtext').val()
        }
       
        var event = thundashop.Ajax.createEvent(null, "addEntry", this, data);
        thundashop.Ajax.post(event);
    }
}

app.Menu.init();