
$(document).ready(function() {
    var config = {
        draggable: true,
        closeOnClick: false,
        title: __f("MainMenu"),
        items: [
            {
                icontype: "awesome",
                icon: "fa-folder",
                iconsize : "30",
                title: __f("Product list"),
                click: app.ProductManager.loadImportProduct,
                class: 'layout'
            },
            {
                icontype: "awesome",
                icon: "fa-th",
                iconsize : "30",
                click: thundashop.MainMenu.showPageLayoutSelection,
                title: __f("Select different page layouts"),
                disableOnSystemPages: true,
                extraArgs: {}
            },
            {
                icontype: "awesome",
                icon: "fa-sitemap",
                iconsize : "30",
                click: getshop.MenuEditor.open,
                title: __f("Setup your menus"),
                disableOnSystemPages: true,
                extraArgs: {}
            },
            {
                icontype: "awesome",
                icon: "fa-lock",
                iconsize : "30",
                click: thundashop.app.productmanager.uploadImage,
                extraArgs: {},
                items: [
                    {
                        text: __f('All'),
                        class: 'security',
                        click: thundashop.MainMenu.updateUserLevel,
                        extraArgs: 0,
                    },
                    {
                        text: __f('Customers'),
                        class: 'security',
                        click: thundashop.MainMenu.updateUserLevel,
                        extraArgs: 10,
                    },
                    {
                        text: __f('Editors'),
                        class: 'security',
                        click: thundashop.MainMenu.updateUserLevel,
                        extraArgs: 50,
                    },
                    {
                        text: __f('Admins'),
                        class: 'security',
                        click: thundashop.MainMenu.updateUserLevel,
                        extraArgs: 100
                    }
                ],
                disableOnSystemPages: true
            },
            {
                icontype: "awesome",
                icon: "fa-arrows",
                iconsize : "30",
                title: __f("Move applications"),
                click: thundashop.MainMenu.reorderApplicationClicked,
                extraArgs: {},
                disableOnSystemPages: true
            },
            {
                type: 'seperator',
                title: __f('Webshop')
            },
            {
                icontype: "awesome",
                icon: "fa-tint",
                iconsize : "30",
                title: __f("Change/select theme/skin"),
                click: thundashop.MainMenu.showHideDesignSelection,
                extraArgs: {}
            },
            {
                icontype: "awesome",
                icon: "fa-gear",
                iconsize : "30",
                title: __f("Store settings / more applications available here"),
                click: function() {
                    navigate('?page=settings');
                },
                extraArgs: {}
            },
            {
                icontype: "awesome",
                icon: "fa-globe",
                iconsize : "30",
                title: __f("Setup your own domain"),
                click: function() {
                    navigate('?page=domain');
                },
                extraArgs: {}
            },
            {
                icontype: "awesome",
                icon: "fa-gears",
                iconsize : "30",
                title: __f("Page settings"),
                click: thundashop.MainMenu.goToStoresettings,
                extraArgs: {},
                disableOnSystemPages: true
            }
        ]
    };
    config.ishidden = getshopmaintoolboxhidden;

    mainmenu = new GetShopToolbox(config);
    $(mainmenu.outerContainer).css('position', 'fixed');
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
            title: __f('More'),
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

  

    lastScrollTop = st;
});