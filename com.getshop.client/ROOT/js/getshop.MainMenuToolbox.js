var mainmenu;

$(document).ready(function() {
    var config = {
        draggable: true,
        closeOnClick: false,
        title: __f("Menu"),
        items: []
    };
    config.ishidden = getshopmaintoolboxhidden;
    
});

// Updates the mainmenu on navigation.
PubSub.subscribe('NAVIGATION_COMPLETED', function() {

    var moreApps = $('.moreapps').find('.renderstandalone');
    if(!isAdministrator) {
        return;
    }
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
                clazz: 'renderstandalone',
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
        mainmenu.outerContainer.find('.disableOnSystemPages').addClass('disabled');
    } else {
        mainmenu.outerContainer.find('.disableOnSystemPages').removeClass('disabled');
    }
});
