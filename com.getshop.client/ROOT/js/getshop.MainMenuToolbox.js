$(document).ready(function() {
    var config = {
        draggable: true,
        closeOnClick: false,
        title: "MainMenu",
        items: [
            {
                icon: "",
                click: "",
                extraArgs: {},
                items: [
                    {
                        icon: "/skin/default/images/one-column-icon.png",
                        title: "One column layout",
                        click: thundashop.MainMenu.changeLayout,
                        extraArgs: 1,
                        class: 'layout',
                        disableOnSystemPages: true
                    },
                    {
                        icon: "/skin/default/images/one-column-icon.png",
                        title: "One column layout",
                        click: thundashop.MainMenu.changeLayout,
                        extraArgs: 2,
                        class: 'layout',
                        disableOnSystemPages: true
                    },
                    {
                        icon: "/skin/default/images/one-column-icon.png",
                        title: "One column layout",
                        click: thundashop.MainMenu.changeLayout,
                        extraArgs: 3,
                        class: 'layout',
                        disableOnSystemPages: true
                    },
                    {
                        icon: "/skin/default/images/one-column-icon.png",
                        title: "One column layout",
                        click: thundashop.MainMenu.changeLayout,
                        extraArgs: 4,
                        class: 'layout',
                        disableOnSystemPages: true
                    },
                    {
                        icon: "/skin/default/images/one-column-icon.png",
                        title: "7",
                        click: thundashop.MainMenu.changeLayout,
                        extraArgs: 7,
                        class: 'layout',
                        disableOnSystemPages: true
                    },
                    {
                        icon: "/skin/default/images/one-column-icon.png",
                        title: "8",
                        click: thundashop.MainMenu.changeLayout,
                        extraArgs: 8,
                        class: 'layout',
                        disableOnSystemPages: true
                    },
                    {
                        icon: "/skin/default/images/one-column-icon.png",
                        title: "9",
                        click: thundashop.MainMenu.changeLayout,
                        extraArgs: 9,
                        class: 'layout',
                        disableOnSystemPages: true
                    },
                    {
                        icon: "/skin/default/images/one-column-icon.png",
                        title: "10",
                        click: thundashop.MainMenu.changeLayout,
                        extraArgs: 10,
                        class: 'layout',
                        disableOnSystemPages: true
                    },
                    {
                        icon: "/skin/default/images/two-column-left-icon.png",
                        title: "11",
                        click: thundashop.MainMenu.changeLayout,
                        extraArgs: 11,
                        class: 'layout',
                        disableOnSystemPages: true
                    },
                    {
                        icon: "/skin/default/images/two-column-rigth-icon.png",
                        title: "12",
                        click: thundashop.MainMenu.changeLayout,
                        extraArgs: 12,
                        class: 'layout',
                        disableOnSystemPages: true
                    },
                    {
                        icon: "/skin/default/images/three-column-icon.png",
                        title: "13",
                        click: thundashop.MainMenu.changeLayout,
                        extraArgs: 13,
                        class: 'layout',
                        disableOnSystemPages: true
                    },
                    {
                        icon: "/skin/default/images/three-column-icon.png",
                        title: "14",
                        click: thundashop.MainMenu.changeLayout,
                        extraArgs: 14,
                        class: 'layout',
                        disableOnSystemPages: true
                    },
                    {
                        icon: "/skin/default/images/three-column-icon.png",
                        title: "15",
                        click: thundashop.MainMenu.changeLayout,
                        extraArgs: 15,
                        class: 'layout',
                        disableOnSystemPages: true
                    },
                    {
                        icon: "/skin/default/images/three-column-icon.png",
                        title: "16",
                        click: thundashop.MainMenu.changeLayout,
                        extraArgs: 16,
                        class: 'layout',
                        disableOnSystemPages: true
                    },
                    {
                        icon: "/skin/default/images/three-column-icon.png",
                        title: "16",
                        click: thundashop.MainMenu.changeLayout,
                        extraArgs: 17,
                        class: 'layout',
                        disableOnSystemPages: true
                    }
                ]
            },
            {
                icon: "/skin/default/images/add_product.png",
                title: "Product list",
                click: app.ProductManager.loadImportProduct,
                class: 'layout',
                disableOnSystemPages: true
            },
            {
                type: 'seperator',
                title: 'Security',
                disableOnSystemPages: true
            },
            {
                icon: "/skin/default/images/lock.png",
                click: thundashop.app.productmanager.uploadImage,
                extraArgs: {},
                items: [
                    {
                        text: 'All',
                        class: 'security',
                        click: thundashop.MainMenu.updateUserLevel,
                        extraArgs: 0,
                    },
                    {
                        text: 'Customers',
                        class: 'security',
                        click: thundashop.MainMenu.updateUserLevel,
                        extraArgs: 10,
                    },
                    {
                        text: 'Editors',
                        class: 'security',
                        click: thundashop.MainMenu.updateUserLevel,
                        extraArgs: 50,
                    },
                    {
                        text: 'Admins',
                        class: 'security',
                        click: thundashop.MainMenu.updateUserLevel,
                        extraArgs: 100
                    }
                ],
                disableOnSystemPages: true
            },
            {
                type: 'seperator',
                title: 'Applications',
                disableOnSystemPages: true
            },
            {
                icon: "/skin/default/images/add_plus.png",
                title: "Add more applications to this page.",
                click: thundashop.MainMenu.showAddApplication,
                extraArgs: {},
                disableOnSystemPages: true
            },
            {
                icon: "/skin/default/images/trash-can.png",
                title: "Remove applications that are added",
                click: thundashop.MainMenu.deleteApplicationClicked,
                extraArgs: {},
                disableOnSystemPages: true
            },
            {
                icon: "/skin/default/images/reorder.png",
                title: "Move applications",
                click: thundashop.MainMenu.reorderApplicationClicked,
                extraArgs: {},
                disableOnSystemPages: true
            },
            {
                icon: "/skin/default/images/information.png",
                title: "Application information",
                click: thundashop.MainMenu.displayApplicationInformation,
                extraArgs: {},
                disableOnSystemPages: true
            },
            {
                type: 'seperator',
                title: 'Webshop',
                disableOnSystemPages: true
            },
            {
                icon: "/skin/default/images/color-palette.png",
                title: "Change/select theme/skin",
                click: thundashop.MainMenu.showHideDesignSelection,
                extraArgs: {}
            },
            {
                icon: "/skin/default/images/settings.png",
                title: "Store settings / more applications available here",
                click: function() {
                    window.location.hash = 'page=settings';
                },
                extraArgs: {}
            },
            {
                icon: "/skin/default/images/domain-names.png",
                title: "Setup your own domain",
                click: function() {
                    window.location.hash = 'page=domain';
                },
                extraArgs: {}
            },
            {
                icon: "/skin/default/images/page.png",
                title: "Page settings",
                click: thundashop.MainMenu.goToStoresettings,
                extraArgs: {},
                disableOnSystemPages: true
            }
        ]
    };

    mainmenu = new GetShopToolbox(config);

    $(mainmenu.outerContainer).css('left', '100px');
    $(mainmenu.outerContainer).css('top', '100px');
});

// Updates the menu on navigation.
PubSub.subscribe('NAVIGATION_COMPLETED', function() {

    var moreApps = $('.moreapps').find('.renderstandalone');

    var config = mainmenu.getConfig();
    var items = [];
    $(config.items).each(function() {
        if (!this.addon) {
            items.push(this);
        }
    });
    config.items = items;

    if (moreApps.length > 0) {
        var seperator = {
            type: 'seperator',
            title: 'More',
            addon: true
        };

        config.items.push(seperator);

        moreApps.each(function() {
            var appName = $(this).attr('app');
            var appId = $(this).attr('appid');
            var item = {
                icon: $(this).find('img').attr('src'),
                title: appName,
                click: function() {
                },
                class: 'renderstandalone',
                appid: appId,
                addon: true
            };
            config.items.push(item);
        });
    }

    mainmenu.setConfig(config);

    var type = $('.skelholder').find('#skeletontype').attr('value');
    mainmenu.outerContainer.find('.layout').removeClass('active');
    mainmenu.outerContainer.find(".layout[extraarg='" + type + "']").addClass("active");

    var userlevel = $('.skelholder').find('#securitylevel').attr('value');
    mainmenu.outerContainer.find('.security').removeClass('active');
    mainmenu.outerContainer.find(".security[extraarg='" + userlevel + "']").addClass("active");

    var systemPage = $('.skelholder').find('#systempage').attr('value');
    if (systemPage) {
        mainmenu.outerContainer.find('.disableOnSystemPages').hide();
    } else {
        mainmenu.outerContainer.find('.disableOnSystemPages').show();
    }


});

var lastScrollTop = 0;
var totalMove = 0;
// Make menu follow when scrolling

$(window).scroll(function(e) {
    var st = $(this).scrollTop();
    var positiontop = $(mainmenu.outerContainer).position().top;

    var move = 0;
    var moveText = "";
    if (st > lastScrollTop) {
        var diff = st - lastScrollTop;
        move = positiontop + diff;
    } else {
        var diff = lastScrollTop - st;
        move = positiontop - diff;
    }

    var diff = st - lastScrollTop;
    totalMove += diff;

    $(mainmenu.outerContainer).animate({
        top: "+=" + diff + "px"
    }, 70, 'linear');

    lastScrollTop = st;
});