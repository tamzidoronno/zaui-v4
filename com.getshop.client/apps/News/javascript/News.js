app.News = { 
    needPackery : false,
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
    loadFilteredNews : function(event, ui) {
        var offset = ui.value;
        var type = $( "#slider-range-max" ).attr('type');
        var event = thundashop.Ajax.createEvent('','loadFilteredNews',$('.News'), {
            "type" : type,
            "newsdate" : $('.grid13[offset="'+offset+'"]').attr('type'),
            "offset" : offset
        });
        
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.globalnewsbox').html(res);
            if(app.News.needPackery) {
                $(document).find('img').batchImageLoad({
                    loadingCompleteCallback: function() {
                        $('.newscontainerbox').packery({ gutter: 10 });
                    }
                });
            }
        });
        
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
         
    loadSettings: function(element, application) {
        var config = { showSettings : true, draggable: true, title: "Settings", items: [] }
        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
};

app.News.init();