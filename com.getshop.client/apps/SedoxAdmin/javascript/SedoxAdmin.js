app.SedoxAdmin = {
    init: function() {
        $(document).on('click', '.SedoxAdmin .sedox_admin_topmenu .entry', app.SedoxAdmin.topMenuClicked);
    },
    loadSettings: function(element, application) {
        var config = {
            draggable: true,
            app: true,
            application: application,
            title: "Settings",
            items: []
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    },
            
    topMenuClicked: function() {
        var changeTo = $(this).attr('changeto');
        var menu = $(this).parent().attr('menu');
        var data = {
            menu : menu,
            changeTo : changeTo
        };
        
        var event = thundashop.Ajax.createEvent(null, "changeToSubMenu", this, data);
        thundashop.Ajax.post(event);
    }
};

app.SedoxAdmin.init();