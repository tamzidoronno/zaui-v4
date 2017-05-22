if (typeof(getshop) === "undefined") {
    getshop = {};
}

getshop.MenuEditor = {
    init: function() {
        $(document).on('click', ".Menu .menulist .menu .menuitem", getshop.MenuEditor.menuClicked);
        $(document).on('change', ".Menu .select_menu_selected", getshop.MenuEditor.differentSelected);
        $(document).on('click', ".Menu .menulist .menu .menuitem i.fa-edit", getshop.MenuEditor.changeMenuName);
        $(document).on('click', ".Menu .menulist .menu .menuitem i.fa-trash", getshop.MenuEditor.deleteMenu);
        $(document).on('click', ".Menu .menu.addMultiLevel", getshop.MenuEditor.addMultiLevel);
        $(document).on('keyup', ".Menu .titleinformation #itemname", getshop.MenuEditor.nameKeyUp);
        $(document).on('keyup', ".Menu .titleinformation #icontext", getshop.MenuEditor.iconKeyUp);
        $(document).on('keyup', ".Menu .titleinformation #itemlink", getshop.MenuEditor.itemLinkChanged);
        $(document).on('keyup', ".Menu .titleinformation #scrollAnchor", getshop.MenuEditor.scrollAnchorChanged);
        $(document).on('keyup', ".Menu .titleinformation #scrollPageId", getshop.MenuEditor.scrollPageIdChanged);
        $(document).on('click', ".Menu .titleinformation #hideElement", getshop.MenuEditor.hideElementChanged);
        $(document).on('click', ".Menu .titleinformation #openInSeperatedTab", getshop.MenuEditor.openInSeperatedTab);
        $(document).on('click', ".Menu .titleinformation #pageScroll", getshop.MenuEditor.pageScrollElementChanged);
        $(document).on('mouseenter', ".Menu .menuentries.horizontal .entry", getshop.MenuEditor.showSubEntries);
        $(document).on('mouseleave', ".Menu .menuentries.horizontal .entry", getshop.MenuEditor.hideSubEntries);
        $(document).on('click', ".Menu .save", getshop.MenuEditor.saveMenuEditor);
        $(document).on('click', ".Menu .mobilenavigatemenu", getshop.MenuEditor.showMenu);
        $(document).on('click', ".Menu .cancel", getshop.MenuEditor.closeMenuEditor);
        $(document).on('change', ".Menu .userrolescheckboxes", getshop.MenuEditor.roleChanged);
        $(document).on('change', ".Menu #userlevel", getshop.MenuEditor.userLevelChanged);
        $(document).on('change', ".menu_item_language", getshop.MenuEditor.itemLanguageChanged);
        $(document).on('click', ".gs_scrollitem", getshop.MenuEditor.scrollToAnchor);
        $(document).on('click', ".doscrollnav", getshop.MenuEditor.doScrollNavigate);
        $(document).on('mouseenter', ".Menu .dots .dot", getshop.MenuEditor.showIndicator);
        $(document).on('mouseleave', ".Menu .dots .dot", getshop.MenuEditor.hideIndicator);
    },
    
    roleChanged: function() {
        var id = $(this).attr('id').split('_')[1];
        if ($(this).is(':checked')) {
            getshop.MenuEditor.activeItem.roleIds.push(id);
        } else {
            var index = getshop.MenuEditor.activeItem.roleIds.indexOf(id);    // <-- Not supported in <IE9
            if (index !== -1) {
                getshop.MenuEditor.activeItem.roleIds.splice(index, 1);
            }
        }
    },
    
    openInSeperatedTab: function() {
        getshop.MenuEditor.activeItem.openInSeperatedTab = $(this).is(':checked');
    },
    
    hideIndicator : function() {
        $('.menuindicator').remove();
    },
    
    doScrollNavigate : function() {
        var page = $(this).attr('page');
        var scrollTop = $('.gsucell[anchor="page_'+page+'"]').offset().top;
        thundashop.framework.scrollToPosition(scrollTop);
        $('.closescrollmenu').click();
    },
    
    showIndicator : function() {
        var indicator = $(this).attr('data-indicator');
        var top = $(this).offset().top + 5 - $(document).scrollTop();
        var right = $(this).closest('.Menu').width();
        $('.menuindicator').remove();
        var menuindicator = $('<span class="menuindicator">' + indicator + "</span>");
        menuindicator.css('position', 'fixed');
        menuindicator.css('z-index', '100');
        menuindicator.css('top', top);
        menuindicator.hide();
        $("body").prepend(menuindicator);
        menuindicator.show();
    },
    
    pageScrollElementChanged : function() {
        getshop.MenuEditor.activeItem.pageScroll = $(this).is(':checked');
    },
    differentSelected: function() {
        getshop.MenuEditor.menuChanged();
        $(this).closest('.Menu').find('.menuentries:visible').find('.entry:first-child a').click();
    },
    
    deleteMenu: function() {
        var yes = confirm("Are you sure you want to delete this?");
        
        if (yes) {
            $(this).closest('.menuitem').attr('deleted', 'true');
            $(this).closest('.menuitem').fadeOut();
        }
        
    },
    
    selectMenu: function() {
        var selectedS = localStorage.getItem("gs_menu_selected");
        
        if (!selectedS) {
            getshop.MenuEditor.menuChanged();
            return;
        }
        
        var selectedOption = $('.Menu .select_menu_selected option[value="'+selectedS+'"]');
        if (selectedOption.length == 0) {
            getshop.MenuEditor.menuChanged();
            return;
        }
        
        selectedOption.attr('selected', 'true');
        getshop.MenuEditor.menuChanged();
    },
    
    menuChanged: function() {
        var me = $('.Menu .select_menu_selected');
        var name = me.val();
        me.closest('.Menu').find('.menuentries').hide();
        me.closest('.Menu').find('.menuentries[menuname="'+name+'"]').show();
        localStorage.setItem("gs_menu_selected", name);
    },
    
    changeMenuName: function() {
        var name = prompt("Please enter new name");
        if (name) {
            $(this).closest('div').find('span').html(name);
            var list = $(this).closest('.menuitem').attr('list').replace('container', 'tree');
            var tree = window[list].setNewMenuName(name);
        }
    },
    
    guid: function(seperator) {
        function s4() {
          return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
        }
        return s4() + s4() + seperator + s4() + seperator + s4() + seperator +
          s4() + seperator + s4() + s4() + s4();
    },
    
    addMultiLevel: function() {
        var uuid = getshop.MenuEditor.guid("_");
        var name = __f('New list');
        
        
        
        var appId = $(this).closest('.app').attr('appid');
        var div = $('<div class="menuitem" list="container_'+uuid+'" appid="'+uuid+'"><i class="fa fa-trash"></i> <i class="fa fa-edit"></i> <span>'+name+'</span></div>');
        var config  = {"menuname": name , "items":[]};
        $('.MenuEditor .menueditor').prepend("<div class='menutree' id='container_"+uuid+"'></div>");
        $('.MenuEditor .menulist .menu').append(div);
        
        var tree = new getshop.Tree('container_'+uuid, config);
        tree.refresh();
        window['tree_'+uuid] = tree;
    },
    
    scrollToAnchor: function() {
        var scrollPageId = $(this).attr('scrollPageId');
        var scrollAnchor = $(this).attr('scrollAnchor');
        
        if ($('.gsmobilemenuinstance').is(':visible')) {
            $('.gsbody').show();
            $('.gsmobilemenuinstance').hide();
        }
        
        var currentPageId = $('.gsbody_inner').attr('pageId');
        
        var diff = 0;
        
        if ($('.gsarea[area="header"]').css('position') === "fixed") {
            diff = $('.gsarea[area="header"]').outerHeight();
       }
        
        if (currentPageId === scrollPageId) {
            var target = $("#"+scrollAnchor);
            if (!target.length) {
                return;
            }
            $('html,body').animate({ scrollTop: target.offset().top - diff}, 300);
        } else {
            var link = '/?page='+scrollPageId;
            doNavigation(link, link, link, function(success) {
                var target = $("#"+scrollAnchor);
                if (!target.length) {
                    return;
                }
                $(document).ready(function() {
                    $('html,body').animate({ scrollTop: target.offset().top - diff}, 300);
                })
                
            });
        }
    },
    
    scrollAnchorChanged: function() {
        getshop.MenuEditor.activeItem.scrollAnchor = $(this).val();
    },
    hideElementChanged: function() {
        getshop.MenuEditor.activeItem.hidden = $(this).is(':checked');
    },
    scrollPageIdChanged: function() {
        getshop.MenuEditor.activeItem.scrollPageId = $(this).val();
    },
    showSubEntries : function() {
        $(this).children('.entries').show();
    },
    itemLanguageChanged: function() {
        var checked = $(this).is(':checked');
        var language = $(this).val();
        var disabledLanguages = [];
        for (var i in getshop.MenuEditor.activeItem.disabledLangues) {
            var iLang = getshop.MenuEditor.activeItem.disabledLangues[i];
            if (iLang != language) {
                disabledLanguages.push(iLang);
            }
        }
        
        if (!checked) {
            disabledLanguages.push(language);
        }
        
        getshop.MenuEditor.activeItem.disabledLangues = disabledLanguages;
    },
    hideSubEntries : function() {
        $(this).children('.entries').hide();
    },
    showMenu : function(e) {
        
        var menuentries = $(this).closest('.Menu').find(".menuentries");
        if($(menuentries).is(':visible')) {
            $(menuentries).slideUp();
        } else {
            $(menuentries).slideDown();
        }
        e.stopPropagation();
    },
    closeMenuEditor : function(e) {
        thundashop.common.hideInformationBox();
        e.stopPropagation();
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
    },
    saveMenuEditor : function() {
        var data = {};
        $(this).closest('.MenuEditor').find('.menuitem').each(function() {
            var show = $(this).attr('list');
            var id = show.replace("container_","");
            var list = window["tree_"+id];
            
            var items = list.config.items;
            for(var key in items) {
                if(!items[key].link) {
                    items[key].link = items[key].linke;
                }
            }
            
            data[$(this).attr('appid')] = {
                "name" : list.config.menuname,
                "items" : items,
                "deleted" : $(this).attr('deleted')
            }    
        });
        
        var event = thundashop.Ajax.createEvent('','updateLists', $('.MenuEditor'),data);
        thundashop.Ajax.post(event);
        thundashop.common.hideInformationBox();
    },

    setHomePage: function(entry) {
        var name = $(entry).find('.text').html();
        var data = {
            entryId: $(entry).attr('entryid')
        }
        
        var event = thundashop.Ajax.createEvent(null, "setHomePage", entry, data);
        event['synchron'] = true;
        thundashop.Ajax.postWithCallBack(event, function() {
            alert(name+__f(' page is now set as home page'));
            getshop.MenuEditor.list.refresh();
        });
    },
    
    setAsHomePage: function(pageName) {
        var data = {
            pageName: pageName
        }
        
        var event = thundashop.Ajax.createEvent(null, "setPageHomePage", $('.Menu')[0], data);
        thundashop.Ajax.postWithCallBack(event);
    },
    
    editorStarted: function(appId) {
        $('.menueditorcontainer .menu.setashomepage').droppable({
            accept: '.dropaccept',
            drop: function(event, ui) {
                getshop.MenuEditor.setHomePage(ui.draggable);
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
        thundashop.common.showInformationBoxNew(event, __f('Menu Editor'));
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
            $('.titleinformation #scrollPageId').val(getshop.MenuEditor.activeItem.scrollPageId);    
            $('.titleinformation #hideElement').val('checked',false);
            $('.titleinformation #openInSeperatedTab').attr('checked', getshop.MenuEditor.activeItem.openInSeperatedTab);
            if(getshop.MenuEditor.activeItem.hidden) {
                $('.titleinformation #hideElement').attr('checked','true');
            } else {
                $('.titleinformation #hideElement').removeAttr('checked');
            }
            if(getshop.MenuEditor.activeItem.pageScroll) {
                $('.titleinformation #pageScroll').attr('checked','true');
            } else {
                $('.titleinformation #pageScroll').removeAttr('checked');
            }
            if (getshop.MenuEditor.activeItem.link) {
                $('.titleinformation #itemlink').attr('pageId', getshop.MenuEditor.activeItem.link);
                $('.titleinformation #itemlink').val(getshop.MenuEditor.activeItem.link);
            }
            if (getshop.MenuEditor.activeItem.linke) {
                $('.titleinformation #itemlink').attr('pageId', getshop.MenuEditor.activeItem.linke);
                $('.titleinformation #itemlink').val(getshop.MenuEditor.activeItem.linke);
            }

            $('.titleinformation #icontext').val(getshop.MenuEditor.activeItem.icon);    
            $('.titleinformation #pageScroll').val(getshop.MenuEditor.activeItem.pageScroll);    
            
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
            
            $('.titleinformation .userrolescheckboxes').removeAttr('checked');
            for (var i in getshop.MenuEditor.activeItem.roleIds) {
                var roleId = getshop.MenuEditor.activeItem.roleIds[i];
                $('#role_'+roleId).attr('checked', 'true');
            }
            
            $('.menu_item_language').each(function() {
                var val = $(this).val();
                var disabled = false;
                
                for (var i in getshop.MenuEditor.activeItem.disabledLangues) {
                    var iLang = getshop.MenuEditor.activeItem.disabledLangues[i];
                    if (iLang == val) {
                        disabled = true;
                    }
                }
                if (disabled) {
                    $(this).removeAttr('checked');
                } else {
                    $(this).attr('checked', "checked");
                }
                
            });
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
    loadSettings : function(element, application) {
         var config = {
            draggable: true,
            app : true,
            application: application,
            showSettings : true,
            title: "Settings",
            items: [
                {
                    icontype: "awesome",
                    icon: "fa-pencil-square",
                    iconsize : "30",
                    title: __f("Edit menu"),
                    click: function() {
                        getshop.MenuEditor.open(application);
                    }
                }
                
            ]
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
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