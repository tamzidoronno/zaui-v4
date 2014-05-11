if (typeof(getshop) === "undefined") {
    getshop = {};
}

getshop.MenuEditor = {
    init: function() {
        $(document).on('click', ".MenuEditor .menulist .menu .menuitem", getshop.MenuEditor.menuClicked);
        $(document).on('keyup', ".MenuEditor .titleinformation #itemname", getshop.MenuEditor.nameKeyUp);
        $(document).on('keyup', ".MenuEditor .titleinformation #icontext", getshop.MenuEditor.iconKeyUp);
        $(document).on('keyup', ".MenuEditor .titleinformation #itemlink", getshop.MenuEditor.itemLinkChanged);
        $(document).on('click', ".MenuEditor .save", getshop.MenuEditor.saveMenuEditor);
        $(document).on('click', ".MenuEditor .cancel", getshop.MenuEditor.closeMenuEditor);
        $(document).on('change', ".MenuEditor #userlevel", getshop.MenuEditor.userLevelChanged);
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

    editorStarted: function() {
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
        $('.menutree').hide();
        $('#'+show).fadeIn(200);
        $('.disabled').fadeOut(300);
        getshop.MenuEditor.activeItem = null;
        getshop.MenuEditor.refreshItemDetails();
        $('.menulist .active').removeClass('active');
        $(this).addClass('active');
    },

    open: function() {
        var event = thundashop.Ajax.createEvent('', 'showMenuEditor', $(this), {});
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
            $('.titleinformation').hide();
        } else {
            $('.iteminformation').hide();
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