$(document).ready(function() {
    var config = {
        draggable : true,
        closeOnClick: false,
        title : "Layout",
        items : [
            {
                icon : "/skin/default/images/one-column-icon.png",
                title : "One column layout",
                click : thundashop.MainMenu.changeLayout,
                extraArgs: 4
            },
            
            {
                icon : "/skin/default/images/two-column-left-icon.png",
                title : "Two column layout with a left sidebar",
                click : thundashop.MainMenu.changeLayout,
                extraArgs: 2
            },
            {
                icon : "/skin/default/images/two-column-rigth-icon.png",
                title : "Two column layout with a right sidebar",
                click : thundashop.MainMenu.changeLayout,
                extraArgs: 3
            },
            {
                icon : "/skin/default/images/three-column-icon.png",
                title : "Three column layout",
                click : thundashop.MainMenu.changeLayout,
                extraArgs: 1
            },
            {
                type: 'seperator',
                title: 'Security'
            },
            {
                icon : "/skin/default/images/lock.png",
                click : thundashop.app.productmanager.uploadImage,
                extraArgs: {},
                items : [
                    {
                        icon : "/skin/default/images/two-column-rigth-icon.png",
                        title : "Two column layout with a right sidebar",
                        click : thundashop.MainMenu.changeLayout,
                        extraArgs: 3
                    },
                    {
                        icon : "/skin/default/images/three-column-icon.png",
                        title : "Three column layout",
                        click : thundashop.MainMenu.changeLayout,
                        extraArgs: 1
                    }        
                ]
            },
            {
                type: 'seperator',
                title: 'Applications'
            }, 
            {
                icon : "/skin/default/images/add_plus.png",
                title : "Add more applications to this page.",
                click : thundashop.MainMenu.showAddApplication,
                extraArgs: {}
            },
            {
                icon : "/skin/default/images/trash-can.png",
                title : "Remove applications that are added",
                click : thundashop.MainMenu.deleteApplicationClicked,
                extraArgs: {}
            },
            {
                icon : "/skin/default/images/reorder.png",
                title : "Move applications",
                click : thundashop.MainMenu.reorderApplicationClicked,
                extraArgs: {}
            },
            {
                type: 'seperator',
                title: 'Webshop'
            },
            {
                icon : "/skin/default/images/color-palette.png",
                title : "Change/select theme/skin",
                click : thundashop.MainMenu.showHideDesignSelection,
                extraArgs: {}
            },
            {
                icon : "/skin/default/images/settings.png",
                title : "Store settings / more applications available here",
                click : function() {
                    window.location.hash = 'page=settings';
                },
                extraArgs: {}
            },
            
            {
                icon : "/skin/default/images/domain-names.png",
                title : "Setup your own domain",
                click : function() {
                    window.location.hash = 'page=domain';
                },
                extraArgs: {}
            },
            
            {
                icon : "/skin/default/images/page.png",
                title : "Page settings",
                click : thundashop.MainMenu.goToStoresettings,
                extraArgs: {}
            }
        ]
    };
    
    mainmenu = new GetShopToolbox(config);
    
    $(mainmenu.outerContainer).css('left', '100px');
    $(mainmenu.outerContainer).css('top', '100px');
});