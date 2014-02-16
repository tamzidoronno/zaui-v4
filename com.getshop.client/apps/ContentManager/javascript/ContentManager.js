app.ContentManager = {
    removeText : function(extra, application) {
        var event = thundashop.Ajax.createEvent('','saveContent',application, {
            "content" : ""
        });
        thundashop.Ajax.post(event);
    },
    activateDeactivatePDF : function(extra, application) {
        var haspdf = application.find('.content').attr('haspdf');
        if(haspdf === "0") {
            haspdf = 1;
        } else {
            haspdf = 0;
        }
        var data = {
            "pdf" : haspdf
        };
        var event = thundashop.Ajax.createEvent('','activatePdf',application, data);
        thundashop.Ajax.post(event);
        
    },
    editContent : function(extra, application) {
        var contentArea = application.find('.content');
        function s4() {
            return Math.floor((1 + Math.random()) * 0x10000)
                    .toString(16)
                    .substring(1);
        }
        ;

        function guid() {
            return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
                    s4() + '-' + s4() + s4() + s4();
        }
        var id = guid();
        contentArea.attr('id', id);
        thundashop.common.activateCKEditor(id, {
            "notdestroyonblur" : true
        });
//        contentArea.focus();
    },
    
    loadSettings : function(element, application) {
         var config = {
            draggable: true,
            app : true,
            application: application,
            title: "Settings",
            items: [
                {
                    icontype: "awesome",
                    icon: "fa-edit",
                    iconsize : "30",
                    title: __f("Edit text content"),
                    click: app.ContentManager.editContent
                },
                {
                    icontype: "awesome",
                    icon: "fa-download",
                    iconsize : "30",
                    title: __f("Activate / Deactivate pdf button"),
                    click: app.ContentManager.activateDeactivatePDF
                }
                
            ]
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
}; 