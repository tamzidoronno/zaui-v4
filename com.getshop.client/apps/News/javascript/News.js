app.News = { 
    
    init: function() {
        $(document).on('click', '.News .addevent', $.proxy(this.addEvent, app.News));
        $(document).on('click', '.News .delete', this.deleteEvent );
        $(document).on('click', '.News .publish', this.publishEvent );
        $(document).on('click', '.News .showimageeditor', app.News.showEditImage);
    },
    showEditImage: function(application) {
        var app = $(application).hasClass('app') ? application : this;
        var event = thundashop.Ajax.createEvent("", "showImageEditor", app);
        thundashop.common.showInformationBoxNew(event, __f('Image Editor'));
    },            
    publishEvent: function() {
        var id = $(this).closest('.news_container').attr('id');
        var data = {
            id : id
        }
        var event = thundashop.Ajax.createEvent('', 'publishEntry', $('.News'), data);
        thundashop.Ajax.post(event);
    },
            
    deleteEvent: function() {
        var id = $(this).closest('.news_container').attr('id');
        var data = {
            id : id
        }
        var event = thundashop.Ajax.createEvent('', 'removeEntry', $('.News'), data);
        thundashop.Ajax.post(event);
    },
            
    addEvent: function() {
        var subject = $('.News .outeraddnewscontainer #subject').val();
        var text = $('.News .outeraddnewscontainer textarea').val()
        var data = {
            subject : subject,
            text: text,
        }
        var event = thundashop.Ajax.createEvent('', 'addEntry', $('.News'), data);
        thundashop.Ajax.post(event);
    },
            
    removeApplication: function(data, app) {
        var appid = app.attr('appid');
        thundashop.Skeleton.removeApplication(appid, 'middle');
    },
            
    loadSettings: function(element, application) {
        var config = {
            draggable: true,
            title: "Settings",
            items: [
                {
                    icontype: "awesome",
                    icon: "fa-trash-o",
                    iconsize : "30",
                    title: "Remove application",
                    click: app.News.removeApplication,
                    extraArgs: {}
                }
            ]
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
};

app.News.init();