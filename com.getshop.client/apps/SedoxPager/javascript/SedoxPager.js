app.SedoxPager = {
    init: function() {
        $(document).on('click', '.pagerbutton', app.SedoxPager.goToPage);
    },
    
    goToPage: function() {
        if (!$(this).attr('go_to_page_number')) {
            return;
        }
        
        var event = thundashop.Ajax.createEvent(null, "setPageNumber", this, {pageNumber: $(this).attr('go_to_page_number')});
        thundashop.Ajax.post(event);
    },
    
    loadSettings: function(element, application) {
        var config = {
            draggable: true,
            application: application,
            title: __f("Settings"),
            items: [
                {
                    icontype: "awesome",
                    icon: "fa-chain",
                    iconsize : "30",
                    title: __f("Set connected appid"),
                    click: function() {
                        app.SedoxPager.showSetAppid(application);
                    }
                }
            ]
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    },
    
    showSetAppid: function(application) {
        var appId = prompt("Please enter id of app this app is connected to");

        var event = thundashop.Ajax.createEvent(null, "setConnectedAppId", application, {appId: appId});
        thundashop.Ajax.post(event);
    }
};

app.SedoxPager.init();