thundashop.ApplicationManager = {
    init: function () {
        PubSub.subscribe('setting_switch_toggled', this.onMessage, this);
        $(document).on('click', '.gs_show_application_add_list', thundashop.ApplicationManager.showApplicationAddList)
        $(document).on('click', '.gs_add_applicationlist .gs_add_app_entry .gsaddappbutton', thundashop.ApplicationManager.addApplication)
        $(document).on('click', '.gs_add_applicationlist .closeaddappbox', thundashop.ApplicationManager.closeAppList)
        $(document).on('click', '.gs_add_applicationlist .gsselectmoduletab', thundashop.ApplicationManager.selectModule)
    },
    onMessage: function (msg, data) {
        if (data.id == "toggle_application_sticky") {
            this.toggleApplicationStick(data)
        }
    },
    selectModule: function () {
        $('.gsselectmoduletab').removeClass('gsselectedmoduletab');
        $(this).addClass('gsselectedmoduletab');
        var module = $(this).attr('data-module');
        $('.gsappstoaddinmodule').hide();
        $('.gsappstoaddinmodule[data-module="' + module + '"]').show();
    },
    closeAppList: function () {
        $('.gs_add_applicationlist').fadeOut();
    },
    toggleApplicationStick: function (data) {
        if (data.state == "off") {
            data.config.sticky = "0";
        } else {
            data.config.sticky = "1";
        }
        var event = thundashop.Ajax.createEvent('ApplicationManager', 'setSticky', data.entry, data.config);
        thundashop.Ajax.post(event);
    },
    showApplicationAddList: function () {
        var menu = $('.gs_add_applicationlist');
        var left = $(this).offset().left;
        if ((left + 600) > $(document).width()) {
            left = $(document).width() - 600;
        }
        $('.gsappstoaddinmodule').hide();
        menu.css('left', left);
        menu.css('top', $(this).offset().top);
        menu.attr('cellid', $(this).closest('.gscell').attr('cellid'));
        if (menu.is(':visible')) {
            menu.fadeOut(300);
        } else {
            menu.fadeIn(100, function () {
                $('.gs_add_applicationlist .gsselectmoduletab[data-module="cms"]').click();
            });
        }
    },
    addApplication: function () {
        var data = {
            cellId: $(this).closest('.gs_add_applicationlist').attr('cellid'),
            appId: $(this).closest('.gs_add_app_entry').attr('appId'),
            pageId: $('#gspageid').val()
        };
        
        var event = thundashop.Ajax.createEvent(null, "addApplicationToCell", this, data);
        thundashop.Ajax.post(event);
    }
};

thundashop.ApplicationManager.init();
